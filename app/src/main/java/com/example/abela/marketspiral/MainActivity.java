package com.example.abela.marketspiral;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import com.example.abela.marketspiral.Core.Item;
import com.example.abela.marketspiral.GUI.ItemsFragment;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Communicator, Dataloader, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener, LocationListener {

    public static GoogleApiClient mGoogleApiClient;
    public static Location mLastLocation;
    public static Context context;
    public static SharedPreferences sharedPreferences;
    BackFetch backFetch = new BackFetch(this);
    //-------------------------------------------------------
    HashMap<Integer, List<Item>> itemsHashmap;
    FragmentManager mFragmentManager;
    android.support.v4.app.FragmentTransaction mFragmentTransaction;

    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 0;  /* 10 secs */
    private long FASTEST_INTERVAL = 0;

    LocationManager locationManager;
    LocationSettingDialog locationSettingDialog;
    //---------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                // .setAction("Action", null).show();
                // replace(new SupermarketsFragment());
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


          //locControl();
        //backFetch();

        ItemsFragment itemsFragment = new ItemsFragment();
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.containerFrame, itemsFragment, "itemsfragment");
        transaction.addToBackStack("current");
        transaction.commit();
//-----------------------------------------------------------------------------
        PlayServiceCheck playServiceCheck = new PlayServiceCheck(getApplicationContext());
        if (playServiceCheck.isPlayServiceOk()) {
            buildGoogleApiClient();
            mGoogleApiClient.connect();
        }
//-----------------------------------------------------------------------------
        context = getApplicationContext();
        locationSettingDialog = new LocationSettingDialog(MainActivity.this);
    }


    @Override
    protected void onResumeFragments() {
        if(itemsHashmap==null){
         backFetch();
        }
        super.onResumeFragments();
    }

    public void backFetch(){
        if(mLastLocation==null){
            getLastKnownLocation();
        }
          else if (isLocationEnabled()) {
        backFetch.execute();
    }
}
    public void locControl() {
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {

            }

            public void onProviderDisabled(String provider) {
            }

            public void onProviderEnabled(String provider) {

                  if(itemsHashmap==null){
                     // backFetch.execute();
                     // backFetch.execute();
                      Log.d("ab","enabled");
                   }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
        };

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
}

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        List<Item> itemList = new ArrayList<>();
       /*  if (id == R.id.nav_beverages) {

            loadItems(0);


        }else if (id == R.id.nav_bread_bakery) {
            loadItems(1);
        }else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.d("ab","focus changed");

        if(itemsHashmap==null){
          init();

        }
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void replaceFragment(Fragment fragment, Bundle bundle) {
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        fragment.setArguments(bundle);
        transaction.replace(R.id.containerFrame, fragment);
        transaction.addToBackStack(String.valueOf(bundle.get("title")));
        Log.d("ab", "replace 1" + bundle.get("title"));

        transaction.commit();
    }

    @Override
    public void dataload(HashMap<Integer, List<Item>> itemsHash) {
        itemsHashmap = itemsHash;

        List<Item> itemList = new ArrayList<>();
        Item item1;
        for (int i = 0; i < itemsHashmap.size(); i++) {
            for (int j = 0; j < itemsHashmap.get(i).size(); j++) {
                item1 = itemsHashmap.get(i).get(j);
                itemList.add(item1);
            }
        }

        ItemsFragment itemsFragment = (ItemsFragment) getSupportFragmentManager().findFragmentByTag("itemsfragment");
        itemsFragment.prepareItems(itemList);
    }

    public void loadItems(int cat) {

        List<Item> itemList = new ArrayList<>();
        Item item1;

        for (int j = 0; j < itemsHashmap.get(cat).size(); j++) {
            item1 = itemsHashmap.get(cat).get(j);
            itemList.add(item1);
        }

        ItemsFragment itemsFragment = (ItemsFragment) getSupportFragmentManager().findFragmentByTag("itemsfragment");
        itemsFragment.prepareItems(itemList);
    }

    //=====================================================================================
    public void startLocationUpdates() {
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);

}
    private void stopLocationUpdates() {
        Toast.makeText(this, "stoped", Toast.LENGTH_SHORT).show();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, MainActivity.this);
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates(this);
        }
    }

    synchronized void buildGoogleApiClient() {

    try{
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }catch (Exception e){
    }
}
    public void getLastKnownLocation() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location loc = null;
        if (mGoogleApiClient.isConnected()) {
                loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                writeLocation(loc);
        }

        if (loc != null) {
            mLastLocation = loc;
        } else {
            loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); //if google null try from gps provider
            writeLocation(loc);
            if (loc != null) {
                mLastLocation = loc;
            } else {
                loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);// else try from net provider last location for search
                writeLocation(loc);
                if (loc != null) {
                    mLastLocation = loc;
                } else {
                    loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);// else try from passive
                    writeLocation(loc);
                    if (loc != null) {
                        mLastLocation = loc;
                    }
                }
            }
        }
    }
    private void writeLocation(Location loc) {
        sharedPreferences=this.getPreferences(context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("mlastLocation",""+loc);
        editor.commit();

//==================================================================
        if(loc!=null) {
            TextWriteRead textWriteRead =new TextWriteRead();
            textWriteRead.writeToFile(String.valueOf(loc.getLatitude())+":"+String.valueOf(loc.getLongitude()),getApplication(),"mlastLocation.txt");
        }
    }

     public void init (){
      if(isLocationEnabled()){
        getLastKnownLocation();
          backFetch.execute();
      }
     }

    public boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean is_GPS_Enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean is_Network_Enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (is_GPS_Enabled == false && is_Network_Enabled == false) {

            if(!locationSettingDialog.isDialogVisible()){
            locationSettingDialog.showSettingsAlert();}

            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }
    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onLocationChanged(Location location) {
        writeLocation(location);
    }
 //====================================================================================
}
