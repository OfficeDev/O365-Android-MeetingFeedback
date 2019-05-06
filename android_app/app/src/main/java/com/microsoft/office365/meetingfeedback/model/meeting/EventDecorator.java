/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.meetingfeedback.model.meeting;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.View;

import com.microsoft.office365.meetingfeedback.R;
import com.microsoft.office365.meetingfeedback.model.webservice.payload.MeetingServiceResponseData;
import com.microsoft.office365.meetingfeedback.util.FormatUtil;
import com.microsoft.office365.meetingfeedback.view.EventsRecyclerViewAdapter;

import java.io.Serializable;
import java.util.Locale;

public class EventDecorator implements Serializable {

    public final String mEventId;
    public final String mOrganizerName;
    public final String mSubject;
    public final String mFormattedDate;
    public final String mFormattedTime;
    public final MeetingServiceResponseData mServiceData;
    public final String mDescription;
    public final String mOrganizerAddress;
    public final boolean mIsOrganizer;

    public EventDecorator(com.microsoft.graph.models.extensions.Event event, MeetingServiceResponseData serviceData) {
        mEventId = event.iCalUId;
        mOrganizerName = event.organizer.emailAddress.name;
        mOrganizerAddress = event.organizer.emailAddress.address;
        mSubject = event.subject;
        mDescription = event.bodyPreview;
        mFormattedDate = FormatUtil.displayFormattedEventDate(event);
        mFormattedTime = FormatUtil.displayFormattedEventTime(event);
        mServiceData = serviceData;
        mIsOrganizer = event.isOrganizer;
    }

    public String formattedDateAndTime() {
        return String.format(Locale.US, "%s, %s", mFormattedDate, mFormattedTime);
    }

    public Spanned descriptionAsHtml() {
        return Html.fromHtml(mDescription.replaceAll("(?s)<!--.*?-->", ""));
    }

    public boolean hasAlreadyRated() {
        return mServiceData != null && mServiceData.mHasAlreadyRated;
    }

    public float yourRating() {
        if (mServiceData == null) {
            return 0.0f;
        }
        return mServiceData.mYourRating;
    }

    public boolean hasRatings() {
        return mServiceData != null && (mServiceData.mRatings != null && mServiceData.mRatings.size() > 0);
    }

    public int getRatingCount() {
        if (mServiceData == null || mServiceData.mRatings == null) {
            return 0;
        }
        return mServiceData.mRatings.size();
    }

    public Float getAvgRating() {
        if (mServiceData != null) {
            return mServiceData.mAverageRating;
        }
        return 0.0f;
    }

    public void setupEventDisplay(Context context, EventsRecyclerViewAdapter.EventsViewHolder viewHolder) {
        viewHolder.mRatingsCount.setVisibility(View.GONE);
        if (hasRatings()) {
            viewHolder.mRatingsCount.setVisibility(View.VISIBLE);
            String rating = context.getResources().getQuantityString(R.plurals.rating, getRatingCount());
            viewHolder.mRatingsCount.setText(String.format("of %d %s", getRatingCount(), rating));
            viewHolder.mEventRatingBar.setVisibility(View.VISIBLE);
            viewHolder.mEventRatingBar.setRating(getAvgRating());
            viewHolder.mRatedLabel.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mEventRatingBar.setVisibility(View.GONE);
            viewHolder.mRatedLabel.setVisibility(View.GONE);
        }
        if (hasAlreadyRated()) {
            viewHolder.mEventRatingButton.setVisibility(View.GONE);
        } else {
            viewHolder.mEventRatingButton.setVisibility(View.VISIBLE);
        }
        if (mIsOrganizer) {
            viewHolder.mEventRatingButton.setVisibility(View.GONE);
        }
    }
}
