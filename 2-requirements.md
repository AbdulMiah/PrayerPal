**Mobile Development 2021/22 Portfolio**
# Requirements

Your username: `Abdul Miah`

Student ID: `2035950`

_Complete the information above and then enumerate your functional and non functional requirements below.__

Functional Requirements 
•	The application must allow users to navigate between different pages by using a Bottom Navigation bar
•	The application must give the user the option to accept or deny Location Permissions
•	If user agrees to Location Permissions, the application must display prayer times based off user’s location
•	If user denies Location Permissions, the application must let user pick a location from a map and display prayer times for that location
•	The application must contain a way for users to search on a map so they can see prayer times in different locations
•	The application must allow users to use a compass to find the direction of prayer
•	The application must allow users to track their prayers by letting them check off prayers they have already completed
•	The application must contain a list of supplications
•	The application must allow the user to click one of the supplications from the list so they can see the Arabic, Transliteration and Meaning of the supplication
•	The application must allow the user to search for supplications so they can easily find the one they want
•	The application must notify the user when it’s time for prayer so they can pray on time

Non-Functional Requirements 
•	When user launches app for the first time, the application shall request Location Permissions immediately
•	When the user orientates the device to landscape mode, the application shall stay in portrait mode
•	When the user grants Location Permissions, the application shall call the prayer API using user’s location within 5 seconds
•	When the user denies Location Permissions, the application shall take the user to a Map Activity withing 2 seconds
•	When the user selects their location on the Map, the application shall add a marker and animate towards that marker with a duration of 3000ms
•	When the user is using the compass and facing towards the Qibla (direction of prayer), the device shall vibrate continuously for 50ms
•	When the user clicks on a checkbox on the tracker, the device shall vibrate for 50ms
•	When the user navigates to the Dua (supplications) page, the application shall display a loading fragment immediately
•	Once the JSON response is received for the Dua page, the application shall display a ListView of all the supplications within 3 seconds
