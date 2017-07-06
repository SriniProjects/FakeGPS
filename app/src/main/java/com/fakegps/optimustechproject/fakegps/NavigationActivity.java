package com.fakegps.optimustechproject.fakegps;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Boolean doubleBackToExitPressedOnce=false;
    NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Fragment fragment=null;
        Class fragmentClass=null;
        fragmentClass=fragmet_location.class;
        FragmentManager fragmentManager = getSupportFragmentManager();
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        replaceFragment(fragment);
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {

            @Override
            public void onBackStackChanged() {
                Fragment f = getSupportFragmentManager().findFragmentById(R.id.flContent);
                if (f != null) {
                    updateTitleAndDrawer(f);
                }

            }
        });

    }

    private void updateTitleAndDrawer(Fragment fragment) {
        String fragClassName = fragment.getClass().getName();
        if(fragClassName.equals(fragmet_location.class.getName())){
            navigationView.getMenu().getItem(0).setChecked(true);
            setTitle("Set Mock Location");
        }
        if(fragClassName.equals(fragment_history.class.getName())){
            navigationView.getMenu().getItem(2).setChecked(true);
            setTitle("History");
        }
        if(fragClassName.equals(fragment_favourites.class.getName())){
            navigationView.getMenu().getItem(3).setChecked(true);
            setTitle("Favourites");
        }


    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if (!(getSupportFragmentManager().getBackStackEntryCount() == 1)) {
                super.onBackPressed();
            } else {

                if (doubleBackToExitPressedOnce) {
                    this.finishAffinity();
                    return;
                }
                this.doubleBackToExitPressedOnce = true;

                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Press once again to exit", Snackbar.LENGTH_SHORT);

                snackbar.show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }
        }

        if(DbHandler.contains(NavigationActivity.this,"go_to_specific")){
            DbHandler.remove(NavigationActivity.this,"go_to_specific");
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        Fragment fragment = null;
        Class fragmentClass=fragmet_location.class;

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch(id)
        {
            case R.id.mock:
                fragmentClass=fragmet_location.class;
                break;
            case R.id.history:
                fragmentClass=fragment_history.class;
                break;
            case R.id.favourites:
                fragmentClass=fragment_favourites.class;
                break;
            case R.id.go_to:
                dialog_goto dialog_goto = new dialog_goto();
                dialog_goto.show(getFragmentManager(),"Go To");
                break;

            default:fragmentClass=fragmet_location.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        replaceFragment(fragment);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        String fragmentTag = backStateName;
        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) { //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.flContent, fragment, fragmentTag);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }


}