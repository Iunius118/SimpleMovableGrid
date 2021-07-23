package com.github.iunius118.simplemovablegrid;

import com.github.iunius118.simplemovablegrid.client.renderer.GridRenderer;
import com.github.iunius118.simplemovablegrid.config.SimpleMovableGridConfig;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmlclient.registry.ClientRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

@Mod(SimpleMovableGrid.MOD_ID)
public class SimpleMovableGrid{
    public static final String MOD_ID = "simplemovablegrid";
    public static final Logger LOGGER = LogManager.getLogger();

    public SimpleMovableGrid() {
        final var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        final var forgeEventBus = MinecraftForge.EVENT_BUS;

        // Register lifecycle event listeners
        modEventBus.addListener(this::initClient);

        // Register config
        registerConfig();

        // Register event listeners
        forgeEventBus.addListener(GridRenderer::render);
    }

    private void initClient(final FMLClientSetupEvent event) {
        bindKeys();
    }

    private void registerConfig() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SimpleMovableGridConfig.clientSpec, MOD_ID + ".toml");
    }

    private void bindKeys() {
        KeyMapping keyEnable = createKeyBinding("enable", InputConstants.UNKNOWN.getValue(), "main");
        KeyMapping keySetPosition = createKeyBinding("setPosition", InputConstants.UNKNOWN.getValue(), "main");

        ClientRegistry.registerKeyBinding(keyEnable);
        ClientRegistry.registerKeyBinding(keySetPosition);

        Consumer<TickEvent.ClientTickEvent> listener = event -> {
            if (event.phase != TickEvent.Phase.END) return;

            Minecraft client = Minecraft.getInstance();

            while (keyEnable.consumeClick()) {
                SimpleMovableGridConfig.Client config = SimpleMovableGridConfig.CLIENT;
                boolean enabled = config.toggleEnabled();

                LocalPlayer player = client.player;
                if (player != null)
                    player.sendMessage(createChatMessage("Grid: " + (enabled ? "shown" : "hidden")), player.getUUID());
            }

            while (keySetPosition.consumeClick()) {
                LocalPlayer player = client.player;
                if (player == null) continue;

                SimpleMovableGridConfig.Client config = SimpleMovableGridConfig.CLIENT;
                Vec3i pos = config.setPos(player.getBlockX(), player.getBlockY(), player.getBlockZ());
                player.sendMessage(createChatMessage("Position is set to (" + pos.toShortString() + ")"), null);
            }
        };

        MinecraftForge.EVENT_BUS.addListener(listener);
    }

    private KeyMapping createKeyBinding(String name, int key, String category) {
        return new KeyMapping("key." + MOD_ID + "." + name, key, "key.categories." + MOD_ID + "." + category);
    }

    private Component createChatMessage(String message) {
        return new TextComponent("").append(new TextComponent("[SimpleMovableGrid] ").withStyle(ChatFormatting.YELLOW)).append(new TextComponent(message).withStyle(ChatFormatting.RESET));
    }
}
