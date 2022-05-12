**Mobile Development 2021/22 Portfolio**
# API description

Your username: `c2035950`

Student ID: `2035950`

_Complete the information above and then write your 300-word API description here.__


I decided to use Bottom Navigation to navigate through the app as this form of navigation felt the most natural over the other options such as Navigation Drawers or menus in a Tool Bar. Product Director at Google, Wroblewski (2015), says “Obvious Always Wins” and that critical parts of an application should not be hidden behind menus as it could “negatively impact usage”.

Instead on Bottom Navigation, I could have used Tabs; however, Tabs are often utilised when material is closely connected and of equal significance to one another. Bottom navigation made the most sense for me since I needed a mechanism for the user to transition between different types of content, and they’re also more ergonomically positioned at the bottom than Tabs.

For the Dua feature, instead of storing the Dua data in a Room database I created my own RestAPI hosted on Pythonanywhere and retrieving the JSON response using Volley. Since there are a lot of supplications in Islam, I decided this approach because my plan for this feature is to keep adding supplications to the database to the point where there are hundreds, if not thousands of supplications. If I was to use Room, the device‘s storage would increase as I add more data to the database which is incredibly inefficient. On the other hand, going with this approach will provide a better user experience since the user does not require an internet connection to access data from Room.

To send notifications in the background, I decided to use Services. Instead of Service, I could have used the WorkManager API, which Google recommends over the other Service classes. However, WorkManager’s intended use is to run heavy background work that are deferrable and are not required to run immediately (Alangode, 2020). So scheduling notifications would not be an appropriate use of WorkManager.

---

### Reference

Wroblewski. L (2015) **_Obvious Always Wins_** [Online] Available at: [https://www.lukew.com/ff/entry.asp?1945](https://www.lukew.com/ff/entry.asp?1945)  (Accessed on: 24/04/2022)

Alangode. H (2020) **_I want to schedule notification on specific date should I use workmanager?_** [Online] Available at: [https://stackoverflow.com/questions/60538366/i-want-to-schedule-notification-on-specific-date-should-i-use-workmanager](https://stackoverflow.com/questions/60538366/i-want-to-schedule-notification-on-specific-date-should-i-use-workmanager) (Accessed on: 12/05/2022)