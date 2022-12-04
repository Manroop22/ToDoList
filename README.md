
# ToDoList

## About
This is the repository for my COSC310 Indiviual Project project. We are creating an Android to-do list app for students.

## Class structure
All layout code is stored in the layout folder as ten xml files.
- activity_main.xml contains the layout for our main screen, including a RecyclerView for to-do items
- todo_item.xml is the layout of an individual to-do item, which are used to populate the RecyclerView
- activity_edit_todo.xml contains the layout for the edit to-do page that users can reach through the context menu after clicking on a to-do
- activity_edit_tag.xml contains the layout for the edit tag screen that users can reach through the context menu after clicking on a to-do
- tag_item.xml is the layout of an individual tag item
- filter_menu_item.xml contains the layout of an individual filter item
- activity_total_grade.xml is the layout for the max grade input page
- activity_grade_received.xml is the layout for the grade received page
- activity_maps.xml is the layout for showing the todoLocation on a map and contained the google autocomplete fragment to make searches. Also, it allowed to move to the activity_get_origin.xml where the directions/route between origin and destination is shown.
- activity_get_origin.xml is the layout for showing the route between the orign and the destination  on a map and dispaying the distance between them in a textfield. In addition, it also has the google autocomplete fragment to make searches.

All functional code is contained in .java files in the todolist folder.

**MainActivity** controls the main screen. By default, it extends AppCompatActivity, and it extends
our custom **ToDoClickListener**. The onCreate() method contains initialization code. 
New to-do's are created from user input using the createToDo method.
The onEditClick() and onCheckClick() methods are bound to the RecyclerView elements (individual to-dos).

**EditToDoActivity** controls the edit to-do page. Users can navigate to this page by clicking on a to-do.
This triggers the onEditClick() method and sends the to-do array list to the **EditToDoActivity** class.
On the edit-to page, users can change the text of their to-do, and set a due date.

**ToDo** is a serializable class to store information about to-dos.

**ToDoAdapter** is a custom RecyclerViewAdapter. It contains an inner class, **MyViewHolder**, which 
is a custom ViewHolder.

**ToDoClickListener** is an interface implemented by the **MainActivity**. 
It contains the onEditClick() and onCheckClick() method.

**FilterPowerMenuItem** defines each item in a PowerMenu for the filter menu on the main activity.

**EditTagActivity** controls the edit tag page. Users can navigate to this page through the context 
menu that pops up when clicking on the three dots on a task. There, they can add new or existing 
tags to tasks. They can also delete existing tags.

**FilterMenuAdapter** is a custom MenuBaseAdapter. 

**TagAdapter** is a custom RecyclerViewAdapter. It contains an inner class, **MyViewHolder**, which 
is a custom ViewHolder.

**TagClickListener** is an interface implemented by the **AddTagActivity**.
It contains the onDeleteClick() method.

**GradeReceived** is an activity where the user can enter the grade they received on an assignment

**TotalGradeActivity** is an activity where the user can input grade weight of an assignment.

**NotificationSender** is a class that handles sending the notification when the application is not running. It is all back-end and the user does not directly touch it

Test code is contained in **EditToDoActivityUnitTest**

**Config.java** contains the Google API Key. This class has not been pushed to github and will be provided to the TA separately.

**MapsActivity** is an activity which is used to set the location of the the todo. Once the location is set the the user can move to the the getOrigins Activity by clicking on the Get Directions Button. The MapsActivity asks for permission for using the device's location and proceeds getting the location of the todo and then after saving it, the user can also get directions to do the tasks(todo).

**GetOrigin** is an activity which is used to show the routes between the Origin location selected and the Location of the todo task. This activity is called only when the the location of the todo task has been set and the user clicks on the Get Directions Button.

## Documentation for Assignment 4(Individual)

In this individual assignment, I have made use of Google APIs. These include the:
- Google Places  API
- Google Maps SDK for Android
- Google Directions API

### Google Places API

The Google Places API has been used to get the Place details including the PlaceName, the Place Latitude and Longitude values. These values have been used in the MapsActivity to store the locationDetails of the ToDo task. a Google Place Autocomplete Fragment Search Bar has been added to both the MapsActivity and the GetOrigin activity. These search bars give the users auto-suggestions as they start typing in any place name or address. Once the place is selected, that places variables(name, latitude and longitude) are pulled out and used for storage or to show on the map. Furthermore, the Places API is also used to get the location details of the device being used.

### Google Maps SDK for Android

