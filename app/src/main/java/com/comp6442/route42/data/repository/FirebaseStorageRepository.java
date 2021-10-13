package com.comp6442.route42.data.repository;

import android.net.Uri;

import com.comp6442.route42.BuildConfig;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import timber.log.Timber;

public class FirebaseStorageRepository {
  public static final long BUFFER_SIZE = 1024 * 1024 * 5; // 5MB
  private static FirebaseStorageRepository instance = null;
  private static FirebaseStorage storage = null;
  private static String bucketUrl = null;

  private FirebaseStorageRepository(FirebaseStorage storage) {
    FirebaseStorageRepository.storage = storage;
  }

  public static FirebaseStorageRepository getInstance() {
    if (FirebaseStorageRepository.instance == null) {
      FirebaseStorage storage = FirebaseStorage.getInstance();
      if (BuildConfig.EMULATOR) {
        try {
          storage.useEmulator("10.0.2.2", 9199);
        } catch (IllegalStateException exc) {
          Timber.d(exc);
        }
      }
      FirebaseStorageRepository.instance = new FirebaseStorageRepository(storage);
      FirebaseStorageRepository.bucketUrl = FirebaseApp.getInstance().getOptions().getStorageBucket();
    }
    return FirebaseStorageRepository.instance;
  }

  public StorageReference get(String path) {
    String url = String.format("gs://%s/%s", bucketUrl, path);
//    Timber.i(url);

    return storage.getReferenceFromUrl(url);
  }
  public StorageReference uploadSnapshotFromLocal(String localFilename,String storageFilename, String basePath) {
    StorageReference snapshotFolderRef = storage.getReference().child("snapshots/"+ storageFilename);
    Uri file = Uri.fromFile(new File(basePath+"/"+localFilename));
    UploadTask uploadTask = snapshotFolderRef.putFile(file);
// Register observers to listen for when the download is done or if it fails
    uploadTask.addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure( Exception exception) {
        // Handle unsuccessful uploads
        Timber.e("error uploading snapshot");
      }
    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
      @Override
      public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
      }
    });
    return snapshotFolderRef;

  }

}
