package com.fakegps.optimustechproject.fakegps;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by satyam on 25/7/17.
 */

public class dialog_about_us  extends DialogFragment {
    View view;
    TextView version;

    ////////////// ABOUT US DIALOG FRAGMENT /////////

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_about_us, null);
        builder.setView(view);

//        version=(TextView)view.findViewById(R.id.version_num);
//        String versionName = "";
//        int versionCode = -1;
//        try {
//            PackageInfo packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
//            versionName = packageInfo.versionName;
//            versionCode = packageInfo.versionCode;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        version.setText("v "+versionName);

        return builder.create();
    }
}
