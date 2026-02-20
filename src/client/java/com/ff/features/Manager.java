package com.ff.features;

import java.util.ArrayList;
import java.util.List;

public class Manager {
    public static final List<Feature> FEATURES = new ArrayList<>();

    public static void register(Feature feature) {
        FEATURES.add(feature);
    }

    public static void tick() {
        for (Feature f : FEATURES) {
            if (f.isEnabled()) {
                f.onTick();
            }
        }
    }
}