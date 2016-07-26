# FilterShare

Installation Guide & User Manual 

Installation: Git clone the application to your repository and install it through Android Studio.
Minimum Android API Support: API 21(Lollipop)
Target Android API Support: API 23(Marshmallow)
Permission needed: Camera and External Storage access

Download and start FilterShare app.
Notice the permissions request for camera and external storage and accept them.
Select ‘Ok’, then user should be able to open the app with the camera preview open.
As it is the first execution of the app, the tutorial that describes the function of the camera page starts.
Touch ‘Got it’, then the user can select the picture and goes on to the next page.
The consecutive tutorials that describes other pages start.
On the camera preview page, there are three icons
‘Change camera view’: switch camera mode between front and back views.
‘Take photo’: takes photo
‘Go to gallery’: takes user to page with photos from gallery
Take a photo with ‘Take photo’ icon or select a photo from gallery.
It will navigate to a page with ‘RE-TAKE’ button and ‘CONFIRM’ button.
‘RE-TAKE’ takes user back to Step 7.
‘CONFIRM’ takes user to Step 10.
Start ‘share_filter’ activity.
You can either choose to 
make your own filter by clicking ‘+’ button → go to Step 15
use the filters that others shared → go to Step 12
Look at the list of filters applied to the image that you’ve chosen. Scroll down to take a look at more filters. Before you reach the end of the scroll, the app will automatically fetch more filters from the server.
You can choose the order of filters in the view. You can either choose to see the recent filters first or the most popular filters first. Also, you can see your own filters at this page.
If you have chosen a filter in mind, touch that image. Then you will see the specific value of each attributes the filter has. Press ‘SAVE’ button to use the filter to the image that you’ve chosen.
With the image that you’ve chosen, you can make your own filter by selecting any of the following attributes; <Brightness,Contrast,Saturation,Sharpen,Temperature,Tint,Vignette,Grain>
After selecting a single attribute, you can adjust the value associated with that with the slider which appears, to achieve the desired filter effect.
If you don’t like the image you’re working on now, you can press ‘Change Image’ to re-choose the image.
Once you’re done, press ‘Next’ to share the filter.
You can input the name of the filter, your username, and the hashtags you want to describe the filter.
Once you’re done, press ‘Share’ to share the filter. After that, you will go to share filter activity. If you don’t like the filter at this moment, press ‘Back’ button to go back to filter making activity.
