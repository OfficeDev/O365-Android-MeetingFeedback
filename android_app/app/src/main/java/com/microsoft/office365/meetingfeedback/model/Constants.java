/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.meetingfeedback.model;

public interface Constants {
    String AUTHORITY_URL = "https://login.microsoftonline.com/common";
    //Replace with your specific identifiers
    String CLIENT_ID = "<YOUR_CLIENT_ID>";
    String SERVICE_ENDPOINT = "<YOUR_WEBSERVICE_ENDPOINT>"; //The Ruby On Rails app in the webservice folder. format as http://10.0.2.2:3000
    String REVIEW_SENDER_ADDRESS = "<YOUR_REVIEW_SENDER_ADDRESS>";
    String[] Scopes = {"openid",
            "https://graph.microsoft.com/Calendars.ReadWrite",
            "https://graph.microsoft.com/Mail.ReadWrite",
            "https://graph.microsoft.com/Mail.Send",
            "https://graph.microsoft.com/User.Read"};
    String MICROSOFT_GRAPH_ENDPOINT = "https://graph.microsoft.com/v1.0/";
    String MICROSOFT_GRAPH_RESOURCE_ID = "https://graph.microsoft.com";
}
