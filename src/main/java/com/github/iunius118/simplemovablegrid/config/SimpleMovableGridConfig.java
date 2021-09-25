package com.github.iunius118.simplemovablegrid.config;

import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import org.apache.commons.lang3.tuple.Pair;

public class SimpleMovableGridConfig {
    public static class Client {
        public final BooleanValue enabled;
        public final EnumValue<AxisDrawable> axisX;
        public final EnumValue<AxisDrawable> axisY;
        public final EnumValue<AxisDrawable> axisZ;
        public final IntValue x;
        public final IntValue y;
        public final IntValue z;

        Client(ForgeConfigSpec.Builder builder) {
            enabled = builder
                    .comment("Whether to show grid lines")
                    .define("enabled", true);

            axisX = builder
                    .comment("Area on X-axis where grid lines are displayed")
                    .defineEnum("axisX", AxisDrawable.POSITIVE);

            axisY = builder
                    .comment("Area on Y-axis where grid lines are displayed")
                    .defineEnum("axisY", AxisDrawable.POSITIVE);

            axisZ = builder
                    .comment("Area on Z-axis where grid lines are displayed")
                    .defineEnum("axisZ", AxisDrawable.POSITIVE);

            x = builder
                    .comment("X-coordinate value of base position of grid lines")
                    .defineInRange("x", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);

            y = builder
                    .comment("Y-coordinate value of base position of grid lines")
                    .defineInRange("y", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);

            z = builder
                    .comment("Z-coordinate value of base position of grid lines")
                    .defineInRange("z", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        }

        public boolean enabled() {
            return enabled.get();
        }

        public boolean toggleEnabled() {
            enabled.set(!enabled.get());
            return enabled.get();
        }

        public Vector3d getPos() {
            return new Vector3d(x.get(), y.get(), z.get());
        }

        public Vector3i setPos(int posX, int posY, int posZ) {
            x.set(posX);
            y.set(posY);
            z.set(posZ);
            return new Vector3i(x.get(), y.get(), z.get());
        }


        public AxisDrawable getAxisXDrawable() {
            return axisX.get();
        }

        public AxisDrawable getAxisYDrawable() {
            return axisY.get();
        }

        public AxisDrawable getAxisZDrawable() {
            return axisZ.get();
        }
    }

    public static ForgeConfigSpec clientSpec;
    public static final Client CLIENT;

    static {
        final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
    }
}
