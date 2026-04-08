package com.example.photogalleryapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

public class MainActivity extends AppCompatActivity {

    // Launchers for camera and folder selection
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> folderLauncher;

    // Stores selected folder and captured image URI
    Uri selectedFolderUri;
    Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Handle camera result
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Toast.makeText(this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Handle folder selection and persist permission
        folderLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                        selectedFolderUri = result.getData().getData();

                        // Important: retain access to selected folder
                        getContentResolver().takePersistableUriPermission(
                                selectedFolderUri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION |
                                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        );

                        Toast.makeText(this, "Folder Selected!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    // Triggered when user clicks "Take Photo"
    public void takePhoto(View view) {

        // Ensure folder is selected before capturing
        if (selectedFolderUri == null) {
            Toast.makeText(this, "Please select folder first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Request camera permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 100);
            return;
        }

        openCamera();
    }

    // Opens camera and saves image to selected folder
    private void openCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null) {

            try {
                // Create new image file inside selected folder
                androidx.documentfile.provider.DocumentFile folder =
                        androidx.documentfile.provider.DocumentFile.fromTreeUri(this, selectedFolderUri);

                String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";

                androidx.documentfile.provider.DocumentFile newFile =
                        folder.createFile("image/jpeg", fileName);

                if (newFile == null) {
                    Toast.makeText(this, "File creation failed", Toast.LENGTH_SHORT).show();
                    return;
                }

                photoUri = newFile.getUri();

                // Pass URI to camera app
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                cameraLauncher.launch(intent);

                // Grant permission to camera app
                grantUriPermission(
                        "com.android.camera",
                        photoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                );

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error saving image", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "No Camera App Found", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle camera permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Opens folder picker
    public void chooseFolder(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        folderLauncher.launch(intent);
    }

    // Opens gallery activity to display images
    public void viewGallery(View view) {
        if (selectedFolderUri == null) {
            Toast.makeText(this, "Please select a folder first!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, GalleryActivity.class);
        intent.putExtra("folderUri", selectedFolderUri.toString());
        startActivity(intent);
    }
}