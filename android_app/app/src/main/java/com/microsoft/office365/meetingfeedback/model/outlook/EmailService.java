/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.meetingfeedback.model.outlook;

import android.text.TextUtils;
import android.util.Log;

import com.microsoft.graph.authentication.MSALAuthenticationProvider;
import com.microsoft.graph.concurrency.ICallback;
import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.models.extensions.EmailAddress;
import com.microsoft.graph.models.extensions.Event;
import com.microsoft.graph.models.extensions.IGraphServiceClient;
import com.microsoft.graph.models.extensions.ItemBody;
import com.microsoft.graph.models.extensions.Message;
import com.microsoft.graph.models.extensions.Recipient;
import com.microsoft.graph.models.generated.BodyType;
import com.microsoft.graph.requests.extensions.GraphServiceClient;
import com.microsoft.office365.meetingfeedback.model.Constants;
import com.microsoft.office365.meetingfeedback.model.meeting.RatingData;
import com.microsoft.office365.meetingfeedback.util.FormatUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the creation of the message and contacting the
 * mail service to send the message. The app must have
 * connected to Office 365 and discovered the mail service
 * endpoints before using the createDraftMail method.
 */
public class EmailService {

    private EmailInterface mEmailClient;
    private static final String TAG = "EmailService";
    private IGraphServiceClient graphClient;

    public EmailService(MSALAuthenticationProvider authenticationManager) {
        graphClient = GraphServiceClient
                .builder()
                .authenticationProvider(authenticationManager)
                .buildClient();
    }

    /**
     * Sends an email message using the Microsoft Graph API on Office 365. The mail is sent
     * from the address of the signed in user.
     *
     * @param event      Details about the event.
     * @param ratingData Details about the rating (rate and comments).
     */
    public void sendRatingMail (
            final Event event,
            final RatingData ratingData
    ) {
        // create the email
        Message msg = createMailPayload(
                formatSubject(event),
                formatBody(event, ratingData),
                event.organizer.emailAddress.address
        );
        // send it using our service
        graphClient.me().sendMail(msg, false)
                .buildRequest()
                .post(new ICallback<Void>() {

                    @Override
                    public void success(Void aVoid) {
                        Log.d(TAG, "Successfully sent mail");
                    }

                    @Override
                    public void failure(ClientException ex) {
                        Log.e(TAG, ex.getMessage());
                    }
                });
    }

    private Message createMailPayload(
            String subject,
            String htmlBody,
            String recipient) {
        EmailAddress mailRecipient = new EmailAddress();
        mailRecipient.address = recipient;

        Recipient toRecipient = new Recipient();
        toRecipient.emailAddress = mailRecipient;

        EmailAddress ratingAddress = new EmailAddress();
        ratingAddress.address = Constants.REVIEW_SENDER_ADDRESS;

        Recipient sender = new Recipient();
        sender.emailAddress = ratingAddress;
        Recipient from = new Recipient();
        from.emailAddress = ratingAddress;

        ItemBody body = new ItemBody();
        body.contentType = BodyType.HTML;
        body.content = htmlBody;

        Message sampleMsg = new Message();
        sampleMsg.subject = subject;
        sampleMsg.body = body;
        List<Recipient> recipientsList = new ArrayList<>();
        recipientsList.add(toRecipient);
        sampleMsg.toRecipients = recipientsList;
        sampleMsg.sender = sender;
        sampleMsg.from = from;

        return sampleMsg;
    }

    private String formatSubject(Event event) {
        return String.format(
                "Your meeting, %s, on %s (%s) , was recently reviewed.",
                event.subject,
                FormatUtil.displayFormattedEventDate(event),
                FormatUtil.displayFormattedEventTime(event));
    }

    private String formatBody(Event event, RatingData ratingData) {
        StringBuilder stringBuilder = new StringBuilder();
        String eventDate = FormatUtil.displayFormattedEventDate(event);
        String eventTime = FormatUtil.displayFormattedEventTime(event);
        stringBuilder.append(String.format("<div>Your meeting, %s, on %s (%s) , was recently reviewed.</div>", event.subject, eventDate, eventTime));
        stringBuilder.append(String.format("<div>Rating: %s </div>", ratingData.mRating));
        String remarks = TextUtils.isEmpty(ratingData.mReview) ? "No Remarks." : ratingData.mReview;
        stringBuilder.append(String.format("<div>Remarks/How to improve: %s</div>", remarks));
        return stringBuilder.toString();
    }
}
