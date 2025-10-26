package io.github.baptistegh.trino.group.http;

import io.airlift.bootstrap.Bootstrap;
import io.trino.spi.security.GroupProvider;
import io.trino.spi.security.GroupProviderFactory;

import com.google.inject.Injector;
import com.google.inject.Scopes;

import java.util.Map;

import static io.airlift.configuration.ConfigBinder.configBinder;
import static java.util.Objects.requireNonNull;

import io.airlift.http.client.HttpClientBinder;

public class HttpGroupProviderFactory implements GroupProviderFactory {
    @Override
    public String getName() {
        return "http";
    }

    @Override
    public GroupProvider create(Map<String, String> requiredConfig) {
        requireNonNull(requiredConfig, "config is null");

        Bootstrap app = new Bootstrap(
            binder -> {
                configBinder(binder).bindConfig(HttpGroupProviderConfig.class);
                binder.bind(GroupProvider.class).to(HttpGroupProvider.class).in(Scopes.SINGLETON);
                HttpClientBinder
                    .httpClientBinder(binder)
                    .bindHttpClient("http-group-provider", ForHttpGroupProvider.class);
            });

        Injector injector = app
            .doNotInitializeLogging()
            .setRequiredConfigurationProperties(requiredConfig)
            .initialize();

        return injector.getInstance(GroupProvider.class);
    }
}