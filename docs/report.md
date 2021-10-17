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
| u7233149 | Kai Hirota | Lead / Architect |
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

### Use cases

With Route42, users can:

* Record various workouts, including walking, running, and cycling.
* Track performance metrics and see the recorded workouts in an interactive map.
* Follow other users, and view and like other people's workouts.

### Examples

1. Scenario: As an avid runner, Adam wants to record his daily jogs.
	1. When heading out for a run, Adam brings his smartphone, like he usually does in order to listen to music.
	2. Once Adam has warmed up and is ready to start his workout, he will open the Route42 app, tap on the `+` button to create a new post.
	3. Adam selects which kind of work out he will be engaging in, and then tap on the start button to begin the tracking.
	4. Until Adam taps on "pause" or "end activity," the app will record Adam's location every 10 seconds.
	5. Once Adam finishes a workout, he can create a post of the completed workout, and add multiple hashtags.
2. Scenario: As a triathlete, Emily wants to record her various types of workouts and analyze the performance change over time.
	1. After using Route42 to log a workout, Emily can edit the post description to note any self-evaluation.
	2. After having used the app to record several workouts, Emily can view her own profile to see her past recorded workouts.
	3. Since performance metrics like distance and pace are recorded as well, Emily will be able to see how her performance has changed.
	4. Emily will also be able to see the instantaneous metrics during her workout, such as the current location, speed, distance from the starting point, elapsed time, and calories burned.
	5. Sometimes, Emily may choose to run or cycle in remote locations. She can record the workout, and schedule the post to be posted later.
3. Scenario: As a Software Engineer approaching 35 years old, Jesus would like to start getting in the habit of jogging, but have not been able to start.
	1. Jesus can follow his friends and strangers on Route42 app to see how others are working out.
	2. Jesus sees the improvement in metrics such as distance and pace over time, and gets a better idea of the rate of progress he could strive towards.
	3. Jesus feels more motivated to start working out after seeing his friends improve over time.
	4. When tired mid-workout, Jesus can also pause the workout and rest.
	5. Navigating away from the Activity screen will automatically the workout. 

### UML

![ClassDiagramExample](./images/ClassDiagramExample.png)
*[Replace the above with a class diagram. You can look at how we have linked an image here as an example of how you can do it too.]*

### Architecture

![img](Report.assets/final-architecture.png)

 [Source](https://developer.android.com/jetpack/guide)

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
	- Why: By storing data in a view model class, data is not deleted when views are destroyed (e.g. when the user navigates to another page, or when the phone is rotated). Also, by listening to changes to `LiveData` members of the view model, views can update directly to changes in persistent data stored in Firebase, through the `LiveData` class. This improves separation of UI layer from the data layer, as database references only exist in the view model class. 

- Multi-threading / background execution

  - Where: `PhotoMapFragment`
  - Why: When making the REST API call to `search/knn`, the communication is handled by a background worker thread. This ensures the UI thread (the main thread) does not freeze and remains responsive.


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

## Appendix
