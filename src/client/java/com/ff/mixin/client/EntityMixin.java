package com.ff.mixin.client;

import com.ff.feature.features.GlowingShadowlings;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
    private void onIsGlowing(CallbackInfoReturnable<Boolean> cir) {
        if (!GlowingShadowlings.INSTANCE.isEnabled()) return;
        Entity entity = (Entity) (Object) this;

        if (!(entity instanceof DisplayEntity.ItemDisplayEntity itemDisplay)) return;

        ItemStack stack = itemDisplay.getItemStack();
        if (!stack.isOf(Items.OAK_BOAT)) return;

        var cmd = stack.get(DataComponentTypes.CUSTOM_MODEL_DATA);
        if (cmd == null || cmd.floats().isEmpty()) return;

        float val = cmd.floats().get(0);
        if (val >= 6994f && val <= 7008f) {
            cir.setReturnValue(true);
        }
    }
}