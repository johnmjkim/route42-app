package com.comp6442.route42.ui.fragment;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.comp6442.route42.R;
import com.comp6442.route42.data.CreatePostViewModel;
import com.comp6442.route42.data.UserViewModel;
import com.comp6442.route42.data.model.Post;
import com.comp6442.route42.data.model.User;
import com.comp6442.route42.data.repository.PostRepository;
import com.comp6442.route42.data.repository.UserRepository;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class CreatePostFragment extends Fragment {

    private CreatePostViewModel mViewModel;
    private MaterialButton cancelPostButton, createPostButton;
    private EditText postDescriptionInput;
    private String uid;
    private UserViewModel userViewModel;
    private PostRepository postRepository;
    public static CreatePostFragment newInstance() {
        return new CreatePostFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        uid = getArguments().getString("uid");
        return inflater.inflate(R.layout.create_post_fragment, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.loadProfileUser(this.uid);
        postRepository = PostRepository.getInstance();

        cancelPostButton = view.findViewById(R.id.cancel_post_button);
        createPostButton = view.findViewById(R.id.create_post_button);
        postDescriptionInput = view.findViewById(R.id.post_description_input);
        cancelPostButton.setOnClickListener(event -> {

            Bundle bundle = new Bundle();
            bundle.putString("uid", uid);

            Fragment fragment = new FeedFragment();
            fragment.setArguments(bundle);

            getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_view, fragment)
                    .commit();
        });
        createPostButton.setOnClickListener(event -> {
            onClickCreatePostHandler();
        });
        postDescriptionInput.setOnClickListener(event -> {
            System.out.println("clicked input for create post");
            postDescriptionInput.setText("");
        });
    }
    private void onClickCreatePostHandler() {

        DocumentReference uidRef = UserRepository.getInstance().getOne(uid);
        User liveUser = userViewModel.getLiveUser().getValue();
        assert liveUser != null;
        String username = liveUser.getUserName();
        int isPublic = liveUser.getIsPublic();
        String profilePicUrl = liveUser.getProfilePicUrl();
        Date postDateTime = new Date();
        String postDescription = postDescriptionInput.getText().toString().trim();
        List<String > hashTags = getHashTagsFromTextInput(postDescription);
        Double latitude = 0.0;
        Double longitude = 0.0;
        int likeCount = 0;
        String imageUrl = "";
        List<DocumentReference> likedBy = new ArrayList<>(0);
        Post newPost = new Post(uidRef, username, isPublic, profilePicUrl,postDateTime,postDescription, "", latitude,longitude, hashTags,likeCount, imageUrl,likedBy);
        postRepository.createOne(newPost);

    }

    private List<String> getHashTagsFromTextInput(String textInput) {

        List<String> hashTags = new ArrayList<>();

        String currentTag = "";

        textInput = textInput.toLowerCase().trim();
        for (int i=0; i<textInput.length(); i++) {
            char c = textInput.charAt(i);
            if(c == '#') {
                if ( currentTag.length()>0) {
                    hashTags.add(currentTag.trim());
                    currentTag = "";
                }
                currentTag+= c;

            } else if (currentTag.length()>0 && Pattern.matches("[:space:]" , Character.toString(c))) {
                hashTags.add(currentTag.trim());
                currentTag = "";
            }
            else if (currentTag.length()>0 && Pattern.matches("\\p{Punct}" , Character.toString(c)) ) {
                hashTags.add(currentTag.trim());
                currentTag = "";
            } else if (currentTag.length()>0) {
                currentTag += c;
            }
        }
        if (currentTag.length()>0) {
            hashTags.add(currentTag.trim());
        }
        return hashTags;
    }
}