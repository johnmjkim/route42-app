package com.comp6442.route42.data.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Point implements Parcelable {

  public static final Creator<Point> CREATOR = new Creator<Point>() {
    @Override
    public Point createFromParcel(Parcel in) {
      return new Point(in);
    }

    @Override
    public Point[] newArray(int size) {
      return new Point[size];
    }
  };
  private Double latitude;
  private Double longitude;

  public Point() {
  }

  public Point(@NonNull Double latitude, @NonNull Double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  protected Point(Parcel in) {
    if (in.readByte() == 0) {
      latitude = null;
    } else {
      latitude = in.readDouble();
    }
    if (in.readByte() == 0) {
      longitude = null;
    } else {
      longitude = in.readDouble();
    }
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
    return "Point{" +
            ", latitude=" + latitude +
            ", longitude=" + longitude +
            '}';
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int i) {
    if (latitude == null) {
      parcel.writeByte((byte) 0);
    } else {
      parcel.writeByte((byte) 1);
      parcel.writeDouble(latitude);
    }
    if (longitude == null) {
      parcel.writeByte((byte) 0);
    } else {
      parcel.writeByte((byte) 1);
      parcel.writeDouble(longitude);
    }
  }

  public LatLng toLatLng() {
    return new LatLng(this.latitude, this.longitude);
  }

  public static Point fromLocation(Location location) {
    return new Point(location.getLatitude(), location.getLongitude());
  }
}
