package com.chiralux.photowatermark;

public class WatermarkOptions {
    public enum Position { LEFT_TOP, CENTER, RIGHT_BOTTOM }

    private final int fontSize;
    private final String color;
    private final Position position;

    public WatermarkOptions(int fontSize, String color, String positionStr) {
        this.fontSize = fontSize;
        this.color = color;
        this.position = parsePosition(positionStr);
    }

    public int getFontSize() {
        return fontSize;
    }

    public String getColor() {
        return color;
    }

    public Position getPosition() {
        return position;
    }

    private Position parsePosition(String s) {
        if (s == null) return Position.RIGHT_BOTTOM;
        s = s.toLowerCase().trim();
        if (s.equals("left-top") || s.equals("left_top") || s.equals("lt")) {
            return Position.LEFT_TOP;
        } else if (s.equals("center") || s.equals("middle") || s.equals("c")) {
            return Position.CENTER;
        } else if (s.equals("right-bottom") || s.equals("right_bottom") || s.equals("rb")) {
            return Position.RIGHT_BOTTOM;
        } else {
            return Position.RIGHT_BOTTOM;
        }
    }
}
