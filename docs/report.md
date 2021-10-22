# Route 42 Design Document

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

## Team

### Team Members and Roles

| UID | Name | Role |
| :--- | :----: | ---: |
| u7233149 | Kai Hirota | Full-Stack |
| u7269158 | John (Min Jae) Kim | Data Structure, Feature Testing |
| u7234659 | Honggic Oh | Search, Feature Testing |
| u7199021| Theo Darmawan | Full-Stack |

### Meeting minutes

- [Meeting 1 - 31st August](https://gitlab.cecs.anu.edu.au/u7233149/software-construction-group-project/-/blob/report/docs/meetings/aug31.md)
- [Meeting 2 - 7th September](https://gitlab.cecs.anu.edu.au/u7233149/software-construction-group-project/-/blob/report/docs/meetings/sep7.md)
- [Meeting 3 - 8th October](https://gitlab.cecs.anu.edu.au/u7233149/software-construction-group-project/-/blob/report/docs/meetings/oct8.md)

### Conflict Resolution Protocol

- Conflicts will be resolved through civil discussion and democratic voting process involving all parties interested in the matter.
	- For example, if someone wants to change the direction or the concept of the app, everyone must be involved in the decision-making. If someone wants to change a small class in the project, then that can be done either through voting, or by mutual agreement upon directly discussing with the person who created the class.
	

## Application Description

**Targets Users: Workout Enthusiasts**

Route42 is a social networking app for athletes of various levels. With Route42, users can:

1. Record workouts, including walking, running, and cycling.
2. Track performance metrics and see the recorded workouts in an interactive map.
3. Follow other users, and view and like other people's workouts.
4. Search for posts by username, hashtags, and proximity to the user's location

==TODO add screenshots==

### Use Case Example

1. Athletic Activity Tracking and Sharing
	1. Michael runs at ANU running club, and wants to record and share his daily runs.
	2. Once ready, he starts the `run` activity on the app
	3. The app tracks Michael's location and route, and display it on a map. Performance metrics are displayed in real time.
	4. After finishing his run, Michael ends the `run` activity on the app.
	5. The app will display a post template for sharing the completed activity.
	6. Michael may write a description and add hashtags like `#ANUrunning` before sharing it. The app will extract the hashtags and tag the post for you.
	7. The created post can immediately be viewed by other users on their feeds. 
	8. Michael can also select his past posts or posts created by others and see an interactive map of the route associated with the post.
2. Social networking and searching
	1. Emily is an avid runner who recently started competing in marathons. Emily wants to connect with other aspiring athletes.
	2. Emily can search for posts on Route42 app by username and hashtags, and look at other athletes and their workouts and routes.
	3. Emily can also search for posts by geographical proximity, and the app will visualize the places where others logged their workouts on an interactive map.

##### Scheduled Actions

If the user does not have an active internet connection, the app allows scheduling of posts and likes.
To schedule a post, the user checks the `schedule` button and selects the time delay.
To schedule a like, the user long-clicks the like button and selects the time delay.

##### Pausing a workout
If the user needs to pause the workout, they can manually do so. Otherwise, navigating away from the `Activity` screen will automatically pause it for them.

## Diagrams

### Architecture

<img src="report.assets/Architecture.png" alt="Architecture" style="zoom:67%;" />

[Link](https://app.creately.com/diagram/K2ScahytOcK)

### Mobile App

<img src="report.assets/Route42%20UML.png" alt="Route42 UML" style="zoom:80%;" />

[Link](https://lucid.app/lucidchart/d393dc76-9233-4176-90cd-def360405cbf/edit?invitationId=inv_ca06d23e-a397-4482-a2fc-3623c77f8ec2)

### REST API

<img src="report.assets/Route42%20REST%20API%20UML.png" alt="Route42 REST API UML" style="zoom:80%;" />

[Link](https://lucid.app/lucidchart/7f02648c-8f14-4af9-95ab-6c9379064044/edit?invitationId=inv_8e28283d-2136-4e55-8941-7e763d5021cf)

## Design Decisions

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

  - <img src="https://oozou.com/rails/active_storage/blobs/eyJfcmFpbHMiOnsibWVzc2FnZSI6IkJBaHBBcVVvIiwiZXhwIjpudWxsLCJwdXIiOiJibG9iX2lkIn19--c8573fcc38b58509d10a83145f6b519d306ed039/1*VSXfNBCsxa3_wCOAqR88aQ.png" alt="img" style="zoom:50%;" />

    [Source](https://oozou.com/rails/active_storage/blobs/eyJfcmFpbHMiOnsibWVzc2FnZSI6IkJBaHBBcVVvIiwiZXhwIjpudWxsLCJwdXIiOiJibG9iX2lkIn19--c8573fcc38b58509d10a83145f6b519d306ed039/1*VSXfNBCsxa3_wCOAqR88aQ.png)
    
  - What: Composition of Android application based on single or a couple activities, each managing one or more fragments.

  - Where: Entire application.

  - Why:
    - Route42 primarily has one activity called `MainActivity` which contains a fragment container view, which swaps between fragments based on user interactions.
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
	- Why: By storing data in a view model class, data is not deleted when views are destroyed (e.g. when the user navigates to another page, or when the phone is rotated). Also, by listening to changes to `LiveData` members of the view model, views can update directly to changes in persistent data stored in Firebase, through listening to the `LiveData` class. This improves separation of UI layer from the data layer, as the UI is not dependent on any repository classes.

- Multi-threading / background execution

  - Where: `PhotoMapFragment`, `ScheduleablePost` , `SchedulableLike`
  - Why: When making the REST API call to `search/knn`, the communication is handled by a background worker thread. This ensures the UI thread (the main thread) does not freeze and remains responsive.
  Scheduled actions involving IO operations and network calls are also handled in the background to minimize load on the UI thread.

### **Grammars**

*Production Rules* <br>
\<Term> ::=  \<Factor> | \<Term> + \<Term> | \<Term> + \<Connector>
<br>
\<Factor> ::= \<Keyword> | \<bracket>
<br>
\<Connector> ::= \<and> | \<or>
<br>

*[How do you design the grammar? What are the advantages of your designs?]*<br>

- Our grammar is simple and obvious. Dividing input data into factors and connectors to understand it easily.<br>
- This is using for search data so mostly input data is keyword and need to classify it to bracket and connectors <br>

*If there are several grammars, list them all under this section and what they relate to.*

### **Tokenizer and Parsers**

*[Where do you use tokenisers and parsers? How are they built? What are the advantages of the designs?]*

Every token either contains an operator and two expressions, or a key and value.<br>Tokens are extracted by prioritizing parenthesis, and then extracting from left to right. <br>Example 1 below is an example of a token that has the key `hashtags` and value `["test"]`. <br>For example, if a query consists of 10 hashtags chained by OR, then the resulting `QuerySyntaxTree` will be equivalent to a linked list, where each node only has a right child.

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

1. Search functionality can handle partially valid and invalid search queries. (medium)

1. *Bug 1:*

- *A space bar (' ') in the sign in email will crash the application.*
- ... 

2. *Bug 2:*
3. ...

*List all the known errors and bugs here. If we find bugs/errors that your team do not know of, it shows that your testing is not through.*

## Testing Summary

- Number of test cases: 13
	- UI Tests : 15
	- Unit Tests : ??(update later)

- Types of tests created: ...

| UI/Unit Tests  |  Class Name  | Test Description | Code Coverage | Numbers of Tests |
|      :---:     |    :----:   |      :---      |     :----:     |     :----:     |
| UI             |  LoginTest  | <ul><li>Check login with correct and wrong id and password</li></ul> | N/A           | 2 |
| UI             |  MainTest   | <ul><li>Switch the page throughout navigation bar</li><li>create posts and check the post is properly made</li><li>cancel making a post</li><li>making a schduled post</li><li>like and unlike the post</li><li> block and unblock user</li><li>follow and unfollow user</li><li>block following user</li><li>follow blocked user</li></ul> | N/A           | #of tests |
| Unit           |  KNearestNeighbourServiceTest  | <ul><li>Test call method of rest-api service post</li><li>Test printing Strings</li></ul> | 70%           | 2 |
| Unit           |  QueryStringTest  | <ul><li>Test query is properly made</li></ul> | 100%           | 2 |
| Unit           |  SearchServiceTest  | <ul><li>Test search input data is properly made to the query</li><li>Calling query</li></ul> | 100%           | 1 |
| Unit           |  CryptoTest  | <ul><li>Check Encryption</li></ul> | 100%           | 3 |
| Unit           |  UserListAdapterTest  | <ul><li>?</li></ul> | ?           | ? |
| Unit           |  BaseActivityTest  | <ul><li>Check factors</li></ul> | 100%           | 1 |
| Unit           |  PointTest  | <ul><li>Check latitude and longitude</li></ul> | 75%           | 4 |
| Unit           |  UserTest  | <ul><li>Check factors</li></ul> | 92%           | 7 |
| Unit           |  SchedulableTest  | <ul><li>?</li></ul> | ?           | ? |
| Unit           |  UserViewModelTest  | <ul><li>Tests method of getUser and setUser</li> | 75%           | 2 |
| Unit           |  PostTest  | <ul><li>Check factors</li><li>Check extracting hashtags from input text</li></ul> | 86%           | 17 |
| Unit           |  ActiveMapViewModelTest  | <ul><li>Check activity data and types</li><li>Check elapsed time</li><li>Check last update time</li><li>Check reset function</li><li>Check pastlocation</li><li>Check snapshot file name</li></ul> | 73%           | 7 |


*Please provide some screenshots of your testing summary, showing the achieved testing coverage. Feel free to provide further details on your tests.*

## Implemented Features

- Easy: 6
- Medium: 6
- Hard: 1
- Very Hard: 1



Improved Search

1. Search functionality can handle partially valid and invalid search queries. (medium)

UI Design and Testing

1. UI tests using espresso or similar. Please note that your tests must be of reasonable quality. (For UI testing, you may use something such as espresso) (hard)

Greater Data Usage, Handling and Sophistication

1. Read data instances from multiple local files in different formats (JSON, XML or
Bespoken). (easy)
2. User profile activity containing a media file (image, animation (e.g. gif), video). (easy)
3. Use GPS information. (easy)
4. User statistics. Provide users with the ability to see a report of total views, total followers, total posts, total likes, in a graphical manner. (medium)

User Interactivity

1. The ability to micro-interact with 'posts' (e.g. like, report, etc.) [stored in-memory]. (easy)
2. The ability for users to ‘follow’ other users. There must be an adjustment to either the user’s timeline in relation to their following users or a section specifically dedicated to posts by followed users. [stored in-memory] (medium)
5. Scheduled actions. At least two different types of actions must be schedulable. For
example, a user can schedule a post, a like, a follow, a comment, etc. (medium)

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

