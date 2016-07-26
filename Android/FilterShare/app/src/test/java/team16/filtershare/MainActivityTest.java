package team16.filtershare;

// MainActivityTest.java

// Static imports for assertion methods
import android.os.Build;
import android.widget.ImageButton;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

/**
 * Created by harrykim on 2016. 7. 26..
 */

@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricGradleTestRunner.class)
public class MainActivityTest {
    private MainActivity activity;

    // @Before => JUnit 4 annotation that specifies this method should run before each test is run
    // Useful to do setup for objects that are needed in the test
    @Before
    public void setup() {
        // Convenience method to run MainActivity through the Activity Lifecycle methods:
        // onCreate(...) => onStart() => onPostCreate(...) => onResume()
        activity = Robolectric.setupActivity(MainActivity.class);
    }

    // @Test => JUnit 4 annotation specifying this is a test to be run
    // The test simply checks that our TextView exists and has the text "Hello world!"
    @Test
    public void Button_CaputreButtonExists() {
        ImageButton capture_button = (ImageButton) activity.findViewById(R.id.button_capture);
        int button_height = capture_button.getHeight();
        assertEquals(button_height, 70);

    }

    @Test
    public void Rect_AutoFocusRectExists() {
        AutofocusRect mAutofocusRect = (AutofocusRect) activity.findViewById(R.id.af_rect);
        int button_height = mAutofocusRect.getHeight();
        assertEquals(button_height, 60);

    }
}

