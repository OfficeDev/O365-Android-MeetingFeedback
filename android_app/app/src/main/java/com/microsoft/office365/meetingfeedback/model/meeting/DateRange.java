/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.meetingfeedback.model.meeting;

import com.microsoft.office365.meetingfeedback.util.FormatUtil;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class DateRange implements Serializable {
    public Calendar mStart;
    public Calendar mEnd;

    public DateRange(Calendar start, Calendar end) {
        mStart = start;
        mEnd = end;
    }

    public boolean isWithinRange(com.microsoft.graph.models.extensions.Event event) {
        Date dateToCompare = FormatUtil.convertStringToDate(event.start.dateTime);
        Date start = mStart.getTime();
        Date end = mEnd.getTime();
        return !(dateToCompare.after(end) || dateToCompare.before(start));
    }

    public String getRangeAsFormattedString() {
        String start = FormatUtil.formatDate(mStart.getTime());
        String end = FormatUtil.formatDate(mEnd.getTime());
        return String.format("%s - %s", end, start);
    }
}
