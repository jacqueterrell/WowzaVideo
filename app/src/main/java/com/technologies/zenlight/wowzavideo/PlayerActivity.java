package com.technologies.zenlight.wowzavideo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import com.wowza.gocoder.sdk.api.player.WOWZPlayerConfig;
import com.wowza.gocoder.sdk.api.player.WOWZPlayerView;
import com.wowza.gocoder.sdk.api.status.WOWZState;
import com.wowza.gocoder.sdk.api.status.WOWZStatus;
import com.wowza.gocoder.sdk.api.status.WOWZStatusCallback;

public class PlayerActivity extends AppCompatActivity implements WOWZStatusCallback {

    WOWZPlayerView mStreamPlayerView;
    WOWZPlayerConfig mStreamPlayerConfig;
    private WOWZStatusCallback statusCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_layout);
        statusCallback = this;
        final Button btnStartStream = findViewById(R.id.btn_start_stream);
        final Button btnStopStream = findViewById(R.id.btn_stop_stream);

        btnStartStream.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mStreamPlayerView.play(mStreamPlayerConfig,statusCallback);
                btnStartStream.setVisibility(View.GONE);
                btnStopStream.setVisibility(View.VISIBLE);
            }
        });

        btnStopStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStreamPlayerView.stop(statusCallback);
                btnStartStream.setVisibility(View.VISIBLE);
                btnStopStream.setVisibility(View.GONE);
            }
        });

        mStreamPlayerView = findViewById(R.id.vwStreamPlayer);
        mStreamPlayerConfig = new WOWZPlayerConfig();
        mStreamPlayerConfig.setIsPlayback(true);
        mStreamPlayerConfig.setAudioEnabled(true);
        mStreamPlayerConfig.setVideoEnabled(true);
        mStreamPlayerConfig.setHostAddress(AppConstants.HOST_ADDRESS);
        mStreamPlayerConfig.setApplicationName(AppConstants.APPLICATION_NAME);
        mStreamPlayerConfig.setStreamName(AppConstants.STREAM_NAME);
        mStreamPlayerConfig.setPortNumber(AppConstants.PORT_NUMBER);
//        mStreamPlayerConfig.setHLSEnabled(true);
//        mStreamPlayerConfig.setHLSBackupURL("rtmp://origin.cdn.wowza.com:1935/live/0I0p2R0JWZmiAEREY1Mw0BPY1par5721");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mStreamPlayerView.stop(statusCallback);
    }

    @Override
    public void onWZStatus(WOWZStatus wowzStatus) {

        final StringBuffer statusMessage = new StringBuffer("Broadcast status: ");

        switch (wowzStatus.getState()) {
            case WOWZState.STARTING:
                statusMessage.append("Broadcast initialization");
                break;

            case WOWZState.READY:
                statusMessage.append("Ready to begin streaming");
                break;

            case WOWZState.RUNNING:
                statusMessage.append("Streaming is active");
                break;

            case WOWZState.STOPPING:
                statusMessage.append("Broadcast shutting down");
                break;

            case WOWZState.IDLE:
                statusMessage.append("The broadcast is stopped");
                break;

            default:
                return;
        }

        // Display the status message using the U/I thread
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(PlayerActivity.this, statusMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onWZError(final WOWZStatus wowzStatus) {
        // If an error is reported by the GoCoder SDK, display a message
        // containing the error details using the U/I thread
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(PlayerActivity.this,
                        "Streaming error: " + wowzStatus.getLastError().getErrorDescription(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