The Google Maops SDK is used in the ToDoList app in the MapsActivity and the getOrigin activity. This has been used to show the map on the phone screen. The would take the Place variables like the Place Latitude and Longitude and Place Name and use that information to show a pointer on the Map depicting that particular place. This visualization helps to give the user a better experience of the App. Furthermore, the map fragment is also used to depict the route between the origin and the taskLocation in the GetOrigin Activity.

### Google Directions API

This API is used i the GetOrigin Activity. When we select the Location for the origin, then this API is used to get the Directions between the ToDo task location and the the location for the origin. These details are then used to form a route, which is depicted as a line joining the two places.

## New Features added in the Individual Assignment

### Adding a Location
<img width="150" alt="addLocation" src="https://user-images.githubusercontent.com/77290018/205484404-331b8b19-702e-4f07-8aba-783a53472319.png">
<img width="150" alt="editLocation" src="https://user-images.githubusercontent.com/77290018/205483952-4d832cb3-7d56-46a9-9594-8dd5e3dd439d.png">
<img width="150" alt="DefaultMap" src="https://user-images.githubusercontent.com/77290018/205483949-e624f1b5-416a-4ba8-94d7-f1a3fab3f038.png">

The user can now add a location to ToDo task. This can be done in 2 ways:

#### Searching in the Places Autocomplete Fragment

<img width="150" alt="Autocomplete" src="https://user-images.githubusercontent.com/77290018/205483947-0adedf1f-2f91-4bab-84df-4275f1548af5.png">
<img width="150" alt="mapPointer" src="https://user-images.githubusercontent.com/77290018/205483955-fb8ffa48-abae-41ad-b7ae-428c5c778894.png">

This will give the user auto-suggestions depending on whatever the user is typing in the fragment.
APIs used:
- Places API to search for place details and get suggestions
- Maps SDK to show the selected point on the Map.

#### Using the Current Location

<img width="150" alt="CurrentLocation" src="https://user-images.githubusercontent.com/77290018/205483948-1d42c4ba-6fee-4a72-9f36-d105d00dd3de.png">

The user can use the current location of the device and save that as the ToDo location.
APIs used:
- Places API to search for place details(of the current location)
- Maps SDK to show the selected point on the Map.

### Getting the Direction to the ToDo task location

<img width="150" alt="distance_Directions" src="https://user-images.githubusercontent.com/77290018/205483951-6ec5f0a9-8092-4d8c-9c00-052fe2eb1df7.png">

The user can get the direction routes to the ToDo task location. This can be done by clicking on the Get Directions Button in the activity_maps.xml which taskes the user to the GetOrigin activity where the user needs to set a location for the Origin point which can be done as:

#### Using the Current Location As Origin Button

This is will use the current location as the origin point and will then show a route based upon the 2 points.
APIs used:
- Directions API to get the direction detail between the 2 places
- Maps SDK to show the route on the map

#### Searching in the Places Autocomplete Fragment

<img width="150" alt="Autocomplete" src="https://user-images.githubusercontent.com/77290018/205483947-0adedf1f-2f91-4bab-84df-4275f1548af5.png">

This will give the user auto-suggestions depending on whatever the user is typing in the fragment. The place selected will be set as origin and then the direction route will be obtained between these 2 places and then be shown on the map.
APIs used:
- Directions API to get the direction detail between the 2 places
- Maps SDK to show the route on the map
- Place API to get the Place details for the origin and its search suggestions.

### Distance between Origin and ToDo Location

<img width="150" alt="distance_Directions" src="https://user-images.githubusercontent.com/77290018/205483951-6ec5f0a9-8092-4d8c-9c00-052fe2eb1df7.png">

The GetOrigin Activity also calculates the distance between the point of Origin and the ToDo Task Location and shows the distance to the user.

Since our system already had a GUI, we made improvements to the existing GUI. We designed a custom icon and added that to our app. We also implemented general functionality and interface improvements, such as adding a search bar.

### Filtering Tasks Based Upon Location(Adjusted)

The user can also Filter his ToDo tasks based upon those that have their Location Set by selecting the 'Location' Tag under the Filters Menu.

<img width="150" alt="FilterLocationTag" src="https://user-images.githubusercontent.com/77290018/205483954-c6a897ba-807b-47ae-9774-38fdfe5805db.png">

## Compiling our code
There are two ways to compile our code:
1) clone the project in Android studio and run it on an emulator in the app
2) use the [apk file](https://github.com/COSC310-Team12/ToDoList/releases/download/v0.2.0/ToDoList-v0.2.0.apk) from our latest [release](https://github.com/COSC310-Team12/ToDoList/releases) to install the app on an Android phone

