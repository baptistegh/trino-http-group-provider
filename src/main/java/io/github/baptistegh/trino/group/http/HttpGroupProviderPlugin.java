package io.github.baptistegh.trino.group.http;

import com.google.common.collect.ImmutableList;
import io.trino.spi.Plugin;
import io.trino.spi.security.GroupProviderFactory;


public class HttpGroupProviderPlugin implements Plugin {
    @Override
    public Iterable<GroupProviderFactory> getGroupProviderFactories() {
        return ImmutableList.of(new HttpGroupProviderFactory());
    }
}