/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.meetingfeedback.inject;

import com.microsoft.office365.meetingfeedback.MeetingFeedbackApplication;
import com.microsoft.office365.meetingfeedback.model.DataStore;
import com.microsoft.office365.meetingfeedback.model.service.MyMeetingsService;
import com.microsoft.office365.meetingfeedback.model.service.RatingServiceAlarmManager;
import com.microsoft.office365.meetingfeedback.model.webservice.RatingService;
import com.microsoft.office365.meetingfeedback.model.webservice.RatingServiceManager;
import com.microsoft.office365.meetingfeedback.util.ConnectivityUtil;
import com.microsoft.office365.meetingfeedback.util.SharedPrefsUtil;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, injects = {MyMeetingsService.class})
public class ApplicationModule {

    private final MeetingFeedbackApplication mApplication;

    public ApplicationModule(MeetingFeedbackApplication application) {
        this.mApplication = application;
    }

    @Provides
    @Singleton
    public RatingServiceManager providesRatingServiceManager(DataStore dataStore) {
        return new RatingServiceManager(new RatingService(), dataStore);
    }

    @Provides
    public SharedPrefsUtil providesSharedPrefsUtil() {
        return new SharedPrefsUtil(mApplication);
    }

    @Provides
    @Singleton
    public DataStore providesDataStore(SharedPrefsUtil sharedPrefsUtil) {
        return new DataStore(sharedPrefsUtil);
    }

    @Provides
    public ConnectivityUtil providesConnectivityUtil() {
        return new ConnectivityUtil(mApplication);
    }

    @Provides
    public RatingServiceAlarmManager providesRatingServiceAlarmManager() {
        return new RatingServiceAlarmManager(mApplication);
    }
}
