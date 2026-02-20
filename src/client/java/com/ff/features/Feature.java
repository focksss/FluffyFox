package com.ff.features;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public abstract class Feature {
    protected final String name;
    protected final String alias;
    protected boolean enabled;

    public Feature(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }

    public String getName() { return name; }
    public String getAlias() { return alias; }

    public boolean isEnabled() { return enabled; }

    public void toggle() {
        enabled = !enabled;
        if (enabled) onEnable();
        else onDisable();
    }

    protected void onEnable() {}
    protected void onDisable() {}

    public abstract LiteralArgumentBuilder<FabricClientCommandSource> buildCommand();

    public void onTick() {}
}