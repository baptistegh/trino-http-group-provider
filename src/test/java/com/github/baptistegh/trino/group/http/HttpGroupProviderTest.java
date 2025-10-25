package com.github.baptistegh.trino.group.http;

import io.airlift.http.client.HttpClient;
import io.airlift.http.client.Request;
import io.airlift.http.client.Response;
import io.airlift.http.client.ResponseHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HttpGroupProviderTest {

    @Mock
    private HttpClient httpClient;

    @Mock
    private Response response;

    private HttpGroupProvider provider;
    private HttpGroupConfig config;

    @BeforeEach
    void setUp() {
        config = new HttpGroupConfig()
                .setEndpoint("http://test-api/groups")
                .setAuthToken("test-token");
        provider = new HttpGroupProvider(httpClient, config);
    }

    @Test
    void shouldReturnGroupsForUser() {
        // Given
        String jsonResponse = """
            ["admin", "users", "developers"]
            """;
        when(response.getStatusCode()).thenReturn(200);
        when(response.getInputStream()).thenReturn(new ByteArrayInputStream(jsonResponse.getBytes(StandardCharsets.UTF_8)));
        when(httpClient.execute(any(Request.class), any(ResponseHandler.class))).thenAnswer(invocation -> {
            ResponseHandler<Set<String>, RuntimeException> handler = invocation.getArgument(1);
            return handler.handle(invocation.getArgument(0), response);
        });

        // When
        Set<String> groups = provider.getGroups("testuser");

        // Then
        assertThat(groups)
                .hasSize(3)
                .containsExactlyInAnyOrder("admin", "users", "developers");
    }

    @Test
    void shouldReturnEmptySetWhenUserNotFound() {
        // Given
        when(response.getStatusCode()).thenReturn(404);
        when(httpClient.execute(any(Request.class), any(ResponseHandler.class))).thenAnswer(invocation -> {
            ResponseHandler<Set<String>, RuntimeException> handler = invocation.getArgument(1);
            return handler.handle(invocation.getArgument(0), response);
        });

        // When
        Set<String> groups = provider.getGroups("unknown-user");

        // Then
        assertThat(groups).isEmpty();
    }
}