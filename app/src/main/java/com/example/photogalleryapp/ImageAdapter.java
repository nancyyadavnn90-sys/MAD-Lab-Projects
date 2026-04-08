package com.example.photogalleryapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> images; // Stores image paths/URIs

    public ImageAdapter(Context context, ArrayList<String> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size(); // Total number of images
    }

    @Override
    public Object getItem(int i) {
        return images.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {

        // Reuse view for better performance
        if (view == null) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.item_image, parent, false);
        }

        ImageView imageView = view.findViewById(R.id.grid_item_image);
        String path = images.get(i);

        // Clear previous image (important for recycling views)
        imageView.setImageDrawable(null);

        // Handle both URI and file path images
        if (path.startsWith("content://")) {
            imageView.setImageURI(android.net.Uri.parse(path));
        } else {
            imageView.setImageURI(
                    android.net.Uri.fromFile(new java.io.File(path))
            );
        }

        return view;
    }
}