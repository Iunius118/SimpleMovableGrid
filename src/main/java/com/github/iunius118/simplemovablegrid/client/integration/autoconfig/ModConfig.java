package com.github.iunius118.simplemovablegrid.client.integration.autoconfig;

import com.github.iunius118.simplemovablegrid.client.SimpleMovableGrid;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
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

    public boolean enabled() {
        return enabled;
    }

    public Vec3 getPos() {
        return new Vec3(x, y, z);
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

    public enum AxisDrawable {
        NONE,
        NEGATIVE,
        POSITIVE,
        BOTH,
        ;

        public boolean canDraw(AxisDrawable sign) {
            switch(sign) {
                case POSITIVE, NEGATIVE -> {
                    return this == sign || this == BOTH;
                }
                case BOTH -> {
                    return this != NONE;
                }
                default -> {
                    return false;
                }
            }
        }
    }
}
