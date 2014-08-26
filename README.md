# MovieLinker
Back in college, when my friends and I wanted to go to the movies we would make a day of it.  We wanted to watch multiple movies.  Often times we couldn't come to a consensus of what to watch.  We spent a lot of time reading information about movies, watching trailers, and most of all coming up with a good viewing schedule.

MovieLinker simplifies all this by:

1.  Allowing you to select a theatre near your current location
2.  Displaying that theatres' offerings
3.  Providing HD trailers
4.  Generating all possible schedules of the selected movies based off the current time, within a 30 minute wait

## Screenshots
<a href="http://imgur.com/VzfyfTD"><img src="http://i.imgur.com/VzfyfTD.png" title="Hosted by imgur.com"/></a>
<a href="http://imgur.com/NvHu5aI"><img src="http://i.imgur.com/NvHu5aI.png" title="Hosted by imgur.com"/></a>
<a href="http://imgur.com/gPaFnzY"><img src="http://i.imgur.com/gPaFnzY.png" title="Hosted by imgur.com"/></a>
<a href="http://imgur.com/dIGfsur"><img src="http://i.imgur.com/dIGfsur.png" title="Hosted by imgur.com"/></a>


## Building

The build requires [Gradle](http://www.gradleware.com/)
v1.10 and the [Android SDK](http://developer.android.com/sdk/index.html)
to be installed in your development environment. In addition you'll need to set
the `ANDROID_HOME` environment variable to the location of your SDK:

    export ANDROID_HOME=/opt/tools/android-sdk

After satisfying those requirements, the build is pretty simple:

* Run `gradle assemble` from the root directory to build the APK only
* Run `gradle build` from the root directory to build the app and also run
  the integration tests, this requires a connected Android device or running
  emulator.

## Acknowledgements

This project uses the [TMS OnConnect Data Delivery API](http://developer.tmsapi.com/io-docs) to provide theatre and movie listings.

It also uses libraries such as:
 
 * [Google Play Services] (https://developer.android.com/google/play-services/index.html)
 * [Android Async HTTP](https://github.com/loopj/android-async-http)
 * [UniversalImageLoader](https://github.com/nostra13/Android-Universal-Image-Loader)
 * [NonScrollableGridView] (https://github.com/fliped/lib/blob/master/Lib/src/main/java/by/flipdev/lib/helper/customview)
 * [ExpandableHeightListView] (https://github.com/surespot/android/tree/master/src/com/twofours/surespot/ui)
 * [JodaTime] (http://www.joda.org/joda-time/) 
 * [Gradle](https://github.com/gradle/gradle)

