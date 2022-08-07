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
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

@Mod(SimpleMovableGrid.MOD_ID)
public class SimpleMovableGrid{
    public static final String MOD_ID = "simplemovablegrid";
    public static final Logger LOGGER = LogManager.getLogger();

    public SimpleMovableGrid() {
        final var forgeEventBus = MinecraftForge.EVENT_BUS;

        // Register config
        registerConfig();

        // Register event listeners
        bindKeys();
    }

    private void registerConfig() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SimpleMovableGridConfig.clientSpec, MOD_ID + ".toml");
    }

    private void bindKeys() {
        KeyMapping keyEnable = createKeyBinding("enable", InputConstants.UNKNOWN.getValue(), "main");
        KeyMapping keySetPosition = createKeyBinding("setPosition", InputConstants.UNKNOWN.getValue(), "main");

        // Register key bind event listener
        Consumer<RegisterKeyMappingsEvent> registerKeyMappingsListener = event -> {
            event.register(keyEnable);
            event.register(keySetPosition);
        };

        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(registerKeyMappingsListener);

        // Register key click event listener
        Consumer<TickEvent.ClientTickEvent> clientTickEventListener = event -> {
            if (event.phase != TickEvent.Phase.END)
                return;

            Minecraft client = Minecraft.getInstance();

            while (keyEnable.consumeClick()) {
                SimpleMovableGridConfig.Client config = SimpleMovableGridConfig.CLIENT;
                boolean enabled = config.toggleEnabled();

                LocalPlayer player = client.player;
                if (player != null) {
                    MutableComponent message = Component.translatable(enabled ? "simplemovablegrid.grid.shown" : "simplemovablegrid.grid.hidden");
                    player.displayClientMessage(createChatMessage(message), false);
                }
            }

            while (keySetPosition.consumeClick()) {
                LocalPlayer player = client.player;
                if (player == null) continue;

                SimpleMovableGridConfig.Client config = SimpleMovableGridConfig.CLIENT;
                Vec3i pos = config.setPos(player.getBlockX(), player.getBlockY(), player.getBlockZ());

                MutableComponent message = Component.translatable("simplemovablegrid.setPosition.success", pos.toShortString());
                client.player.displayClientMessage(createChatMessage(message), false);
            }
        };

        MinecraftForge.EVENT_BUS.addListener(clientTickEventListener);
    }

    private KeyMapping createKeyBinding(String name, int key, String category) {
        return new KeyMapping("key." + MOD_ID + "." + name, key, "key.categories." + MOD_ID + "." + category);
    }

    private Component createChatMessage(MutableComponent message) {
        return Component.literal("").append(Component.literal("[Simple Movable Grid] ").withStyle(ChatFormatting.YELLOW)).append(message.withStyle(ChatFormatting.RESET));
    }
}
