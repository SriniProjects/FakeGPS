package com.fakegps.optimustechproject.fakegps;

import android.Manifest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by satyam on 5/7/17.
 */

public class fragmet_location extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    View parentView;
    ImageView add_to_fav,share;
    ProgressDialog progress;
    Double lati=21.0,longi=78.0,curr_lati=21.0,curr_longi=78.0;
    FloatingActionButton fab;
    History history,favourites;
    int flg=0;
    TextView location;
    LinearLayout search_ll;
    String ci,co;
    double la,lo;
    Gson gson=new Gson();
    List<Double> latitude=new ArrayList<Double>(),longitude=new ArrayList<Double>();
    List<Double> latitude2=new ArrayList<Double>(),longitude2=new ArrayList<Double>();
    List<String> city=new ArrayList<String>(),country=new ArrayList<String>();
    List<String> city2=new ArrayList<String>(),country2=new ArrayList<String>();
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    String[] l;
    String map_type;
    Notification noti;
    NotificationManager nMN;
    Locale myLocale;
    Button set;
    String curr_lang;
    TextView txt_curr;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_location, container, false);

        fab=(FloatingActionButton)parentView.findViewById(R.id.fab);
        add_to_fav=(ImageView)parentView.findViewById(R.id.add_to_fav);
        share=(ImageView)parentView.findViewById(R.id.share);
        location=(TextView)parentView.findViewById(R.id.location);
        search_ll=(LinearLayout)getActivity().findViewById(R.id.search_ll);

        txt_curr=(TextView)parentView.findViewById(R.id.txt_curr);

        set=(Button)parentView.findViewById(R.id.btn_set_loc);
        progress=new ProgressDialog(getContext());
        progress.setMessage(getResources().getString(R.string.locating));
        progress.setCancelable(false);
        progress.setIndeterminate(true);
        // progress.show();

        la=lati;
        lo=longi;
        ci="";
        co="";

        mMapView = (MapView) parentView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(DbHandler.contains(getActivity(),"language")){
            curr_lang=DbHandler.getString(getActivity(),"language","");
        }
        else{
            curr_lang="english";
            DbHandler.putString(getActivity(),"language","english");
        }

        txt_curr.setText(getResources().getString(R.string.current_location));
       // changeLang(curr_lang);


        if(DbHandler.contains(getActivity(),"map_type")){
            map_type=DbHandler.getString(getActivity(),"map_type","");
        }
        else{
            map_type="normal";
            DbHandler.putString(getActivity(),"map_type","normal");
        }

        final Geocoder geocoder=new Geocoder(getContext(), Locale.getDefault());
        final GPSTracker gpsTracker=new GPSTracker(getContext());

        if(DbHandler.contains(getActivity(),"go_to_specific_search")){
            l=DbHandler.getString(getActivity(),"go_to_specific_search","").split("%");
            lati=Double.valueOf(l[0]);
            longi=Double.valueOf(l[1]);

            la=lati;
            lo=longi;
            ci=l[2];
            co="";
            location.setText(l[2]);
            Log.e("loc",String.valueOf(l));

            Toast.makeText(getActivity(),String.valueOf(lati)+"2",Toast.LENGTH_SHORT).show();
            setMarker();

        }

        else if(DbHandler.contains(getActivity(),"go_to_specific")){

            l=DbHandler.getString(getActivity(),"go_to_specific","").split("%");
            lati=Double.valueOf(l[0]);
            longi=Double.valueOf(l[1]);

            location.setText("");
            la=lati;
            lo=longi;

            Toast.makeText(getActivity(),String.valueOf(lati)+"3",Toast.LENGTH_SHORT).show();
            setMarker();

        }
        else {
            if (gpsTracker.canGetLocation()) {
                progress.dismiss();
                List<Address> addresses;
                try {
                    addresses = geocoder.getFromLocation(gpsTracker.getLatitude(), gpsTracker.getLongitude(), 1);
                    if (addresses.size() != 0) {

                        lati = gpsTracker.getLatitude();
                        longi = gpsTracker.getLongitude();
                        curr_lati = lati;
                        curr_longi = longi;

                        la=lati;
                        lo=longi;
                        ci=addresses.get(0).getLocality();
                        co=addresses.get(0).getCountryName();

                        Toast.makeText(getActivity(),String.valueOf(lati)+"1",Toast.LENGTH_SHORT).show();


                        location.setText(addresses.get(0).getLocality() + " " + addresses.get(0).getCountryName() + "\n" + String.valueOf(lati) + " " + String.valueOf(longi));
                        setMarker();
                    } else {
                        location.setText("");
                        lati=gpsTracker.getLatitude();
                        longi=gpsTracker.getLongitude();

                        la=lati;
                        lo=longi;

                        curr_lati = lati;
                        curr_longi = longi;

                        Toast.makeText(getActivity(),String.valueOf(lati)+"5",Toast.LENGTH_SHORT).show();
                        setMarker();
                    }

                } catch (Exception e) {
                    location.setText("");
                    lati=gpsTracker.getLatitude();
                    longi=gpsTracker.getLongitude();

                    la=lati;
                    lo=longi;

                    curr_lati = lati;
                    curr_longi = longi;

                    Toast.makeText(getActivity(),String.valueOf(lati)+"4",Toast.LENGTH_SHORT).show();
                    setMarker();

                    e.printStackTrace();

                }

            } else {
                progress.dismiss();

                gpsTracker.showSettingsAlert();
            }
        }


        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("history",DbHandler.getString(getContext(),"history","{}"));
                if(flg==0) {
                    flg=1;
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop_black_24dp));
                    fab.setColorFilter(getResources().getColor(R.color.white));
                    setMock();

                    if(DbHandler.contains(getContext(),"history")){
                        history=gson.fromJson(DbHandler.getString(getContext(),"history","{}"),History.class);
                        latitude=history.getLatitude();
                        longitude=history.getLongitude();
                        city=history.getCity();
                        country=history.getCountry();

                        latitude.add(la);
                        longitude.add(lo);
                        city.add(ci);
                        country.add(co);

                        History his=new History(latitude,longitude,city,country);

                        DbHandler.putString(getContext(),"history",gson.toJson(his));

                    }
                    else{

                        latitude.add(la);
                        longitude.add(lo);
                        city.add(ci);
                        country.add(co);

                        History his=new History(latitude,longitude,city,country);

                        DbHandler.putString(getContext(),"history",gson.toJson(his));

                    }

                    Intent intentAction = new Intent(getContext(),NavigationActivity.class);
                    intentAction.putExtra("action","actionStop");
                    PendingIntent pIntent = PendingIntent.getActivity(getContext(),1,intentAction,PendingIntent.FLAG_UPDATE_CURRENT);


                    nMN = (NotificationManager) getContext().getSystemService(NOTIFICATION_SERVICE);
                    noti = new Notification.Builder(getContext())
                            .setContentTitle(getResources().getString(R.string.new_mock))
                            .setContentText(ci+" ,"+co+"\n"+la+" , "+lo)
                            .setSmallIcon(R.drawable.ic_map_black_24dp)
                            .setAutoCancel(true)
                            .addAction(R.drawable.ic_stop_black_24dp,"Stop",pIntent)
                            .setOngoing(true)
                            .build();

                    noti.flags = Notification.FLAG_NO_CLEAR;
                    nMN.notify(0, noti);



                    Toast.makeText(getContext(),getResources().getString(R.string.check_internet2),Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else{
                    flg=0;
                    nMN.cancelAll();
                    setCurrent();
                    Toast.makeText(getContext(),String.valueOf(curr_lati),Toast.LENGTH_LONG).show();
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
                    fab.setColorFilter(getResources().getColor(R.color.white));
                }
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("history",DbHandler.getString(getContext(),"history","{}"));
                if(flg==0) {
                    flg=1;
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop_black_24dp));
                    fab.setColorFilter(getResources().getColor(R.color.white));
                    setMock();

                    if(DbHandler.contains(getContext(),"history")){
                        history=gson.fromJson(DbHandler.getString(getContext(),"history","{}"),History.class);
                        latitude=history.getLatitude();
                        longitude=history.getLongitude();
                        city=history.getCity();
                        country=history.getCountry();

                        latitude.add(la);
                        longitude.add(lo);
                        city.add(ci);
                        country.add(co);

                        History his=new History(latitude,longitude,city,country);

                        DbHandler.putString(getContext(),"history",gson.toJson(his));

                    }
                    else{

                        latitude.add(la);
                        longitude.add(lo);
                        city.add(ci);
                        country.add(co);

                        History his=new History(latitude,longitude,city,country);

                        DbHandler.putString(getContext(),"history",gson.toJson(his));

                    }

                    Intent intentAction = new Intent(getContext(),NavigationActivity.class);
                    intentAction.putExtra("action","actionStop");
                    PendingIntent pIntent = PendingIntent.getActivity(getContext(),1,intentAction,PendingIntent.FLAG_UPDATE_CURRENT);


                    nMN = (NotificationManager) getContext().getSystemService(NOTIFICATION_SERVICE);
                    noti = new Notification.Builder(getContext())
                            .setContentTitle(getResources().getString(R.string.new_mock))
                            .setContentText(ci+" ,"+co+"\n"+la+","+lo)
                            .setSmallIcon(R.drawable.ic_map_black_24dp)
                            .setAutoCancel(true)
                            .addAction(R.drawable.ic_stop_black_24dp,"Stop",pIntent)
                            .setOngoing(true)
                            .build();

                    noti.flags = Notification.FLAG_NO_CLEAR;
                    nMN.notify(0, noti);



                    Toast.makeText(getContext(),getResources().getString(R.string.check_internet2),Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else{
                    flg=0;
                    nMN.cancelAll();
                    setCurrent();
                    //Toast.makeText(getContext(),String.valueOf(curr_lati),Toast.LENGTH_LONG).show();
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
                    fab.setColorFilter(getResources().getColor(R.color.white));
                }
            }
        });

        add_to_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!location.getText().toString().equals("")) {

                    if (DbHandler.contains(getContext(), "favourites")) {
                        favourites = gson.fromJson(DbHandler.getString(getContext(), "favourites", "{}"), History.class);
                        latitude = favourites.getLatitude();
                        longitude = favourites.getLongitude();
                        city = favourites.getCity();
                        country = favourites.getCountry();

                        latitude.add(la);
                        longitude.add(lo);
                        city.add(ci);
                        country.add(co);

                        History fav = new History(latitude, longitude, city, country);

                        DbHandler.putString(getContext(), "favourites", gson.toJson(fav));

                    } else {

                        latitude.add(la);
                        longitude.add(lo);
                        city.add(ci);
                        country.add(co);

                        History fav = new History(latitude, longitude, city, country);

                        DbHandler.putString(getContext(), "favourites", gson.toJson(fav));

                    }


                    Toast.makeText(getContext(), getResources().getString(R.string.added_to_fav), Toast.LENGTH_SHORT).show();
                    add_to_fav.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                    add_to_fav.setColorFilter(getResources().getColor(R.color.white));
                }
                else{
                    Toast.makeText(getContext(), getResources().getString(R.string.no_loc), Toast.LENGTH_SHORT).show();
                }
               // setLocale("hi");
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(location.getText().toString().equals("")){
                    Toast.makeText(getContext(),getResources().getString(R.string.no_loc),Toast.LENGTH_LONG).show();
                }
                else{
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT,
                            "http://maps.google.com/?q="+String.valueOf(lati)+","+String.valueOf(longi));
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                }
            }
        });


        search_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(getActivity());
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });

        return parentView;
    }

    public void setMarker(){

        final Geocoder geocoder=new Geocoder(getContext(), Locale.getDefault());
        final GPSTracker gpsTracker=new GPSTracker(getContext());

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                googleMap.setMyLocationEnabled(true);

                LatLng loc = new LatLng(lati,longi);

                if(map_type.equals("normal")) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
                if(map_type.equals("terrain")) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                }
                if(map_type.equals("satellite")) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }
                if(map_type.equals("hybrid")) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }
                if(map_type.equals("none")) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                }

                googleMap.addMarker(new MarkerOptions().position(loc).title(getResources().getString(R.string.marker_title)).snippet(getResources().getString(R.string.marker_description)));

                List<Address> addresses2;
                try {
                    addresses2 = geocoder.getFromLocation(Double.valueOf(lati), Double.valueOf(longi), 1);
                    if (addresses2.size() != 0) {
                        ci=addresses2.get(0).getLocality();
                        co=addresses2.get(0).getCountryName();
                        location.setText(addresses2.get(0).getLocality() + " " + addresses2.get(0).getCountryName() + "\n" + String.valueOf(lati) + " , " + String.valueOf(longi));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(),getResources().getString(R.string.check_internet),Toast.LENGTH_SHORT).show();

                }

                CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        googleMap.clear();
                        googleMap.addMarker(new MarkerOptions().position(latLng).title(getResources().getString(R.string.marker_title)).snippet(getResources().getString(R.string.marker_description)));

                        lati=latLng.latitude;
                        longi=latLng.longitude;

                        la=lati;
                        lo=longi;
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                        List<Address> addresses;
                        try {
                            addresses = geocoder.getFromLocation(Double.valueOf(lati), Double.valueOf(longi), 1);
                            if (addresses.size() != 0) {
                                location.setText(addresses.get(0).getLocality()+" "+addresses.get(0).getCountryName()+"\n"+String.valueOf(lati)+" , "+String.valueOf(longi));

                                ci=addresses.get(0).getLocality();
                                co=addresses.get(0).getCountryName();
                                la=lati;
                                lo=longi;

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            location.setText(String.valueOf(lati)+" , "+String.valueOf(longi));
                            Toast.makeText(getContext(),getResources().getString(R.string.check_internet),Toast.LENGTH_SHORT).show();
                        }


                    }
                });
                googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {

                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {

                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        lati=marker.getPosition().latitude;
                        longi=marker.getPosition().longitude;


                    }
                });

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                Toast.makeText(getContext(),String.valueOf(place.getName())+"\n"+String.valueOf(place.getLatLng().latitude)+" , "+String.valueOf(place.getLatLng().longitude),Toast.LENGTH_LONG).show();
                LatLng l=place.getLatLng();

                Intent intent = new Intent(getActivity(), NavigationActivity.class);
                DbHandler.putString(getActivity(), "go_to_specific_search", String.valueOf(String.valueOf(l.latitude) + "%" + String.valueOf(l.longitude)+"%"+String.valueOf(place.getName())+" "+String.valueOf(l.latitude)+" "+String.valueOf(l.longitude)));
                startActivity(intent);

                Log.i(TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        // mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }



    private void setMock(){
        LocationManager lm = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy( Criteria.ACCURACY_FINE );

        String mocLocationProvider = LocationManager.GPS_PROVIDER;//lm.getBestProvider( criteria, true );

        if ( mocLocationProvider == null ) {
            Toast.makeText(getContext(), getResources().getString(R.string.no_loc_provider), Toast.LENGTH_SHORT).show();
            return;
        }
        lm.addTestProvider(mocLocationProvider, false, false,
                false, false, true, true, true, 0, 5);
        lm.setTestProviderEnabled(mocLocationProvider, true);

        Location loc = new Location(mocLocationProvider);
        Location mockLocation = new Location(mocLocationProvider); // a string
        mockLocation.setLatitude(lati);  // double
        mockLocation.setLongitude(longi);
        mockLocation.setAltitude(loc.getAltitude());
        mockLocation.setTime(System.currentTimeMillis());
        mockLocation.setAccuracy(1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        }
        lm.setTestProviderLocation( mocLocationProvider, mockLocation);

    }

    private void setCurrent(){

        final Geocoder geocoder=new Geocoder(getContext(), Locale.getDefault());

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                googleMap.setMyLocationEnabled(true);

                googleMap.clear();

                LatLng loc = new LatLng(lati,longi);

                if(map_type.equals("normal")) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
                if(map_type.equals("terrain")) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                }
                if(map_type.equals("satellite")) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }
                if(map_type.equals("hybrid")) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }
                if(map_type.equals("none")) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                }

                googleMap.addMarker(new MarkerOptions().position(loc).title(getResources().getString(R.string.marker_title)).snippet(getResources().getString(R.string.marker_description)));

                List<Address> addresses2;
                try {
                    addresses2 = geocoder.getFromLocation(Double.valueOf(curr_lati), Double.valueOf(curr_longi), 1);
                    if (addresses2.size() != 0) {
                        ci=addresses2.get(0).getLocality();
                        co=addresses2.get(0).getCountryName();
                        location.setText(addresses2.get(0).getLocality() + " " + addresses2.get(0).getCountryName() + "\n" + String.valueOf(lati) + " , " + String.valueOf(longi));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(),getResources().getString(R.string.check_internet),Toast.LENGTH_SHORT).show();

                }

                CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }
        });
    }



//    public void setLocale(String lang) {
//
//        myLocale = new Locale(lang);
//        Resources res = getResources();
//        DisplayMetrics dm = res.getDisplayMetrics();
//        Configuration conf = res.getConfiguration();
//        conf.locale = myLocale;
//        res.updateConfiguration(conf, dm);
//        Intent refresh = new Intent(getContext(), NavigationActivity.class);
//        startActivity(refresh);
//    }
}

