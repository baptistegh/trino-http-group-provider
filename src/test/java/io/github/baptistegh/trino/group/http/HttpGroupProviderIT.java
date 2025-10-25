package io.github.baptistegh.trino.group.http;

import io.airlift.http.client.Request;
import io.airlift.http.client.Response;
import io.airlift.http.client.testing.TestingHttpClient;
import io.airlift.http.client.testing.TestingHttpClient.Processor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.google.common.net.MediaType.JSON_UTF_8;
import static io.airlift.http.client.HttpStatus.INTERNAL_SERVER_ERROR;
import static io.airlift.http.client.HttpStatus.NOT_FOUND;
import static io.airlift.http.client.HttpStatus.OK;
import static io.airlift.http.client.HttpStatus.UNAUTHORIZED;
import static io.airlift.http.client.testing.TestingResponse.mockResponse;
import static org.assertj.core.api.Assertions.assertThat;

class HttpGroupProviderIT {

    private TestingHttpClient httpClient;
    private HttpGroupProvider provider;

    @BeforeEach
    void setUp() {
        httpClient = new TestingHttpClient(new Processor() {
            @Override
            public Response handle(Request request) {
                String pathInfo = request.getUri().getPath();
                String authHeader = request.getHeader("Authorization");

                if (!"Bearer test-token".equals(authHeader)) {
                    return mockResponse(UNAUTHORIZED, JSON_UTF_8, "Unauthorized");
                }

                if (pathInfo.endsWith("/testuser")) {
                    return mockResponse(OK, JSON_UTF_8, "[\"admin\",\"users\",\"developers\"]");
                }
                else if (pathInfo.endsWith("/unknown")) {
                    return mockResponse(NOT_FOUND, JSON_UTF_8, "User not found");
                }
                else if (pathInfo.endsWith("/error")) {
                    return mockResponse(INTERNAL_SERVER_ERROR, JSON_UTF_8, "Internal error"); 
                }
                else {
                    return mockResponse(NOT_FOUND, JSON_UTF_8, "Not found");
                }
            }
        });

        // Configure the provider
        HttpGroupConfig config = new HttpGroupConfig()
                .setEndpoint("http://test/groups")
                .setAuthToken("test-token");
        provider = new HttpGroupProvider(httpClient, config);
    }

    @AfterEach
    void tearDown() {
        if (httpClient != null) {
            httpClient.close();
        }
    }

    @Test
    void shouldFetchGroupsFromHttpEndpoint() {
        // When
        Set<String> groups = provider.getGroups("testuser");

        // Then
        assertThat(groups)
                .hasSize(3)
                .containsExactlyInAnyOrder("admin", "users", "developers");
    }

    @Test
    void shouldReturnEmptySetWhenUserNotFound() {
        // When
        Set<String> groups = provider.getGroups("unknown");

        // Then
        assertThat(groups).isEmpty();
    }

    @Test
    void shouldHandleServerError() {
        // When
        Set<String> groups = provider.getGroups("error");

        // Then
        assertThat(groups).isEmpty();
    }
}