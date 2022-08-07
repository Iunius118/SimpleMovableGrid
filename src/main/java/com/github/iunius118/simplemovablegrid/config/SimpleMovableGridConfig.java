package com.github.iunius118.simplemovablegrid.config;

import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.fml.config.IConfigSpec;
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
        public final BooleanValue isLabelEnabled;
        public final ForgeConfigSpec.ConfigValue<String> definitionInJson;

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

            builder .comment("Label settings")
                    .push("label");

            isLabelEnabled = builder
                    .comment("Whether to show labels")
                    .define("isLabelEnabled", false);

            definitionInJson = builder
                    .comment("JSON string defining labels")
                    .define("definitionInJson", "{\"labels\":[[\"(0,0,0)\"],[\"(1,0,0)\"],1,[\"(3,0,0)\",\"(3,1,0)\"],2,[1,\"(6,1,0)\",2,\"(6,4,0)\",\"(6,5,0)\"],25,[\"(0,0,1)\"],31,[\"(0,0,2)\"],63,[\"(0,0,4)\"],894,[31,\"(31,31,31)\"]]}");

            builder.pop();
        }

        public boolean enabled() {
            return enabled.get();
        }

        public boolean toggleEnabled() {
            enabled.set(!enabled.get());
            return enabled.get();
        }

        public Vec3 getPos() {
            return new Vec3(x.get(), y.get(), z.get());
        }

        public Vec3i setPos(int posX, int posY, int posZ) {
            x.set(posX);
            y.set(posY);
            z.set(posZ);
            return new Vec3i(x.get(), y.get(), z.get());
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

        public boolean isLabelEnabled() {
            return isLabelEnabled.get();
        }

        public LabelDefinition getLabelDefinition() {
            String jsonString = definitionInJson.get();
            return new LabelDefinition(jsonString);
        }
    }

    public static IConfigSpec<?> clientSpec;
    public static final Client CLIENT;

    static {
        final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
    }
}
