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

    public boolean flyingAxonWarningEnabled = true;
    public double flyingAxonWarningRadius = 10.0;
    public double flyingAxonWarningAngle = 35.0;

    public void updateInternal() {
        Antiblind.INSTANCE.setEnabled(antiBlindEnabled);

        AntiScroll.INSTANCE.setEnabled(antiScrollEnabled);

        FixSkinRendering.INSTANCE.setEnabled(fixSkinRenderingEnabled);

        IcelessSnakes.INSTANCE.setEnabled(icelessSnakesEnabled);

        WatchedTitles.INSTANCE.setEnabled(watchedTitlesEnabled);

        GlowingShadowlings.INSTANCE.setEnabled(glowingShadowlingsEnabled);

        BulbHolderWaypoints.INSTANCE.setEnabled(bulbHolderWaypointsEnabled);

        FlyingAxonWarning.INSTANCE.setEnabled(flyingAxonWarningEnabled);
        FlyingAxonWarning.INSTANCE.warningRadius = flyingAxonWarningRadius;
        FlyingAxonWarning.INSTANCE.warningAngle = flyingAxonWarningAngle;
    }
}
