/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.meetingfeedback;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.microsoft.office365.meetingfeedback.model.EventFilter;
import com.microsoft.office365.meetingfeedback.model.meeting.EventGroup;
import com.microsoft.office365.meetingfeedback.model.meeting.RatingData;
import com.microsoft.office365.meetingfeedback.model.service.RatingServiceAlarmManager;
import com.microsoft.office365.meetingfeedback.model.webservice.RatingServiceManager;
import com.microsoft.office365.meetingfeedback.view.CalendarFragmentPagerAdapter;
import com.microsoft.office365.meetingfeedback.view.CalendarRangeFragment;

import javax.inject.Inject;

public class CalendarActivity extends NavigationBarActivity implements
        SwipeRefreshLayout.OnRefreshListener, RatingActivity, PageSettable {

    private static final String TAG = "CalendarActivity";
    public static final String MY_MEETINGS = "My Meetings";

    private ViewPager mCalendarViewPager;
    private CalendarFragmentPagerAdapter mAdapter;
    private CalendarRangeFragment mCalendarRangeFragment;
    private int mPage;

    @Inject
    RatingServiceManager mRatingServiceManager;
    @Inject
    RatingServiceAlarmManager mRatingServiceAlarmManager;

    private Spinner mSpinner;
    private ArrayAdapter mSpinnerAdapter;

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_calendar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCalendarViewPager = findViewById(R.id.activity_calendar_viewpager);
        mSpinner = findViewById(R.id.activity_calendar_select_role);
        mSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.meeting_filter_spinner_options,
                android.R.layout.simple_spinner_item);
        mSpinner.setAdapter(mSpinnerAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateCurrentFilter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                updateCurrentFilter();
            }
        });
        updateCurrentFilter();
        mCalendarViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mPage = position;
                onPageSet(mPage);
                mCalendarRangeFragment.setActivePage(mPage);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        if (savedInstanceState == null) {
            requestCalendarData();
            mCalendarRangeFragment = new CalendarRangeFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.activity_calendar_fragment_frame, mCalendarRangeFragment)
                    .commit();
        } else {
            mCalendarRangeFragment = (CalendarRangeFragment) getSupportFragmentManager().findFragmentById(R.id.activity_calendar_fragment_frame);
            setupViewPagerState();
        }
        //schedule the rating service to begin polling for new meetings
        mRatingServiceManager.loadMyMeetingsAndSchedulePolling(mRatingServiceAlarmManager);
    }

    private void updateCurrentFilter() {
        String filterValue = mSpinner.getSelectedItem().toString();
        EventFilter eventFilter = filterValue.equals(MY_MEETINGS) ? EventFilter.MY_MEETINGS : EventFilter.RATE_MEETINGS;
        mDataStore.setEventFilter(eventFilter);
        setupViewPagerState();
    }

    private void requestCalendarData() {
        mDialogUtil.showProgressDialog(this, R.string.calendar_events, R.string.calendar_events_loading);

        mCalendarService.fetchEvents(dismissDialogCallback(
                null,
                getString(R.string.failure_title),
                getString(R.string.loading_calendar_failed),
                new Runnable() {
                    @Override
                    public void run() {
                        setupViewPagerAdapter();
                        mCalendarRangeFragment.setup();
                        onPageSet(mPage);
                    }
                }
        ));
        mRatingServiceManager.loadRatingsFromWebservice();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupViewPagerState();
    }

    public void onSendRating(com.microsoft.graph.models.extensions.Event event, RatingData ratingData) {
        super.sendRating(
                event,
                ratingData,
                new Runnable() {
                    @Override
                    public void run() {
                        setupViewPagerState();
                    }
                }
        );
    }

    private void setupViewPagerState() {
        setupViewPagerAdapter();
        onPageSet(mPage);
    }

    private void setPageTitle(int position) {
        EventGroup eventGroup = mDataStore.getEventGroups().get(position);
        setTitle(eventGroup.mDateRange.getRangeAsFormattedString());
    }

    private void setupViewPagerAdapter() {
        mAdapter = new CalendarFragmentPagerAdapter(getSupportFragmentManager());
        mCalendarViewPager.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        requestCalendarData();
    }


    @Override
    public void onPageSet(int page) {
        setPageTitle(page);
        mCalendarViewPager.setCurrentItem(page);
    }
}
