package com.github.iunius118.simplemovablegrid.client.integration.autoconfig;

import com.github.iunius118.simplemovablegrid.client.SimpleMovableGrid;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

@Config(name = SimpleMovableGrid.MOD_ID)
public class ModConfig implements ConfigData {
    boolean enabled = true;
    AxisDrawable axisX = AxisDrawable.POSITIVE;
    AxisDrawable axisY = AxisDrawable.POSITIVE;
    AxisDrawable axisZ = AxisDrawable.POSITIVE;
    int x = 0;
    int y = 0;
    int z = 0;
    @ConfigEntry.Gui.CollapsibleObject()
    Label label = new Label();

    public boolean enabled() {
        return enabled;
    }

    public boolean toggleEnabled() {
        enabled = !enabled;
        return enabled;
    }

    public Vec3 getPos() {
        return new Vec3(x, y, z);
    }

    public Vec3i setPos(int posX, int posY, int posZ) {
        x = posX;
        y = posY;
        z = posZ;
        return new Vec3i(x, y, z);
    }

    public AxisDrawable getAxisXDrawable() {
        return axisX;
    }

    public AxisDrawable getAxisYDrawable() {
        return axisY;
    }

    public AxisDrawable getAxisZDrawable() {
        return axisZ;
    }

    public boolean isLabelEnabled() {
        return label.isLabelEnabled;
    }

    public LabelDefinition getLabelDefinition() {
        String jsonString = label.definitionInJson;
        return new LabelDefinition(jsonString, getLayerMask());
    }

    public Vec3i getLayerMask() {
        return new Vec3i(label.layerMaskX, label.layerMaskY, label.layerMaskZ);
    }

    static class Label {
        boolean isLabelEnabled = false;
        String definitionInJson = "{\"labels\":[[\"(0,0,0)\"],[\"(1,0,0)\"],1,[\"(3,0,0)\",\"(3,1,0)\"],2,[1,\"(6,1,0)\",2,\"(6,4,0)\",\"(6,5,0)\"],25,[\"(0,0,1)\"],31,[\"(0,0,2)\"],63,[\"(0,0,4)\"],894,[31,\"(31,31,31)\"]]}";
        int layerMaskX = -1;
        int layerMaskY = -1;
        int layerMaskZ = -1;
    }
}
