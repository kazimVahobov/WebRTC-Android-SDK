package io.antmedia.webrtcandroidframework;

import android.Manifest;
import android.content.Intent;
import android.os.Build;

import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;

/**
 * Created by karinca on 20.10.2017.
 */

public interface IWebRTCClient {

    public static final String[] REQUIRED_PERMISSIONS = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ?
            new String[] {Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.BLUETOOTH_CONNECT}
            :
            new String[] {Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};

    /**
     * Publish mode
     */
    String MODE_PUBLISH = "publish";

    /**
     * Play mode
     */
    String MODE_PLAY = "play";

    /**
     * Join mode
     */
    String MODE_JOIN = "join";

    /**
     * Multi track play
     */
    String MODE_MULTI_TRACK_PLAY = "multi_track_play";

    /**
     * Used for track based conference
     */
    String MODE_TRACK_BASED_CONFERENCE = "track_based_conference";


    /**
     * Camera open order
     * By default front camera is attempted to be opened at first,
     * if it is set to false, another camera that is not front will be tried to be open
     * @param openFrontCamera if it is true, front camera will tried to be opened
     *                        if it is false, another camera that is not front will be tried to be opened
     */
    void setOpenFrontCamera(boolean openFrontCamera);


    /**

     * If mode is MODE_PUBLISH, stream with streamId field will be published to the Server
     * if mode is MODE_PLAY, stream with streamId field will be played from the Server
     *
     * @param url websocket url to connect
     * @param streamId is the stream id in the server to process
     * @param mode one of the MODE_PUBLISH, MODE_PLAY, MODE_JOIN
     * @param token one time token string
     */
    void init(String url, String streamId, String mode, String token, Intent intent);


    /**
     * Starts the streaming according to mode
     */
    void startStream();

    /**
     * Stops the streaming
     */
    void stopStream();

    /**
     * Switches the cameras
     */
    void switchCamera();

    /**
     * Switches the video according to type and its aspect ratio
     * @param scalingType
     */
    void switchVideoScaling(RendererCommon.ScalingType scalingType);

    /**
     * toggle microphone
     * @return
     */
    boolean toggleMic();

    /**
     * Stops the video source
     */
    void stopVideoSource();

    /**
     * Starts or restarts the video source
     */
    void startVideoSource();

    /**
     * Swapeed the fullscreen renderer and pip renderer
     * @param b
     */
    void setSwappedFeeds(boolean b);

    /**
     * Set's the video renderers,
     * @param pipRenderer can be nullable
     * @param fullscreenRenderer cannot be nullable
     */
    void setVideoRenderers(SurfaceViewRenderer pipRenderer, SurfaceViewRenderer fullscreenRenderer);

    /**
     * Get the error
     * @return error or null if not
     */
    String getError();



    void setMediaProjectionParams(int resultCode, Intent data);

    /**
     * Return if data channel is enabled and open
     * @return true if data channel is available
     * false if it's not opened either by mobile or server side
     */
    boolean isDataChannelEnabled();

    /**
     * This is used to get stream info list
     */
    void getStreamInfoList();

    /**
     * This is used to play the specified resolution
     * @param height
     */
    void forceStreamQuality(int height);

    /**
     * This is used to set subscriber parameters for TOTP (time-based one time password)
     * @param subscriberId: Id for publisher or player
     * @param subscriberCode
     */
    void setSubscriberParams(String subscriberId, String subscriberCode);

    /**
     * This is used to set any metadata for WebRTC player
     * @param viewerInfo: metadata e.g name, location or anything
     */
    void setViewerInfo(String viewerInfo);

    /**
     * This is used to set the name of WebRTC stream
     * @param streamName: any name
     */
    void setStreamName(String streamName);


    //FIXME: add comment
    void onCameraSwitch();
    void onVideoScalingSwitch(RendererCommon.ScalingType scalingType);
    void onCaptureFormatChange(int width, int height, int framerate);
    boolean onToggleMic();

}
