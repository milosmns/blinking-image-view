Blinker View for Android
=============
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Build Status](https://travis-ci.org/milosmns/blinking-image-view.svg?branch=master)](https://travis-ci.org/milosmns/blinking-image-view)
![Latest Version](https://img.shields.io/bintray/v/milosmns/maven/blinkerview?color=dk_green&label=Latest%20version&logo=gradle)

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

Both `jCenter` and `mavenCentral` are supported for this dependency.  
To include the `BlinkerView` in your app, add the following line to your **app**'s `build.gradle` **`dependecies`** block.
```gradle
    // look for the latest version on top of this file and replace the placeholder with it
    implementation "me.angrybyte.blinkerview:blinkerview:LATEST_VERSION" 
```
If you're using the View in a library module and would like the dependency to go through to your main project as well, then you should use `api` instead of 
`implementation` in the dependency declaration.  
For older Gradle versions, these keywords are not supported, so you should use the `compile` keyword.

Contributions
-------------
All interested parties need to create a new [Feature request](https://github.com/milosmns/blinking-image-view/issues/new) so that everyone involved in active 
development can discuss the feature or the workaround described. Any pull request not referencing a _Feature request_ will be automatically denied.  
Furthermore, we are trying to test everything that's not trivial and keep the code as clean as humanly possible; thus, any pull requests that fail the CI code 
quality check or fail to properly pass the tests will also be automatically denied. 
If pull requests pass every check (and don't worry, it's really not impossible to pass those), one of the maintainers could then merge the changes to the 
`release` branch - this triggers a CI build with device/emulator tests; and, if all goes ok, the library is automatically deployed to `jCenter` and `MavenCentral`.

Further support
---------------
In case of emergency errors, please [create an issue](https://github.com/milosmns/blinking-image-view/issues/new).
Want to add something? Sure, just [fork this project](https://github.com/milosmns/blinking-image-view/fork) and submit a pull request through GitHub. 
Keep in mind that you need a _Feature request_ first with a finalized discussion (see the Contributions section).
Some more help could potentially be found here:
- StackOverflow, [here](http://stackoverflow.com/questions/tagged/blinking-image-view)
- [On my blog](http://angrybyte.me)
