package com.comp6442.groupproject.data.model;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class TsPoint {

  @NonNull
  private final Timestamp ts;
  @NonNull
  @Exclude
  private final Double latitude;
  @NonNull
  @Exclude
  private final Double longitude;

  public TsPoint(@NonNull Timestamp ts, @NonNull Double latitude, @NonNull Double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.ts = ts;
  }

  @NonNull
  public Timestamp getTs() {
    return ts;
  }

  @NonNull
  public Double getLatitude() {
    return latitude;
  }

  @NonNull
  public Double getLongitude() {
    return longitude;
  }

  @NonNull
  @Override
  public String toString() {
    return "TsPoint{" +
            "ts=" + ts +
            ", latitude=" + latitude +
            ", longitude=" + longitude +
            '}';
  }
}