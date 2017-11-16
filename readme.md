Zoom Splash
=====================

Made by [![Appus Studio](https://github.com/appus-studio/Appus-Splash/blob/master/image/logo.png)](https://appus.software)


Zoom Splash is a clone of animation that was seen in Twitter app. Control allows to easily create a nice splash effect and attach it to your activity. Control customization is also possible.

* [Demo](#demo)
* [Getting Started](#getting-started)
* [Customization](#customization)
* [Info](#info)

# Demo
![](https://github.com/appus-studio/Appus-Splash/blob/master/image/splash_demo.gif)

# Getting Started

##Setup:

1. If you use JCenter repository in your project, just add dependence to main build.gradle:

        dependencies {
                compile 'pro.appus:zoom-splash:1.0.0'
        }
   
   Else add repository to the same build.gradle:
   
        repositories {
                maven {
                        url 'https://dl.bintray.com/roman-voronoy/maven/'
                }
        }
        
        
2. Done!

##Usage example:


    public class MainActivity extends Activity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            .... your onCreate code here ....

            Splash.Builder splash = new Splash.Builder(this, getActionBar());
            splash.perform();
        }
    }

# Customization

Set custom color of background:


        splash.setBackgroundColor(getResources().getColor(R.color.blue));

Set custom image for background:


        splash.setBackgroundImage(getResources().getDrawable(R.drawable.default_splash_image));

Set custom image for splash:


        splash.setSplashImage(getResources().getDrawable(R.drawable.default_splash_image));

Set custom color of splash image:


        splash.setSplashImageColor(getResources().getColor(R.color.blue));
        
        
Set custom splash pivot:
        
        .setPivotXOffset(getResources().getInteger(R.integer.my_x_pivot))
        .setPivotYOffset(getResources().getInteger(R.integer.my_y_pivot))

Set custom splash animation type:

        .setAnimationType(Splash.AnimationType.TYPE_2)

# Info

Developed By
------------

* Igor Malytsky, ![Appus Studio](https://appus.software)

License
--------

    Copyright 2015 Appus Studio.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
