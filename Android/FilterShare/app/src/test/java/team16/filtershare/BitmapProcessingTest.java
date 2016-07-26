package team16.filtershare;

import android.graphics.Color;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by chocho on 7/26/16.
 */

public class BitmapProcessingTest {

    @Before
    public void setUp() {
    }

    @Test
    public void brightness_FiftyAsArgumentValue_ReturnSameColor() {
        assertEquals(BitmapProcessing.brightness(Color.RED, 50), Color.RED);
    }

    @Test
    public void brightness_RGBOver255_BoundTo255() {
        assertEquals(BitmapProcessing.brightness(0xfffefefe, 100), 0xffffffff);
    }

    @Test
    public void brightness_RGBUnder0_BoundTo0() {
        assertEquals(BitmapProcessing.brightness(0xff010101, 0), 0xff000000);
    }

    @Test
    public void brightness_HigherArgumentValue_ReturnBrighterPixel() {
        assertTrue(BitmapProcessing.brightness(Color.RED, 70) > Color.RED);
    }

    @Test
    public void brightness_LowerArgumentValue_ReturnDarkerPixel() {
        assertTrue(BitmapProcessing.brightness(Color.RED, 20) < Color.RED);
    }

    @Test
    public void contrast_FiftyAsArgumentValue_ReturnSameColor() {
        assertEquals(BitmapProcessing.contrast(Color.RED, 50), Color.RED);
    }

    @Test
    public void fade_ZeroAsArgumentValue_ReturnSameBitmap() {
        assertEquals(BitmapProcessing.fade(Color.RED, 0), Color.RED);
    }

    @Test
    public void fade_HigherArgumentValue_ReturnBrighterPixel() {
        assertTrue(BitmapProcessing.fade(Color.RED, 70) > Color.RED);
    }

    @Test
    public void temperature_FiftyAsArgumentValue_ReturnSameColor() {
        assertEquals(BitmapProcessing.temperature(Color.RED, 50), Color.RED);
    }

    @Test
    public void temperature_ArgumentValueLargerThanFifty_ReturnHigherRLowerGLowerB() {
        int color = 0x808080;
        assertTrue(((BitmapProcessing.temperature(color, 80) >> 16) & 0xff)
                > ((color >> 16) & 0xff));
        assertTrue(((BitmapProcessing.temperature(color, 80) >> 8) & 0xff)
                < ((color >> 8) & 0xff));
        assertTrue((BitmapProcessing.temperature(color, 80)& 0xff)
                < (color & 0xff));
    }

    @Test
    public void temperature_ArgumentValueLessThanFifty_ReturnLowerRHigherGHigherB() {
        int color = 0x808080;
        assertTrue(((BitmapProcessing.temperature(color, 20) >> 16) & 0xff)
                < ((color >> 16) & 0xff));
        assertTrue(((BitmapProcessing.temperature(color, 20) >> 8) & 0xff)
                > ((color >> 8) & 0xff));
        assertTrue((BitmapProcessing.temperature(color, 20)& 0xff)
                > (color & 0xff));
    }

    @Test
    public void tint_ZeroAsArgumentValue_ReturnSameColor() {
        assertEquals(BitmapProcessing.tint(Color.RED, 0), Color.RED);
    }

    @Test
    public void tint_ArgumentValueLargerThan0_ReturnHigherR() {
        int color = 0x808080;
        assertTrue(((BitmapProcessing.tint(color, 80) >> 16) & 0xff)
                > ((color >> 16) & 0xff));
    }

    @Test
    public void grain_ZeroAsArgumentValue_ReturnSameColor() {
        assertEquals(BitmapProcessing.grain(Color.RED, 0), Color.RED);
    }

    @After
    public void tearDown() {

    }
}
