package com.comp6442.route42.data.repository;

import com.comp6442.route42.BuildConfig;
import com.comp6442.route42.data.model.Chat;
import com.comp6442.route42.data.model.Message;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.util.List;
import java.util.Objects;

import timber.log.Timber;

public final class ChatRepository extends FirestoreRepository<Chat> {
  private static ChatRepository instance = null;

  private ChatRepository() {
    super("chats", Chat.class);
  }

  public static ChatRepository getInstance() {
    if (ChatRepository.instance == null) {
      ChatRepository.instance = new ChatRepository();
    }
    return ChatRepository.instance;
  }

  public static Gson getJsonDeserializer() {
    return new GsonBuilder().registerTypeAdapter(Timestamp.class, (JsonDeserializer<Timestamp>) (json, type, context) -> {
      String tsString = json.toString();
      int decimalIdx = (tsString.contains(".")) ? tsString.indexOf(".") : tsString.length();
      return new Timestamp(
              Long.parseLong(tsString.substring(0, decimalIdx)),
              (decimalIdx != tsString.length()) ? Integer.parseInt(tsString.substring(decimalIdx + 1)) : 0
      );
    }).registerTypeAdapter(Double.class, (JsonDeserializer<Double>) (json, type, context) -> {
      return json.getAsDouble();
    }).registerTypeAdapter(DocumentReference.class, (JsonDeserializer<DocumentReference>) (json, type, context) -> {
      String str = json.toString();
      return UserRepository.getInstance().getOne(str);
    }).create();
  }

  @Override
  public void createOne(Chat chat) {
    this.collection.document(chat.getId())
            .set(chat)
            .addOnSuccessListener(unused -> Timber.i("Insert succeeded: %s", chat.toString()))
            .addOnFailureListener(Timber::e);
  }

  public Query getConversations(String uid) {
    Timber.d("breadcrumb");
    return this.collection
            .whereArrayContains("participants", UserRepository.getInstance().getOne(uid))
            .orderBy("createdAt", Query.Direction.DESCENDING);
  }
}