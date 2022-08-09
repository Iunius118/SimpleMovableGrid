package com.github.iunius118.simplemovablegrid.client.integration.autoconfig;

public enum AxisDrawable {
    NONE,
    NEGATIVE,
    POSITIVE,
    BOTH,
    ;

    public boolean canDraw(AxisDrawable sign) {
        switch (sign) {
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
