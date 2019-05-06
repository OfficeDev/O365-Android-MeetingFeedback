/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.meetingfeedback.model.authentication;

import android.content.Context;

import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.office365.meetingfeedback.model.Constants;

public class AuthenticationContextBuilder {

    public static PublicClientApplication newInstance(Context context) {
        return new PublicClientApplication(context, Constants.CLIENT_ID);
    }
}
