package com.github.baptistegh.trino.group.http;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import io.airlift.http.client.HttpClientConfig;
import io.airlift.http.client.jetty.JettyHttpClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

class HttpGroupProviderIT {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    private JettyHttpClient httpClient;
    private HttpGroupProvider provider;

    @BeforeEach
    void setUp() {
        httpClient = new JettyHttpClient(new HttpClientConfig());
        HttpGroupConfig config = new HttpGroupConfig()
                .setEndpoint("http://localhost:" + wireMock.getPort() + "/groups")
                .setAuthToken("test-token");
        provider = new HttpGroupProvider(httpClient, config);
    }

    @AfterEach
    void tearDown() {
        httpClient.close();
    }

    @Test
    void shouldFetchGroupsFromHttpEndpoint() {
        // Given
        wireMock.stubFor(get(urlPathMatching("/groups/testuser"))
                .withHeader("Authorization", equalTo("Bearer test-token"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[\"admin\", \"users\", \"developers\"]")));

        // When
        Set<String> groups = provider.getGroups("testuser");

        // Then
        assertThat(groups)
                .hasSize(3)
                .containsExactlyInAnyOrder("admin", "users", "developers");

        // Verify the request
        wireMock.verify(getRequestedFor(urlPathEqualTo("/groups/testuser"))
                .withHeader("Authorization", equalTo("Bearer test-token"))
                .withHeader("Accept", equalTo("application/json")));
    }

    @Test
    void shouldReturnEmptySetWhenUserNotFound() {
        // Given
        wireMock.stubFor(get(urlPathMatching("/groups/unknown"))
                .willReturn(aResponse()
                        .withStatus(404)));

        // When
        Set<String> groups = provider.getGroups("unknown");

        // Then
        assertThat(groups).isEmpty();
    }

    @Test
    void shouldHandleServerError() {
        // Given
        wireMock.stubFor(get(urlPathMatching("/groups/error"))
                .willReturn(aResponse()
                        .withStatus(500)));

        // When
        Set<String> groups = provider.getGroups("error");

        // Then
        assertThat(groups).isEmpty();
    }
}