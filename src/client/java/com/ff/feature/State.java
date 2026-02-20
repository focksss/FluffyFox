package com.ff.feature;

public abstract class State {
    protected final Feature feature;

    public State(Feature feature) { this.feature = feature; }

    public void onEnter() {}
    public void onTick() {}
    public void onExit() {}
}
