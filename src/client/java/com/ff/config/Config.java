package com.ff.config;

import com.ff.feature.features.*;

public class Config {
    public boolean antiBlindEnabled = true;
    public boolean antiScrollEnabled = true;
    public boolean fixSkinRenderingEnabled = true;
    public boolean icelessSnakesEnabled = true;
    public boolean watchedTitlesEnabled = true;
    public boolean glowingShadowlingsEnabled = true;
    public boolean bulbHolderWaypointsEnabled = true;

    public void updateInternal() {
        Antiblind.INSTANCE.setEnabled(antiBlindEnabled);
        AntiScroll.INSTANCE.setEnabled(antiScrollEnabled);
        FixSkinRendering.INSTANCE.setEnabled(fixSkinRenderingEnabled);
        IcelessSnakes.INSTANCE.setEnabled(icelessSnakesEnabled);
        WatchedTitles.INSTANCE.setEnabled(watchedTitlesEnabled);
        GlowingShadowlings.INSTANCE.setEnabled(glowingShadowlingsEnabled);
        BulbHolderWaypoints.INSTANCE.setEnabled(bulbHolderWaypointsEnabled);
    }
}
