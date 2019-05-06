/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.meetingfeedback.model.outlook;

import com.microsoft.graph.authentication.MSALAuthenticationProvider;
import com.microsoft.graph.concurrency.ICallback;
import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.models.extensions.Event;
import com.microsoft.graph.models.extensions.IGraphServiceClient;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.extensions.GraphServiceClient;
import com.microsoft.graph.requests.extensions.IEventCollectionPage;
import com.microsoft.office365.meetingfeedback.model.DataStore;
import com.microsoft.office365.meetingfeedback.model.meeting.DateRange;
import com.microsoft.office365.meetingfeedback.util.FormatUtil;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class CalendarService {

    private IGraphServiceClient graphClient;
    private CalendarInterface mCalendarClient;
    private DataStore mDataStore;
    private List<Event> mAccumulatedEvents;

    public CalendarService(MSALAuthenticationProvider authenticationManager, DataStore dataStore) {
        mDataStore = dataStore;
        graphClient = GraphServiceClient
                .builder()
                .authenticationProvider(authenticationManager)
                .buildClient();
    }

    public void fetchEvents(final ICallback<IEventCollectionPage> callback) {
        getEvents(new ICallback<IEventCollectionPage>() {
            @Override
            public void success(IEventCollectionPage iEventCollectionPage) {
                mAccumulatedEvents = iEventCollectionPage.getCurrentPage();
                mDataStore.setEvents(mAccumulatedEvents);
                if(null != callback) {
                    callback.success(iEventCollectionPage);
                }
            }

            @Override
            public void failure(ClientException error) {
                if(null != callback) {
                    callback.failure(error);
                }
            }
        });
    }

    private void getEvents(ICallback<IEventCollectionPage> callback) {
        DateRange dateRange = getDateRange();
        String startDateTime = FormatUtil.convertDateToUrlString(dateRange.mStart.getTime());
        String endDateTime = FormatUtil.convertDateToUrlString(dateRange.mEnd.getTime());
        String preferredTimezone = "outlook.timezone=\"" + TimeZone.getDefault().getID() + "\"";

        List<Option> options = new java.util.LinkedList<>();
        options.add(new HeaderOption("Content-type", "application/json"));
        options.add(new HeaderOption("Prefer", preferredTimezone));
        options.add(new QueryOption("startdatetime", startDateTime));
        options.add(new QueryOption("enddatetime", endDateTime));
        options.add(new QueryOption("orderby", "start/datetime desc"));
        options.add(new QueryOption("top", "150"));

        graphClient.me().events().buildRequest(options)
                .select("subject,start,end,organizer,isOrganizer,attendees,bodyPreview,iCalUID")
                .get(callback);
    }

    private DateRange getDateRange() {
        Calendar end = Calendar.getInstance();
        Calendar start = Calendar.getInstance();
        start.add(Calendar.DAY_OF_MONTH, -28);
        return new DateRange(start, end);
    }
}
