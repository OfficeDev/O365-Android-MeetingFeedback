/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.meetingfeedback.model;

import com.microsoft.identity.client.IAccount;

public class User {

    private String mUserId;
    private String mUsername;


    public User(IAccount userInfo) {
        mUserId = userInfo.getAccountIdentifier().getIdentifier();
        mUsername = userInfo.getUsername();
    }

    public String getUserId() {
        return mUserId;
    }

    public String getUsername() {
        return mUsername;
    }
}
