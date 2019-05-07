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

import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.AuthenticationResult;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.office365.meetingfeedback.model.Constants;
import com.microsoft.office365.meetingfeedback.model.User;

import java.util.List;

public class ConnectActivity extends BaseActivity implements AuthenticationCallback {

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
        mConnectButton.setVisibility(View.GONE);
        authenticate();
        mConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticate();
            }
        });
    }

    private void authenticate() {
        List<IAccount> accounts = publicClientApplication.getAccounts();
        if (!accounts.isEmpty()) {
            IAccount iAccount = accounts.get(0);
            publicClientApplication.acquireTokenSilentAsync(Constants.Scopes,
                    iAccount, Constants.AUTHORITY_URL, true, this);
        } else {

            publicClientApplication.acquireToken(this, Constants.Scopes, this);
        }
    }

    @Override
    public void onSuccess(AuthenticationResult authenticationResult) {
        Log.d(TAG, "authentication success!");
        User user = new User(authenticationResult.getAccount());
        mDataStore.setUser(user);
        finish();
        Intent intent = new Intent(ConnectActivity.this, CalendarActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onError(MsalException e) {
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
