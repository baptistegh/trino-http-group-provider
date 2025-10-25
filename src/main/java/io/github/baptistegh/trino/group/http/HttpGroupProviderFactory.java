package io.github.baptistegh.trino.group.http;

import io.airlift.bootstrap.Bootstrap;
import io.airlift.http.client.HttpClient;
import io.airlift.http.client.HttpClientConfig;
import io.airlift.http.client.jetty.JettyHttpClient;
import io.trino.spi.security.GroupProvider;
import io.trino.spi.security.GroupProviderFactory;
import com.google.inject.Scopes;

import java.util.Map;

public class HttpGroupProviderFactory implements GroupProviderFactory {
    @Override
    public String getName() {
        return "http";
    }

    @Override
    public GroupProvider create(Map<String, String> config) {
        try {
            Bootstrap app = new Bootstrap(
                    binder -> {
                        binder.bind(HttpGroupConfig.class).in(Scopes.SINGLETON);
                        binder.bind(HttpClientConfig.class).in(Scopes.SINGLETON);
                    });

            app.setRequiredConfigurationProperties(config);
            HttpGroupConfig groupConfig = app
                    .initialize()
                    .getInstance(HttpGroupConfig.class);

            HttpClient httpClient = new JettyHttpClient(new HttpClientConfig());

            return new HttpGroupProvider(httpClient, groupConfig);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}