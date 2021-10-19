package com.comp6442.route42.model;

import com.comp6442.route42.data.model.User;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class UserTest {
    User user = new User();
    List followerList = new ArrayList();
    List followingList = new ArrayList();
    List banList = new ArrayList();
    List bannedByList = new ArrayList();

    @Test
    public void followerTest(){
        followerList.add("Abi");
        followerList.add("Gail");
        followerList.add("Geese");
        user.setFollowers(followerList);
        Assert.assertEquals(user.getFollowers(),followerList);
    }

    @Test
    public void followingTest() {
        followingList.add("Paul");
        followingList.add("Kent");
        followingList.add("Mike");
        user.setFollowing(followingList);
        Assert.assertEquals(user.getFollowing(), followingList);
    }
    @Test
    public void blockedByTest() {
        bannedByList.add("hops");
        bannedByList.add("clinton");
        bannedByList.add("clare");
        user.setBlockedBy(bannedByList);
        Assert.assertEquals(user.getBlockedBy(), bannedByList);
    }
    @Test
    public void blockTest() {
        banList.add("Michael");
        banList.add("Ben");
        banList.add("Oliver");
        user.setBlocked(banList);
        Assert.assertEquals(user.getBlocked(), banList);
    }

    @Test
    public void infoTest() {
        user.setId("/users/f2ecbd83-66e1-49e7-bea5-52f8aaa14a83");
        user.setUserName("testuser");
        user.setEmail("test@gmail.com");
        user.setPassword("123456");
        user.setIsPublic(0);
        user.setProfilePicUrl("https://images.unsplash.com/photo-1415769663272-8504c6cc02b3?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODY1MTk&ixlib=rb-1.2.1&q=80&w=200");
        Assert.assertEquals(user.getId(), "/users/f2ecbd83-66e1-49e7-bea5-52f8aaa14a83");
        Assert.assertEquals(user.getUserName(), "testuser");
        Assert.assertEquals(user.getEmail(), "test@gmail.com");
        Assert.assertEquals(user.getPassword(), "123456");
        Assert.assertEquals(user.getIsPublic(), 0);
        Assert.assertEquals(user.getProfilePicUrl(), "https://images.unsplash.com/photo-1415769663272-8504c6cc02b3?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODY1MTk&ixlib=rb-1.2.1&q=80&w=200");
    }
    @Test
    public void toStringTest() {

        User user1 = new User("/users/f2ecbd83-66e1-49e7-bea5-52f8aaa14a83",
                "test@gmail.com", "blue", followingList,
                followerList, "123456", 0,
                "https://images.unsplash.com/photo-1415769663272-8504c6cc02b3?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODY1MTk&ixlib=rb-1.2.1&q=80&w=200",
                bannedByList, banList);

        Assert.assertEquals(user1.toString(), "User{uid='/users/f2ecbd83-66e1-49e7-bea5-52f8aaa14a83', email='test@gmail.com', userName='blue', isPublic=0, profilePicUrl='https://images.unsplash.com/photo-1415769663272-8504c6cc02b3?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyNjA3NjR8MHwxfHJhbmRvbXx8fHx8fHx8fDE2MzE2ODY1MTk&ixlib=rb-1.2.1&q=80&w=200', blockedBy=[], following=[], followers=[], password='123456'}");
    }

}
