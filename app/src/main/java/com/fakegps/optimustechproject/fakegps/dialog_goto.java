package com.fakegps.optimustechproject.fakegps;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by satyam on 5/7/17.
 */

public class dialog_goto extends DialogFragment {

    View view;
    EditText lati,longi;
    Button submit;
    double la=0.0,lo=0.0;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_goto, null);
        builder.setView(view);

        lati=(EditText)view.findViewById(R.id.lati_et);
        longi=(EditText)view.findViewById(R.id.longi_et);
        submit=(Button)view.findViewById(R.id.go_to);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!lati.getText().toString().equals("") & !longi.getText().toString().equals("")) {
                    la = Double.valueOf(lati.getText().toString());
                    lo = Double.valueOf(longi.getText().toString());

                    Intent intent = new Intent(getActivity(), NavigationActivity.class);
                    if(DbHandler.contains(getActivity(),"go_to_specific_search")){
                        DbHandler.remove(getActivity(),"go_to_specific_search");
                    }
                    DbHandler.putString(getActivity(), "go_to_specific", String.valueOf(String.valueOf(la) + "%" + String.valueOf(lo)));
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getActivity(),getResources().getString(R.string.toast_enter_lat_lon),Toast.LENGTH_LONG).show();
                }

            }
        });



        return builder.create();
    }
}
