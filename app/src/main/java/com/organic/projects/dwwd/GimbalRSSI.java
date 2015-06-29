package com.organic.projects.dwwd;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.gimbal.android.BeaconEventListener;
import com.gimbal.android.BeaconManager;
import com.gimbal.android.BeaconSighting;
import com.gimbal.android.CommunicationManager;
import com.gimbal.android.PlaceManager;

/**
 * Created by fleur on 6/28/15.
 */
public class GimbalRSSI {
    private BeaconEventListener beaconSightingListener;
    private BeaconManager beaconManager;
    public static Integer previousRSSI;

    protected void manageRSSI(FullscreenActivity activity) {
        final FullscreenActivity fullscreenActivity = activity;
        beaconSightingListener = new BeaconEventListener() {
            @Override
            public void onBeaconSighting(BeaconSighting sighting) {
                Log.i("INFO", sighting.toString());
                TextView fullscreenContent = (TextView) fullscreenActivity.findViewById(R.id.fullscreen_content);

                fullscreenContent.setText(sighting.getBeacon().getName() + "\n" + sighting.getRSSI());

                previousRSSI = sighting.getRSSI();

            }
        };
        beaconManager = new BeaconManager();
        beaconManager.addListener(beaconSightingListener);

        PlaceManager.getInstance().startMonitoring();
        beaconManager.startListening();
        CommunicationManager.getInstance().startReceivingCommunications();
    }
}
