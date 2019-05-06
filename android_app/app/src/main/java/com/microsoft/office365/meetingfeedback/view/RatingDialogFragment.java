/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.meetingfeedback.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.microsoft.office365.meetingfeedback.BaseDialogFragment;
import com.microsoft.office365.meetingfeedback.R;
import com.microsoft.office365.meetingfeedback.RatingActivity;
import com.microsoft.office365.meetingfeedback.model.DataStore;
import com.microsoft.office365.meetingfeedback.model.meeting.RatingData;
import com.microsoft.office365.meetingfeedback.model.outlook.EmailService;
import com.microsoft.office365.meetingfeedback.model.webservice.RatingServiceManager;

import javax.inject.Inject;

public class RatingDialogFragment extends BaseDialogFragment {

    @Inject
    DataStore mDataStore;

    @Inject
    EmailService mRatingServiceManager;
    @Inject
    RatingServiceManager mEmailService;

    private static final String MEETING_ID = "MEETING_ID";
    private Button mPositiveButton;
    private com.microsoft.graph.models.extensions.Event mEvent;

    private EditText mComments;
    private TextView mOrganizer;
    private RatingBar mRatingBar;
    private String mUsername;

    public static RatingDialogFragment newInstance(String meetingId) {
        RatingDialogFragment ratingDialogFragment = new RatingDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(MEETING_ID, meetingId);
        ratingDialogFragment.setArguments(bundle);
        return ratingDialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String meetingId = getArguments().getString(MEETING_ID);
        mEvent = mDataStore.getEventById(meetingId);
        mUsername = mDataStore.getUser().getUsername();

        //Get the root ViewGroup so we can correctly inflate the view
        final ViewGroup rootViewGroup = (ViewGroup) ((ViewGroup) this.getActivity()
                .findViewById(android.R.id.content)).getChildAt(0);
        View inflatedView = LayoutInflater.from(getActivity()).inflate(
                R.layout.fragment_rating_dialog,
                rootViewGroup,
                false
        );

        mOrganizer = inflatedView.findViewById(R.id.fragment_rating_organizer_name);
        mComments = inflatedView.findViewById(R.id.fragment_rating_comments);
        mRatingBar = inflatedView.findViewById(R.id.fragment_rating_rating_bar);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(inflatedView)
                .setTitle(buildEventTitle())
                .setPositiveButton(R.string.rate_button_txt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final com.microsoft.graph.models.extensions.Event event = mDataStore.getEventById(mEvent.iCalUId);
                        final RatingData ratingData = buildRatingData();

                        RatingActivity ratingActivity = (RatingActivity)getActivity();
                        ratingActivity.onSendRating(event, ratingData);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null);

        mOrganizer.setText(mEvent.organizer.emailAddress.name);

        final AlertDialog alertDialog = builder.create();

        if (savedInstanceState == null) {
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    mPositiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    //disabled until the rating is actually set!
                    mPositiveButton.setEnabled(false);
                }
            });
        }
        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                //user rated the event...it's ok to rate now!
                mPositiveButton.setEnabled(true);
            }
        });

        return alertDialog;
    }

    private RatingData buildRatingData() {
        return new RatingData(mEvent.iCalUId, mRatingBar.getRating(), mComments.getText().toString(), mUsername);
    }

    private String buildEventTitle() {
        return String.format("Rate %s", mEvent.subject);
    }

}
