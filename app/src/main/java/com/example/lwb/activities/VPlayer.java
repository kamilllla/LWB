package com.example.lwb.activities;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.lwb.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class VPlayer extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener {

FirebaseFirestore db;
    String api_key = "AIzaSyDag2vVeI-9vCBB4J1e8LLVjhnK3CqAZN8";
    String loadUrl;
    YouTubePlayerView ytPlayer;
    Button toTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vplayer);
        toTest=findViewById(R.id.totTest);
        //WebView webView = findViewById(R.id.wv);
       // webView.setWebViewClient(new WebViewClientK());
        db = FirebaseFirestore.getInstance();
        // включаем поддержку JavaScript
        //webView.getSettings().setJavaScriptEnabled(true);

        String category = getIntent().getStringExtra("category");
        String guid = getIntent().getStringExtra("guid");
        ytPlayer = (YouTubePlayerView)findViewById(R.id.yp);
        toTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(VPlayer.this, TestActivity.class);
                intent.putExtra("category", category);
                intent.putExtra("guid", guid);
                startActivity(intent);

            }
        });


        db.collection("Видеогиды").document(category).collection(category).document(guid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                                          @Override
                                                                                                                          public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                                                              if (task.isSuccessful()) {
                                                                                                                                  loadUrl = task.getResult().get("video").toString();
                                                                                                                                  ytPlayer.initialize(api_key, new YouTubePlayer.OnInitializedListener() {
                                                                                                                                      // Implement two methods by clicking on red
                                                                                                                                      // error bulb inside onInitializationSuccess
                                                                                                                                      // method add the video link or the playlist
                                                                                                                                      // link that you want to play In here we
                                                                                                                                      // also handle the play and pause
                                                                                                                                      // functionality
                                                                                                                                      @Override
                                                                                                                                      public void onInitializationSuccess(
                                                                                                                                              YouTubePlayer.Provider provider,
                                                                                                                                              YouTubePlayer youTubePlayer, boolean b)
                                                                                                                                      {
                                                                                                                                          youTubePlayer.loadVideo(loadUrl);
                                                                                                                                          youTubePlayer.play();
                                                                                                                                      }

                                                                                                                                      // Inside onInitializationFailure
                                                                                                                                      // implement the failure functionality
                                                                                                                                      // Here we will show toast
                                                                                                                                      @Override
                                                                                                                                      public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                                                                                                                                          YouTubeInitializationResult
                                                                                                                                                                                  youTubeInitializationResult)
                                                                                                                                      {
                                                                                                                                          Toast.makeText(getApplicationContext(), "Video player Failed", Toast.LENGTH_SHORT).show();
                                                                                                                                      }
                                                                                                                                  });





                                                                                                                              }
                                                                                                                              else {
                                                                                                                                  Log.i("LOG", "ERROR success");
                                                                                                                              }
                                                                                                                          }
                                                                                                                      }
        );

    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                        boolean wasRestored) {

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(api_key, this);
        }
    }
    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return ytPlayer;
    }


}