package team16.filtershare;

// MainActivityTest.java

import android.os.Build;
import android.widget.ImageButton;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by harrykim on 2016. 7. 26..
 */

//This codes are derived from the robolectric tutorial from codepath.com to check the entire activity cycles
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class MainActivityTest {
    private MainActivity main_activity;


    @Before
    public void setUp() {
        main_activity = Robolectric.setupActivity(MainActivity.class);
    }


    // Test that simulates the full lifecycle of an activity
    @Test
    public void Button_CaputreButtonExists() {
        //createWithIntent("my extra_value");
        assertNotNull(main_activity);
        ImageButton capture_button = (ImageButton) main_activity.findViewById(R.id.button_capture);
        assertNotNull(capture_button);
        assertEquals(capture_button.getHeight(), 70);

        // ... add assertions ...
    }

    @Test
    public void Button_GalleryButtonExists() {
        //createWithIntent("my extra_value");
        assertNotNull(main_activity);
        ImageButton gallery_button = (ImageButton) main_activity.findViewById(R.id.button_gallery);
        assertNotNull(gallery_button);
        assertEquals(gallery_button.getHeight(), 70);

        // ... add assertions ...
    }

    @Test
    public void Button_ChangeButtonExists() {
        //createWithIntent("my extra_value");
        assertNotNull(main_activity);
        ImageButton change_button = (ImageButton) main_activity.findViewById(R.id.button_gallery);
        assertNotNull(change_button);
        assertEquals(change_button.getHeight(), 70);

        // ... add assertions ...
    }

    @Test
    public void Rect_AutoFocusRectExists() {
        AutofocusRect mAutofocusRect = (AutofocusRect) main_activity.findViewById(R.id.af_rect);
        int button_height = mAutofocusRect.getHeight();
        assertNotNull(mAutofocusRect);
        assertEquals(button_height, 60);
    }

}

