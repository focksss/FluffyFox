package com.ff.feature;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;

public abstract class Feature {
    protected final String name;
    protected final String alias;
    protected boolean enabled;
    protected State state = new State(this) { };

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

    public void setEnabled(boolean enabled) {
        boolean wasEnabled = this.enabled;
        this.enabled = enabled;
        if (wasEnabled == enabled) return;
        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    protected void onEnable() {}
    protected void onDisable() {}

    public abstract LiteralArgumentBuilder<FabricClientCommandSource> buildCommand(String commandRoot);

    public void onTick() {
        state.onTick();
    }

    public void onRender(WorldRenderContext ctx) {}

    public void setState(State newState) {
        if (state != null) {
            state.onExit();
        }
        state = newState;
        state.onEnter();
    }
}