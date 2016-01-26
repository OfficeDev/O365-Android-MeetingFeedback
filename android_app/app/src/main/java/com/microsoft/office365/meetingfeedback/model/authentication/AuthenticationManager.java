/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.meetingfeedback.model.authentication;

import android.webkit.CookieManager;

import com.microsoft.aad.adal.AuthenticationCallback;
import com.microsoft.aad.adal.AuthenticationContext;
import com.microsoft.aad.adal.AuthenticationResult;
import com.microsoft.aad.adal.AuthenticationResult.AuthenticationStatus;
import com.microsoft.aad.adal.PromptBehavior;
import com.microsoft.office365.meetingfeedback.model.Constants;
import com.microsoft.office365.meetingfeedback.model.DataStore;
import com.microsoft.office365.meetingfeedback.model.User;
import com.microsoft.office365.meetingfeedback.model.service.RatingServiceAlarmManager;
import com.microsoft.services.orc.resolvers.ADALDependencyResolver;

public class AuthenticationManager {

    private static final String TAG = "AuthenticationManager";
    private DataStore mDataStore;
    private AuthenticationContext mAuthenticationContext;
    private RatingServiceAlarmManager mAlarmManager;

    public AuthenticationManager(DataStore dataStore, AuthenticationContext authenticationContext,
                                 RatingServiceAlarmManager alarmManager) {
        mDataStore = dataStore;
        mAuthenticationContext = authenticationContext;
        mAlarmManager = alarmManager;
    }

    /**
     * Description: Calls AuthenticationContext.acquireToken(...) once to authenticate with
     * user's credentials and avoid interactive prompt on later calls.
     */
    public void authenticate(final AuthenticationCallback resultsCallback) {
        AuthenticationCallback<AuthenticationResult> authenticationCallback = new AuthenticationCallback<AuthenticationResult>() {
            @Override
            public void onSuccess(final AuthenticationResult authenticationResult) {
                if (authenticationResult != null && authenticationResult.getStatus() == AuthenticationStatus.Succeeded) {
                    //cache the user
                    User user = new User(authenticationResult.getUserInfo());
                    mDataStore.setUser(user);
                    resultsCallback.onSuccess(authenticationResult);
                }
            }

            @Override
            public void onError(Exception exception) {
                resultsCallback.onError(exception);
            }
        };

        mAuthenticationContext.acquireToken(
                Constants.OUTLOOK_RESOURCE_ID,
                Constants.CLIENT_ID,
                Constants.REDIRECT_URI,
                null,
                PromptBehavior.Auto,
                null,
                authenticationCallback
        );
    }

    public void signout() {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        mAuthenticationContext.getCache().removeAll();
        mDataStore.logout();
        mAlarmManager.cancelRatingService();
    }

}
