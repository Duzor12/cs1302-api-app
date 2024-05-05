# Deadline

Modify this file to satisfy a submission requirement related to the project
deadline. Please keep this file organized using Markdown. If you click on
this file in your GitHub repository website, then you will see that the
Markdown is transformed into nice-looking HTML.

## Part 1.1: App Description

    Welcome to Charger Finder, a convenient application designed to help electric vehicle (EV) owners locate charging stations near their current location or a custom address.

    Primary Functions:

    Current Location Search: Utilize your device's IP geolocation to find nearby charging stations.
    Custom Address Search: Input a specific address to search for charging stations in that area.

    This application integrates with two main APIs:

    Abstract API for Geolocation: Provides geolocation data based on IP address.
    Open Charge Map API: Supplies data on EV charging stations worldwide.

    git repository Url: https://github.com/Duzor12/cs1302-api-app.git


## Part 1.2: APIs


### API 1: Abstract API for Geolocation

    https://ipgeolocation.abstractapi.com/v1/?api_key=YOUR_API_KEY

    This API returns geolocation data based on the IP address of the device. Replace YOUR_API_KEY with your actual API key.

### API 2: Open Charge Map API

    https://api.openchargemap.io/v3/poi?maxresults=100&latitude=LATITUDE&longitude=LONGITUDE&key=YOUR_API_KEY

    This API provides information about charging stations based on latitude and longitude coordinates. Replace `YOUR_API_KEY`, `LATITUDE`, and `LONGITUDE` with your actual API key and coordinates.

### API 3: LocationIQ API

    https://us1.locationiq.com/v1/search?format=json&normalizeaddress=1&key=YOUR_API_KEY&q=SEARCH_QUERY

    This API offers geocoding and reverse geocoding services, allowing you to convert addresses into geographic coordinates and vice versa. Replace `YOUR_API_KEY` with your actual API key and `SEARCH_QUERY` with your desired address or location.

### API 4: Bing Maps API

    https://www.bing.com/api/maps/mapcontrol?callback=GetMap&key=YOUR_API_KEY

    This API enables mapping and visualization of points on a map interface. I made use of the Webview to display results from this.

## Part 2: New

    Working on this project, I learned a lot about integrating APIs into a Java application, especially within a JavaFX framework. Understanding how to handle asynchronous tasks and UI updates based on API responses was particularly enlightening.

## Part 3: Retrospect

    Reflecting on this project, if I were to start over, I would focus more on error handling and user feedback. Implementing better error messages and visual cues for loading and processing data would enhance the user experience. Additionally, I would explore more ways to optimize the application's performance, especially when dealing with potentially large datasets from APIs.
