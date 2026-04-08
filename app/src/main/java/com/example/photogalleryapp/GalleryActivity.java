package com.example.photogalleryapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    GridView gridView;
    ArrayList<String> imagePaths = new ArrayList<>();
    ImageAdapter adapter;
    String folderUriString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        gridView = findViewById(R.id.gridView);

        // Get folder URI passed from MainActivity
        folderUriString = getIntent().getStringExtra("folderUri");

        adapter = new ImageAdapter(this, imagePaths);
        gridView.setAdapter(adapter);

        // Open image in detail view when clicked
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(GalleryActivity.this, DetailActivity.class);
            intent.putExtra("path", imagePaths.get(position));
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadImages(); // Reload images when activity resumes
    }

    private void loadImages() {

        imagePaths.clear();

        if (folderUriString != null) {
            Uri folderUri = Uri.parse(folderUriString);
            DocumentFile folder = DocumentFile.fromTreeUri(this, folderUri);

            if (folder != null && folder.isDirectory()) {
                for (DocumentFile file : folder.listFiles()) {

                    // Add only valid image files
                    if (file.isFile() && file.exists() && file.getType() != null &&
                            file.getType().startsWith("image/")) {

                        imagePaths.add(file.getUri().toString());
                    }
                }
            }
        } else {
            // Fallback: load from app-specific storage
            File folder = getExternalFilesDir("MyPhotos");

            if (folder != null && folder.exists()) {
                File[] files = folder.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.exists()) {
                            imagePaths.add(file.getAbsolutePath());
                        }
                    }
                }
            }
        }

        adapter.notifyDataSetChanged();

        // Show message if no images found
        if (imagePaths.isEmpty()) {
            Toast.makeText(this, "No images found", Toast.LENGTH_SHORT).show();
        }
    }
}