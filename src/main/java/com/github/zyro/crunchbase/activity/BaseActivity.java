package com.github.zyro.crunchbase.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import com.github.zyro.crunchbase.R;
import com.github.zyro.crunchbase.service.CrunchbaseClient;
import com.github.zyro.crunchbase.service.Preferences_;
import com.github.zyro.crunchbase.util.HeaderTransformer;
import com.googlecode.androidannotations.annotations.*;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

/** Common behaviour, encapsulated in an abstract Activity. */
@EActivity
@OptionsMenu(R.menu.menu)
public abstract class BaseActivity extends FragmentActivity
        implements PullToRefreshAttacher.OnRefreshListener{

    /** Access to remote data. */
    @Bean
    protected CrunchbaseClient client;

    /** Access to application preferences. */
    @Pref
    protected Preferences_ preferences;

    protected PullToRefreshAttacher attacher;

    /** Request common window features, BaseActivity has no view of its own. */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final PullToRefreshAttacher.Options options =
                new PullToRefreshAttacher.Options();
        options.headerTransformer = new HeaderTransformer();
        attacher = PullToRefreshAttacher.get(this, options);
    }

    /** Initialize the state of the options menu items based on stored prefs. */
    @AfterViews
    public void initMenuState() {
        getActionBar().setDisplayShowTitleEnabled(false);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        /*((Switch) findViewById(R.id.loadImagesSwitch))
                .setChecked(preferences.loadImages().get());*/
    }

    public void addRefreshableView(final View view) {
        attacher.addRefreshableView(view, this);
    }

    @Override
    public void onRefreshStarted(final View view) {
        refreshButton();
    }

    public void onRefreshCompleted() {
        attacher.setRefreshComplete();
    }

    public abstract void refresh();

    @OptionsItem(R.id.refreshButton)
    public void refreshButton() {
        attacher.setRefreshing(true);
        refresh();
    }

    /** Listener for the action bar search button. */
    @OptionsItem(R.id.searchButton)
    public void searchButton() {
        final LayoutInflater inflater = (LayoutInflater)
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.search_dialog, null);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_action_search_dark)
                .setTitle(R.string.search_title)
                .setView(view)
                .setPositiveButton(R.string.search,
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog,
                                                final int whichButton) {
                                dialog.dismiss();
                            }
                })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog,
                                                final int whichButton) {
                                dialog.dismiss();
                            }
                }).create();

        view.findViewById(R.id.searchQuery).setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(final View view,
                                              final boolean hasFocus) {
                        if(hasFocus) {
                            dialog.getWindow().setSoftInputMode(WindowManager.
                                LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        }
                    }
                });

        dialog.show();
    }

}