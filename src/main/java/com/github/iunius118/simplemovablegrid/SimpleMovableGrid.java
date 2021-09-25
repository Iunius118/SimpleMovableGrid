package com.github.iunius118.simplemovablegrid;

import com.github.iunius118.simplemovablegrid.client.renderer.GridRenderer;
import com.github.iunius118.simplemovablegrid.config.SimpleMovableGridConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

@Mod(SimpleMovableGrid.MOD_ID)
public class SimpleMovableGrid{
    public static final String MOD_ID = "simplemovablegrid";
    public static final Logger LOGGER = LogManager.getLogger();

    public SimpleMovableGrid() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        final IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

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
        KeyBinding keyEnable = createKeyBinding("enable", GLFW.GLFW_KEY_UNKNOWN, "main");
        KeyBinding keySetPosition = createKeyBinding("setPosition", GLFW.GLFW_KEY_UNKNOWN, "main");

        ClientRegistry.registerKeyBinding(keyEnable);
        ClientRegistry.registerKeyBinding(keySetPosition);

        Consumer<TickEvent.ClientTickEvent> listener = event -> {
            if (event.phase != TickEvent.Phase.END) return;

            Minecraft client = Minecraft.getInstance();

            while (keyEnable.consumeClick()) {
                SimpleMovableGridConfig.Client config = SimpleMovableGridConfig.CLIENT;
                boolean enabled = config.toggleEnabled();

                ITextComponent message = createChatMessage("Grid: " + (enabled ? "shown" : "hidden"));
                Minecraft.getInstance().gui.getChat().addMessage(message);
            }

            while (keySetPosition.consumeClick()) {
                ClientPlayerEntity player = client.player;
                if (player == null) continue;

                BlockPos blockPos = player.blockPosition();
                SimpleMovableGridConfig.Client config = SimpleMovableGridConfig.CLIENT;
                Vector3i pos = config.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                ITextComponent message = createChatMessage("Position is set to (" + pos.toShortString() + ")");
                Minecraft.getInstance().gui.getChat().addMessage(message);
            }
        };

        MinecraftForge.EVENT_BUS.addListener(listener);
    }

    private KeyBinding createKeyBinding(String name, int key, String category) {
        return new KeyBinding("key." + MOD_ID + "." + name, key, "key.categories." + MOD_ID + "." + category);
    }

    private ITextComponent createChatMessage(String message) {
        return new StringTextComponent("").append(new StringTextComponent("[SimpleMovableGrid] ").withStyle(TextFormatting.YELLOW)).append(new StringTextComponent(message).withStyle(TextFormatting.RESET));
    }
}
