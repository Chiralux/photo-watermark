package com.chiralux.photowatermark;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WatermarkOptionsTest {

    @Test
    void parsePositionVariants() {
        WatermarkOptions a = new WatermarkOptions(24, "#fff", "left-top");
        WatermarkOptions b = new WatermarkOptions(24, "#fff", "center");
        WatermarkOptions c = new WatermarkOptions(24, "#fff", "right-bottom");
        assertEquals(WatermarkOptions.Position.LEFT_TOP, a.getPosition());
        assertEquals(WatermarkOptions.Position.CENTER, b.getPosition());
        assertEquals(WatermarkOptions.Position.RIGHT_BOTTOM, c.getPosition());
    }
}
