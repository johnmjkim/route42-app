package com.comp6442.route42.model;
import com.comp6442.route42.data.model.User;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserTest {
    User user = new User();
    List followerList = new ArrayList();
    List followingList = new ArrayList();
    List blockedList = new ArrayList<>();
    List blockedByList = new ArrayList<>();

    private void setRelations() {
        followerList.addAll(Arrays.asList("Abi", "Gail", "Geese"));
        followingList.addAll(Arrays.asList("Paul", "Kent", "Mike"));
        blockedList.addAll(Arrays.asList("Michael", "Ben", "Oliver"));
        blockedByList.addAll(Arrays.asList("hops", "clinton", "clare"));
        user.setFollowers(followerList);
        user.setFollowing(followingList);
        user.setBlocked(blockedList);
        user.setBlockedBy(blockedByList);
    }

    private void setInformation() {
        user.setId("f2ecbd83-66e1-49e7-bea5-52f8aaa14a83");
        user.setUserName("testuser");
        user.setEmail("test@gmail.com");
        user.setPassword("123456");
        user.setIsPublic(0);
        user.setProfilePicUrl("https://images.unsplash.com/photo-1415769663272-8504c6cc02b3?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODY1MTk&ixlib=rb-1.2.1&q=80&w=200");
    }

    @Before
    public void setupTest() {
        setRelations();
        setInformation();
    }

    @Test
    public void followerTest() {
        Assert.assertEquals(followerList, user.getFollowers());
    }

    @Test
    public void followingTest() {
        Assert.assertEquals(followingList, user.getFollowing());
    }
    @Test
    public void blockedByTest() {
        Assert.assertEquals(blockedByList, user.getBlockedBy());
    }
    @Test
    public void blockTest() {
        Assert.assertEquals(blockedList, user.getBlocked());
    }

    @Test
    public void idTest() {
        Assert.assertEquals(user.getId(), "f2ecbd83-66e1-49e7-bea5-52f8aaa14a83");
    }

    @Test
    public void infoTest() {
        Assert.assertEquals(user.getId(), "f2ecbd83-66e1-49e7-bea5-52f8aaa14a83");
        Assert.assertEquals(user.getUserName(), "testuser");
        Assert.assertEquals(user.getEmail(), "test@gmail.com");
        Assert.assertEquals(user.getPassword(), "123456");
        Assert.assertEquals(user.getIsPublic(), 0);
        Assert.assertEquals(user.getProfilePicUrl(), "https://images.unsplash.com/photo-1415769663272-8504c6cc02b3?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODY1MTk&ixlib=rb-1.2.1&q=80&w=200");
    }
    @Test
    public void toStringTest() {
        User user1 = new User("f2ecbd83-66e1-49e7-bea5-52f8aaa14a83",
                "test@gmail.com", "blue", followingList,
                followerList, "123456", 0,
                "https://images.unsplash.com/photo-1415769663272-8504c6cc02b3?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODY1MTk&ixlib=rb-1.2.1&q=80&w=200",
                blockedByList, blockedList);

      Assert.assertEquals("User{uid='f2ecbd83-66e1-49e7-bea5-52f8aaa14a83', email='test@gmail.com', userName='blue', isPublic=0, profilePicUrl='https://images.unsplash.com/photo-1415769663272-8504c6cc02b3?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODY1MTk&ixlib=rb-1.2.1&q=80&w=200', blockedBy=[hops, clinton, clare], following=[Paul, Kent, Mike], followers=[Abi, Gail, Geese], password='123456'}", user1.toString());
    }

}
