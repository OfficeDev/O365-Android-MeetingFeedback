/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.meetingfeedback;

import com.microsoft.office365.meetingfeedback.model.meeting.RatingData;

public interface RatingActivity {
    void onSendRating(com.microsoft.graph.models.extensions.Event event, RatingData ratingData);
}
