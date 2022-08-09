package com.github.iunius118.simplemovablegrid.client.renderer;

import com.github.iunius118.simplemovablegrid.client.integration.autoconfig.ModConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;

public class GridRenderer {
    private static final int GRID_MAX = 32;

    public static void render(Vec3 gridPos) {
        var mainCamera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 cameraPos = mainCamera.getPosition();
        Vec3 originPos = gridPos.subtract(cameraPos);
        renderGrid(originPos);
    }

    private static void renderGrid(Vec3 pos) {
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        RenderSystem.disableCull();
        RenderSystem.disableTexture();
        RenderSystem.disableBlend();
        RenderSystem.lineWidth(1.0F);

        buffer.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        renderGrid(buffer, pos);
        tesselator.end();

        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
        RenderSystem.enableCull();
    }

    private static void renderGrid(BufferBuilder buffer, Vec3 pos) {
        final double x = pos.x;
        final double y = pos.y;
        final double z = pos.z;

        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        ModConfig.AxisDrawable xDrawable = config.getAxisXDrawable();
        ModConfig.AxisDrawable yDrawable = config.getAxisYDrawable();
        ModConfig.AxisDrawable zDrawable = config.getAxisZDrawable();

        if (xDrawable.canDraw(ModConfig.AxisDrawable.NEGATIVE)) renderGridNegativeX(buffer, x, y, z, yDrawable, zDrawable);
        if (xDrawable.canDraw(ModConfig.AxisDrawable.POSITIVE)) renderGridPositiveX(buffer, x, y, z, yDrawable, zDrawable);
        if (yDrawable.canDraw(ModConfig.AxisDrawable.NEGATIVE)) renderGridNegativeY(buffer, x, y, z, zDrawable);
        if (yDrawable.canDraw(ModConfig.AxisDrawable.POSITIVE)) renderGridPositiveY(buffer, x, y, z, zDrawable);
        if (zDrawable.canDraw(ModConfig.AxisDrawable.NEGATIVE)) {
            buffer.vertex(x, y, z - GRID_MAX).color(0.25F, 0.25F, 1F, 1F).endVertex();
            buffer.vertex(x, y, z).color(0.25F, 0.25F, 1F, 1F).endVertex();
        }
        if (zDrawable.canDraw(ModConfig.AxisDrawable.POSITIVE)) {
            buffer.vertex(x, y, z).color(0.25F, 0.25F, 1F, 1F).endVertex();
            buffer.vertex(x, y, z + GRID_MAX).color(0.25F, 0.25F, 1F, 1F).endVertex();
        }
    }

    private static void renderGridNegativeX(BufferBuilder buffer, double x, double y, double z, ModConfig.AxisDrawable yDrawable, ModConfig.AxisDrawable zDrawable) {
        buffer.vertex(x - GRID_MAX, y, z).color(1F, 0.25F, 0.25F, 1F).endVertex();
        buffer.vertex(x, y, z).color(1F, 0.25F, 0.25F, 1F).endVertex();

        if (yDrawable.canDraw(ModConfig.AxisDrawable.NEGATIVE)) {
            int i = 2;
            for (; i < GRID_MAX; i += 2) {
                buffer.vertex(x - GRID_MAX, y - i, z).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(x, y - i, z).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(x - i, y - GRID_MAX, z).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(x - i, y, z).color(1F, 1F, 1F, 1F).endVertex();
            }
            buffer.vertex(x - GRID_MAX, y - i, z).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
            buffer.vertex(x, y - i, z).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
            buffer.vertex(x - i, y - GRID_MAX, z).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
            buffer.vertex(x - i, y, z).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
        }

        if (yDrawable.canDraw(ModConfig.AxisDrawable.POSITIVE)) {
            int i = 2;
            for (; i < GRID_MAX; i += 2) {
                buffer.vertex(x - GRID_MAX, y + i, z).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(x, y + i, z).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(x - i, y + GRID_MAX, z).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(x - i, y, z).color(1F, 1F, 1F, 1F).endVertex();
            }
            buffer.vertex(x - GRID_MAX, y + i, z).color(1F, 1F, 0F, 1F).endVertex();
            buffer.vertex(x, y + i, z).color(1F, 1F, 0F, 1F).endVertex();
            buffer.vertex(x - i, y + GRID_MAX, z).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
            buffer.vertex(x - i, y, z).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
        }

        if (zDrawable.canDraw(ModConfig.AxisDrawable.NEGATIVE)) {
            int i = 2;
            for (; i < GRID_MAX; i += 2) {
                buffer.vertex(x - GRID_MAX, y, z - i).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(x, y, z - i).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(x - i, y, z - GRID_MAX).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(x - i, y, z).color(1F, 1F, 1F, 1F).endVertex();
            }
            buffer.vertex(x - GRID_MAX, y, z - i).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
            buffer.vertex(x, y, z - i).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
            buffer.vertex(x - i, y, z - GRID_MAX).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
            buffer.vertex(x - i, y, z).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
        }

        if (zDrawable.canDraw(ModConfig.AxisDrawable.POSITIVE)) {
            int i = 2;
            for (; i < GRID_MAX; i += 2) {
                buffer.vertex(x - GRID_MAX, y, z + i).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(x, y, z + i).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(x - i, y, z).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(x - i, y, z + GRID_MAX).color(1F, 1F, 1F, 1F).endVertex();
            }
            buffer.vertex(x - GRID_MAX, y, z + i).color(1F, 1F, 0F, 1F).endVertex();
            buffer.vertex(x, y, z + i).color(1F, 1F, 0F, 1F).endVertex();
            buffer.vertex(x - i, y, z).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
            buffer.vertex(x - i, y, z + GRID_MAX).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
        }
    }

