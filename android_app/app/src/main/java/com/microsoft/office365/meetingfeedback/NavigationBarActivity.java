/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.meetingfeedback;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.microsoft.identity.client.IAccount;
import com.microsoft.office365.meetingfeedback.model.User;

import java.util.List;


public abstract class NavigationBarActivity extends BaseActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private TextView mUsernameTextView;
    private TextView mEmailTextView;
    private Button mSignOutButton;

    protected abstract int getActivityLayoutId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        if (mDataStore.isUserLoggedIn()) {
            setUpNavigationDrawer();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if (mDataStore.isUserLoggedIn()) {
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggle.
        if (mDataStore.isUserLoggedIn()) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    private void setUpNavigationDrawer() {
        Toolbar toolbar = findViewById(R.id.action_bar);
        FrameLayout frameLayout = findViewById(R.id.activity_navigation_drawer_activity_container);
        LayoutInflater.from(this).inflate(getActivityLayoutId(), frameLayout);

        mDrawerLayout = findViewById(R.id.activity_navigation_drawer_navigation_drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_loading) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mUsernameTextView = findViewById(R.id.activity_navigation_drawer_username_text_view);
        mEmailTextView = findViewById(R.id.activity_navigation_drawer_email_text_view);
        mSignOutButton = findViewById(R.id.activity_navigation_drawer_sign_out_button);
        mSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<IAccount> accounts = publicClientApplication.getAccounts();
                if (!accounts.isEmpty()) {
                    for (final IAccount account : accounts) {
                        publicClientApplication.removeAccount(account);
                        //Clear objects that hold user state
                        mDataStore.setUser(null);
                        finish();

                        Intent intent = new Intent(NavigationBarActivity.this, ConnectActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }

            }
        });
        User user = mDataStore.getUser();
        mEmailTextView.setText(user.getUserId());
        mUsernameTextView.setText(user.getUsername());
    }

}
