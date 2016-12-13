# Student Companion App
Student Companion App is an Android application developed as a submission for 300CEM an Android Development Module at Coventry University.
The aim of the app is to help students succeed by providing them with tools necessary to stay organised and on top of their assignments and other student responsibilities.

# Layout
The app uses a tabbed layout, this means that all the most important features of the app are on the main screen of the app. There are no complicated menus to get to another feature, the only thing the user has to do to access another main feature of the app is to scroll right or left. To achieve this fragments are used. Every tab is a seperate fragment that can have its own layout and functionality independent to the activity its running in.

# Sections

## Notes Fragment
The notes fragment allows the user to quickly list down things to do, each item has a checkmark next to it, when clicked it checks the item as done. This feature is great for lists of assignments to be done,
or upcoming deadlines.
## Voice Memos Fragment
This fragment allows users to record audio and playback audio clips of any length. This feature can be used for short voice memos to remember something or even recording longer audio files such as lectures.
## Class Locator Fragment
The class locator fragment utilizes the google maps API to let the user put markers on the map with a name attached. These markers are saved by the application. When the marker is clicked the user can then
press another button which will take them to the google maps app which will provide them with the directions to the selected marker from the users current position. Great for students who aren't familiar with their University yet.
# Data Persistence
Data persistence in this application is achieved by using SQL databases and saving to the internal memory of the phone. The notes and markers are saved using simple SQL databases while the voice memos are recorded straight to the internal storage of the phone.

# Youtube Video
The following video goes in-depth about every major part of the applicaiton, explaining the functionality behind it.
https://www.youtube.com/watch?v=w6k3mTyt-Ic
