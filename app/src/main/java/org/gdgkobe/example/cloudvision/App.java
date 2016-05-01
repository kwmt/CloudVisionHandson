package org.gdgkobe.example.cloudvision;

import android.app.Application;

import org.gdgkobe.example.cloudvision.model.CloudVisionAPI;

/**
 * Created by kwmt on 2016/04/30.
 */
public class App extends Application {

    private static CloudVisionAPI sCloudVisionAPI;

    @Override
    public void onCreate() {
        super.onCreate();

        sCloudVisionAPI = new CloudVisionAPI(getApplicationContext());

    }

    public static CloudVisionAPI getCloudVisionApi() {
        return sCloudVisionAPI;
    }
}
