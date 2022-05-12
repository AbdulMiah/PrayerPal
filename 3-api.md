**Mobile Development 2021/22 Portfolio**
# API description

Your username: `c2035950`

Student ID: `2035950`

_Complete the information above and then write your 300-word API description here.__

I decided to use Bottom Navigation to navigate through the app as this form of navigation felt the most natural over the other options such as Navigation Drawers or menus in a Tool Bar. Product Director at Google, Luke Wroblewski (2015), says “Obvious Always Wins” and that critical parts of an application should not be hidden behind menus as it could “negatively impact usage”. 
Instead on Bottom Navigation, I could have used Tabs; however, Tabs are often utilised when material is closely connected and of equal significance to one another. Bottom navigation made the most sense for me since I needed a mechanism for the user to transition between different types of content, and they’re also more ergonomically positioned at the bottom than Tabs.

Since opening an activity takes up an entire screen, switching between pages becomes less fluent as the user must wait for activities to load before they can proceed. In addition, as a developer, having multiple activities in a single application becomes difficult to manage. Therefore, I opted to use fragments as this approach allows me to keep the main layout of the app, which is a bottom navigation and background colour, and modularise the main content, allowing me to create a reusable user interface.

For the Dua feature, instead of storing the Dua data in a Room database I created my own RestAPI hosted on Pythonanywhere and retrieving the JSON response using Volley. I decided this approach because my plan for this feature is to keep adding supplications to the database to the point where there are hundreds, if not thousands of supplications. If I was to use Room, the device‘s storage would increase as I add more data to the database which is very inefficient.

<!-- 
Formative feedback from Sandy

- Your coverage of the BottomNav is excellent, you have done a really good job of laying out the alternative choices and why you didn't go with them, no comments on that.

- You have quite a lot of text about Fragments, but for me it's a little verbose. I don't think I entirely agree with your comment about switching activities being less fluent. That's more the result of UI choices. You could implement an app using Fragments and one not using Fragments and they could appear to be exactly the same. You have a sensible rationale for making use of fragments, but I'm not sure this is a case where there are really 'choices' as such. The kind of fuinctionality you've discussed basically requires fragments if you're building an app in a vaguely sensible manner. I wonder if there might be aspects of your work where the decisions you have to make between competing API choices are a little more salient?

- You description of the API access is good too, and you are painting the alternative nicely, although I think you could be a little more specific about what is being stored and so why it would continue to grow and grow if kept on device.

-->
