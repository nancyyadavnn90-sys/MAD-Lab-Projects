package com.example.photogalleryapp;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.util.Date;

public class DetailActivity extends AppCompatActivity {

    String path;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Set title for detail screen
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Image Details");
        }

        ImageView imageView = findViewById(R.id.imageView);
        TextView details = findViewById(R.id.details);

        // Get image path from GalleryActivity
        path = getIntent().getStringExtra("path");

        if (path != null) {

            // Handle images from Storage Access Framework (content URI)
            if (path.startsWith("content://")) {

                Uri uri = Uri.parse(path);
                imageView.setImageURI(uri);

                DocumentFile docFile =
                        DocumentFile.fromSingleUri(this, uri);

                if (docFile != null) {
                    String info = "Name: " + docFile.getName() +
                            "\nPath: " + path +
                            "\nSize: " + docFile.length() + " bytes" +
                            "\nDate: " + new Date(docFile.lastModified());
                    details.setText(info);
                } else {
                    details.setText("Path: " + path);
                }

            } else {

                // Handle images stored as file path
                file = new File(path);
                imageView.setImageURI(Uri.fromFile(file));

                String info = "Name: " + file.getName() +
                        "\nPath: " + file.getAbsolutePath() +
                        "\nSize: " + file.length() + " bytes" +
                        "\nDate: " + new Date(file.lastModified());

                details.setText(info);
            }

        } else {
            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // Delete selected image with confirmation
    public void deleteImage(View view) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Image")
                .setMessage("Are you sure you want to delete this image?")
                .setPositiveButton("Yes", (dialog, which) -> {

                    try {
                        boolean deleted = false;

                        // Delete using URI (for SAF)
                        if (path != null && path.startsWith("content://")) {
                            Uri uri = Uri.parse(path);
                            DocumentFile docFile = DocumentFile.fromSingleUri(this, uri);
                            if (docFile != null && docFile.exists()) {
                                deleted = docFile.delete();
                            }

                            // Delete using File path
                        } else if (file != null && file.exists()) {
                            deleted = file.delete();
                        }

                        if (deleted) {
                            Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Delete failed. Image might be read-only or already removed.", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error deleting image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
}