/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.meetingfeedback.inject;

import com.microsoft.graph.authentication.MSALAuthenticationProvider;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.office365.meetingfeedback.BaseActivity;
import com.microsoft.office365.meetingfeedback.CalendarActivity;
import com.microsoft.office365.meetingfeedback.ConnectActivity;
import com.microsoft.office365.meetingfeedback.MeetingDetailActivity;
import com.microsoft.office365.meetingfeedback.model.Constants;
import com.microsoft.office365.meetingfeedback.model.DataStore;
import com.microsoft.office365.meetingfeedback.model.outlook.CalendarService;
import com.microsoft.office365.meetingfeedback.model.outlook.EmailService;
import com.microsoft.office365.meetingfeedback.util.DialogUtil;
import com.microsoft.office365.meetingfeedback.view.CalendarPageFragment;
import com.microsoft.office365.meetingfeedback.view.CalendarRangeFragment;
import com.microsoft.office365.meetingfeedback.view.RatingDialogFragment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(injects = {
        ConnectActivity.class,
        CalendarActivity.class,
        MeetingDetailActivity.class,
        CalendarRangeFragment.class,
        RatingDialogFragment.class,
        CalendarPageFragment.class,
},
        addsTo = ApplicationModule.class,
        library = true)

public class ActivityModule {

    private BaseActivity mActivity;

    public ActivityModule(BaseActivity activity) {
        mActivity = activity;
    }

    @Provides
    public DialogUtil providesDialogUtil() {
        return new DialogUtil();
    }

    @Provides
    @Singleton
    public MSALAuthenticationProvider providesMSALAuthenticationProvider(PublicClientApplication publicClientApplication) {
        MSALAuthenticationProvider msalAuthenticationProvider = new MSALAuthenticationProvider(mActivity,
                mActivity.getApplication(),
                publicClientApplication,
                Constants.Scopes);
        return msalAuthenticationProvider;
    }

    @Provides
    @Singleton
    public PublicClientApplication providesPublicClientApplication() {
        return new PublicClientApplication(mActivity.getApplication(), Constants.CLIENT_ID);//todo: will application context work in all cases here?
    }

    @Provides
    @Singleton
    public EmailService providesEmailService(MSALAuthenticationProvider authenticationManager) {
        return new EmailService(authenticationManager);
    }

    @Provides
    @Singleton
    public CalendarService providesCalendarService(MSALAuthenticationProvider authenticationManager, DataStore dataStore) {
        return new CalendarService(authenticationManager, dataStore);
    }
}
