package pl.internetexperts.whereismichalservice;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Timer;
import java.util.TimerTask;

public class AppService extends Service {

    private FusedLocationProviderClient mFusedLocationClient;

    protected Location mLastLocation;

    // constant
    public static final long NOTIFY_INTERVAL = 300 * 1000;

    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Gets data from the incoming Intent
        //String dataString = workIntent.getDataString();

        // Do work here, based on the contents of dataString
        // cancel if already existed
        if(mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);
    }

    /*@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
      return START_STICKY;
      //return START_REDELIVER_INTENT;
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();    //For Cancel Timer
    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    // Interval task starts here
                    updateCoordinates();
                    //stopService(new Intent(AppService.this, AppService.class));
                    //startService(new Intent(AppService.this, AppService.class));
                }

            });
        }
    }

    protected void updateCoordinates() {
        final RequestQueue queue = Volley.newRequestQueue(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();
                            String coordinates =  mLastLocation.getLatitude() + "," + mLastLocation.getLongitude();

                            // ### REQUEST - update coordinates
                            String url ="http://internetexperts.pl/y/update-coordinates.php?coordinates="+coordinates;
                            // Request a string response from the provided URL.
                            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {

                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            });
                            // Add the request to the RequestQueue.
                            queue.add(stringRequest);

                        }
                    }
                });
    }
}