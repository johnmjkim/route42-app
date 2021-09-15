package com.comp6442.route42.data.model;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

@IgnoreExtraProperties
public class Message extends Model {
  @ServerTimestamp private Date timestamp;
  private DocumentReference author;
  private String text;

  public Message() {
  }

  public Message(DocumentReference author, String text, Date timestamp) {
    this();
    this.author = author;
    this.text = text;
    this.timestamp = timestamp;
  }

  public Message(String id, DocumentReference author, String text, Date timestamp) {
    this.id = id;
    this.author = author;
    this.text = text;
    this.timestamp = timestamp;
  }

  public DocumentReference getAuthor() {
    return author;
  }

  public String getText() {
    return text;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public void setAuthor(DocumentReference author) {
    this.author = author;
  }

  public void setText(String text) {
    this.text = text;
  }

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }
}
