/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.meetingfeedback.model;

public interface Constants {
    String AUTHORITY_URL = "https://login.microsoftonline.com/common";

    // Update these constants with the values for your application:
    String CLIENT_ID = "364e5e2a-259c-4d33-b350-75b168cd7c9f";
    String REDIRECT_URI = "msal364e5e2a-259c-4d33-b350-75b168cd7c9f://auth";
    String SERVICE_ENDPOINT = "localhost:3000";
    String REVIEW_SENDER_ADDRESS = "v-gendun@microsoft.com";
    String[] Scopes = {"https://graph.microsoft.com/User.Read"};
    String MICROSOFT_GRAPH_ENDPOINT = "https://graph.microsoft.com/v1.0/";
    String MICROSOFT_GRAPH_RESOURCE_ID = "https://graph.microsoft.com/";
}
