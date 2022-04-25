**Mobile Development 2021/22 Portfolio**
# API description

Your username: `Abdul Miah`

Student ID: `2035950`

_Complete the information above and then write your 300-word API description here.__

I decided to use Bottom Navigation to navigate through the app as this form of navigation felt the most natural over the other options such as Navigation Drawers or menus in a Tool Bar. Product Director at Google, Luke Wroblewski (2015), says “Obvious Always Wins” and that critical parts of an application should not be hidden behind menus as it could “negatively impact usage”. 
Instead on Bottom Navigation, I could have used Tabs; however, Tabs are often utilised when material is closely connected and of equal significance to one another. Bottom navigation made the most sense for me since I needed a mechanism for the user to transition between different types of content, and they’re also more ergonomically positioned at the bottom than Tabs.

Since opening an activity takes up an entire screen, switching between pages becomes less fluent as the user must wait for activities to load before they can proceed. In addition, as a developer, having multiple activities in a single application becomes difficult to manage. Therefore, I opted to use fragments as this approach allows me to keep the main layout of the app, which is a bottom navigation and background colour, and modularise the main content, allowing me to create a reusable user interface.

For the Dua feature, instead of storing the Dua data in a Room database I created my own RestAPI hosted on Pythonanywhere and retrieving the JSON response using Volley. I decided this approach because my plan for this feature is to keep adding supplications to the database to the point where there are hundreds, if not thousands of supplications. If I was to use Room, the device‘s storage would increase as I add more data to the database which is very inefficient.
