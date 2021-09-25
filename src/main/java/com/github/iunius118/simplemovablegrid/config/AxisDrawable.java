package com.github.iunius118.simplemovablegrid.config;

public enum AxisDrawable {
    NONE,
    NEGATIVE,
    POSITIVE,
    BOTH,
    ;

    public boolean canDraw(AxisDrawable sign) {
        switch (sign) {
            case NEGATIVE:
            case POSITIVE:
                return this == sign || this == BOTH;
            case BOTH:
                return this != NONE;
            default:
                return false;
        }
    }
}
