package io.github.baptistegh.trino.group.http;

import com.google.inject.Binder;
import com.google.inject.Scopes;

import io.airlift.configuration.AbstractConfigurationAwareModule;
import io.trino.spi.security.GroupProvider;

import static io.airlift.configuration.ConfigBinder.configBinder;

public class HttpGroupProviderModule
        extends AbstractConfigurationAwareModule {

    @Override
    protected void setup(Binder binder) {
        configBinder(binder).bindConfig(HttpGroupProviderConfig.class);
        install(innerBinder -> {
            innerBinder.bind(GroupProvider.class).to(HttpGroupProvider.class).in(Scopes.SINGLETON);
        });
    }
}