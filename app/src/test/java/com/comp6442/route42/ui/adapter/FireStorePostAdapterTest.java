package com.comp6442.route42.ui.adapter;

import static org.mockito.Mockito.mock;
import android.view.View;
import com.comp6442.route42.ui.adapter.FirestorePostAdapter;
import org.junit.Assert;
import org.junit.Test;

public class FireStorePostAdapterTest {

    View view = mock(View.class);

    @Test
    public void checkView(){
        FirestorePostAdapter.PostViewHolder postViewHolder = new FirestorePostAdapter.PostViewHolder(view);
        Assert.assertNotNull(postViewHolder);
    }
}
