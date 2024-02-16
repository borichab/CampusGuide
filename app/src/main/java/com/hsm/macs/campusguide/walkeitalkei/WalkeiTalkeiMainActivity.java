package com.hsm.macs.campusguide.walkeitalkei;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.Strategy;
import com.hsm.macs.campusguide.R;

import java.io.IOException;
import java.util.Random;

public class WalkeiTalkeiMainActivity extends ConnectionsActivity {
    //If true, debug logs are shown on the device.
    private static final boolean DEBUG = false;

    //connection strategy for Nearby Connections.
    private static final Strategy STRATEGY = Strategy.P2P_STAR;
    //Length of state change animations.
    private static final long ANIMATION_DURATION = 700;

    //A set of background colors.
    @ColorInt
    private static final int[] COLORS =
            new int[] {
                    0xFFF44336 /* red */,
                    0xFF9C27B0 /* deep purple */,
                    0xFF00BCD4 /* teal */,
                    0xFF4CAF50 /* green */,
                    0xFFFFAB00 /* amber */,
                    0xFFFF9800 /* orange */,
                    0xFF795548 /* brown */
            };

    //This service id will find other nearby devices that are interested in the same thing.
    private static final String SERVICE_ID =
            "com.hsm.macs.campusguide.walkeitalkei.SERVICE_ID";

    private State mState = State.UNKNOWN;

    //A random UID used as this device's endpoint name(HostID).
    private String mName;

    //The background color of the 'CONNECTED' state.
    @ColorInt private int mConnectedColor = COLORS[0];

    //Displays the previous state during animation transitions.
    private TextView mPreviousStateView;

    //Displays the current state.
    private TextView mCurrentStateView;

    //An animator that controls the animation from previous state to current state.
    @Nullable
    private Animator mCurrentAnimator;

    //A running log of debug messages. Only visible when DEBUG=true. for developer
    private TextView mDebugLogView;

    //Listens to holding/releasing the volume key.
    private final GestureDetector mGestureDetector =
            new GestureDetector(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP) {
                @Override
                protected void onHold() {
                    logV("onHold");
                    startRecording();
                }

                @Override
                protected void onRelease() {
                    logV("onRelease");
                    stopRecording();
                }
            };

    //For recording audio as the user speaks.
    @Nullable private AudioRecorder mRecorder;

    //For playing audio from other users nearby.
    @Nullable private AudioPlayer mAudioPlayer;

    //The phone's original media volume.
    private int mOriginalVolume;

    //button for turn on/off discovery for visible only when isHost=true
    Button startDiscoveryButton;

    //this will ensure weather current device is host or not
    public boolean isHost = false;

