/*
 * Copyright 2009 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package com.wambal;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public final class ImageAdapter extends BaseAdapter {

    private Context context;
    private Thumbnail[] thumbs = new Thumbnail[] {
            new Thumbnail(R.drawable.macbook, "Computers"),
            new Thumbnail(R.drawable.reno, "Renovations"),
            new Thumbnail(R.drawable.shopping, "Shopping"),
            new Thumbnail(R.drawable.travel, "Travel")
    };

    public ImageAdapter(final Context context) {
        this.context = context;
    }

    @Override public int getCount() {
        return thumbs.length;
    }

    @Override public Object getItem(final int i) {
        return (i >= 0 && i < thumbs.length) ? thumbs[i] : null;
    }

    @Override public long getItemId(final int i) {
        return (i >= 0 && i < thumbs.length) ? thumbs[i].id : -1;
    }

    @Override public View getView(final int position, final View convertView, final ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(thumbs[position].id);
        return imageView;
    }

    private static class Thumbnail {
        private final String name;
        private final int id;

        private Thumbnail(final int id, final String name) {
            this.id = id;
            this.name = name;
        }

        @Override public String toString() {
            return name;
        }
    }



}
