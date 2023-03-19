package com.github.iunius118.simplemovablegrid.mixin;

import com.github.iunius118.simplemovablegrid.client.renderer.GridRenderer;
import com.github.iunius118.simplemovablegrid.config.SimpleMovableGridConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugRenderer.class)
public class MixinDebugRenderer {
    @Inject(method = "render", at = @At("HEAD"))
    public void onRender(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, double x, double y, double z, CallbackInfo ci) {
        SimpleMovableGridConfig.Client config = SimpleMovableGridConfig.CLIENT;
        if (!config.enabled())
            return;

        GridRenderer.render(poseStack, bufferSource, config.getPos(), config.isLabelEnabled());
    }
}
