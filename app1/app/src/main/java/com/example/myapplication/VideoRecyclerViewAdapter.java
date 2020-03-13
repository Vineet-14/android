package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class VideoRecyclerViewAdapter extends RecyclerView.Adapter<VideoRecyclerViewAdapter.ViewHolder> {
    private ArrayList<Video> videoList;
    private Context context;
    private ArrayList<Uri> selectedVideoSendingList = new ArrayList<>();

    public VideoRecyclerViewAdapter(ArrayList<Video> videoList, Context context) {
        this.videoList = videoList;
        this.context = context;
    }

    @NonNull
    @Override
    public VideoRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.single_video_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoRecyclerViewAdapter.ViewHolder holder, int position) {
        Glide.with(context).load(videoList.get(position).getUri()).into(holder.videoThumbnail);
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public ArrayList<Uri> getSelectedVideosList() {
        return selectedVideoSendingList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView videoThumbnail, videoSelection;
        private boolean isSelected = false;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            videoThumbnail = itemView.findViewById(R.id.video_thumbnail);
            videoSelection = itemView.findViewById(R.id.video_selected);
            videoThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = ViewHolder.this.getAdapterPosition();
                    if(isSelected){
                        videoSelection.setVisibility(View.GONE);
                        isSelected=false;
                        selectedVideoSendingList.remove(videoList.get(position));
                    }else{
                        videoSelection.setVisibility(View.VISIBLE);
                        isSelected=true;
                        if(!selectedVideoSendingList.contains(videoList.get(position))){
                            selectedVideoSendingList.add(videoList.get(position).getUri());
                        }
                    }
                }
            });
        }
    }
}
