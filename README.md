# TCSDemo

This is a demo of a simple weather application. It has been created in 2 days and contains google maps to find location of the user and another screen to provide 3 day forecast for the specific location.

App uses Google Maps, Retrofit, Volley, Espresso, Custom Views(custom RecyclerView) and takes use of the [weather api](https://openweathermap.org/api). The weather api provides 3 day forecast with intervals of 3 hours. **Important:** API restricts the use of the key for 60 calls a minute!

### This is the first screen
![screenshot_2019-02-16-13-12-09-045](https://user-images.githubusercontent.com/26084498/52906267-e444d700-3250-11e9-8d3d-ad086bb766bf.jpeg)

Application handles internet connection status and both automatic and manual input of address to fetch specific location. For automatic location security exception is handled after Marshmallow. For manual location user writes address inside edittext field and google api fetch location. Because *geocoder.getLocationFromName* didn't work app uses Volley library to fetch json from google maps api which provides coordinates from address. After fetching coordinates we find location with *geocoder.getLocation*. App handles false input of address!

MapsActivity handles rotation and doesn't reload map. In addition when search of address has begun, after rotation keeps waiting for result address. Snackbar appears after adding a marker on the map to inform user to check location and click the ballon's marker to go to next screen to find weather forecast.

### This is the second screen
![screenshot_2019-02-17-01-32-46-989](https://user-images.githubusercontent.com/26084498/52906428-51a63700-3254-11e9-8cc2-7596b5b102cc.jpeg)

The Recycler view in the second screen has a cool animation and is formated to keep position after rotation. In addition after rotation doesn't reload info from the api. Because title was very long a rolling TextView was added to the toolbar. The activity makes use of Retrofit library to fetch info.

### Android testing patterns
Espresso has been used to perform some simple test. It handles check for internet connection, check of dialog boxes and idling resources so to wait for the recycler view to appear in second screen.

### Other features are:
- Icons at recycler view have been added as .xml to look perfect in different screen sizes
- Different layout has been made for bigger screens like tablets
- All strings are in strings.xml to help for app's translation
- All dimens are in dimens.xml
- Styles have been created for different Views

### LinkedIn info
- https://www.linkedin.com/in/george-soloupis-346a40135/

### Enjoy :+1:

