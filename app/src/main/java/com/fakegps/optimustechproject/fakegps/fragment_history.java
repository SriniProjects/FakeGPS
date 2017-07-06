package com.fakegps.optimustechproject.fakegps;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

/**
 * Created by satyam on 5/7/17.
 */

public class fragment_history extends Fragment {

    View parentView;
    TextView no_mock;
    RecyclerView recyclerView;
    History history;
    Gson gson=new Gson();

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_history, container, false);

        no_mock=(TextView)parentView.findViewById(R.id.no_mock);
        recyclerView=(RecyclerView)parentView.findViewById(R.id.recycler);

        if(DbHandler.contains(getContext(),"history")){

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setHasFixedSize(true);
            adapter_history adapter_history=new adapter_history(gson.fromJson(DbHandler.getString(getContext(),"history","{}"),History.class));
            recyclerView.setAdapter(adapter_history);
        }
        else{
            recyclerView.setVisibility(View.GONE);
            no_mock.setVisibility(View.VISIBLE);
        }

        return parentView;
    }
}
