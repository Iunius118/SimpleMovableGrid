package com.github.iunius118.simplemovablegrid.config;

import com.github.iunius118.simplemovablegrid.SimpleMovableGrid;
import com.github.iunius118.simplemovablegrid.client.renderer.GridRenderer;
import com.google.gson.*;
import net.minecraft.core.BlockPos;
import net.minecraft.util.GsonHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LabelDefinition {
    public static final LabelDefinition EMPTY = new LabelDefinition();

    private final List<Label> labels;

    public LabelDefinition(String jsonString) {
        labels = parseJson(jsonString);
    }

    private LabelDefinition() {
        labels = Collections.emptyList();
    }

    private List<Label> parseJson(String jsonString){
        List<Label> result = new ArrayList<>();

        try {
            JsonObject jsonObj = new Gson().fromJson(jsonString, JsonObject.class);
            JsonArray labelsJson = GsonHelper.getAsJsonArray(jsonObj, "labels");

            int posZX = 0;
            int posY = 0;
            final int posZXMax = GridRenderer.GRID_MAX * GridRenderer.GRID_MAX - 1;
            final int posXMax = GridRenderer.GRID_MAX - 1;
            final int posYMax = GridRenderer.GRID_MAX - 1;
            final int posZMax = GridRenderer.GRID_MAX - 1;

            for (JsonElement labelsXZJson : labelsJson) {
                if (posZX > posZXMax) {
                    break;
                } else if (labelsXZJson.isJsonArray()) {
                    JsonArray labelsYJson = labelsXZJson.getAsJsonArray();

                    for (JsonElement labelJson : labelsYJson) {
                        if (posY > posYMax) {
                            break;
                        } else if (GsonHelper.isStringValue(labelJson)) {
                            // Add text and pos to list
                            int posX = posZX % (posXMax + 1);
                            int posZ = (posZX / (posXMax + 1)) % (posZMax + 1);
                            var blockPos = new BlockPos(posX, posY, posZ);
                            result.add(new Label(blockPos, labelJson.getAsString()));
                            posY++;
                        } else if (GsonHelper.isNumberValue(labelJson)) {
                            // Skip to next text in y-axis
                            int dy = labelJson.getAsInt();
                            if (dy > 0) {
                                posY += dy;
                            }
;                        }
                    }

                    posZX++;
                    posY = 0;
                } else if (GsonHelper.isNumberValue(labelsXZJson)) {
                    // Skip to next y array
                    int dxz = labelsXZJson.getAsInt();
                    if (dxz > 0) {
                        posZX += labelsXZJson.getAsInt();
                    }
                }
            }
        } catch (JsonSyntaxException e) {
            SimpleMovableGrid.LOGGER.error(e.toString());
        }

        return result;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public record Label(BlockPos pos, String text) {}
}
