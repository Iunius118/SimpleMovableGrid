package com.github.iunius118.simplemovablegrid.client.renderer;

import com.github.iunius118.simplemovablegrid.client.SimpleMovableGrid;
import com.github.iunius118.simplemovablegrid.client.integration.autoconfig.AxisDrawable;
import com.github.iunius118.simplemovablegrid.client.integration.autoconfig.LabelDefinition;
import com.github.iunius118.simplemovablegrid.client.integration.autoconfig.ModConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.List;

public class GridRenderer {
    public static final int GRID_MAX = 32;

    public static void render(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, Vec3 gridPos, boolean isLabelEnabled) {
        var mainCamera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 cameraPos = mainCamera.getPosition();
        Vec3 originPos = gridPos.subtract(cameraPos);
        Matrix4f pose = poseStack.last().pose();
        renderGrid(pose, originPos);

        if (isLabelEnabled)
            renderLabels(poseStack, bufferSource, gridPos);
    }

    private static void renderGrid(Matrix4f pose, Vec3 pos) {
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        RenderSystem.disableCull();
        RenderSystem.disableBlend();
        RenderSystem.lineWidth(1.0F);

        buffer.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        renderGrid(pose, buffer, pos);
        tesselator.end();

        RenderSystem.enableBlend();
        RenderSystem.enableCull();
    }

    private static void renderGrid(Matrix4f pose, BufferBuilder buffer, Vec3 pos) {
        final float x = (float) pos.x;
        final float y = (float) pos.y;
        final float z = (float) pos.z;

        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        AxisDrawable xDrawable = config.getAxisXDrawable();
        AxisDrawable yDrawable = config.getAxisYDrawable();
        AxisDrawable zDrawable = config.getAxisZDrawable();

        if (xDrawable.canDraw(AxisDrawable.NEGATIVE)) renderGridNegativeX(pose, buffer, x, y, z, yDrawable, zDrawable);
        if (xDrawable.canDraw(AxisDrawable.POSITIVE)) renderGridPositiveX(pose, buffer, x, y, z, yDrawable, zDrawable);
        if (yDrawable.canDraw(AxisDrawable.NEGATIVE)) renderGridNegativeY(pose, buffer, x, y, z, zDrawable);
        if (yDrawable.canDraw(AxisDrawable.POSITIVE)) renderGridPositiveY(pose, buffer, x, y, z, zDrawable);
        if (zDrawable.canDraw(AxisDrawable.NEGATIVE)) {
            buffer.vertex(pose, x, y, z - GRID_MAX).color(0.25F, 0.25F, 1F, 1F).endVertex();
            buffer.vertex(pose, x, y, z).color(0.25F, 0.25F, 1F, 1F).endVertex();
        }
        if (zDrawable.canDraw(AxisDrawable.POSITIVE)) {
            buffer.vertex(pose, x, y, z).color(0.25F, 0.25F, 1F, 1F).endVertex();
            buffer.vertex(pose, x, y, z + GRID_MAX).color(0.25F, 0.25F, 1F, 1F).endVertex();
        }
    }

    private static void renderGridNegativeX(Matrix4f pose, BufferBuilder buffer, float x, float y, float z, AxisDrawable yDrawable, AxisDrawable zDrawable) {
        buffer.vertex(pose, x - GRID_MAX, y, z).color(1F, 0.25F, 0.25F, 1F).endVertex();
        buffer.vertex(pose, x, y, z).color(1F, 0.25F, 0.25F, 1F).endVertex();

        if (yDrawable.canDraw(AxisDrawable.NEGATIVE)) {
            int i = 2;
            for (; i < GRID_MAX; i += 2) {
                buffer.vertex(pose, x - GRID_MAX, y - i, z).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(pose, x, y - i, z).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(pose, x - i, y - GRID_MAX, z).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(pose, x - i, y, z).color(1F, 1F, 1F, 1F).endVertex();
            }
            buffer.vertex(pose, x - GRID_MAX, y - i, z).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
            buffer.vertex(pose, x, y - i, z).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
            buffer.vertex(pose, x - i, y - GRID_MAX, z).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
            buffer.vertex(pose, x - i, y, z).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
        }

        if (yDrawable.canDraw(AxisDrawable.POSITIVE)) {
            int i = 2;
            for (; i < GRID_MAX; i += 2) {
                buffer.vertex(pose, x - GRID_MAX, y + i, z).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(pose, x, y + i, z).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(pose, x - i, y + GRID_MAX, z).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(pose, x - i, y, z).color(1F, 1F, 1F, 1F).endVertex();
            }
            buffer.vertex(pose, x - GRID_MAX, y + i, z).color(1F, 1F, 0F, 1F).endVertex();
            buffer.vertex(pose, x, y + i, z).color(1F, 1F, 0F, 1F).endVertex();
            buffer.vertex(pose, x - i, y + GRID_MAX, z).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
            buffer.vertex(pose, x - i, y, z).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
        }

        if (zDrawable.canDraw(AxisDrawable.NEGATIVE)) {
            int i = 2;
            for (; i < GRID_MAX; i += 2) {
                buffer.vertex(pose, x - GRID_MAX, y, z - i).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(pose, x, y, z - i).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(pose, x - i, y, z - GRID_MAX).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(pose, x - i, y, z).color(1F, 1F, 1F, 1F).endVertex();
            }
            buffer.vertex(pose, x - GRID_MAX, y, z - i).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
            buffer.vertex(pose, x, y, z - i).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
            buffer.vertex(pose, x - i, y, z - GRID_MAX).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
            buffer.vertex(pose, x - i, y, z).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
        }

        if (zDrawable.canDraw(AxisDrawable.POSITIVE)) {
            int i = 2;
            for (; i < GRID_MAX; i += 2) {
                buffer.vertex(pose, x - GRID_MAX, y, z + i).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(pose, x, y, z + i).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(pose, x - i, y, z).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(pose, x - i, y, z + GRID_MAX).color(1F, 1F, 1F, 1F).endVertex();
            }
            buffer.vertex(pose, x - GRID_MAX, y, z + i).color(1F, 1F, 0F, 1F).endVertex();
            buffer.vertex(pose, x, y, z + i).color(1F, 1F, 0F, 1F).endVertex();
            buffer.vertex(pose, x - i, y, z).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
            buffer.vertex(pose, x - i, y, z + GRID_MAX).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
        }
    }

