package com.comp6442.route42.ui.adapter;

import android.view.View;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class FireStorePostAdapterTest {

  View view = Mockito.mock(View.class);

  @Test
  public void checkView() {
    FirestorePostAdapter.PostViewHolder postViewHolder = new FirestorePostAdapter.PostViewHolder(view);
    Assert.assertNotNull(postViewHolder);
  }
}
