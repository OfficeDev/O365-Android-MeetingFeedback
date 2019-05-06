/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.meetingfeedback;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.aad.adal.AuthenticationCallback;
import com.microsoft.aad.adal.AuthenticationResult;
import com.microsoft.aad.adal.IWindowComponent;
import com.microsoft.identity.client.IAccount;

import java.util.List;

public class ConnectActivity extends BaseActivity implements AuthenticationCallback<AuthenticationResult>, IWindowComponent {

    private Button mConnectButton;
    private ProgressBar mConnectProgressBar;
    private TextView mDescriptionTextView;
    private String TAG = "ConnectActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        mConnectButton = findViewById(R.id.activity_connect_connect_button);
        mConnectProgressBar = findViewById(R.id.activity_connect_progress_bar);
        mDescriptionTextView = findViewById(R.id.activity_connect_description_text_view);
        mConnectButton.setVisibility(View.VISIBLE);
        List<IAccount> accounts = mAuthenticationManager.getAccounts();
        //mConnectButton.setOnClickListener(new View.OnClickListener() {
        //   @Override
        //   public void onClick(View v) {
        //        mAuthenticationManager.authenticate(ConnectActivity.this);
        //        mAuthenticationManager.authenticate(ConnectActivity.this);
        //    }
        //});
    }

    @Override
    public void onSuccess(AuthenticationResult authenticationResult) {
        Log.d(TAG, "authentication success!");

        finish();
        Intent intent = new Intent(ConnectActivity.this, CalendarActivity.class);
        startActivity(intent);
    }

    @Override
    public void onError(Exception e) {
        Log.e(TAG, "authentication failure! Exception: " + e);
        mConnectButton.setVisibility(View.VISIBLE);
        mConnectProgressBar.setVisibility(View.GONE);
        mDescriptionTextView.setText(R.string.connect_text_error);
        mDescriptionTextView.setVisibility(View.VISIBLE);
        Toast.makeText(
                ConnectActivity.this,
                R.string.connect_toast_text_error,
                Toast.LENGTH_LONG).show();

    }

    // Add this function in your Authenticating activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        msalAuthenticationProvider.handleInteractiveRequestRedirect(requestCode, resultCode, data);
    }
}
