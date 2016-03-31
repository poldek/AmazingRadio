package com.szycha.amazingradio.AppService;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.szycha.amazingradio.R;
import com.szycha.amazingradio.ScrollingActivity;

import java.io.IOException;

public class StreamService extends Service implements
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnBufferingUpdateListener {

    private static final String ACTION_BUTTON_PLAY_PAUSE = "com.szycha.amazingradio.AppService.ACTION_PLAY_PAUSE";
    private static final String ACTION_BITTON_CLOSE = "com.szycha.amazingradio.AppService.ACTION_CLOSE";


    // radio UNISI
    public String URL_STREAM;

    // notification
    private static final int NOTIFICATION_ID = 1;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;
    private boolean isPausedInCall = false;
    private NotificationCompat.Builder builder;
    private String NAZWA_RADIA;

    //intent
    private Intent bufferIntent;

    public static final String BROADCAST_BUFFER = "com.szycha.amazingradio.AppService";

    private MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("create", "service created");

        bufferIntent = new Intent(BROADCAST_BUFFER);

        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);

        mediaPlayer.reset();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Przychodzi z Activity
        try {
            URL_STREAM = intent.getStringExtra("radio_link");
            NAZWA_RADIA = intent.getStringExtra("nazwa");

        } catch (RuntimeException ex) {
        }
        Log.d("play", "play streaming");
        Utils.setDataBooleanToSP(this, Utils.IS_STREAM, true);

        if (intent != null) {
            if (intent.getAction() != null) {
                if (intent.getAction().equals(ACTION_BUTTON_PLAY_PAUSE)) {
                    if (mediaPlayer.isPlaying()) {
                        initNotification("Pause", 1);
                        Utils.setDataBooleanToSP(this, Utils.IS_STREAM, false);
                        //Wysylam brodcasta do activity ze jest pausa i wszytskie buttony pausy na play
                        bufferIntent.putExtra("buffering", "2");
                        sendBroadcast(bufferIntent);
                    }
                }

            }
        }


        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (mediaPlayer != null) {
                            pauseMedia();
                            isPausedInCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        if (mediaPlayer != null) {
                            if (isPausedInCall) {
                                isPausedInCall = false;
                                playMedia();
                            }
                        }
                        break;
                }
            }
        };

        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        mediaPlayer.reset();

        /**
         * play media
         */
        if (!mediaPlayer.isPlaying()) {
            try {
//                Log.d("streamm", "" + URL_STREAM);
                mediaPlayer.setDataSource(URL_STREAM);

                // sent to UI radio is buffer
                sendBufferingBroadcast();
                initNotification(NAZWA_RADIA, 0); //play

                mediaPlayer.prepareAsync();
            } catch (IllegalArgumentException e) {
                Log.d("error", e.getMessage());
            } catch (IllegalStateException e) {
                Log.d("error", e.getMessage());
            } catch (IOException e) {
                Log.d("error", e.getMessage());
            } catch (RuntimeException ex) {

            }
        }


        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        stopMedia();
        stopSelf();
    }


    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Toast.makeText(this, "Error not valid playback", Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Toast.makeText(this, "Error server died", Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Toast.makeText(this, "Error unknown", Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }


    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        // sent to UI, audio has buffered
        sendBufferCompleteBroadcast();

        playMedia();
    }


    private void pauseMedia() {
        try {
            if (mediaPlayer.isPlaying())
                mediaPlayer.pause();
        } catch (IllegalStateException ex) {
        }
    }

    private void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    private void stopMedia() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.stop();
    }

    /**
     * sent buffering
     */
    private void sendBufferingBroadcast() {
        bufferIntent.putExtra("buffering", "1");
        sendBroadcast(bufferIntent);
    }

    /**
     * sent buffering complete
     */
    private void sendBufferCompleteBroadcast() {
        bufferIntent.putExtra("buffering", "0");
        sendBroadcast(bufferIntent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("tag", "remove notification");
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }

        if (phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }

        cancelNotification();
    }

    /**
     * show notificaiton
     */
    private void initNotification(String nazwa_radia, int icona) {

        Intent notificationIntent = new Intent(getApplicationContext(), ScrollingActivity.class);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent intent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Amazing Radio")
                .setContentText(nazwa_radia);

        int iconaPlayPause = android.R.drawable.ic_media_pause;
        String textPausePLay = null;
        switch (icona) {
            case 0: //PLay
                iconaPlayPause = android.R.drawable.ic_media_pause;
                textPausePLay = "Pause";
                bufferIntent.putExtra("buffering", "3");
                sendBroadcast(bufferIntent);
                break;
            case 1: //Pause
                iconaPlayPause = android.R.drawable.ic_media_play;
                textPausePLay = "PLay";

                break;
            default:
                break;
        }

        //Click na play Pause
        Intent favoritesIntent = new Intent(this, StreamService.class);
        favoritesIntent.setAction(ACTION_BUTTON_PLAY_PAUSE);
        PendingIntent favoritesPendingIntent = PendingIntent.getService(this, 1, favoritesIntent, 0);
        //Click STOP KASOWANIE NOTY

        //Click STOP KASOWANIE NOTY
        //Intent stopIntent = new Intent(this, StreamService.class);
        //stopIntent.setAction(ACTION_BITTON_CLOSE);
        //PendingIntent stopFavorites = PendingIntent.getService(this, 1, stopIntent, 0);


        builder.addAction(iconaPlayPause, textPausePLay, favoritesPendingIntent);
        //builder.addAction(android.R.drawable.ic_delete, "Stop", stopFavorites);
        builder.setContentIntent(intent);
        builder.setOngoing(true);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

    }

    /**
     * cancel notification
     */
    private void cancelNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
        builder.setOngoing(false);
    }
}