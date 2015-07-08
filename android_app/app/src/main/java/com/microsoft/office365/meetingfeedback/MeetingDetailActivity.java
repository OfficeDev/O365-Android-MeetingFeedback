/*
 *  Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license. See full license at the bottom of this file.
 */
package com.microsoft.office365.meetingfeedback;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.office365.meetingfeedback.event.SendRatingEvent;
import com.microsoft.office365.meetingfeedback.event.SendRatingSuccessEvent;
import com.microsoft.office365.meetingfeedback.event.UserRatingAddedSuccessEvent;
import com.microsoft.office365.meetingfeedback.event.UserRatingsLoadedSuccessEvent;
import com.microsoft.office365.meetingfeedback.model.meeting.EventDecorator;
import com.microsoft.office365.meetingfeedback.model.webservice.RatingServiceManager;
import com.microsoft.office365.meetingfeedback.model.webservice.payload.MeetingServiceResponseData;
import com.microsoft.office365.meetingfeedback.view.RatingDialogFragment;
import com.microsoft.office365.meetingfeedback.view.RatingsRecyclerViewAdapter;
import com.microsoft.office365.meetingfeedback.view.ShowRatingDialogEvent;
import com.microsoft.outlookservices.Event;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class MeetingDetailActivity extends NavigationBarActivity {

    @Inject
    RatingServiceManager mRatingServiceManager;

    public static final String EVENT_ID_EXTRA = "EVENT_ID_EXTRA";
    private static final String TAG = "MeetingDetailActivity";
    private String mEventId;

    private Button mEventRatingButton;
    private TextView mExistingRating;
    private TextView mMeetingDetailEventDate;
    private TextView mActivityTitle;
    private RecyclerView mMeetingRatings;
    private TextView mMeetingOrganizer;

    private Event mEvent;
    private EventDecorator mEventDecorator;

    private TextView mEventDescription;
    private LinearLayout mRatingHeader;
    private LinearLayout mRatingNone;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout mRatingArea;

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activty_meeting_detail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventId = getIntent().getStringExtra(EVENT_ID_EXTRA);
        mEvent = mDataStore.getEventById(mEventId);
        mEventDecorator = new EventDecorator(mEvent, mDataStore.getWebServiceRatingDataForEvent(mEventId));
        mMeetingOrganizer = (TextView) findViewById(R.id.activity_meeting_detail_organizer);
        mMeetingDetailEventDate = (TextView) findViewById(R.id.activity_meeting_detail_event_date);
        mMeetingRatings = (RecyclerView) findViewById(R.id.activity_meeting_detail_recycler_view);
        mEventRatingButton = (Button) findViewById(R.id.rate_button);
        mExistingRating = (TextView) findViewById(R.id.event_existing_rating);
        mActivityTitle = (TextView) findViewById(R.id.activity_meeting_detail_title);
        mEventDescription = (TextView) findViewById(R.id.activity_meeting_detail_description);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_meeting_detail_swipe_refresh_layout);
        mRatingArea = (LinearLayout) findViewById(R.id.rating_area);
        mRatingHeader = (LinearLayout) findViewById(R.id.activity_meeting_detail_rating_header);
        mRatingNone = (LinearLayout) findViewById(R.id.activity_meeting_detail_rating_none);

        mMeetingOrganizer.setText(mEvent.getOrganizer().getEmailAddress().getName());
        mActivityTitle.setText(mEventDecorator.mSubject);
        mEventDescription.setText(mEventDecorator.descriptionAsHtml());
        mMeetingDetailEventDate.setText(mEventDecorator.formattedDateAndTime());

        mEventRatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new ShowRatingDialogEvent(mEventId));
            }
        });
        setupRecyclerView();
        setupRatingAdapter();
        setupRatingButton();
        setupRatingArea();

        setTitle(getString(R.string.meeting_details));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRatingServiceManager.loadRatingFromWebservice(mEvent);
            }
        });
        mRatingServiceManager.loadRatingFromWebservice(mEvent); //refresh the event
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRatingArea() {
        if (mEventDecorator.hasRatings()) {
            //has ratings!
            mMeetingRatings.setVisibility(View.VISIBLE);
            mRatingHeader.setVisibility(View.VISIBLE);
            mRatingNone.setVisibility(View.GONE);
        } else {
            //doesnt..
            mMeetingRatings.setVisibility(View.GONE);
            mRatingHeader.setVisibility(View.GONE);
            mRatingNone.setVisibility(View.VISIBLE);
        }

    }

    private void setupRatingAdapter() {
        mMeetingRatings.setAdapter(new RatingsRecyclerViewAdapter(this, mDataStore.getWebServiceRatingDataForEvent(mEventId)));
    }

    private void setupRecyclerView() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mMeetingRatings.setHasFixedSize(true);
        mMeetingRatings.setLayoutManager(layoutManager);
        mMeetingRatings.setItemAnimator(new DefaultItemAnimator());
    }

    private void setupRatingButton() {
        //rating button
        if (mEventDecorator.isOwner(mDataStore.getUsername())) {
            mEventRatingButton.setVisibility(View.GONE);
        } else if (mEventDecorator.hasAlreadyRated()) {
            mEventRatingButton.setVisibility(View.GONE);
        } else {
            mEventRatingButton.setVisibility(View.VISIBLE);
        }
        if (mEventDecorator.hasRatings()) {
            mRatingArea.setVisibility(View.VISIBLE);
            String rating = getResources().getQuantityString(R.plurals.rating, mEventDecorator.getRatingCount());
            mExistingRating.setText(String.format(getString(R.string.avg_rating_text), mEventDecorator.getAvgRating(), mEventDecorator.getRatingCount(), rating));
        } else {
            mRatingArea.setVisibility(View.GONE);
            mExistingRating.setText(R.string.no_ratings_text);
        }
    }

    public void onEvent(UserRatingsLoadedSuccessEvent event) {
        mSwipeRefreshLayout.setRefreshing(false);
        updateUIState();
    }

    public void onEvent(UserRatingAddedSuccessEvent userRatingAddedSuccessEvent) {
        //reload the adapter since the data changed!
        MeetingServiceResponseData webServiceRatingDataForEvent = mDataStore.getWebServiceRatingDataForEvent(mEventId);
        mEventDecorator = new EventDecorator(mEvent, webServiceRatingDataForEvent);
        updateUIState();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void updateUIState() {
        setupRatingAdapter();
        setupRatingArea();
        setupRatingButton();
    }

    public void onEvent(SendRatingSuccessEvent sendRatingSuccessEvent) {
        Log.d(TAG, "SendRatingSuccessEvent received!");
        Toast.makeText(this, "Rating Sent!", Toast.LENGTH_SHORT).show();
        mDialogUtil.dismissDialog(this);
    }

    public void onEvent(ShowRatingDialogEvent event) {
        RatingDialogFragment.newInstance(event.mEventId).show(getSupportFragmentManager(), RATING_DIALOG_FRAGMENT_TAG);
    }

    public void onEvent(SendRatingEvent event) {
        mDialogUtil.showProgressDialog(this, getString(R.string.submit_rating), getString(R.string.submitting_rating_description));
        mClientManager.getEmailClientManager().sendRating(event.mRatingData);
    }


}

// *********************************************************
//
// O365-Android-MeetingFeedback, https://github.com/OfficeDev/O365-Android-MeetingFeedback
//
// Copyright (c) Microsoft Corporation
// All rights reserved.
//
// MIT License:
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
// *********************************************************