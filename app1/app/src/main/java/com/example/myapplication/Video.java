package com.example.myapplication;

import android.net.Uri;

public class Video {
    private Uri uri;
    private String name;
    private int duration;
    private float size;

    public Video(Uri uri, String name, int duration, float size) {
        this.uri = uri;
        this.name = name;
        this.duration = duration;
        this.size = size;
    }

    public Uri getUri() {
        return uri;
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public float getSize() {
        return size;
    }
}
