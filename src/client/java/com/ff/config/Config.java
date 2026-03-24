package com.ff.config;

import com.ff.feature.features.AntiScroll;
import com.ff.feature.features.Antiblind;
import com.ff.feature.features.FixSkinRendering;
import com.ff.feature.features.IcelessSnakes;

public class Config {
    public boolean antiBlindEnabled = true;
    public boolean antiScrollEnabled = true;
    public boolean fixSkinRenderingEnabled = true;
    public boolean icelessSnakesEnabled = true;

    public void updateInternal() {
        Antiblind.INSTANCE.setEnabled(antiBlindEnabled);
        AntiScroll.INSTANCE.setEnabled(antiScrollEnabled);
        FixSkinRendering.INSTANCE.setEnabled(fixSkinRenderingEnabled);
        IcelessSnakes.INSTANCE.setEnabled(icelessSnakesEnabled);
    }
}
