package com.github.iunius118.simplemovablegrid.client;

import com.github.iunius118.simplemovablegrid.client.integration.autoconfig.ModConfig;
import com.mojang.blaze3d.platform.InputConstants;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class SimpleMovableGrid implements ClientModInitializer {
    public static final String MOD_ID = "simplemovablegrid";
    private static final String MOD_NAME = "Simple Movable Grid";

    public static final Logger LOGGER = LogManager.getFormatterLogger(MOD_NAME);

    @Override
    public void onInitializeClient() {
        registerConfig();
        bindKeys();
    }

    private void registerConfig() {
        AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
    }

    private void bindKeys() {
        KeyMapping keyEnable = KeyBindingHelper.registerKeyBinding(createKeyBinding("enable", InputConstants.UNKNOWN.getValue(), "main"));
        KeyMapping keySetPosition = KeyBindingHelper.registerKeyBinding(createKeyBinding("setPosition", InputConstants.UNKNOWN.getValue(), "main"));
        // KeyMapping keySaveStructure = KeyBindingHelper.registerKeyBinding(createKeyBinding("saveStructure", InputConstants.UNKNOWN.getValue(), "main"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyEnable.consumeClick()) {
                ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
                boolean enabled = config.toggleEnabled();
                AutoConfig.getConfigHolder(ModConfig.class).save();

                LocalPlayer player = client.player;
                if (player != null) {
                    MutableComponent message = Component.translatable(enabled ? "simplemovablegrid.grid.shown" : "simplemovablegrid.grid.hidden");
                    player.displayClientMessage(createChatMessage(message), false);
                }
            }

            while (keySetPosition.consumeClick()) {
                LocalPlayer player = client.player;
                if (player == null) continue;

                ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
                Vec3i pos = config.setPos(player.getBlockX(), player.getBlockY(), player.getBlockZ());
                AutoConfig.getConfigHolder(ModConfig.class).save();

                MutableComponent message = Component.translatable("simplemovablegrid.setPosition.success", pos.toShortString());
                client.player.displayClientMessage(createChatMessage(message), false);
            }

            /*
            while (keySaveStructure.consumeClick()) {
                ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
                if (!config.enabled()) continue;
                // Save the structure only when the grid is enabled
                LocalPlayer player = client.player;
                if (player == null) continue;
                Level level = player.getLevel();
                if (level == null) continue;

                BlockPos pos = new BlockPos(config.getPos());
                ResourceLocation structureName = new ResourceLocation(MOD_ID, getTimeStamp());
                boolean result = saveStructure(level, pos, structureName);

                MutableComponent message = Component.translatable(result ? "structure_block.save_success" : "structure_block.save_failure", structureName.toString());
                client.player.displayClientMessage(createChatMessage(message), false);
            }
            // */
        });
    }

    private String getTimeStamp() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
        return simpleDateFormat.format(timestamp);
    }

    private boolean saveStructure(Level level, BlockPos pos, ResourceLocation structureName) {
        StructureTemplate structureTemplate = new StructureTemplate();
        structureTemplate.fillFromWorld(level, pos, new Vec3i(32, 32, 32), false, Blocks.STRUCTURE_VOID);
        Path gameDirectory = Minecraft.getInstance().gameDirectory.toPath();
        Path saveDirPath = gameDirectory.resolve(Path.of(MOD_ID, "generated", structureName.getNamespace(), "structures"));
        Path saveFilePath = saveDirPath.resolve(Path.of(structureName.getPath() + ".nbt"));

        try {
            Files.createDirectories(saveDirPath);
        } catch (Exception e) {
            LOGGER.error((String)"Failed to create parent directory: {}", saveDirPath);
            return false;
        }

        CompoundTag compoundTag = structureTemplate.save(new CompoundTag());

        try {
            FileOutputStream outputStream = new FileOutputStream(saveFilePath.toFile());
            NbtIo.writeCompressed(compoundTag, outputStream);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private KeyMapping createKeyBinding(String name, int key, String category) {
        return new KeyMapping("key." + MOD_ID + "." + name, key, "key.categories." + MOD_ID + "." + category);
    }

    private Component createChatMessage(MutableComponent message) {
        return Component.literal("").append(Component.literal("[Simple Movable Grid] ").withStyle(ChatFormatting.YELLOW)).append(message.withStyle(ChatFormatting.RESET));
    }
}
