package com.github.iunius118.simplemovablegrid.client;

import com.github.iunius118.simplemovablegrid.client.integration.autoconfig.ModConfig;
import com.github.iunius118.simplemovablegrid.client.renderer.GridRenderer;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public class SimpleMovableGrid implements ClientModInitializer {
    public static final String MOD_ID = "simplemovablegrid";

    @Override
    public void onInitializeClient() {
        registerEventListeners();
        registerConfig();
    }

    private void registerEventListeners() {
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(GridRenderer::render);
    }

    private void registerConfig() {
        AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
    }
}
