/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.meetingfeedback.model.outlook.payload;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EventWrapper {

    @SerializedName("@odata.context")
    public String mODataContext;

    @SerializedName("value")
    public List<Event> mEvents;
}