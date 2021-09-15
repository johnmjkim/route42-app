package com.comp6442.route42.data.model;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Chat extends Model{
  @ServerTimestamp private Date createdAt;
  private List<DocumentReference> participants = new ArrayList<>();
  private CollectionReference messages;

  public Chat() {
  }

  public Chat(List<DocumentReference> participants, CollectionReference messages) {
    this.participants = participants;
    this.messages = messages;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public List<DocumentReference> getParticipants() {
    return participants;
  }

  public CollectionReference getMessages() {
    return messages;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public void setParticipants(List<DocumentReference> participants) {
    this.participants = participants;
  }

  public void setMessages(CollectionReference messages) {
    this.messages = messages;
  }

  @Override
  public String toString() {
    return "Chat{" +
            ", id='" + id + '\'' +
            "createdAt=" + createdAt +
            ", participants=" + participants +
            ", messages=" + messages +
            '}';
  }
}
