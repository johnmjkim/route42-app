package com.comp6442.route42.ui.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.comp6442.route42.data.model.User;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

public class UserListAdapterTest {
  private final List<User> users = Arrays.asList(
          new User("abcd@gmail.com", "test1"),
          new User("abcde@gmail.com", "test2"),
          new User("abcdf@gmail.com", "test3"),
          new User("abcdg@gmail.com", "test4"),
          new User("abcdh@gmail.com", "test5"),
          new User("abcdj@gmail.com", "test6"));

  UserListAdapter userList = new UserListAdapter(users);
  ViewGroup viewgroup;
  View view;

  @Test
  public void checkItemCount() {
    Assert.assertEquals(userList.getItemCount(), 6);
  }

  @Test
  public void checkViewHolder() {
    view = Mockito.mock(View.class);
    UserListAdapter.ViewHolder viewHolder = new UserListAdapter.ViewHolder(view);
    Assert.assertNotNull(viewHolder);
  }
}
