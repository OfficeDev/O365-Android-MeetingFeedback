/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.meetingfeedback.model;

import com.microsoft.graph.models.extensions.Event;
import com.microsoft.office365.meetingfeedback.model.meeting.EventGroup;
import com.microsoft.office365.meetingfeedback.model.webservice.payload.MeetingServiceResponseData;
import com.microsoft.office365.meetingfeedback.util.CalendarUtil;
import com.microsoft.office365.meetingfeedback.util.SharedPrefsUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStore {

    List<Event> mEvents = new ArrayList<>();
    Map<String, Event> mEventsMap = new HashMap<>();
    Map<String, MeetingServiceResponseData> mMeetingServiceData = new HashMap<>();

    private User mUser;
    private Map<String, Double> mMyMeetings;
    private SharedPrefsUtil mSharedPrefsUtil;
    private EventFilter mFilter;

    public DataStore(SharedPrefsUtil sharedPrefsUtil) {
        mSharedPrefsUtil = sharedPrefsUtil;
    }

    public List<EventGroup> getEventGroups() {
        return CalendarUtil.eventsAsEventDecorators(this);
    }

    public List<Event> getEvents() {
        if (mEvents == null) {
            return new ArrayList<>();
        }
        return mEvents;
    }

    public void setMeetingServiceResponseData(List<MeetingServiceResponseData> meetingServiceResponseDatas) {
        for (MeetingServiceResponseData serviceResponseData : meetingServiceResponseDatas) {
            mMeetingServiceData.put(serviceResponseData.mEventId, serviceResponseData);
        }
    }

    public void updateMeetingServiceResponse(MeetingServiceResponseData meetingServiceResponseData) {
        mMeetingServiceData.put(meetingServiceResponseData.mEventId, meetingServiceResponseData);
    }

    public MeetingServiceResponseData getWebServiceRatingDataForEvent(String eventId) {
        return mMeetingServiceData.get(eventId);
    }

    public void setEvents(List<Event> events) {
        mEvents = events;
        for (Event event : events) {
            mEventsMap.put(event.iCalUId, event);
        }
    }

    public Event getEventById(String id) {
        return mEventsMap.get(id);
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
        if (null != mUser) {
            mSharedPrefsUtil.setSavedUserId(mUser.getUserId());
        } else {
            mSharedPrefsUtil.setSavedUserId(null);
        }
    }

    public boolean isUserLoggedIn() {
        return mSharedPrefsUtil.getSavedUserId() != null;
    }

    public String getUserId() {
        return mSharedPrefsUtil.getSavedUserId();
    }

    public String getUsername() {
        return mUser != null ? mUser.getUsername() : null;
    }

    public void setMyMeetings(Map<String, Double> myMeetings) {
        mMyMeetings = myMeetings;
        mSharedPrefsUtil.setSavedMeetingResults(mMyMeetings);
    }

    public void logout() {
        mUser = null;
        mEvents = null;
        mEvents = null;
        mMyMeetings = null;
    }

    public List<Event> getFilteredEvents() {
        List<Event> filteredEvents = new ArrayList<>();
        for (Event event : getEvents()) {
            boolean condition = event.isOrganizer;
            condition = mFilter.equals(EventFilter.MY_MEETINGS) == condition;
            if (condition) {
                filteredEvents.add(event);
            }
        }
        return filteredEvents;
    }

    public void setEventFilter(EventFilter eventFilter) {
        mFilter = eventFilter;
    }
}
