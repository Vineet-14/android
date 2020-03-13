package com.example.myapplication;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class VideoFragment extends Fragment implements View.OnClickListener {
    private ArrayList<Video> videoList = new ArrayList<>();
    private String[] projection = new String[]{
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE
    };
    private String sortOrder = MediaStore.Video.Media.DISPLAY_NAME + " ASC";
    private RecyclerView videosHolder;
    private CardView sendButton;
    private ArrayList<Uri> sendingVideosList=new ArrayList<>();
    private VideoRecyclerViewAdapter recyclerViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_video, container, false);
        videosHolder = v.findViewById(R.id.video_recycler_view);
        sendButton = v.findViewById(R.id.video_send);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        try (Cursor cursor = this.getActivity().getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,null, null, sortOrder
        )) {
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            int nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
            int durationColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                int duration = cursor.getInt(durationColumn);
                int size = cursor.getInt(sizeColumn);

                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);

                videoList.add(new Video(contentUri, name, duration, size));

//                Log.e("=====>","size is:"+videoList.size());
            }
//            Log.e("=====>","size is:"+videoList.size());
            recyclerViewAdapter = new VideoRecyclerViewAdapter(videoList, this.getContext());
            videosHolder.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, true));
            videosHolder.setAdapter(recyclerViewAdapter);
        }catch (Exception e){
            e.printStackTrace();
        }
        sendButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==sendButton){
            sendingVideosList = recyclerViewAdapter.getSelectedVideosList();
        }
    }
}
