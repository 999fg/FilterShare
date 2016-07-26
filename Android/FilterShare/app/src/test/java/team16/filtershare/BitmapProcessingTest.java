package team16.filtershare;

import android.graphics.Color;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.logging.Filter;

import static org.mockito.Mockito.*;

import static org.junit.Assert.assertEquals;
import android.graphics.Color;

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
    public void contrast_FiftyAsArgumentValue_ReturnSameColor() {
        assertEquals(BitmapProcessing.contrast(Color.RED, 50), Color.RED);
    }

    @Test
    public void fade_ZeroAsArgumentValue_ReturnSameBitmap() {
        assertEquals(BitmapProcessing.fade(Color.RED, 0), Color.RED);
    }

    @Test
    public void temperature_FiftyAsArgumentValue_ReturnSameColor() {
        assertEquals(BitmapProcessing.temperature(Color.RED, 50), Color.RED);
    }

    @Test
    public void tint_ZeroAsArgumentValue_ReturnSameColor() {
        assertEquals(BitmapProcessing.tint(Color.RED, 0), Color.RED);
    }

    @Test
    public void grain_ZeroAsArgumentValue_ReturnSameColor() {
        assertEquals(BitmapProcessing.grain(Color.RED, 0), Color.RED);
    }
    /*
    @Test
    public void brightness_ValueProcessing_CorrectReturn() {
        assertEquals(BitmapProcessing.brightness(0x, ));
    }
    */

    @After
    public void tearDown() {

    }
}
