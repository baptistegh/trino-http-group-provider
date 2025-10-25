package com.github.baptistegh.trino.group.http;

import io.airlift.configuration.Config;
import io.airlift.configuration.ConfigDescription;

import jakarta.validation.constraints.NotNull;

public class HttpGroupConfig {
    private String endpoint;
    private String authToken;

    @NotNull
    public String getEndpoint() {
        return endpoint;
    }

    @Config("http-group-provider.endpoint")
    @ConfigDescription("HTTP endpoint to fetch user groups")
    public HttpGroupConfig setEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public String getAuthToken() {
        return authToken;
    }

    @Config("http-group-provider.auth-token")
    @ConfigDescription("Authentication token for the HTTP endpoint")
    public HttpGroupConfig setAuthToken(String authToken) {
        this.authToken = authToken;
        return this;
    }
}