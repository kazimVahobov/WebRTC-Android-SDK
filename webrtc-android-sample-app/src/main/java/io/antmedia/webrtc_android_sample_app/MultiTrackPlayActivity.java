package io.antmedia.webrtc_android_sample_app;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.RequiresApi;

import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;

import java.util.ArrayList;
import java.util.List;

import io.antmedia.webrtcandroidframework.IWebRTCClient;
import io.antmedia.webrtcandroidframework.WebRTCClient;
import io.antmedia.webrtcandroidframework.apprtc.CallActivity;

import static io.antmedia.webrtcandroidframework.apprtc.CallActivity.EXTRA_CAPTURETOTEXTURE_ENABLED;

public class MultiTrackPlayActivity extends AbstractSampleSDKActivity {
    private WebRTCClient webRTCClient;
    private String webRTCMode;
    private Button startStreamingButton;
    private String operationName = "";
    private String tokenId;
    private ToggleButton track1Button;
    private ToggleButton track2Button;
    private String[] allTracks;
    private String serverUrl;
    private EditText streamIdEditText;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set window styles for fullscreen-window size. Needs to be done before
        // adding content.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        //getWindow().getDecorView().setSystemUiVisibility(getSystemUiVisibility());

        setContentView(R.layout.activity_multitrack);


        webRTCClient = new WebRTCClient( this,this);

        //webRTCClient.setOpenFrontCamera(false);
        streamIdEditText = findViewById(R.id.stream_id_edittext);
        streamIdEditText.setText("streamId" + (int)(Math.random()*9999));

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this /* Activity context */);
        //String streamId = "stream" + (int)(Math.random() * 999);
        //streamId = "stream_multi_track";
        tokenId = "tokenId";

        serverUrl = sharedPreferences.getString(getString(R.string.serverAddress), SettingsActivity.DEFAULT_WEBSOCKET_URL);

        SurfaceViewRenderer cameraViewRenderer = findViewById(R.id.player1);

        SurfaceViewRenderer pipViewRenderer = findViewById(R.id.player2);

        List<SurfaceViewRenderer> rendererList = new ArrayList<>();
        rendererList.add(cameraViewRenderer);
        rendererList.add(pipViewRenderer);

        startStreamingButton = findViewById(R.id.start_streaming_button);
        
        track1Button = findViewById(R.id.track_1_button);
        track2Button = findViewById(R.id.track_2_button);

        track1Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                webRTCClient.enableTrack(webRTCClient.getStreamId(), allTracks[0], isChecked);
            }
        });

        track2Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                webRTCClient.enableTrack(webRTCClient.getStreamId(), allTracks[1], isChecked);
            }
        });

        //webRTCClient.setVideoRenderers(pipViewRenderer, cameraViewRenderer);

        webRTCClient.setRemoteRendererList(rendererList);

        this.getIntent().putExtra(EXTRA_CAPTURETOTEXTURE_ENABLED, true);

        webRTCMode = IWebRTCClient.MODE_MULTI_TRACK_PLAY;

       // this.getIntent().putExtra(CallActivity.EXTRA_VIDEO_FPS, 24);
        webRTCClient.init(serverUrl, streamIdEditText.getText().toString(), webRTCMode, tokenId, this.getIntent());

    }

    public void startStreaming(View v) {

        if (!webRTCClient.isStreaming()) {
            ((Button)v).setText("Stop " + operationName);
            webRTCClient.startStream();
        }
        else {
            ((Button)v).setText("Start " + operationName);
            webRTCClient.stopStream();
        }
    }


    @Override
    public void onPlayStarted(String streamId) {
        Log.w(getClass().getSimpleName(), "onPlayStarted");
        Toast.makeText(this, "Play started", Toast.LENGTH_LONG).show();
        webRTCClient.switchVideoScaling(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
    }

    @Override
    public void noStreamExistsToPlay(String streamId) {
        Log.w(getClass().getSimpleName(), "noStreamExistsToPlay");
        Toast.makeText(this, "No stream exist to play", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        webRTCClient.stopStream();

    }

    @Override
    public void onDisconnected(String streamId) {

        Log.w(getClass().getSimpleName(), "disconnected");
        Toast.makeText(this, "Disconnected", Toast.LENGTH_LONG).show();

        finish();
    }

    public void onOffVideo(View view) {
        if (webRTCClient.isVideoOn()) {
            webRTCClient.disableVideo();
        }
        else {
            webRTCClient.enableVideo();
        }
    }

    public void onOffAudio(View view) {
        if (webRTCClient.isAudioOn()) {
            webRTCClient.disableAudio();
        }
        else {
            webRTCClient.enableAudio();
        }
    }

    @Override
    public void onTrackList(String[] tracks) {
        allTracks = new String[tracks.length+1];

        allTracks[0] = webRTCClient.getStreamId();
        for (int i = 0; i < tracks.length; i++) {
            allTracks[i+1] = tracks[i];
            Log.i(getClass().getSimpleName(), "track id: " + tracks[i]);
        }

        webRTCClient.play(webRTCClient.getStreamId(), tokenId, allTracks);
    }
}
