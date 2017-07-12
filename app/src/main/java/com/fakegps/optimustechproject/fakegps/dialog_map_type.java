package com.fakegps.optimustechproject.fakegps;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * Created by satyam on 8/7/17.
 */

public class dialog_map_type extends DialogFragment{

    View view;
    RadioGroup radioGroup;
    String map_type="normal";
    RadioButton btn1,btn2,btn3,btn4,btn5;
    Button set;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.fragment_map_type, null);
        builder.setView(view);

        radioGroup=(RadioGroup)view.findViewById(R.id.radio_group);
        btn1=(RadioButton)view.findViewById(R.id.normal);
        btn2=(RadioButton)view.findViewById(R.id.satellite);
        btn3=(RadioButton)view.findViewById(R.id.hybrid);
        btn4=(RadioButton)view.findViewById(R.id.terrain);
        btn5=(RadioButton)view.findViewById(R.id.none);

        set=(Button)view.findViewById(R.id.set);

        map_type=DbHandler.getString(getActivity(),"map_type","");
        radioGroup.clearCheck();

        if(map_type.equals("normal")){
            btn1.setChecked(true);
        }
        if(map_type.equals("satellite")){
            btn2.setChecked(true);
        }
        if(map_type.equals("hybrid")){
            btn3.setChecked(true);
        }
        if(map_type.equals("terrain")){
            btn4.setChecked(true);
        }
        if(map_type.equals("none")){
            btn5.setChecked(true);
        }


        btn1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    DbHandler.putString(getActivity(), "map_type", "normal");
                }
            }
        });
        btn2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    DbHandler.putString(getActivity(), "map_type", "satellite");
                }
            }
        });
        btn3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    DbHandler.putString(getActivity(), "map_type", "hybrid");
                }
            }
        });
        btn4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    DbHandler.putString(getActivity(), "map_type", "terrain");
                }
            }
        });
        btn5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    DbHandler.putString(getActivity(), "map_type", "none");
                }
            }
        });

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),NavigationActivity.class);
                startActivity(intent);
            }
        });
        return builder.create();
    }
}
