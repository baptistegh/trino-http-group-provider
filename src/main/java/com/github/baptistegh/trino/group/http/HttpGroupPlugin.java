package com.github.baptistegh.trino.group.http;

import io.trino.spi.Plugin;
import io.trino.spi.security.GroupProviderFactory;

import java.util.List;

public class HttpGroupPlugin
        implements Plugin
{
    @Override
    public Iterable<GroupProviderFactory> getGroupProviderFactories()
    {
        return List.of(new HttpGroupProviderFactory());
    }
}