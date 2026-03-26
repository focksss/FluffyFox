package com.ff.mixin.client;

import com.ff.feature.features.BulbHolderWaypoints;
import com.ff.feature.features.WatchedTitles;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.ff.FluffyFoxClient.MC;

@Mixin(SoundSystem.class)
public abstract class SoundSystemMixin {

    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)Lnet/minecraft/client/sound/SoundSystem$PlayResult;", at = @At("HEAD"))
    private void onPlaySound(SoundInstance sound, CallbackInfoReturnable<SoundSystem.PlayResult> cir) {
        if (WatchedTitles.INSTANCE.isEnabled()) {

            Identifier id = sound.getId();
            long now = MC.player != null ? MC.player.age : 0;

            // Reset if the sequence stalled
            if (now - WatchedTitles.lastSoundTick > WatchedTitles.RESET_TICKS) {
                WatchedTitles.telegraphStep = 0;
            }

            Identifier evokerPrepare = SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON.id();
            Identifier tridentThunder = SoundEvents.ITEM_TRIDENT_THUNDER.value().id();

            if (id.equals(evokerPrepare)) {
                if (WatchedTitles.telegraphStep == 0) {
                    WatchedTitles.onTelegraphSound(0); // "2"
                    WatchedTitles.telegraphStep = 1;
                    WatchedTitles.lastSoundTick = now;
                } else if (WatchedTitles.telegraphStep == 1) {
                    WatchedTitles.onTelegraphSound(1); // "1"
                    WatchedTitles.telegraphStep = 2;
                    WatchedTitles.lastSoundTick = now;
                }
            } else if (id.equals(tridentThunder)) {
                if (WatchedTitles.telegraphStep == 2) {
                    WatchedTitles.onTelegraphSound(2); // "0"
                    WatchedTitles.telegraphStep = 0;
                    WatchedTitles.lastSoundTick = now;
                }
            }
        }

        if (BulbHolderWaypoints.INSTANCE.isEnabled()) {
            Identifier id  = sound.getId();
            if (MC.player == null) return;
            long now = System.currentTimeMillis();
            String soundPath = id.getPath();
            if (soundPath.contains("goat_horn") && !soundPath.contains("0") && !soundPath.contains("1")) {
                Vec3d pos = new Vec3d(sound.getX(), sound.getY(), sound.getZ());
                System.out.println(pos.x + ", " + pos.y + ", " + pos.z);
                BulbHolderWaypoints.INSTANCE.onGoatHorn(pos, now);
            }
        }
    }
}