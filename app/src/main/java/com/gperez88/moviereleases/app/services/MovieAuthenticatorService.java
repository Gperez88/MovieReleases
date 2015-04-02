package com.gperez88.moviereleases.app.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by GPEREZ on 3/29/2015.
 */
public class MovieAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private MovieAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new MovieAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
