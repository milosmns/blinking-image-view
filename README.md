Silly Android
=============
[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Build Status](https://travis-ci.org/milosmns/blinking-image-view.svg?branch=master)](https://travis-ci.org/milosmns/blinking-image-view)
[![Download](https://api.bintray.com/packages/milosmns/maven/blinkerview/images/download.svg)](https://bintray.com/milosmns/maven/blinkerview/_latestVersion)

What is this?
-------------
_Blinker View_ is an Android View that blinks a given drawable. Yes, it's that simple. Place it in your layout and you can now blink the drawable it holds.

One important thing to note here is that - contrary to many approaches you can find on the web - it only blinks the given "_source_" drawable. 
The whole View is always at 100% alpha (i.e. always fully visible), including the View's background drawable. 
To blink the whole View, you don't need the custom-view approach, you can create an Alpha animation and go with that.

Examples
--------

To use the `BlinkerView` in an XML layout, you would add something like this:

```xml
    <me.angrybyte.blinkerview.BlinkerView
        android:id="@+id/blinkerView"
        android:layout_width="@dimen/tap_dimension"
        android:layout_height="@dimen/tap_dimension"
        android:background="@android:color/darker_gray"
        android:padding="@dimen/icon_padding"
        app:blink_drawable="@drawable/ic_red_circle"
        app:blink_scale_type="constrain" />
```

All attributes that are currently available are:

  1. `blink_drawable` - Drawable reference, the one that will be blinking (default is `null`)
  2. `blink_interval` - Time value in milliseconds, telling the View how long to animate between visible and invisible states (default is `500`)
  3. `blink_autostart` - Whether to automatically start the blinking when View is first inflated (default is `false`)
  4. `blink_use_fading` - Whether to display a fading animation while blinking (default is `true`)
  5. `blink_scale_type` - How to scale the drawable inside the Blinker View (default is `stretch`). Options are:
      - `stretch` - This should be self-explanatory, stretches the drawable to the view's bounds
      - `constrain` - Snaps the drawable's size to the view's smaller dimension (width or height), keeping the original drawable's aspect ratio
      - `center` - Centers the drawable inside the view, keeping original drawable size

Note that most of these attributes are also available on the view class via getters/setters.  
If you want other attributes available on the view class too, please submit a request through the issues tab.

Setup
-----
- Gradle build - `jCenter` and `mavenCentral` are both supported
- **AppCompat**, minimum version `25.0.2` - _Silly Android_ heavily relies on the features provided by this library. Note that including other versions might work too, but don't report issues if it does not. You've been warned! 

To include _Silly Android_ in your app, add the following line to your app's `build.gradle`'s `dependecies` block.
```gradle
    // look for the latest version on top of this file
    compile "me.angrybyte.sillyandroid:sillyandroid:VERSION_NAME" 
```

Code organization
-----------------

- The backbone of the library is here: `me.angrybyte.sillyandroid.SillyAndroid`. It contains various utilities to help around with accessing APIs more easily.
- Check out the `me.angrybyte.sillyandroid.extras.Coloring` class for various color and drawable utilities.
- Android components such as Activity, Fragment, Dialog, View and ViewGroup have been enhanced to include features from the _Silly Android_ internally, you can check that out in the `me.angrybyte.sillyandroid.components` package. They are now called `EasyActivity`, `EasyFragment` and so on.
- Even more enhancements are added to the _Easy_ component set using _Silly Android_'s annotations, such as View injections, typed `T findView(int id)` methods, automated click and long-click handling, and more. For information about that, see package `me.angrybyte.sillyandroid.parsable.components`.
- For a coded demo of the fully enhanced Activity class, go to `me.angrybyte.sillyandroid.demo.MainActivity`.
- Check out the colors, UI sizes and text sizes added in `sillyandroid/src/main/res`.

Contributions and how we determine what to include
--------------------------------------------------
All interested parties need to create a new [Feature request](https://github.com/milosmns/silly-android/issues/new) so that everyone involved in active development can discuss the feature or the workaround described. Any pull request not referencing a _Feature request_ will be automatically denied. You need to have actual reasons backed up by real-world facts in order to include stuff in the library - otherwise, we would have a huge library with lots of useless components that would need to be removed with ProGuard (and we don't want to be another AppCompat).

Furthermore, we are trying to test everything that's not trivial and keep the code as clean as humanly possible; thus, any pull requests that fail the CI code quality check or fail to properly pass the tests will also be denied. If pull requests pass every check (and don't worry, it's not impossible to pass), one of the admins could then merge the changes to the `release` branch - this triggers a CI build with device/emulator tests. If all goes ok, the library is automatically deployed to `jCenter` and `MavenCentral`.

Further support
---------------
In case of emergency errors, please [create an issue](https://github.com/milosmns/silly-android/issues/new).
We missed something really useful? Have an idea for a cleaner API? [Fork this project](https://github.com/milosmns/silly-android/fork) and submit a pull request through GitHub. Keep in mind that you need a _Feature request_ first with a finalized discussion (see the Contributions section).
Some more help may be found here:
- StackOverflow [here](http://stackoverflow.com/questions/tagged/silly-android)
- [On my blog](http://angrybyte.me)