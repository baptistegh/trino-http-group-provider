package com.github.baptistegh.trino.group.http;

import io.airlift.http.client.HttpClient;
import io.airlift.http.client.Request;
import io.airlift.http.client.Response;
import io.airlift.http.client.ResponseHandler;
import io.trino.spi.security.GroupProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Set;

import static io.airlift.http.client.Request.Builder.prepareGet;
import static java.util.Objects.requireNonNull;

public class HttpGroupProvider
        implements GroupProvider
{
    private final HttpClient httpClient;
    private final String endpoint;
    private final String authToken;
    private final ObjectMapper objectMapper;

    public HttpGroupProvider(HttpClient httpClient, HttpGroupConfig config)
    {
        this.httpClient = requireNonNull(httpClient, "httpClient is null");
        this.endpoint = requireNonNull(config.getEndpoint(), "endpoint is null");
        this.authToken = config.getAuthToken();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Set<String> getGroups(String user)
    {
        try {
            Request request = prepareGet()
                    .setUri(URI.create(endpoint + "/" + user))
                    .setHeader("Accept", "application/json")
                    .setHeader(authToken != null ? "Authorization" : null, 
                             authToken != null ? "Bearer " + authToken : null)
                    .build();

            return httpClient.execute(request, new ResponseHandler<Set<String>, RuntimeException>() {
                @Override
                public Set<String> handleException(Request request, Exception exception)
                {
                    throw new RuntimeException("Failed to get groups for user: " + user, exception);
                }

                @Override
                public Set<String> handle(Request request, Response response)
                        throws RuntimeException
                {
                    if (response.getStatusCode() == 200) {
                        try {
                            String[] groups = objectMapper.readValue(response.getInputStream(), String[].class);
                            return Set.copyOf(Arrays.asList(groups));
                        }
                        catch (IOException e) {
                            throw new RuntimeException("Failed to parse groups response for user: " + user, e);
                        }
                    }
                    return Set.of();
                }
            });
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to get groups for user: " + user, e);
        }
    }
}