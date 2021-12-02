package com.github.iunius118.simplemovablegrid.client.renderer;

import com.github.iunius118.simplemovablegrid.SimpleMovableGrid;
import com.github.iunius118.simplemovablegrid.config.AxisDrawable;
import com.github.iunius118.simplemovablegrid.config.SimpleMovableGridConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelLastEvent;

public class GridRenderer {
    private static final int GRID_MAX = 32;

    public static void render(RenderLevelLastEvent event) {
        SimpleMovableGridConfig.Client config = SimpleMovableGridConfig.CLIENT;
        if (!config.enabled()) return;

        beginRenderProfile();

        Vec3 gridPos = config.getPos();
        // Get main camera for Forge version
        var mainCamera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 cameraPos = mainCamera.getPosition();
        Vec3 originPos = gridPos.subtract(cameraPos);

        // Transform for Forge version (before rendering)
        var poseStack = event.getPoseStack();
        var modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushPose();
        modelViewStack.mulPoseMatrix(poseStack.last().pose());
        RenderSystem.applyModelViewMatrix();

        render(originPos);

        // Transform for Forge version (after rendering)
        modelViewStack.popPose();
        RenderSystem.applyModelViewMatrix();

        endRenderProfile();
    }

    private static void beginRenderProfile() {
        var profiler = Minecraft.getInstance().getProfiler();
        profiler.push(SimpleMovableGrid.MOD_ID + ":render_grid");
    }

    private static void endRenderProfile() {
        var profiler = Minecraft.getInstance().getProfiler();
        profiler.pop();
    }

    private static void render(Vec3 pos) {
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableTexture();
        RenderSystem.disableBlend();
        RenderSystem.lineWidth(1.0F);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        buffer.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        renderGrid(buffer, pos);
        tesselator.end();

        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
    }

    private static void renderGrid(BufferBuilder buffer, Vec3 pos) {
        final double x = pos.x;
        final double y = pos.y;
        final double z = pos.z;

        SimpleMovableGridConfig.Client config = SimpleMovableGridConfig.CLIENT;
        AxisDrawable xDrawable = config.getAxisXDrawable();
        AxisDrawable yDrawable = config.getAxisYDrawable();
        AxisDrawable zDrawable = config.getAxisZDrawable();

        if (xDrawable.canDraw(AxisDrawable.NEGATIVE)) renderGridNegativeX(buffer, x, y, z, yDrawable, zDrawable);
        if (xDrawable.canDraw(AxisDrawable.POSITIVE)) renderGridPositiveX(buffer, x, y, z, yDrawable, zDrawable);
        if (yDrawable.canDraw(AxisDrawable.NEGATIVE)) renderGridNegativeY(buffer, x, y, z, zDrawable);
        if (yDrawable.canDraw(AxisDrawable.POSITIVE)) renderGridPositiveY(buffer, x, y, z, zDrawable);
        if (zDrawable.canDraw(AxisDrawable.NEGATIVE)) {
            buffer.vertex(x, y, z - GRID_MAX).color(0.25F, 0.25F, 1F, 1F).endVertex();
            buffer.vertex(x, y, z).color(0.25F, 0.25F, 1F, 1F).endVertex();
        }
        if (zDrawable.canDraw(AxisDrawable.POSITIVE)) {
            buffer.vertex(x, y, z).color(0.25F, 0.25F, 1F, 1F).endVertex();
            buffer.vertex(x, y, z + GRID_MAX).color(0.25F, 0.25F, 1F, 1F).endVertex();
        }
    }

    private static void renderGridNegativeX(BufferBuilder buffer, double x, double y, double z, AxisDrawable yDrawable, AxisDrawable zDrawable) {
        buffer.vertex(x - GRID_MAX, y, z).color(1F, 0.25F, 0.25F, 1F).endVertex();
        buffer.vertex(x, y, z).color(1F, 0.25F, 0.25F, 1F).endVertex();

        if (yDrawable.canDraw(AxisDrawable.NEGATIVE)) {
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

        if (yDrawable.canDraw(AxisDrawable.POSITIVE)) {
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

        if (zDrawable.canDraw(AxisDrawable.NEGATIVE)) {
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

        if (zDrawable.canDraw(AxisDrawable.POSITIVE)) {
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

    private static void renderGridPositiveX(BufferBuilder buffer, double x, double y, double z, AxisDrawable yDrawable, AxisDrawable zDrawable) {
        buffer.vertex(x, y, z).color(1F, 0.25F, 0.25F, 1F).endVertex();
        buffer.vertex(x + GRID_MAX, y, z).color(1F, 0.25F, 0.25F, 1F).endVertex();

        if (yDrawable.canDraw(AxisDrawable.NEGATIVE)) {
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

        if (yDrawable.canDraw(AxisDrawable.POSITIVE)) {
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

        if (zDrawable.canDraw(AxisDrawable.NEGATIVE)) {
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

        if (zDrawable.canDraw(AxisDrawable.POSITIVE)) {
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

    private static void renderGridNegativeY(BufferBuilder buffer, double x, double y, double z, AxisDrawable zDrawable) {
        buffer.vertex(x, y - GRID_MAX, z).color(0.25F, 1F, 0.25F, 1F).endVertex();
        buffer.vertex(x, y, z).color(0.25F, 1F, 0.25F, 1F).endVertex();

        if (zDrawable.canDraw(AxisDrawable.NEGATIVE)) {
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

        if (zDrawable.canDraw(AxisDrawable.POSITIVE)) {
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

    private static void renderGridPositiveY(BufferBuilder buffer, double x, double y, double z, AxisDrawable zDrawable) {
        buffer.vertex(x, y, z).color(0.25F, 1F, 0.25F, 1F).endVertex();
        buffer.vertex(x, y + GRID_MAX, z).color(0.25F, 1F, 0.25F, 1F).endVertex();

        if (zDrawable.canDraw(AxisDrawable.NEGATIVE)) {
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

        if (zDrawable.canDraw(AxisDrawable.POSITIVE)) {
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