    private static void renderGridPositiveX(Matrix4f pose, BufferBuilder buffer, float x, float y, float z, AxisDrawable yDrawable, AxisDrawable zDrawable) {
        buffer.vertex(pose, x, y, z).color(1F, 0.25F, 0.25F, 1F).endVertex();
        buffer.vertex(pose, x + GRID_MAX, y, z).color(1F, 0.25F, 0.25F, 1F).endVertex();

        if (yDrawable.canDraw(AxisDrawable.NEGATIVE)) {
            int i = 2;
            for (; i < GRID_MAX; i += 2) {
                buffer.vertex(pose, x, y - i, z).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(pose, x + GRID_MAX, y - i, z).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(pose, x + i, y - GRID_MAX, z).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(pose, x + i, y, z).color(1F, 1F, 1F, 1F).endVertex();
            }
            buffer.vertex(pose, x, y - i, z).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
            buffer.vertex(pose, x + GRID_MAX, y - i, z).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
            buffer.vertex(pose, x + i, y - GRID_MAX, z).color(1F, 1F, 0F, 1F).endVertex();
            buffer.vertex(pose, x + i, y, z).color(1F, 1F, 0F, 1F).endVertex();
        }

        if (yDrawable.canDraw(AxisDrawable.POSITIVE)) {
            int i = 2;
            for (; i < GRID_MAX; i += 2) {
                buffer.vertex(pose, x, y + i, z).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(pose, x + GRID_MAX, y + i, z).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(pose, x + i, y, z).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(pose, x + i, y + GRID_MAX, z).color(1F, 1F, 1F, 1F).endVertex();
            }
            buffer.vertex(pose, x, y + i, z).color(1F, 1F, 0F, 1F).endVertex();
            buffer.vertex(pose, x + GRID_MAX, y + i, z).color(1F, 1F, 0F, 1F).endVertex();
            buffer.vertex(pose, x + i, y, z).color(1F, 1F, 0F, 1F).endVertex();
            buffer.vertex(pose, x + i, y + GRID_MAX, z).color(1F, 1F, 0F, 1F).endVertex();
        }

        if (zDrawable.canDraw(AxisDrawable.NEGATIVE)) {
            int i = 2;
            for (; i < GRID_MAX; i += 2) {
                buffer.vertex(pose, x, y, z - i).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(pose, x + GRID_MAX, y, z - i).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(pose, x + i, y, z - GRID_MAX).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(pose, x + i, y, z).color(1F, 1F, 1F, 1F).endVertex();
            }
            buffer.vertex(pose, x, y, z - i).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
            buffer.vertex(pose, x + GRID_MAX, y, z - i).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
            buffer.vertex(pose, x + i, y, z - GRID_MAX).color(1F, 1F, 0F, 1F).endVertex();
            buffer.vertex(pose, x + i, y, z).color(1F, 1F, 0F, 1F).endVertex();
        }

        if (zDrawable.canDraw(AxisDrawable.POSITIVE)) {
            int i = 2;
            for (; i < GRID_MAX; i += 2) {
                buffer.vertex(pose, x, y, z + i).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(pose, x + GRID_MAX, y, z + i).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(pose, x + i, y, z).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(pose, x + i, y, z + GRID_MAX).color(1F, 1F, 1F, 1F).endVertex();
            }
            buffer.vertex(pose, x, y, z + i).color(1F, 1F, 0F, 1F).endVertex();
            buffer.vertex(pose, x + GRID_MAX, y, z + i).color(1F, 1F, 0F, 1F).endVertex();
            buffer.vertex(pose, x + i, y, z).color(1F, 1F, 0F, 1F).endVertex();
            buffer.vertex(pose, x + i, y, z + GRID_MAX).color(1F, 1F, 0F, 1F).endVertex();
        }
    }

