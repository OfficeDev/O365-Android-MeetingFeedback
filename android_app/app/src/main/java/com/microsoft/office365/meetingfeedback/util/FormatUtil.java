/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.meetingfeedback.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatUtil {
    private static final String RAW_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS";

    public static String displayFormattedEventDate(com.microsoft.graph.models.extensions.Event event) {
        return formatDate(convertStringToDate(event.start.dateTime));
    }

    public static String formatDate(Date eventDate) {
        return new SimpleDateFormat("MMMM dd", Locale.getDefault()).format(eventDate);
    }
    public static String formatDateCompact(Date eventDate) {
        return new SimpleDateFormat("MMM dd", Locale.getDefault()).format(eventDate);
    }

    public static String displayFormattedEventTime(com.microsoft.graph.models.extensions.Event event) {
        Date eventDate = convertStringToDate(event.start.dateTime);
        Date eventDateEnd = convertStringToDate(event.end.dateTime);
        String formattedStartTime = new SimpleDateFormat("HH:mm", Locale.US).format(eventDate);
        String formattedEndTime = new SimpleDateFormat("HH:mm", Locale.US).format(eventDateEnd);
        return String.format("%s - %s", formattedStartTime, formattedEndTime);
    }

    public static Date convertStringToDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat(RAW_DATE_FORMAT, Locale.getDefault());
        try {
            return format.parse(date);
        } catch(ParseException pe) {
            throw new IllegalArgumentException(pe);
        }
    }

    public static String convertDateToUrlString(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(RAW_DATE_FORMAT, Locale.getDefault());
        return simpleDateFormat.format(date);
    }

}
