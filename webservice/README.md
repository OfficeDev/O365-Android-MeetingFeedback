# Office 365 Meeting Feedback sample for Android
# Table of Contents.
[Introduction](#introduction.)

[Device Requirements](#device-requirements)

[Prerequisites](#prerequisites)

[Configure your Azure AD tenant](#configure-your-Azure-AD-tenant)

[Install the Ruby on Rails application](#install-the-Ruby-on-Rails-application)

[Setup an email group to receive anonymous email notifications](#setup-an-email-group-to-receive-anonymous-email-notifications)

[Open the sample using Android Studio](#open-the-sample-using-Android-Studio)

[Contributing](#contributing)

[Questions and comments](#questions-and-comments)

[Additional resources](#additional-resources)

[![Build Status](https://travis-ci.org/microsoftgraph/nodejs-webhooks-rest-sample.svg)](https://travis-ci.org/microsoftgraph/nodejs-webhooks-rest-sample)

<a name="introduction"></a>

## Introduction
[![Meeting Feedback sample for Android](/readme-images/O365-Android-MeetingFeedback-video_play_icon.png)](http://youtu.be/VXdEtKIPxi8 "Click to see the sample in action")

Meeting Feedback uses the Office 365 endpoints in the Microsoft Graph to retrieve calendar events and send email's in behalf of the signed in user.

## Device requirements
<a name="device-requirements"></a>

To run the sample, your device needs to meet the following requirements:

* Android API level 15 or later.
 
## Prerequisites
<a name="prerequisites"></a>

The sample includes an Android client and a Ruby on Rails server-side component. You also must setup an alias from where your users can get email notifications.

### Android client component

* [Android Studio](http://developer.android.com/sdk/index.html) version 1.0 or later.
* An Office 365 account. You can sign up for [an Office 365 Developer subscription](http://aka.ms/o365-android-connect-signup) that includes the resources that you need to start building Office 365 apps. If you get a developer subscription you also get an Office 365 account. 
* A client id and redirect uri values of an application registered in Azure. The application must run the **Send mail as a user** and **Read user calendars** permissions. You can also [add a native client application in Azure](https://msdn.microsoft.com/office/office365/HowTo/add-common-consent-manually#bk_RegisterNativeApp) and [grant proper permissions](https://github.com/OfficeDev/O365-Android-MeetingFeedback/wiki/Grant-permissions-to-the-application-in-Azure) to it.

### Ruby on Rails application component

* Ruby 2.0.0
* Ruby Development Kit (for Windows installations only)

## Configure your Azure AD tenant
<a name="Configure-your-Azure-AD-tenant"></a>

## Install the Ruby on Rails application
<a name="install-the-Ruby-on-Rails-application"></a>

1. Go to the webservice folder in the O365-Android_MeetingFeedback repository.
2. Run the following commands to perform the following tasks:

	* Install the builder gem
	* Install the Meeting Feedback Ruby on Rails application
	* Create the Meeting Feedback database
	* Start the Meeting Feedback service on port 5000

```
gem install builder
bundle install
rake db:create && rake db:migrate && bundle exec foreman start -p 5000
```

## Setup an email group to receive anonymous email notifications
<a name="setup-an-email-group-to-receive-anonymous-email-notifications"></a>

The app sends an email notification to the meeting organizer when one of the meetings that he hosts gets reviewed. The email comes from a designated email address to keep the feedback anonymous. You must setup an Exchange group on Office 365 for this to work. To setup an Exchange group, follow these steps.

1. Go to http://office.com and sign in with a tenant administrator.
2. Click on the **Admin** tile.
3. On the navigation bar, click **Admin** -> **Exchange**.
4. Click **groups** in the **recipients** section.
5. Click the plus sign to add a new **security group**.
6. Add a display name, alias, and email address and save the group.
7. Select the group and click the pencil icon to edit.
8. Go to **group delegation** and give the users of the app the **Send As** permission as shown in the following picture.
![Office 365 Meeting Feedback sample](/readme-images/O365-Android-MeetingFeedback-SendAs.png "Send As permission in an Exchange group")
9. Update the value of the **REVIEW\_SENDER\_ADDRESS** value in the **Constants.java** file to match the email address of your group.
 

## Open the sample using Android Studio
<a name="open-the-sample-using-Android-Studio"></a>

1. Install [Android Studio](http://developer.android.com/tools/studio/index.html#install-updates) and add the Android SDK packages according to the [instructions](http://developer.android.com/sdk/installing/adding-packages.html) on developer.android.com.
2. Download or clone this sample.
3. Start Android Studio.
	1. Select **Open an existing Android Studio project**.
	2. Browse to your local repository and select the O365-Android-MeetingFeedback project. Click **OK**.
4. Open the Constants.java file.
	1. Find the SERVICE\_ENDPOINT constant and set the value to the machine IP address and port where you're running the Ruby on Rails service.
	2. Find the CLIENT\_ID constant and set its String value equal to the client id you registered in Azure Active Directory.
	3. Find the REDIRECT\_URI constant and set its String value equal to the redirect URI you registered in Azure Active Directory.
    ![Office 365 Meeting Feedback sample](/readme-images/O365-Android-MeetingFeedback-Constants.png "Client ID and Redirect URI values in Constants file")

    > Note: If you have don't have CLIENT\_ID and REDIRECT\_URI values, [add a native client application in Azure](https://msdn.microsoft.com/office/office365/HowTo/add-common-consent-manually#bk_RegisterNativeApp) and take note of the CLIENT\_ID and REDIRECT_URI.

Once you've built the sample, you can run it on an emulator or device.

<a name="contributing"></a>
## Contributing ##

If you'd like to contribute to this sample, see [CONTRIBUTING.MD](/CONTRIBUTING.md).

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/). For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.

## Questions and comments
<a name="questions-and-comments"></a>

We'd love to get your comments about the Office 365 Meeting Feedback sample. You can send your questions and suggestions to us in the [Issues](https://github.com/OfficeDev/O365-Android-Connect/issues) section of this repository.

Questions about Office 365 development in general should be posted to [Stack Overflow](http://stackoverflow.com/questions/tagged/Office365+API). Make sure that your questions or comments are tagged with [Office365] and [API].

Do you want to read about our developer journey building this sample? Read our [Medium article](https://medium.com/p/572432b96089).

## Additional resources
<a name="additional-resources"></a>
* [Office 365 APIs platform overview](https://msdn.microsoft.com/office/office365/howto/platform-development-overview)
* [Office 365 SDK for Android](https://github.com/OfficeDev/Office-365-SDK-for-Android)
* [Get started with Office 365 APIs in apps](https://msdn.microsoft.com/office/office365/howto/getting-started-Office-365-APIs)
* [Office 365 APIs starter projects and code samples](https://msdn.microsoft.com/office/office365/howto/starter-projects-and-code-samples)
* [Office 365 Connect sample for Android](https://github.com/OfficeDev/O365-Android-Connect)
* [Office 365 Code Snippets for Android](https://github.com/OfficeDev/O365-Android-Snippets)
* [Office 365 APIs Starter Project for Android](https://github.com/OfficeDev/O365-Android-Start)
* [Office 365 Profile sample for Android](https://github.com/OfficeDev/O365-Android-Profile)

## Copyright
Copyright (c) 2019 Microsoft. All rights reserved.
