package com.fakegps.optimustechproject.fakegps;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by satyam on 5/7/17.
 */

public class History {

    List<Double> latitude=new ArrayList<Double>(),longitude=new ArrayList<Double>();
    List<String> city=new ArrayList<String>(),country=new ArrayList<String>();



    public History( List<Double> latitude,  List<Double> longitude, List<String> city,  List<String> country)
    {
        this.latitude=latitude;
        this.longitude=longitude;
        this.city=city;
        this.country=country;

    }

    public List<String> getCity(){
        return city;
    }
    public List<String> getCountry(){
        return country;
    }
    public List<Double> getLatitude(){
        return latitude;
    }
    public List<Double> getLongitude(){
        return longitude;
    }
}
