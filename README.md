# FilterShare
FilterShare is a custom filter generating and sharing android application. A user can make various filters within the application and share them with others.

## Demo
Demo video is available in [https://youtu.be/u-lKAyZW4tg](https://youtu.be/u-lKAyZW4tg)

## Installation Guide & User Manual

### Installation
Git clone the application to your repository and install it through Android Studio.

* Minimum Android API Support: API 21(Lollipop)

* Target Android API Support: API 23(Marshmallow)

* Permission needed: Camera and External Storage access

### User Manual
1. Download and start FilterShare app.

2. Notice the permissions request for camera and external storage and accept them.

3. Select ‘Ok’, then user should be able to open the app with the camera preview open.

4. As it is the first execution of the app, the tutorial that describes the function of the camera page starts.

5. Touch ‘Got it’, then the user can select the picture and goes on to the next page.

6. The consecutive tutorials that describes other pages start.

7. On the camera preview page, there are three icons
  a. ‘Change camera view’: switch camera mode between front and back views.
  b. ‘Take photo’: takes photo
  c. ‘Go to gallery’: takes user to page with photos from gallery

8. Take a photo with ‘Take photo’ icon or select a photo from gallery.

9. It will navigate to a page with ‘RE-TAKE’ button and ‘CONFIRM’ button.
  a. ‘RE-TAKE’ takes user back to Step 7.
  b. ‘CONFIRM’ takes user to Step 10.

10. Start ‘share_filter’ activity.

11. You can either choose to 
  a. make your own filter by clicking ‘+’ button → go to Step 15
  b. use the filters that others shared → go to Step 12

12. Look at the list of filters applied to the image that you’ve chosen. Scroll down to take a look at more filters. Before you reach the end of the scroll, the app will automatically fetch more filters from the server.

13. You can choose the order of filters in the view. You can either choose to see the recent filters first or the most popular filters first. Also, you can see your own filters at this page.

14. If you have chosen a filter in mind, touch that image. Then you will see the specific value of each attributes the filter has. Press ‘SAVE’ button to use the filter to the image that you’ve chosen.

15. With the image that you’ve chosen, you can make your own filter by selecting any of the following attributes; <Brightness,Contrast,Saturation,Sharpen,Temperature,Tint,Vignette,Grain>

16. After selecting a single attribute, you can adjust the value associated with that with the slider which appears, to achieve the desired filter effect.

17.	If you don’t like the image you’re working on now, you can press ‘Change Image’ to re-choose the image.

18.	Once you’re done, press ‘Next’ to share the filter.

19.	You can input the name of the filter, your username, and the hashtags you want to describe the filter.

20.	Once you’re done, press ‘Share’ to share the filter. After that, you will go to share filter activity. If you don’t like the filter at this moment, press ‘Back’ button to go back to filter making activity.
