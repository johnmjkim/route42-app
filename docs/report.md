# Route 42 Report

## Table of Contents

1. [Team Members and Roles](#team-members-and-roles)
2. [Conflict Resolution Protocol](#conflict-resolution-protocol)
2. [Application Description](#application-description)
3. [Application UML](#application-uml)
3. [Application Design and Decisions](#application-design-and-decisions)
4. [Summary of Known Errors and Bugs](#summary-of-known-errors-and-bugs)
5. [Testing Summary](#testing-summary)
6. [Implemented Features](#implemented-features)
7. [Team Meetings](#team-meetings)

## Team Members and Roles

| UID | Name | Role |
| :--- | :----: | ---: |
| u7233149 | Kai Hirota | Lead / Architect |
| u7269158 | John (Min Jae) Kim | Data Structure, Feature Testing |
| u7234659 | Honggic Oh | Search,Feature Testing |
| u7199021| Theo Darmawan | Full-Stack |

## Conflict Resolution Protocol

- Conflicts will be resolved through civil discussion and democratic voting process involving all parties interested in the matter.
	- For example, if someone wants to change the direction or the concept of the app, everyone must be involved in the decision-making. If someone wants to change a small class in the project, then that can be done either through voting, or by mutual agreement upon directly discussing with the person who created the class.
	

## Application Description


**Targets Users: Workout enthusiast**

* *Users can use the app to share their workouts*
* *Users can use the app during workout to track metrics and route*
* *Users can view and like other people's workouts*

The Route42 App allows users to collect data on their workout activities including running, cycling , and walking, and posting the activities to the social network feed. During the activity itself, the app collects location data and displays real time location, speed, duration and calories burned.

Users can easily track their current location and route on a map element. They can also choose to pause or resume the activity at their discretion by pressing the Activity button and selecting the respective option from the menu. Navigating away from the Activity screen will automatically pause their workout. 

Once users have completed their workout, they end the Activity and are given the option to create a post of that activity. The app will include the route map (properly scaled), and a summary of the activity data.
Users can also add their own post description, including multiple hashtags, which will be recognized and added to the user's post. 

Additionally users may choose to schedule their posting to the network for later, for example if they are without an internet connection, using a simple toggle.


**Use Case Example**
[To-do: insert screengrabs]
1. Bambang is a student at ANU. He is ready to start a run around campus.
2. Bambang starts up Route42 and clicks Add button to begin a new Activity
3. He starts the activity GPS data tracking and begins his run.
4. During the run, he notices he has his shoelaces untied. 
5. He pauses the activity, ties his shoes, and resumes the activity.
6. Bambang finishes the run! He ends the activity.
7. He writes a cool caption for sharing his achievement. 
8. Unfortunately he is out of data at this moment, but he chooses to schedule the post in 30 minutes. Problem solved. 





*List all the use cases in text descriptions or create use case diagrams. Please refer to https://www.visual-paradigm.com/guide/uml-unified-modeling-language/what-is-use-case-diagram/ for use case diagram.*

## Application UML

![ClassDiagramExample](./images/ClassDiagramExample.png)
*[Replace the above with a class diagram. You can look at how we have linked an image here as an example of how you can do it too.]*

## Application Design and Decisions

### **Data Structures**

- KD Tree
	- Where: REST API `GET /search/knn` with `k`, `lon`, `lat` parameters.
	- Why: KD Tree (K-dimensional tree) is used to store and search 2-D data of location (longitude, latitude). KD tree is useful for finding nearest neighbors and performing range search based on multiple dimensions of data - such as longitude and latitude.
- HashMap
	- Where: Used by the REST API for union and intersection operations between lists of `Post`s.
	- Why: It's the most efficient way of finding set union and intersections. This is used to chain the left and right results when executing commands in `QuerySyntaxTree`.
- Binary Tree
	- Where:
		- REST API `POST /search/` endpoint uses `QuerySyntaxTree` to process the query text sent by a client.
		- `QueryTreeNode` is used to extract the hierarchical structure of nodes, each representing a binary operator and two expressions.
	- Why:
		- It allows efficient parsing of tokens, and allows us to express various operations in a recursive manner, making the code easy to read and maintain.



### **Design Patterns**

- Singleton & Repository

  - Where: `UserRepository` and `PostRepository` classes under `repository` submodule.
  - Why: 
  	- Singleton pattern prevents unnecessary creation of multiple instances of connections with the database. By using singleton, the program uses less memory, and managing the connection with the database is easier.
  	- Repository pattern abstracts the database operations and allows decoupling of the database access logic from the application logic.

- Single-activity architecture

  - What: Composition of Android application based on single or a couple activities, each managing one or more fragments.
  - Where: Entire application.
  - Why:
  	- Route42 primarily has one activity called `MainActivity` which contains a fragment container view, which transitions between fragments based on user selection from the bottom navigation bar.
  	- Usage of this architecture reduces lines of code required for the whole app, while making it easier to prototype new features.

- REST API

  - Where: In the cloud (AWS EC2 instance)
  - Why: 
  	- When using Cloud Firestore Android SDK, we have some limitations.
  		- Cannot perform partial text search - for example, we cannot query on substring of a text field.
  		- Cannot use more than one `arrayContains` in a single query.
  		- No support for boolean OR operation between multiple filters.
  		- Every CRUD operation must be asynchronous in order to not freeze up the UI thread.
  	- Using the REST API allows us to use the Firebase-admin SDK, which gives the REST API higher privilege and more capability than the mobile client.
  	- Using REST API allows us to simplify database reads and writes. The downside is that we sacrifice Firestore's document listener feature, where we can listen to updates on documents of interest.

- ViewModel
	- What: An intermediate observable class which enables data to persist independent of fragment lifecycle and attaching listeners to data changes.  
	- Where: `ActiveMapViewModel` and `UserViewModel`
	- Why: By storing data in a view model class, data is not deleted when views are destroyed (e.g. when the user navigates to another page, or when the phone is rotated). Also, by listening to changes to `LiveData` members of the view model, views can update directly to changes in persistent data stored in Firebase, through the `LiveData` class. This improves separation of UI layer from the data layer, as database references only exist in the view model class. 

- Multi-threading / background execution

  - Where: `PhotoMapFragment`
  - Why: When making the REST API call to `search/knn`, the communication is handled by a background worker thread. This ensures the UI thread (the main thread) does not freeze and remains responsive.

  

  <img src="https://oozou.com/rails/active_storage/blobs/eyJfcmFpbHMiOnsibWVzc2FnZSI6IkJBaHBBcVVvIiwiZXhwIjpudWxsLCJwdXIiOiJibG9iX2lkIn19--c8573fcc38b58509d10a83145f6b519d306ed039/1*VSXfNBCsxa3_wCOAqR88aQ.png" alt="img" style="zoom:50%;" />

  [Source](https://oozou.com/rails/active_storage/blobs/eyJfcmFpbHMiOnsibWVzc2FnZSI6IkJBaHBBcVVvIiwiZXhwIjpudWxsLCJwdXIiOiJibG9iX2lkIn19--c8573fcc38b58509d10a83145f6b519d306ed039/1*VSXfNBCsxa3_wCOAqR88aQ.png)

  ![img](Report.assets/final-architecture.png)

   [Source](https://developer.android.com/jetpack/guide)

### **Grammars**

*Search Engine*
<br> *Production Rules* <br>
\<Non-Terminal> ::= \<some output>
<br>
\<Non-Terminal> ::= \<some output>

*[How do you design the grammar? What are the advantages of your designs?]*

*If there are several grammars, list them all under this section and what they relate to.*

### **Tokenizer and Parsers**

*[Where do you use tokenisers and parsers? How are they built? What are the advantages of the designs?]*

Every token either contains an operator and two expressions, or a key and value. Example #1 below is an example of a token that has the key `hashtags` and value `["test"]`. Tokens are extracted by prioritizing parenthesis, and then extracting from left to right. For example, if a query consists of 10 hashtags chained by OR, then the resulting `QuerySyntaxTree` will be equivalent to a linked list, where each node only has a right child.

```
EXAMPLES
1. "test" -> {hashtags: ["test"]}

2. "username: xxx hashtags: #hashtag #android #app" ->
{OR: [
    {userName: "xxx"}, 
    {hashtags: ["#hashtag", "#android", "#app"]}
  ]
}

3. "(username: xxxx or hashtags: #hashtag #android #app) and username: yyy" ->
 {AND: [
     {OR: [
         {userName: "xxx"}, 
         {hashtags: ["#hashtag", "#android", "#app"]}
     ]},
     {userName: "yyy"}
 ]}

```

### **Other**

*[What other design decisions have you made which you feel are relevant? Feel free to separate these into their own subheadings.]*

## Summary of Known Errors and Bugs

*[Where are the known errors and bugs? What consequences might they lead to?]*

*Here is an example:*

1. *Bug 1:*

- *A space bar (' ') in the sign in email will crash the application.*
- ... 

2. *Bug 2:*
3. ...

*List all the known errors and bugs here. If we find bugs/errors that your team do not know of, it shows that your testing is not through.*

## Testing Summary

## Implemented Features

- Easy: 5
- Medium: 5
- Hard: 1
- Very Hard: 1



Improved Search

1. Search functionality can handle partially valid and invalid search queries. (medium)

UI Design and Testing

1. UI tests using espresso or similar. Please note that your tests must be of reasonable quality. (For UI testing, you may use something such as espresso) (hard)

Greater Data Usage, Handling and Sophistication

1. User profile activity containing a media file (image, animation (e.g. gif), video). (easy)
2. Use GPS information. (easy)
3. User statistics. Provide users with the ability to see a report of total views, total followers, total posts, total likes, in a graphical manner. (medium)

User Interactivity

1. The ability to micro-interact with 'posts' (e.g. like, report, etc.) [stored in-memory]. (easy)
2. The ability for users to ‘follow’ other users. There must be an adjustment to either the user’s timeline in relation to their following users or a section specifically dedicated to posts by followed users. [stored in-memory] (medium)

User Privacy

1. Privacy II: A user can only see a profile that is Public (consider that there are at least two types of profiles: public and private). (easy)

Peer to Peer Messaging

1. Privacy I: provide users with the ability to ‘block’ users. Preventing them from

	directly messaging them. (medium)

Firebase Integration

1. Use Firebase to implement user Authentication/Authorisation. (easy)

2. Use Firebase to persist all data used in your app (this item replace the requirement

	to retrieve data from a local file) (medium)

3. Using Firebase or another remote database to store user posts and having a user’s

	timeline update as the remote database is updated without restarting the application. E.g. User A makes a post, user B on a separate instance of the application sees user A’s post appear on their timeline without restarting their application. (very hard)

## Team Meetings

- [Meeting 1 - 31st August](https://gitlab.cecs.anu.edu.au/u7233149/software-construction-group-project/-/blob/report/docs/meetings/aug31.md)
- [Meeting 2 - 7th September](https://gitlab.cecs.anu.edu.au/u7233149/software-construction-group-project/-/blob/report/docs/meetings/sep7.md)
- [Meeting 3 - 8th October](https://gitlab.cecs.anu.edu.au/u7233149/software-construction-group-project/-/blob/report/docs/meetings/oct8.md)

