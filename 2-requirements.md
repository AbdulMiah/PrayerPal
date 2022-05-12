**Mobile Development 2021/22 Portfolio**
# Requirements

Your username: `c2035950`

Student ID: `2035950`

_Complete the information above and then enumerate your functional and non functional requirements below.__

### Functional Requirements

- The application must allow users to navigate between different pages by using a Bottom Navigation bar
- If user agrees to Location Permissions, the application must display prayer times based off user’s location and save the location data in a SharedPreference
- If user denies Location Permissions, the application must let user pick a location from a map and display prayer times for that location – location data must be saved in a SharedPreference
- The map must contain a way for users to search so they can easily find prayer times in different locations - save that location data in a SharedPreference
- When the application is re-opened, the application must use the location data saved in the SharedPreference to display prayer times
- The application must allow users to use a compass to find the direction of prayer
- The compass must use the location data saved in the SharedPreference to find the direction of prayer
- When the user is using the compass and facing towards the Qibla (direction of prayer), the device must vibrate continuously for 50ms
- The application must allow users to track their prayers by letting them check off prayers they have already completed
- The tracker must allow the user to interact with a calendar so they can see prayers they have previously completed
- The application must contain a list of supplications
- The application must allow the user to click one of the supplications from the list so they can see the Arabic, Transliteration and Meaning of the supplication
- The application must allow the user to search for supplications by title so they can easily find the one they want
- The application must notify the user when it’s time for prayer so they can pray on time

---

### Non-Functional Requirements

- The application must be locked in portrait mode
- The font used in the application must be consistent
- The application must have consistent styling and colour theme
- The application shall scale the font by the user’s font size preference
- When user launches app for the first time, the application shall request Location Permissions immediately
- When the user grants Location Permissions, the application shall call the prayer API using user’s location within 5 seconds
- When the user denies Location Permissions, the application shall take the user to a Map Activity within 2 seconds
- When the user selects their location on the Map, the application shall add a marker and animate towards that marker with a duration of 3000ms
- The map activity should be possible to use in landscape mode
- When the user clicks on a checkbox on the tracker, the device shall vibrate for 50ms
- When the user clicks on one of the checkboxes on the tracker, the application must save the tracker data in the Room database immediately
- When the user navigates to the Dua (supplications) page, the application shall display a loading fragment immediately
- Once the JSON response is received for the Dua page, the application shall display a ListView of all the supplications within 3 seconds