    private static void renderGridPositiveX(BufferBuilder buffer, double x, double y, double z, ModConfig.AxisDrawable yDrawable, ModConfig.AxisDrawable zDrawable) {
        buffer.vertex(x, y, z).color(1F, 0.25F, 0.25F, 1F).endVertex();
        buffer.vertex(x + GRID_MAX, y, z).color(1F, 0.25F, 0.25F, 1F).endVertex();

        if (yDrawable.canDraw(ModConfig.AxisDrawable.NEGATIVE)) {
            int i = 2;
            for (; i < GRID_MAX; i += 2) {
                buffer.vertex(x, y - i, z).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(x + GRID_MAX, y - i, z).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(x + i, y - GRID_MAX, z).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(x + i, y, z).color(1F, 1F, 1F, 1F).endVertex();
            }
            buffer.vertex(x, y - i, z).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
            buffer.vertex(x + GRID_MAX, y - i, z).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
            buffer.vertex(x + i, y - GRID_MAX, z).color(1F, 1F, 0F, 1F).endVertex();
            buffer.vertex(x + i, y, z).color(1F, 1F, 0F, 1F).endVertex();
        }

        if (yDrawable.canDraw(ModConfig.AxisDrawable.POSITIVE)) {
            int i = 2;
            for (; i < GRID_MAX; i += 2) {
                buffer.vertex(x, y + i, z).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(x + GRID_MAX, y + i, z).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(x + i, y, z).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(x + i, y + GRID_MAX, z).color(1F, 1F, 1F, 1F).endVertex();
            }
            buffer.vertex(x, y + i, z).color(1F, 1F, 0F, 1F).endVertex();
            buffer.vertex(x + GRID_MAX, y + i, z).color(1F, 1F, 0F, 1F).endVertex();
            buffer.vertex(x + i, y, z).color(1F, 1F, 0F, 1F).endVertex();
            buffer.vertex(x + i, y + GRID_MAX, z).color(1F, 1F, 0F, 1F).endVertex();
        }

        if (zDrawable.canDraw(ModConfig.AxisDrawable.NEGATIVE)) {
            int i = 2;
            for (; i < GRID_MAX; i += 2) {
                buffer.vertex(x, y, z - i).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(x + GRID_MAX, y, z - i).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(x + i, y, z - GRID_MAX).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(x + i, y, z).color(1F, 1F, 1F, 1F).endVertex();
            }
            buffer.vertex(x, y, z - i).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
            buffer.vertex(x + GRID_MAX, y, z - i).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
            buffer.vertex(x + i, y, z - GRID_MAX).color(1F, 1F, 0F, 1F).endVertex();
            buffer.vertex(x + i, y, z).color(1F, 1F, 0F, 1F).endVertex();
        }

        if (zDrawable.canDraw(ModConfig.AxisDrawable.POSITIVE)) {
            int i = 2;
            for (; i < GRID_MAX; i += 2) {
                buffer.vertex(x, y, z + i).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(x + GRID_MAX, y, z + i).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(x + i, y, z).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(x + i, y, z + GRID_MAX).color(1F, 1F, 1F, 1F).endVertex();
            }
            buffer.vertex(x, y, z + i).color(1F, 1F, 0F, 1F).endVertex();
            buffer.vertex(x + GRID_MAX, y, z + i).color(1F, 1F, 0F, 1F).endVertex();
            buffer.vertex(x + i, y, z).color(1F, 1F, 0F, 1F).endVertex();
            buffer.vertex(x + i, y, z + GRID_MAX).color(1F, 1F, 0F, 1F).endVertex();
        }
    }

