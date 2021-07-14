package com.github.iunius118.simplemovablegrid.client;

import com.github.iunius118.simplemovablegrid.client.integration.autoconfig.ModConfig;
import com.github.iunius118.simplemovablegrid.client.renderer.GridRenderer;
import com.mojang.blaze3d.platform.InputConstants;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class SimpleMovableGrid implements ClientModInitializer {
    public static final String MOD_ID = "simplemovablegrid";

    @Override
    public void onInitializeClient() {
        registerEventListeners();
        registerConfig();
        bindKeys();
    }

    private void registerEventListeners() {
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(GridRenderer::render);
    }

    private void registerConfig() {
        AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
    }

    private void bindKeys() {
        KeyMapping keyEnable = KeyBindingHelper.registerKeyBinding(createKeyBinding("enable", InputConstants.UNKNOWN.getValue(), "main"));
        KeyMapping keySetPosition = KeyBindingHelper.registerKeyBinding(createKeyBinding("setPosition", InputConstants.UNKNOWN.getValue(), "main"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyEnable.consumeClick()) {
                ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
                boolean enabled = config.toggleEnabled();
                AutoConfig.getConfigHolder(ModConfig.class).save();

                LocalPlayer player = client.player;
                if (player != null) player.sendMessage(createChatMessage("Grid: " + (enabled ? "shown" : "hidden")), null);
            }

            while (keySetPosition.consumeClick()) {
                LocalPlayer player = client.player;
                if (player == null) continue;

                ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
                Vec3i pos = config.setPos(player.getBlockX(), player.getBlockY(), player.getBlockZ());
                AutoConfig.getConfigHolder(ModConfig.class).save();

                client.player.sendMessage(createChatMessage("Position is set to (" + pos.toShortString() + ")"), null);
            }
        });
    }

    private KeyMapping createKeyBinding(String name, int key, String category) {
        return new KeyMapping("key." + MOD_ID + "." + name, key, "key.categories." + MOD_ID + "." + category);
    }

    private Component createChatMessage(String message) {
        return new TextComponent("").append(new TextComponent("[SimpleMovableGrid] ").withStyle(ChatFormatting.YELLOW)).append(new TextComponent(message).withStyle(ChatFormatting.RESET));
    }
}
