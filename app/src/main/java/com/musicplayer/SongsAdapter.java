package com.musicplayer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Collection;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> {
    private ArrayList<Song> mSongs;

    private SongPlayedListener mSongPlayedListener;

    private int mLastPlayingPos = -1;
    private int mNowPlayingPos = -1;

    public SongsAdapter(SongPlayedListener songPlayedListener) {
        mSongPlayedListener = songPlayedListener;
        mSongs = new ArrayList<>();
    }

    public void addSongs(ArrayList<Song> songs) {
        mSongs = songs;
    }

    public boolean addAll(Collection<? extends Song> c) {
        return mSongs.addAll(c);
    }

    public void clear() {
        mSongs.clear();
    }

    public Song remove(int index) {
        return mSongs.remove(index);
    }

    public SongsAdapter(ArrayList<Song> songs) {
        this.mSongs = songs;
    }


    @Override
    public SongsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(mSongs.get(position).getTitle());
        holder.description.setText(mSongs.get(position).getDesc());
        if (mSongs.get(position).getImage() == null) {
            holder.image.setImageResource(R.drawable.song);
        } else {
            holder.image.setImageURI(mSongs.get(position).imageUri);
        }
        holder.duration.setText(mSongs.get(position).getDuration());

        if (position == mNowPlayingPos) {
            holder.playImageBtn.setVisibility(View.GONE);
            holder.pauseImageBtn.setVisibility(View.VISIBLE);
        } else {
            holder.playImageBtn.setVisibility(View.VISIBLE);
            holder.pauseImageBtn.setVisibility(View.GONE);
        }
    }

    public void setNowPlayingPos(int nowPlayingPos) {
        this.mNowPlayingPos = nowPlayingPos;
    }

    public void setLastPlayingPos(int lastPlayingPos) {
        this.mLastPlayingPos = lastPlayingPos;
    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;

        TextView description;
        TextView title;
        ImageView image;
        TextView duration;
        ImageButton playImageBtn;
        ImageButton pauseImageBtn;

        public ViewHolder(View v) {
            super(v);
            mView = v;
            description = (TextView) v.findViewById(R.id.desc);
            description.setSelected(true);
            title = (TextView) v.findViewById(R.id.title);
            title.setSelected(true);
            image = (ImageView) v.findViewById(R.id.image);
            duration = (TextView) v.findViewById(R.id.duration);
            playImageBtn = (ImageButton) v.findViewById(R.id.play_button);
            pauseImageBtn = (ImageButton) v.findViewById(R.id.pause_button);

            pauseImageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mNowPlayingPos = -1;
                    mLastPlayingPos = -1;
                    mSongPlayedListener.onPauseClicked();
                    pauseImageBtn.setVisibility(View.GONE);
                    playImageBtn.setVisibility(View.VISIBLE);
                }
            });

            playImageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mLastPlayingPos = mNowPlayingPos;
                    mNowPlayingPos = getAdapterPosition();
                    notifyItemChanged(mLastPlayingPos);
                    mSongPlayedListener.onPlayClicked(getAdapterPosition());
                    playImageBtn.setVisibility(View.GONE);
                    pauseImageBtn.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    public interface SongPlayedListener {
        void onPlayClicked(int songPosition);

        void onPauseClicked();
    }
}