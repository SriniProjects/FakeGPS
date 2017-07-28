package com.fakegps.optimustechproject.fakegps;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Locale;

/**
 * Created by satyam on 16/7/17.
 */

public class dialog_lang extends DialogFragment {

    View view;
    RadioGroup radioGroup;
    String lang="persian";
    RadioButton btn1,btn2;
    Button set;
    Locale myLocale;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_lang, null);
        builder.setView(view);

        radioGroup=(RadioGroup)view.findViewById(R.id.radio_group);
        btn1=(RadioButton)view.findViewById(R.id.english);
        btn2=(RadioButton)view.findViewById(R.id.persian);

        set=(Button)view.findViewById(R.id.set);

        lang=DbHandler.getString(getActivity(),"language","");
        radioGroup.clearCheck();

        if(lang.equals("english")){
            btn1.setChecked(true);
        }
        if(lang.equals("persian")){
            btn2.setChecked(true);
        }


        btn1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    DbHandler.putString(getActivity(), "language", "english");
                }
            }
        });
        btn2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    DbHandler.putString(getActivity(), "language", "persian");
                }
            }
        });

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn1.isChecked()){
                    changeLang("en");
                }
                else{
                    changeLang("fa");
                }

            }
        });
        return builder.create();
    }

    public void changeLang(String lang)
    {
        if (lang.equalsIgnoreCase(""))
            return;
        myLocale = new Locale(lang);
        //saveLocale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getActivity().getBaseContext().getResources().updateConfiguration(config, getActivity().getBaseContext().getResources().getDisplayMetrics());
        //updateTexts();
        Intent intent=new Intent(getActivity(),NavigationActivity.class);
        startActivity(intent);
        //dismiss();
    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (myLocale != null){
            newConfig.locale = myLocale;
            Locale.setDefault(myLocale);
            getActivity().getBaseContext().getResources().updateConfiguration(newConfig,getActivity(). getBaseContext().getResources().getDisplayMetrics());
        }
    }
}
