/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
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

import com.microsoft.office365.meetingfeedback.model.meeting.EventDecorator;
import com.microsoft.office365.meetingfeedback.model.meeting.RatingData;
import com.microsoft.office365.meetingfeedback.model.webservice.RatingServiceManager;
import com.microsoft.office365.meetingfeedback.model.webservice.payload.MeetingServiceResponseData;
import com.microsoft.office365.meetingfeedback.view.RatingDialogFragment;
import com.microsoft.office365.meetingfeedback.view.RatingsRecyclerViewAdapter;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MeetingDetailActivity extends NavigationBarActivity implements RatingActivity {

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

    private com.microsoft.graph.models.extensions.Event mEvent;
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
        mMeetingOrganizer = findViewById(R.id.activity_meeting_detail_organizer);
        mMeetingDetailEventDate = findViewById(R.id.activity_meeting_detail_event_date);
        mMeetingRatings = findViewById(R.id.activity_meeting_detail_recycler_view);
        mEventRatingButton = findViewById(R.id.rate_button);
        mExistingRating = findViewById(R.id.event_existing_rating);
        mActivityTitle = findViewById(R.id.activity_meeting_detail_title);
        mEventDescription = findViewById(R.id.activity_meeting_detail_description);
        mSwipeRefreshLayout = findViewById(R.id.activity_meeting_detail_swipe_refresh_layout);
        mRatingArea = findViewById(R.id.rating_area);
        mRatingHeader = findViewById(R.id.activity_meeting_detail_rating_header);
        mRatingNone = findViewById(R.id.activity_meeting_detail_rating_none);

        mMeetingOrganizer.setText(mEvent.organizer.emailAddress.name);
        mActivityTitle.setText(mEventDecorator.mSubject);
        mEventDescription.setText(mEventDecorator.descriptionAsHtml());
        mMeetingDetailEventDate.setText(mEventDecorator.formattedDateAndTime());

        mEventRatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RatingDialogFragment.newInstance(mEventId).show(getSupportFragmentManager(), TAG);
            }
        });
        setupRecyclerView();
        setupRatingAdapter();
        setupRatingButton();
        setupRatingArea();

        setTitle(getString(R.string.meeting_details));

        final Callback<Void> loadRatingsCallback = new Callback<Void>() {
            @Override
            public void success(Void aVoid, Response response) {
                mSwipeRefreshLayout.setRefreshing(false);
                updateUIState();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, error.getMessage());
            }
        };
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRatingServiceManager.loadRatingFromWebservice(mEvent, loadRatingsCallback);
            }
        });
        mRatingServiceManager.loadRatingFromWebservice(mEvent, loadRatingsCallback);
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
            //doesn't...
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
        if (mEventDecorator.mIsOrganizer) {
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

    private void updateUIState() {
        setupRatingAdapter();
        setupRatingArea();
        setupRatingButton();
    }

    public void onSendRating(com.microsoft.graph.models.extensions.Event event, RatingData ratingData) {
        super.sendRating(
                event,
                ratingData,
                new Runnable() {
                    @Override
                    public void run() {
                        //update the webservice with data as well
                        MeetingServiceResponseData webServiceRatingDataForEvent = mDataStore.getWebServiceRatingDataForEvent(mEventId);
                        mEventDecorator = new EventDecorator(mEvent, webServiceRatingDataForEvent);
                        updateUIState();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
        );
    }
}
