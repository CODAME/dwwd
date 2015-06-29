package com.organic.projects.dwwd;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gimbal.android.BeaconEventListener;
import com.gimbal.android.BeaconManager;
import com.gimbal.android.BeaconSighting;
import com.gimbal.android.Gimbal;
import com.organic.projects.dwwd.util.SystemUiHider;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class FullscreenActivity extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */

    private BeaconEventListener beaconSightingListener;
    private BeaconManager beaconManager;

    private Integer previousRSSI = 1111;
    private String previousName = "";

    private static String UNDER_WATER = "UNDER WATER";
    private static String OUTER_SPACE = "OUTER SPACE";
    private Map<String, Drawable> locations;
    private String currentLocation = UNDER_WATER;

    private final int interval = 10;
    private Handler handler = new Handler();

    private LinearLayout mProgress;
    private Button mLocationToggle;
    private ImageView mBackground;

    private static Integer requiredSignalStrength = 55;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locations = new HashMap<String, Drawable>();
        locations.put(UNDER_WATER, getResources().getDrawable(R.drawable.ocean_50p));
        locations.put(OUTER_SPACE, getResources().getDrawable(R.drawable.outer_space));

        Gimbal.setApiKey(this.getApplication(), "dde26c72-ea4c-411e-85f8-3f1ddb9c45dc");

        beaconSightingListener = new BeaconEventListener() {
            @Override
            public void onBeaconSighting(BeaconSighting sighting) {
                Log.i("INFO", sighting.toString());
                previousName = sighting.getBeacon().getName();
                previousRSSI = sighting.getRSSI() * -1;
            }
        };
        beaconManager = new BeaconManager();
        beaconManager.addListener(beaconSightingListener);

        beaconManager.startListening();

        setContentView(R.layout.activity_fullscreen);

        handler.postDelayed(changeProgressBar, interval);

        mLocationToggle = (Button) findViewById(R.id.location_toggle);
        mBackground = (ImageView) findViewById(R.id.background);

        mLocationToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Iterator it = locations.entrySet().iterator();
                Boolean foundCurrent = false;
                Boolean sawFirst = false;
                String firstKey = "";
                Boolean weDone = false;
                while (it.hasNext() && !weDone) {
                    Map.Entry pair = (Map.Entry) it.next();
                    if (!sawFirst) {
                        sawFirst = true;
                        firstKey = pair.getKey().toString();
                    }
                    if (foundCurrent) {
                        Drawable background = locations.get(pair.getKey());
                        currentLocation = pair.getKey().toString();
                        mBackground.setImageDrawable(background);
                        weDone = true;
                    }
                    if (pair.getKey() == currentLocation) {
                        foundCurrent = true;
                    }
                }
                if (!weDone) {
                    Drawable background = locations.get(firstKey);
                    currentLocation = firstKey.toString();
                    mBackground.setImageDrawable(background);
                }
            }
        });
    }

    /**
     * Change progress bar
     *
     * @param RSSI
     */
    private Runnable changeProgressBar = new Runnable() {
        @Override
        public void run() {
            mLocationToggle.setVisibility(View.VISIBLE);
            TextView fullscreenContent = (TextView) findViewById(R.id.fullscreen_content);
            fullscreenContent.setText(previousName + " outside beam range");

            TextView fullscreenContentBig = (TextView) findViewById(R.id.fullscreen_content_big);
            fullscreenContentBig.setText(previousRSSI + "\nAWAY");

            Integer width = 0;
            mProgress = (LinearLayout) findViewById(R.id.progress);

            System.out.println("DRAWER: " + previousRSSI);
            handler.postDelayed(this, interval);

            if (previousRSSI < requiredSignalStrength) {
                mLocationToggle.setVisibility(View.GONE);
                width = ViewGroup.LayoutParams.MATCH_PARENT;
                fullscreenContent.setText("ILLICIT TRANSACTION COMPLETE. \n\nNEXT STOP:");

                HashMap<String, Drawable> locationsCopy = new HashMap<String, Drawable>();
                locationsCopy.putAll(locations);
                locationsCopy.remove(currentLocation);

                System.out.println("HEY Length of locations " + locations.size());
                System.out.println("HEY length of locationsCopy " + locationsCopy.size());

                Random generator = new Random();
                Object[] keys = locationsCopy.keySet().toArray();
                Object randomKey = keys[generator.nextInt(keys.length)];

                fullscreenContentBig.setText(randomKey.toString());
            }

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
            mProgress.setLayoutParams(params);

            // error state
            if (previousRSSI == 1111) {
                fullscreenContent.setText("Drone not detected. Try toggling BLE.");
                fullscreenContentBig.setText("");
            }
        }
    };
}

