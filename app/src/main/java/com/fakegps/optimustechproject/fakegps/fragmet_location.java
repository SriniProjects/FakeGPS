package com.fakegps.optimustechproject.fakegps;

import android.Manifest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_location, container, false);

        fab=(FloatingActionButton)parentView.findViewById(R.id.fab);
        add_to_fav=(ImageView)parentView.findViewById(R.id.add_to_fav);
        share=(ImageView)parentView.findViewById(R.id.share);
        location=(TextView)parentView.findViewById(R.id.location);
        search_ll=(LinearLayout)getActivity().findViewById(R.id.search_ll);

        progress=new ProgressDialog(getContext());
        progress.setMessage("Locating...");
        progress.setCancelable(false);
        progress.setIndeterminate(true);
       // progress.show();

        mMapView = (MapView) parentView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getContext());
        } catch (Exception e) {
            e.printStackTrace();
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
        }

       else if(DbHandler.contains(getActivity(),"go_to_specific")){

            l=DbHandler.getString(getActivity(),"go_to_specific","").split("%");
            lati=Double.valueOf(l[0]);
            longi=Double.valueOf(l[1]);

            location.setText("");

            la=lati;
            lo=longi;

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

                        location.setText(addresses.get(0).getLocality() + " " + addresses.get(0).getCountryName() + "\n" + String.valueOf(lati) + " " + String.valueOf(longi));

                        //progress.setVisibility(View.GONE);
                    } else {
                        //Toast.makeText(getContext(), "Unable to get location.. Try again later ", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    location.setText("");
                   // Toast.makeText(getContext(), "Unable to get location.. Try again later ", Toast.LENGTH_LONG).show();

                    e.printStackTrace();

                }
            } else {
                progress.dismiss();
                //progress.setVisibility(View.GONE);

                gpsTracker.showSettingsAlert();
            }
        }


        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                googleMap.setMyLocationEnabled(true);

                LatLng loc = new LatLng(lati,longi);
                //Toast.makeText(getContext(),lati.toString()+longi.toString(),Toast.LENGTH_SHORT).show();
                googleMap.addMarker(new MarkerOptions().position(loc).title("Marker Title").snippet("Marker Description"));

                List<Address> addresses2;
                try {
                    addresses2 = geocoder.getFromLocation(Double.valueOf(lati), Double.valueOf(longi), 1);
                    if (addresses2.size() != 0) {
                        ci=addresses2.get(0).getLocality();
                        co=addresses2.get(0).getCountryName();
                        location.setText(addresses2.get(0).getLocality() + " " + addresses2.get(0).getCountryName() + "\n" + String.valueOf(lati) + " " + String.valueOf(longi));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                   // location.setText("");
                    Toast.makeText(getContext(),"Please check your internet connection for more accuracy",Toast.LENGTH_SHORT).show();

                }

                CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        googleMap.clear();
                        googleMap.addMarker(new MarkerOptions().position(latLng).title("Marker Title").snippet("Marker Description"));

                        lati=latLng.latitude;
                        longi=latLng.longitude;
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                        List<Address> addresses;
                        try {
                            addresses = geocoder.getFromLocation(Double.valueOf(lati), Double.valueOf(longi), 1);
                            if (addresses.size() != 0) {
                                location.setText(addresses.get(0).getLocality()+" "+addresses.get(0).getCountryName()+"\n"+String.valueOf(lati)+" "+String.valueOf(longi));

                                ci=addresses.get(0).getLocality();
                                co=addresses.get(0).getCountryName();
                                la=lati;
                                lo=longi;

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            location.setText("");
                            Toast.makeText(getContext(),"Please check your internet connection for more accuracy",Toast.LENGTH_SHORT).show();
                        }

//                        if(DbHandler.contains(getContext(),"favourites")) {
//                            favourites = gson.fromJson(DbHandler.getString(getContext(), "favourites", "{}"), History.class);
//                            latitude2 = favourites.getLatitude();
//                            longitude2 = favourites.getLongitude();
//                            city2 = favourites.getCity();
//                            country2 = favourites.getCountry();
//
//                            if(latitude.contains(lati) && longitude.contains(longi)){
//                                add_to_fav.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
//                                add_to_fav.setColorFilter(getResources().getColor(R.color.white));
//                                //  flg=1;
//                            }
//                            else{
//                                //flg=0;
//                                add_to_fav.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_border_black_24dp));
//                                add_to_fav.setColorFilter(getResources().getColor(R.color.white));
//
//                            }
//
//                        }

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


                        //Toast.makeText(getContext(),String.valueOf(marker.getPosition().latitude+" "+marker.getPosition().longitude),Toast.LENGTH_SHORT).show();
                    }
                });

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

                    Toast.makeText(getContext(),"Location set successfully",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else{
                    flg=0;
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
                    fab.setColorFilter(getResources().getColor(R.color.white));
                   // setCurrent();

                }
            }
        });

        add_to_fav.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {

                                              if(!location.getText().toString().equals("")) {
                                                  // History his=new History(lati,longi,addresses.get(0).getLocality(),addresses.get(0).getCountryName());
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


                                                  Toast.makeText(getContext(), "Place added to favourites", Toast.LENGTH_SHORT).show();
                                                  add_to_fav.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                                                  add_to_fav.setColorFilter(getResources().getColor(R.color.white));
                                              }
                                              else{
                                                  Toast.makeText(getContext(), "Unable to get location details.Please check your internet connection", Toast.LENGTH_SHORT).show();
                                              }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(location.getText().toString().equals("")){
                    Toast.makeText(getContext(),"No location set",Toast.LENGTH_LONG).show();
                }
                else{
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT,
                            "Hey check out my new location \n"+location.getText().toString());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                Toast.makeText(getContext(),String.valueOf(place.getName())+String.valueOf(place.getLatLng().latitude)+String.valueOf(place.getLatLng().longitude),Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "No location provider found!", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(getContext(), "Working", Toast.LENGTH_SHORT).show();
    }
}


