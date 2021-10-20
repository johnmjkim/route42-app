package com.comp6442.route42.model;

import static org.mockito.Mockito.mock;

import androidx.lifecycle.MutableLiveData;

import com.comp6442.route42.data.model.User;
import com.comp6442.route42.ui.viewmodel.UserViewModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserViewModelTest {
    UserViewModel userViewModel = new UserViewModel();
    User user = mock(User.class);
    List followerList = new ArrayList();
    List followingList = new ArrayList();
    List blockedList = new ArrayList<>();
    List blockedByList = new ArrayList<>();

    private final MutableLiveData<User> liveUser = new MutableLiveData<>();
    private final MutableLiveData<User> profileUser = new MutableLiveData<>();

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
//        userViewModel.loadLiveUser("9d4d5084-7547-4e96-97c6-908b3a0ca3b8");
    }

    @Test
    public void liveUserTest() {
//        userViewModel.setLiveUser(user);
//        liveUser.setValue(user);
//        Assert.assertEquals(liveUser.getValue(), userViewModel.getLiveUser());
    }

    @Test
    public void profileUserTest() {
//        userViewModel.setProfileUser(user);
//        profileUser.setValue(user);
//        Assert.assertEquals(profileUser.getValue(), userViewModel.getProfileUser());
    }
}
