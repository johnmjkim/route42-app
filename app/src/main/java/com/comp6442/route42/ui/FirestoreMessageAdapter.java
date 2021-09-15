package com.comp6442.route42.ui;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.comp6442.route42.R;
import com.comp6442.route42.data.model.Message;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestoreException;

import timber.log.Timber;

/* Class to feed Cloud Firestore documents into the FirestoreRecyclerAdapter */
public class FirestoreMessageAdapter extends FirestoreRecyclerAdapter<Message, FirestoreMessageAdapter.MessageViewHolder> {
  private final String loggedInUID, otherUserId;

  public FirestoreMessageAdapter(
          @NonNull FirestoreRecyclerOptions<Message> options,
          String loggedInUID,
          String otherUserId) {
    super(options);
    this.loggedInUID = loggedInUID;
    this.otherUserId = otherUserId;
  }

  @NonNull
  @Override
  public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
    View view = LayoutInflater.from(viewGroup.getContext())
                              .inflate(R.layout.outgoing_message, viewGroup, false);
    Timber.d("MessageAdapter created.");
    return new MessageViewHolder(view);
  }

  protected void onBindViewHolder(@NonNull MessageViewHolder viewHolder, int position, @NonNull Message message) {
    viewHolder.textView.setText(message.getText());
    Timber.i("Fetched post: %s", message);
    Timber.d("OnBindView complete.");
  }

  @Override
  public void onDataChanged() {
    //Called each time there is a new query snapshot.
    Timber.d("breadcrumb");
  }

  @Override
  public void onError(@NonNull FirebaseFirestoreException e) {
    //Handle the error
    Timber.d(e);
  }

  public static class MessageViewHolder extends RecyclerView.ViewHolder {
    public TextView textView;

    public MessageViewHolder(View view) {
      super(view);
      textView = view.findViewById(R.id.text_gchat_message_me);
    }
  }
}