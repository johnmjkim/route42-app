- brainstorming ideas
    -
- functional specifications
    - basic
        - Users must be able to login (not necessarily sign up). (up to 3 marks)
        - Users must be able to load (from ﬁle(s) or Firebase) and view posts (e.g. on a timeline activity). (up to 10 marks)
        - Users must be able to search for posts by tags (e.g. #COMP2100isTheBest). The search functionality must make use of a tokenizer and parser with a grammar of your own creation. (up to 3 marks without a tokenizer and parser. Up to 12 marks with a tokenizer and parser)
        - There must be a data ﬁle with at least 1,000 valid data instances. There must be a data ﬁle that is used to feed the social network app simulating a data stream. For example, every x seconds, a new item is read from a ﬁle. An item can be a post or an action (e.g. like, follow, etc). (up to 5 marks)

    - stage 2
    Improved Search 1. Search functionality can handle partially valid and invalid search queries. (medium)

    UI Design and Testing

    1. UI must have portrait and landscape layout variants as well as support for diﬀerent screen sizes.
    Simply using Android studio's automated support for orientation and screen sizes and or creating support without eﬀort to make them look reasonable will net you zero marks. (easy)

    2. UI tests using espresso or similar.
    Please note that your tests must be of reasonable quality. (For UI testing, you may use something such as espresso)

    a. Espresso is not covered in lectures/labs, but is a simple framework to write Android UI tests. (hard)

    Greater Data Usage, Handling and Sophistication

    1. Read data instances from multiple local ﬁles in diﬀerent formats (JSON, XML or Bespoken). (easy)

    @@ 2. User proﬁle activity containing a media ﬁle (image, animation (e.g. gif), video). (easy)

    @@ 3. Use GPS information (see the demo presented by our tutors. For example, your app may use the latitude/longitude to show posts). (easy)

    @@ 4. User statistics. Provide users with the ability to see a report of total views, total followers, total posts, total likes, in a graphical manner. (medium)

    5. Deletion method of either a Red-Black Tree and or AVL tree data structure. The deletion of nodes must serve a purpose within your application (e.g. deleting posts). (hard)

    a. Note that this advanced feature will only be considered if the Red-Black tree or AVL tree is the most suitable data structure to the App you are developing. Note that the deletion is not covered in lectures, this is optional (see deletion algorithm in the references of the data structure lecture).

    User Interactivity

    @@ 1. The ability to micro-interact with 'posts' (e.g. like, report, etc.) [stored in-memory]. (easy)

    2. The ability to repost a message from another user (similar to 'retweet' on Twitter) [stored in-memory]. (easy)

    3. The ability for users to ‘follow’ other users. There must be an adjustment to either the user’s timeline in relation to their following users or a section speciﬁcally dedicated to posts by followed users. [stored in-memory] (medium)

    4. The ability to send notiﬁcations based on diﬀerent types of interactions (posts, likes, follows, etc). A notiﬁcation must be sent only after a predetermined number of interactions are set (>= 2 interactions [e.g., one post and one follow or two posts or two follows]). Note that it is not mandatory to use the Android Notiﬁcation classes. (medium)

    5. Scheduled actions. At least two diﬀerent types of actions must be schedulable. For example, a user can schedule a post, a like, a follow, a comment, etc. (medium)

    User Privacy

    @@ 1. Friendship. Users may send friend requests which are then accepted or denied.

    (easy)

    @@ 2. Privacy I: A user must approve a friend's request based on privacy settings. (easy)

    @@ 3. Privacy II: A user can only see a proﬁle that is Public (consider that there are at least two types of proﬁles: public and private). (easy)

    4. Privacy III: A user can only follow someone who shares at least one mutual friend based on privacy settings. (medium)

    Peer to Peer Messaging

    1. Provide users with the ability to message each other directly. (hard)

    2. Privacy I: provide users with the ability to ‘block’ users. Preventing them from directly messaging them. (medium)

    3. Privacy II: provide users with the ability to restrict who can message them by some association (e.g. a setting for: can only message me if we are friends). (hard)

    4. Template messages or Macros (for peer to peer messaging or template posts (e.g. a quick one-tap post)). For example, "Hi %USERNAME%, I am not available now. Call to %PHONE_NUMBER% if it is urgent. Cheers, %MY_USERNAME%". The use of tokenizer and parser is mandatory. (hard)

    Firebase Integration

    @@ 1. Use Firebase to implement user Authentication/Authorisation. (easy)

    @@ 2. Use Firebase to persist all data used in your app (this item replace the requirement to retrieve data from a local ﬁle) (medium)

    @@ 3. Using Firebase or another remote database to store user posts and having a user’s timeline update as the remote database is updated without restarting the application. E.g. User A makes a post, user B on a separate instance of the application sees user A’s post appear on their timeline without restarting their application. (very hard)

- suggestion features
    - do we need permission for route tracking feature?
    - "live" route tracking

- ToDo after meeting
    - Create/clone group repo: https://gitlab.cecs.anu.edu.au/u7233149/software-construction-group-project
    - set up environment and try running the app ^
    - register/join team
    - think of features
    - kai : create brief guide on how to set up the project