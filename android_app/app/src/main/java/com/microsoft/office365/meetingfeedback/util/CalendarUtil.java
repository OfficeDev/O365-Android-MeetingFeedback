/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.meetingfeedback.util;

import com.microsoft.office365.meetingfeedback.model.DataStore;
import com.microsoft.office365.meetingfeedback.model.meeting.DateRange;
import com.microsoft.office365.meetingfeedback.model.meeting.EventDecorator;
import com.microsoft.office365.meetingfeedback.model.meeting.EventGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarUtil {

    public static final int NUM_PAGES_IN_CALENDAR = 4;
    public static final int DAYS_PER_PAGE = 7;

    private static DateRange buildDateRangeForPage(int page) {
        int endOffset = page * DAYS_PER_PAGE;
        int startOffset = (page + 1) * DAYS_PER_PAGE;
        Calendar end = Calendar.getInstance();
        end.add(Calendar.DAY_OF_MONTH, -endOffset);
        Calendar start = Calendar.getInstance();
        start.add(Calendar.DAY_OF_MONTH, -startOffset);
        return new DateRange(start, end);
    }

    public static List<EventGroup> eventsAsEventDecorators(DataStore dataStore) {
        List<EventGroup> eventsGroupedByDateRanges = new ArrayList<>();
        List<com.microsoft.graph.models.extensions.Event> events = dataStore.getFilteredEvents();
        for (int i = 0; i < NUM_PAGES_IN_CALENDAR; i++) {
            DateRange dateRange = buildDateRangeForPage(i);
            List<EventDecorator> eventDecorators = new ArrayList<>();
            for (com.microsoft.graph.models.extensions.Event event : events) {
                if (dateRange.isWithinRange(event)) {
                    eventDecorators.add(new EventDecorator(event, dataStore.getWebServiceRatingDataForEvent(event.iCalUId)));
                }
            }
            eventsGroupedByDateRanges.add(new EventGroup(i, dateRange, eventDecorators));
        }
        return eventsGroupedByDateRanges;
    }


}
