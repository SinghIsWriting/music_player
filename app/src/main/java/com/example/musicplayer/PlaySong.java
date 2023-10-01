package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public
class PlaySong extends AppCompatActivity {

    @Override
    protected
    void onDestroy() {
        super.onDestroy();
        mediaPlayer.start();
        mediaPlayer.release();
        updateSeek.interrupt();
    }

    TextView textView , playerPosition , playerDuration;
    ImageView play , previous , next;
    ArrayList songs;
    MediaPlayer mediaPlayer;
    String textContent;
    int position;
    SeekBar seekBar;
    Thread updateSeek;

    @Override
    protected
    void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);

        Objects.requireNonNull(getSupportActionBar()).hide();

        textView = findViewById(R.id.textView);
        playerPosition = findViewById(R.id.player_position);
        playerDuration = findViewById(R.id.player_duration);
        play = findViewById(R.id.play);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs = (ArrayList)bundle.getParcelableArrayList("songList");
        textContent = intent.getStringExtra("currentSong").toString();
        textView.setText(textContent);
        textView.setSelected(true);

        position = intent.getIntExtra("position" , 0);
        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(this , uri);
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public
            void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                playerPosition.setText(convertFormat(mediaPlayer.getCurrentPosition()));
            }

            @Override
            public
            void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public
            void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());

            }
        });
        mediaPlayer.setOnCompletionListener(mp -> {
            play.setVisibility(View.VISIBLE);
            mediaPlayer.seekTo(0);
            mediaPlayer.stop();
            mediaPlayer.release();
            if (position != songs.size() - 1){
                position = position + 1;
            }else {
                position = 0;
            }
            Uri uri1 = Uri.parse(songs.get(position).toString());
            mediaPlayer = MediaPlayer.create(getApplicationContext() , uri1);
            mediaPlayer.start();
            play.setImageResource(R.drawable.pausebtn);
            int presentPosition = mediaPlayer.getCurrentPosition();
            playerPosition.setText(convertFormat(presentPosition));
            seekBar.setMax(mediaPlayer.getDuration());
            textContent = songs.get(position).toString();
            textView.setText(textContent);

        });

        updateSeek = new Thread(){
            @Override
            public
            void run() {
                int currentPosition = 0;
                try {
                    while (currentPosition<mediaPlayer.getDuration()){
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        Thread.sleep(800);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                updateSeek.start();

            }
        };

        int duration = mediaPlayer.getDuration();
        String playingRange = convertFormat(duration);
        playerDuration.setText(playingRange);

        play.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()){
                play.setImageResource(R.drawable.platbtn);
                mediaPlayer.pause();
            }else {
                play.setImageResource(R.drawable.pausebtn);
                mediaPlayer.start();
            }
        });

        previous.setOnClickListener(v -> {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (position != 0){
                position = position - 1;
            }else {
                position = songs.size() - 1;
            }
            Uri uri12 = Uri.parse(songs.get(position).toString());
            mediaPlayer = MediaPlayer.create(getApplicationContext() , uri12);
            mediaPlayer.start();
            int duration1 = mediaPlayer.getDuration();
            String playingRange1 = convertFormat(duration1);
            playerDuration.setText(playingRange1);
            play.setImageResource(R.drawable.pausebtn);
            int presentPosition = mediaPlayer.getCurrentPosition();
            playerPosition.setText(convertFormat(presentPosition));
            seekBar.setMax(mediaPlayer.getDuration());
            textContent = songs.get(position).toString();
            textView.setText(textContent);
        });

        next.setOnClickListener(v -> {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (position != songs.size() - 1){
                position = position + 1;
            }else {
                position = 0;
            }
            Uri uri13 = Uri.parse(songs.get(position).toString());
            mediaPlayer = MediaPlayer.create(getApplicationContext() , uri13);
            mediaPlayer.start();
            int duration2 = mediaPlayer.getDuration();
            String playingRange2 = convertFormat(duration2);
            playerDuration.setText(playingRange2);
            play.setImageResource(R.drawable.pausebtn);
            int presentPosition = mediaPlayer.getCurrentPosition();
            playerPosition.setText(convertFormat(presentPosition));
            seekBar.setMax(mediaPlayer.getDuration());
            textContent = songs.get(position).toString();
            textView.setText(textContent);
        });
        updateSeek.start();

    }
    @SuppressLint("DefaultLocale")
    private
    String convertFormat(int duration) {
        return String.format("%02d:%02d"
                ,TimeUnit.MILLISECONDS.toMinutes(duration)
                , TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }
}