    private static void renderGridNegativeY(BufferBuilder buffer, double x, double y, double z, ModConfig.AxisDrawable zDrawable) {
        buffer.vertex(x, y - GRID_MAX, z).color(0.25F, 1F, 0.25F, 1F).endVertex();
        buffer.vertex(x, y, z).color(0.25F, 1F, 0.25F, 1F).endVertex();

        if (zDrawable.canDraw(ModConfig.AxisDrawable.NEGATIVE)) {
            int i = 2;
            for (; i < GRID_MAX; i += 2) {
                buffer.vertex(x, y - GRID_MAX, z - i).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(x, y, z - i).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(x, y - i, z - GRID_MAX).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(x, y - i, z).color(1F, 1F, 1F, 1F).endVertex();
            }
            buffer.vertex(x, y - GRID_MAX, z - i).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
            buffer.vertex(x, y, z - i).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
            buffer.vertex(x, y - i, z - GRID_MAX).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
            buffer.vertex(x, y - i, z).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
        }

        if (zDrawable.canDraw(ModConfig.AxisDrawable.POSITIVE)) {
            int i = 2;
            for (; i < GRID_MAX; i += 2) {
                buffer.vertex(x, y - GRID_MAX, z + i).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(x, y, z + i).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(x, y - i, z).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(x, y - i, z + GRID_MAX).color(1F, 1F, 1F, 1F).endVertex();
            }
            buffer.vertex(x, y - GRID_MAX, z + i).color(1F, 1F, 0F, 1F).endVertex();
            buffer.vertex(x, y, z + i).color(1F, 1F, 0F, 1F).endVertex();
            buffer.vertex(x, y - i, z).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
            buffer.vertex(x, y - i, z + GRID_MAX).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
        }
    }

    private static void renderGridPositiveY(BufferBuilder buffer, double x, double y, double z, ModConfig.AxisDrawable zDrawable) {
        buffer.vertex(x, y, z).color(0.25F, 1F, 0.25F, 1F).endVertex();
        buffer.vertex(x, y + GRID_MAX, z).color(0.25F, 1F, 0.25F, 1F).endVertex();

        if (zDrawable.canDraw(ModConfig.AxisDrawable.NEGATIVE)) {
            int i = 2;
            for (; i < GRID_MAX; i += 2) {
                buffer.vertex(x, y, z - i).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(x, y + GRID_MAX, z - i).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(x, y + i, z - GRID_MAX).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(x, y + i, z).color(1F, 1F, 1F, 1F).endVertex();
            }
            buffer.vertex(x, y, z - i).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
            buffer.vertex(x, y + GRID_MAX, z - i).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
            buffer.vertex(x, y + i, z - GRID_MAX).color(1F, 1F, 0F, 1F).endVertex();
            buffer.vertex(x, y + i, z).color(1F, 1F, 0F, 1F).endVertex();
        }

        if (zDrawable.canDraw(ModConfig.AxisDrawable.POSITIVE)) {
            int i = 2;
            for (; i < GRID_MAX; i += 2) {
                buffer.vertex(x, y, z + i).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(x, y + GRID_MAX, z + i).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(x, y + i, z).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(x, y + i, z + GRID_MAX).color(1F, 1F, 1F, 1F).endVertex();
            }
            buffer.vertex(x, y, z + i).color(1F, 1F, 0F, 1F).endVertex();
            buffer.vertex(x, y + GRID_MAX, z + i).color(1F, 1F, 0F, 1F).endVertex();
            buffer.vertex(x, y + i, z).color(1F, 1F, 0F, 1F).endVertex();
            buffer.vertex(x, y + i, z + GRID_MAX).color(1F, 1F, 0F, 1F).endVertex();
        }
    }
}