    //stores host ID
    public String hostId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walkei_talkei_main);
        getSupportActionBar()
                .setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.actionBar));
        getSupportActionBar().setTitle("Campus Guide - Walkei Talkei");

        mPreviousStateView = (TextView) findViewById(R.id.previous_state);
        mCurrentStateView = (TextView) findViewById(R.id.current_state);

        mDebugLogView = (TextView) findViewById(R.id.debug_log);
        mDebugLogView.setVisibility(DEBUG ? View.VISIBLE : View.GONE);
        mDebugLogView.setMovementMethod(new ScrollingMovementMethod());

        mName = generateRandomName();

        isHost = getIntent().getBooleanExtra("is_Host", false);
        hostId = isHost ? null : getIntent().getStringExtra("host_Id");


        if (isHost) {
            ((TextView) findViewById(R.id.name)).setText("Host ID : " + mName);
        } else {
            ((TextView) findViewById(R.id.name)).setText("Client ID : " + mName);
        }

        // Initializing "Start Discovery" Button and setting onClickListener
        startDiscoveryButton = findViewById(R.id.start_discovery_button);
        if (!isHost)
            startDiscoveryButton.setVisibility(View.GONE);
        updateDiscoveryButton();

        startDiscoveryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getState().equals(State.CONNECT_OTHER))
                    setState(State.CONNECTED);
                else if(getState().equals(State.SEARCHING) && getConnectedEndpoints().isEmpty())
                    setState(State.UNKNOWN);
                else if (getState().equals(State.CONNECTED)) {
                    setState(State.CONNECT_OTHER);
                }
                else setState(State.SEARCHING);
            }
        });

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mState == State.CONNECTED && mGestureDetector.onKeyEvent(event)) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Set the media volume to max.
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mOriginalVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        setState(State.SEARCHING);
    }

    @Override
    protected void onStop() {
        // Restore the original volume.
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mOriginalVolume, 0);
        setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);

        // Stop all audio-related threads
        if (isRecording()) {
            stopRecording();
        }
        if (isPlaying()) {
            stopPlaying();
        }

        // After Activity stops, disconnect from Nearby Connections.
        setState(State.UNKNOWN);

        if (mCurrentAnimator != null && mCurrentAnimator.isRunning()) {
            mCurrentAnimator.cancel();
        }

        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (getState() == State.CONNECTED) {
            setState(State.SEARCHING);
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onEndpointDiscovered(Endpoint endpoint) {
        //found an advertiser! it will check weather endpoint name matches entered HostID
        if ((!isHost) && (hostId.equals(endpoint.getName()))) {
            connectToEndpoint(endpoint);
            stopDiscovering();
        }
    }

    @Override
    protected void onConnectionInitiated(Endpoint endpoint, ConnectionInfo connectionInfo) {
        // A connection to another device has been initiated! the auth token, which is the
        // same on both devices, to pick a color to use when we're connected. This way, users can
        // visually see which device they connected with. it will show once after connected in Host device
        mConnectedColor = COLORS[connectionInfo.getAuthenticationDigits().hashCode() % COLORS.length];

        // accept the connection immediately.
        acceptConnection(endpoint);
    }

    @Override
    protected void onEndpointConnected(Endpoint endpoint) {
        Toast.makeText(this, getString(R.string.toast_connected, endpoint.getName()), Toast.LENGTH_SHORT).show();
        //if host device then it will start advertising to other nearby devices
        if (isHost) {
            if (!getState().equals(State.CONNECT_OTHER))
                setState(State.CONNECT_OTHER);
        }
        else
            setState(State.CONNECTED);
    }

    @Override
    protected void onEndpointDisconnected(Endpoint endpoint) {
        Toast.makeText(this, getString(R.string.toast_disconnected, endpoint.getName()), Toast.LENGTH_SHORT).show();
        if (isHost){
            if (getConnectedEndpoints().isEmpty())
                setState(State.SEARCHING);
        }
        else
            setState(State.SEARCHING);
    }

    @Override
    protected void onConnectionFailed(Endpoint endpoint) {
        // on connection Let's try again.
        if ( !isHost && getState().equals(State.SEARCHING)) {
            startDiscovering();
        } else if (isHost && getState().equals(State.SEARCHING)) {
            if (isDiscovering())
                startAdvertising();
            else {
                startDiscovering();
                startAdvertising();
            }
            updateDiscoveryButton();
        }
    }


    //The state has changed.
    private void setState(State state) {
        if (mState == state) {
            logW("State set to " + state + " but already in that state");
            return;
        }

        logD("State set to " + state);
        State oldState = mState;
        mState = state;
        onStateChanged(oldState, state);
    }

    //return The current state.
    private State getState() {
        return mState;
    }

    /**
     * State has changed.
     *
     * @param oldState The previous state. Clean up anything related to this state.
     * @param newState The new state. Prepare the UI for this state.
     */
    private void  onStateChanged(State oldState, State newState) {
        if (mCurrentAnimator != null && mCurrentAnimator.isRunning()) {
            mCurrentAnimator.cancel();
        }

        // Update Nearby Connections to the new state.
        switch (newState) {
            case SEARCHING:
                disconnectFromAllEndpoints();
                if (!isDiscovering())
                    startDiscovering();
                if(!isAdvertising())
                    startAdvertising();
                updateDiscoveryButton();
                break;
            case CONNECTED:
                stopDiscovering();
                stopAdvertising();
                updateDiscoveryButton();
                break;
            case UNKNOWN:
                if (isHost) {
                    updateDiscoveryButton();
                    stopDiscovering();
                    stopAdvertising();
                }
                else
                    stopAllEndpoints();
                break;
            case CONNECT_OTHER:
                if (!isDiscovering())
                    startDiscovering();
                if (!isAdvertising()) {
                    startAdvertising();
                }
                updateDiscoveryButton();
                break;
            default:
                //no-op
                break;
        }

        // Update the UI.
        switch (oldState) {
            case UNKNOWN:
                transitionForward(oldState, newState);
                break;
            case SEARCHING:
                switch (newState) {
                    case UNKNOWN:
                        transitionBackward(oldState, newState);
                        break;
                    case CONNECTED:
                    case CONNECT_OTHER:
                        transitionForward(oldState, newState);
                        break;
                    default:
                        // no-op
                        break;
                }
                break;
            case CONNECT_OTHER:
                switch (newState) {
                    case UNKNOWN:
                    case SEARCHING:
                        transitionBackward(oldState, newState);
                        break;
                    case CONNECTED:
                        transitionForward(oldState, newState);
                        break;
                    default:
                        // no-op
                        break;
                }
                break;
            case CONNECTED:
                transitionBackward(oldState, newState);
                break;
        }
    }

    //this will update UI of Star discovery button on some conditions. this will shown only in Host device
    private void  updateDiscoveryButton() {
        if (getState().equals(State.CONNECT_OTHER) || getState().equals(State.SEARCHING)){
            startDiscoveryButton.setText(R.string.stop_discovery_button_lable);
            startDiscoveryButton.setBackgroundColor(0xFFD32F2F);
        }else{
            startDiscoveryButton.setText(R.string.start_discovery_button_bable);
            startDiscoveryButton.setBackgroundColor(0xFF01A307);
        }
    }

    //Transitions from the old state to the new state with an animation implying moving forward.
    @UiThread
    private void transitionForward(State oldState, final State newState) {
        mPreviousStateView.setVisibility(View.VISIBLE);
        mCurrentStateView.setVisibility(View.VISIBLE);

        updateTextView(mPreviousStateView, oldState);
        updateTextView(mCurrentStateView, newState);

        if (ViewCompat.isLaidOut(mCurrentStateView)) {
            mCurrentAnimator = createAnimator(false /* reverse */);
            mCurrentAnimator.addListener(
                    new AnimatorListener() {
                        @Override
                        public void onAnimationEnd(Animator animator) {
                            updateTextView(mCurrentStateView, newState);
                        }
                    });
            mCurrentAnimator.start();
        }
    }

    //Transitions from the old state to the new state with an animation implying moving backward.
    @UiThread
    private void transitionBackward(State oldState, final State newState) {
        mPreviousStateView.setVisibility(View.VISIBLE);
        mCurrentStateView.setVisibility(View.VISIBLE);

        updateTextView(mCurrentStateView, oldState);
        updateTextView(mPreviousStateView, newState);

        if (ViewCompat.isLaidOut(mCurrentStateView)) {
            mCurrentAnimator = createAnimator(true /* reverse */);
            mCurrentAnimator.addListener(
                    new AnimatorListener() {
                        @Override
                        public void onAnimationEnd(Animator animator) {
                            updateTextView(mCurrentStateView, newState);
                        }
                    });
            mCurrentAnimator.start();
        }
    }

    @NonNull
    private Animator createAnimator(boolean reverse) {
        Animator animator;
        if (Build.VERSION.SDK_INT >= 21) {
            int cx = mCurrentStateView.getMeasuredWidth() / 2;
            int cy = mCurrentStateView.getMeasuredHeight() / 2;
            int initialRadius = 0;
            int finalRadius = Math.max(mCurrentStateView.getWidth(), mCurrentStateView.getHeight());
            if (reverse) {
                int temp = initialRadius;
                initialRadius = finalRadius;
                finalRadius = temp;
            }
            animator =
                    ViewAnimationUtils.createCircularReveal(
                            mCurrentStateView, cx, cy, initialRadius, finalRadius);
        } else {
            float initialAlpha = 0f;
            float finalAlpha = 1f;
            if (reverse) {
                float temp = initialAlpha;
                initialAlpha = finalAlpha;
                finalAlpha = temp;
            }
            mCurrentStateView.setAlpha(initialAlpha);
            animator = ObjectAnimator.ofFloat(mCurrentStateView, "alpha", finalAlpha);
        }
        animator.addListener(
                new AnimatorListener() {
                    @Override
                    public void onAnimationCancel(Animator animator) {
                        mPreviousStateView.setVisibility(View.GONE);
                        mCurrentStateView.setAlpha(1);
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        mPreviousStateView.setVisibility(View.GONE);
                        mCurrentStateView.setAlpha(1);
                    }
                });
        animator.setDuration(ANIMATION_DURATION);
        return animator;
    }

    /** Updates the {@link TextView} with the correct color/text for the given {@link State}. */
    @UiThread
    private void updateTextView(TextView textView, State state) {
        switch (state) {
            case SEARCHING:
                textView.setBackgroundResource(R.color.state_searching);
                textView.setText(R.string.status_searching);
                break;
            case CONNECTED:
                textView.setBackgroundColor(mConnectedColor);
                if (isHost)
                    textView.setText(R.string.status_connected_host);
                else
                    textView.setText(R.string.status_connected);
                break;
            case CONNECT_OTHER:
                textView.setBackgroundColor(mConnectedColor);
                textView.setText("Visible to other devices. Click Stop Discovery to speak");
                break;
            default:
                textView.setBackgroundResource(R.color.state_unknown);
                textView.setText(R.string.status_unknown);
                break;
        }
    }

    /** {@see ConnectionsActivity#onReceive(Endpoint, Payload)} */
    @Override
    protected void onReceive(Endpoint endpoint, Payload payload) {
        if (payload.getType() == Payload.Type.STREAM) {
            if (mAudioPlayer != null) {
                mAudioPlayer.stop();
                mAudioPlayer = null;
            }

            AudioPlayer player =
                    new AudioPlayer(payload.asStream().asInputStream()) {
                        @WorkerThread
                        @Override
                        protected void onFinish() {
                            runOnUiThread(
                                    new Runnable() {
                                        @UiThread
                                        @Override
                                        public void run() {
                                            mAudioPlayer = null;
                                        }
                                    });
                        }
                    };
            mAudioPlayer = player;
            player.start();
        }
    }

    //Stops all currently streaming audio tracks.
    private void stopPlaying() {
        logV("stopPlaying()");
        if (mAudioPlayer != null) {
            mAudioPlayer.stop();
            mAudioPlayer = null;
        }
    }

    //return True if currently playing.
    private boolean isPlaying() {
        return mAudioPlayer != null;
    }

    //Starts recording sound from the microphone and streaming it to all connected devices with Host.
    private void startRecording() {
        logV("startRecording()");
        try {
            ParcelFileDescriptor[] payloadPipe = ParcelFileDescriptor.createPipe();

            // Send the first half of the payload (the read side) to Nearby Connections.
            send(Payload.fromStream(payloadPipe[0]));

            // Use the second half of the payload (the write side) in AudioRecorder.
            mRecorder = new AudioRecorder(payloadPipe[1]);
            mRecorder.start();
        } catch (IOException e) {
            logE("startRecording() failed", e);
        }
    }

    //Stops streaming sound from the microphone.
    private void stopRecording() {
        logV("stopRecording()");
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder = null;
        }
    }

    //return True if currently streaming from the microphone.
    private boolean isRecording() {
        return mRecorder != null && mRecorder.isRecording();
    }


    @Override
    protected String[] getRequiredPermissions() {
        return join(
                super.getRequiredPermissions(),
                Manifest.permission.RECORD_AUDIO);
    }

    //Joins 2 arrays together.
    private static String[] join(String[] a, String... b) {
        String[] join = new String[a.length + b.length];
        System.arraycopy(a, 0, join, 0, a.length);
        System.arraycopy(b, 0, join, a.length, b.length);
        return join;
    }

    /**
     * Queries the phone's contacts for their own profile, and returns their name. Used when
     * connecting to another device.
     */
    @Override
    protected String getName() {
        return mName;
    }

    @Override
    public String getServiceId() {
        return SERVICE_ID;
    }

    @Override
    public Strategy getStrategy() {
        return STRATEGY;
    }

    @Override
    protected void logV(String msg) {
        super.logV(msg);
        appendToLogs(toColor(msg, getResources().getColor(R.color.log_verbose)));
    }

    @Override
    protected void logD(String msg) {
        super.logD(msg);
        appendToLogs(toColor(msg, getResources().getColor(R.color.log_debug)));
    }

    @Override
    protected void logW(String msg) {
        super.logW(msg);
        appendToLogs(toColor(msg, getResources().getColor(R.color.log_warning)));
    }

    @Override
    protected void logW(String msg, Throwable e) {
        super.logW(msg, e);
        appendToLogs(toColor(msg, getResources().getColor(R.color.log_warning)));
    }

    @Override
    protected void logE(String msg, Throwable e) {
        super.logE(msg, e);
        appendToLogs(toColor(msg, getResources().getColor(R.color.log_error)));
    }

    private void appendToLogs(CharSequence msg) {
        mDebugLogView.append("\n");
        mDebugLogView.append(DateFormat.format("hh:mm", System.currentTimeMillis()) + ": ");
        mDebugLogView.append(msg);
    }

    private static CharSequence toColor(String msg, int color) {
        SpannableString spannable = new SpannableString(msg);
        spannable.setSpan(new ForegroundColorSpan(color), 0, msg.length(), 0);
        return spannable;
    }

    private static String generateRandomName() {
        String name = "";
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            name += random.nextInt(10);
        }
        return name;
    }

    private abstract static class AnimatorListener implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animator) {}

        @Override
        public void onAnimationEnd(Animator animator) {}

        @Override
        public void onAnimationCancel(Animator animator) {}

        @Override
        public void onAnimationRepeat(Animator animator) {}
    }

    //States that the UI goes through.
    public enum State {
        UNKNOWN,
        SEARCHING,
        CONNECTED,
        CONNECT_OTHER
    }
}