    private static void renderGridNegativeY(Matrix4f pose, BufferBuilder buffer, float x, float y, float z, AxisDrawable zDrawable) {
        buffer.vertex(pose, x, y - GRID_MAX, z).color(0.25F, 1F, 0.25F, 1F).endVertex();
        buffer.vertex(pose, x, y, z).color(0.25F, 1F, 0.25F, 1F).endVertex();

        if (zDrawable.canDraw(AxisDrawable.NEGATIVE)) {
            int i = 2;
            for (; i < GRID_MAX; i += 2) {
                buffer.vertex(pose, x, y - GRID_MAX, z - i).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(pose, x, y, z - i).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(pose, x, y - i, z - GRID_MAX).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(pose, x, y - i, z).color(1F, 1F, 1F, 1F).endVertex();
            }
            buffer.vertex(pose, x, y - GRID_MAX, z - i).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
            buffer.vertex(pose, x, y, z - i).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
            buffer.vertex(pose, x, y - i, z - GRID_MAX).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
            buffer.vertex(pose, x, y - i, z).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
        }

        if (zDrawable.canDraw(AxisDrawable.POSITIVE)) {
            int i = 2;
            for (; i < GRID_MAX; i += 2) {
                buffer.vertex(pose, x, y - GRID_MAX, z + i).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(pose, x, y, z + i).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(pose, x, y - i, z).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(pose, x, y - i, z + GRID_MAX).color(1F, 1F, 1F, 1F).endVertex();
            }
            buffer.vertex(pose, x, y - GRID_MAX, z + i).color(1F, 1F, 0F, 1F).endVertex();
            buffer.vertex(pose, x, y, z + i).color(1F, 1F, 0F, 1F).endVertex();
            buffer.vertex(pose, x, y - i, z).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
            buffer.vertex(pose, x, y - i, z + GRID_MAX).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
        }
    }

    private static void renderGridPositiveY(Matrix4f pose, BufferBuilder buffer, float x, float y, float z, AxisDrawable zDrawable) {
        buffer.vertex(pose, x, y, z).color(0.25F, 1F, 0.25F, 1F).endVertex();
        buffer.vertex(pose, x, y + GRID_MAX, z).color(0.25F, 1F, 0.25F, 1F).endVertex();

        if (zDrawable.canDraw(AxisDrawable.NEGATIVE)) {
            int i = 2;
            for (; i < GRID_MAX; i += 2) {
                buffer.vertex(pose, x, y, z - i).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(pose, x, y + GRID_MAX, z - i).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(pose, x, y + i, z - GRID_MAX).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(pose, x, y + i, z).color(1F, 1F, 1F, 1F).endVertex();
            }
            buffer.vertex(pose, x, y, z - i).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
            buffer.vertex(pose, x, y + GRID_MAX, z - i).color(0.75F, 0.75F, 0.5F, 1F).endVertex();
            buffer.vertex(pose, x, y + i, z - GRID_MAX).color(1F, 1F, 0F, 1F).endVertex();
            buffer.vertex(pose, x, y + i, z).color(1F, 1F, 0F, 1F).endVertex();
        }

        if (zDrawable.canDraw(AxisDrawable.POSITIVE)) {
            int i = 2;
            for (; i < GRID_MAX; i += 2) {
                buffer.vertex(pose, x, y, z + i).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(pose, x, y + GRID_MAX, z + i).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(pose, x, y + i, z).color(1F, 1F, 1F, 1F).endVertex();
                buffer.vertex(pose, x, y + i, z + GRID_MAX).color(1F, 1F, 1F, 1F).endVertex();
            }
            buffer.vertex(pose, x, y, z + i).color(1F, 1F, 0F, 1F).endVertex();
            buffer.vertex(pose, x, y + GRID_MAX, z + i).color(1F, 1F, 0F, 1F).endVertex();
            buffer.vertex(pose, x, y + i, z).color(1F, 1F, 0F, 1F).endVertex();
            buffer.vertex(pose, x, y + i, z + GRID_MAX).color(1F, 1F, 0F, 1F).endVertex();
        }
    }

    private static void renderLabels(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, Vec3 gridPos) {
        LabelDefinition labelDefinition = SimpleMovableGrid.labelDefinition;
        List<LabelDefinition.Label> labels = labelDefinition.getLabels();

        for (LabelDefinition.Label label : labels) {
            Vec3 pos = Vec3.atCenterOf(label.pos()).add(gridPos);
            DebugRenderer.renderFloatingText(poseStack, bufferSource, label.text(), pos.x, pos.y, pos.z, -1);
        }
    }
}
