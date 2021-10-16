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
| [uid] | [name] | Full-Stack |

## Conflict Resolution Protocol

- Conflicts will be resolved through civil discussion and democratic voting process involving all parties interested in the matter.
	- For example, if someone wants to change the direction or the concept of the app, everyone must be involved in the decision-making. If someone wants to change a small class in the project, then that can be done either through voting, or by mutual agreement upon directly discussing with the person who created the class.

## Application Description

*[What is your application, what does it do? Include photos or diagrams if necessary]*

*Here is a pet specific social media application example*

*PetBook is a social media application specifically targetting pet owners... it provides... certified practitioners, such as veterians are indicated by a label next to their profile...*

**Application Use Cases and or Examples**

*[Provide use cases and examples of people using your application. Who are the target users of your application? How do the users use your application?]*

*Here is a pet training application example*

*Molly wants to inquiry about her cat, McPurr's recent troublesome behaviour*
1. *Molly notices that McPurr has been hostile since...*
2. *She makes a post about... with the tag...*
3. *Lachlan, a vet, writes a reply to Molly's post...*
4. ...
5. *Molly gives Lachlan's reply a 'tick' response*

*Here is a map navigation application example*

*Targets Users: Drivers*

* *Users can use it to navigate in order to reach the destinations.*
* *Users can learn the traffic conditions*
* ...

*Target Users: Those who want to find some good restaurants*

* *Users can find nearby restaurants and the application can give recommendations*
* ...

*List all the use cases in text descriptions or create use case diagrams. Please refer to https://www.visual-paradigm.com/guide/uml-unified-modeling-language/what-is-use-case-diagram/ for use case diagram.*

## Application UML

![ClassDiagramExample](./images/ClassDiagramExample.png)
*[Replace the above with a class diagram. You can look at how we have linked an image here as an example of how you can do it too.]*

## Application Design and Decisions

*Please give clear and concise descriptions for each subsections of this part. It would be better to list all the concrete items for each subsection and give no more than `5` concise, crucial reasons of your design. 



### **Data Structures**

*[What data structures did your team utilise? Where and why?]*

- KD Tree
	- Where: REST API `GET /search/knn` with `k`, `lon`, `lat` parameters.
	- Why: KD Tree (K dimension tree) is used to store and search 2-D data of location (longitude, latitude). KD tree is useful when we need to use multi-dimensional data.
- HashMap
	- Where: Used by the REST API for union and intersection operations between lists of `Post`s.
	- Why:
- Binary Tree
	- Where:
		- REST API `POST /search/` with query encoded as plain text in `query` field of the body sent in JSON format.
		- `QueryTreeNode` is used to extract the hierarchical structure of nodes, each representing a binary operator and two expressions.
	- Why:



### **Design Patterns**

*[What design patterns did your team utilise? Where and why?]*

- Singleton

	- Where: `UserRepository`, `PostRepository`, and `FirebaseStorageRepository` classes under `repository` submodule.
	- Why: Prevents unnecessary creation of multiple instances of connections with the database. By using singleton, it saves memory and connection with the database is easier to manage.

- Repository pattern:

	- Where: `UserRepository`, `PostRepository` classes under `repository` submodule.
	- Why:

- Single-activity architecture

	- Where:
	- Why:

- REST API

	- Where:
	- Why:

- ViewModel

	- Where:
	- Why:

- Multi-threading / background execution

	- Where: `PhotoMapFragment`
	- Why: When making the REST API call to `search/knn`, the communication is handled by a background worker thread. This ensures the UI thread (the main thread) does not freeze and remains responsive.

- Factory method

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
*Use these to Search Query. 
Basically we classify keywords by '(',')','and','or','#' and text for tokenizations.
Our parsing is trying to follow MongoDB query language rule so we can clearly classify the searching type*
For examples
*"test" becomes {$hashtags: ["#test"]} // default search field is hashtag*

*"username: xxxx hashtags: #hashtag #android #app" becomes {$or: [ {"username": "xxx"}, {"hashtags": ["#hashtag", "#android", "#app"]} ]} // default search type is 'or' *

*"username: xxxx and hashtags: #hashtag #android #app" becomes {$and: [ {$userName: "xxx"}, {$hashtags: ["#hashtag", "#android", "#app"]} ]} // search type is 'and'*
  

### **Surpise Item**

*[If you implement the surprise item, explain how your solution addresses the surprise task. What decisions do your team make in addressing the problem?]*

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

*[What features have you tested? What is your testing coverage?]*

*Here is an example:*

*Number of test cases: ...*

*Code coverage: ...*

*Types of tests created: ...*

*Please provide some screenshots of your testing summary, showing the achieved testing coverage. Feel free to provide further details on your tests.*

## Implemented Features

*[What features have you implemented?]*

*Here is an example:*

*User Privacy*

1. *Friendship. Users may send friend requests which are then accepted or denied. (easy)*
2. *Privacy I: A user must approve a friend's request based on privacy settings. (easy)*
3. *Privacy II: A user can only see a profile that is Public (consider that there are at least two types of profiles: public and private). (easy)*
4. *Privacy III: A user can only follow someone who shares at least one mutual friend based on privacy settings. (Medium)*

*Firebase Integration*
1. *Use Firebase to implement user Authentication/Authorisation. (easy)*
2. *Use Firebase to persist all data used in your app (this item replace the requirement to retrieve data from a local file) (medium)*

*List all features you have completed in their separate categories with their difficulty classification. If they are features that are suggested and approved, please state this somewhere as well.*

## Team Meetings

*Here is an example:*

- *[Team Meeting 1 at 31st August](https://gitlab.cecs.anu.edu.au/u7233149/software-construction-group-project/-/blob/report/docs/meetings/aug31.md)*
- *[Team Meeting 2 at 7th September](https://gitlab.cecs.anu.edu.au/u7233149/software-construction-group-project/-/blob/report/docs/meetings/sep7.md)*
- *[Team Meeting 3 at 8th October](https://gitlab.cecs.anu.edu.au/u7233149/software-construction-group-project/-/blob/report/docs/meetings/oct8.md)*

*Either write your meeting minutes here or link to documents that contain them. There must be at least 3 team meetings.*
