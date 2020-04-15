package com.nxtgizmo.androidmqttdemo.dashboard;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.nxtgizmo.androidmqttdemo.R;
import com.nxtgizmo.androidmqttdemo.mqtt_app.MqttApp;

import org.eclipse.paho.android.service.MqttAndroidClient;

import java.net.URI;

import javax.inject.Inject;

import timber.log.Timber;
import android.net.Uri;

public class DashBoardActivity extends AppCompatActivity implements DashboardContract{

    @Inject
    MqttAndroidClient client;
    private TextView message;

    private DashboardPresenter dashboardPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        message = findViewById(R.id.message);

        ((MqttApp)getApplication()).getMqttComponent().inject(this);
        dashboardPresenter = new DashboardPresenter(this,getApplicationContext());
        dashboardPresenter.connectToMqtt(client);
    }



    @Override
    public void onSuccess(String successMessage) {
        Timber.d(successMessage);
        message.setText(successMessage);
    }

    public void debugMsg(String msg) {
        final String str = msg;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                message.setText(str);
                if(str.equals("FULL")){
                    int musicFileIn =   R.raw.testfull;
                    playSound(musicFileIn);
                } else if(str.equals("THREEFROUTH")) {
                    int musicFileIn =   R.raw.testfull;
                }

            }
        });
    }

    public void playSound(int musicFileIn) {
        try {

            MediaPlayer mPlayer= MediaPlayer.create(DashBoardActivity.this,musicFileIn);

            //MediaPlayer mPlayer = new MediaPlayer();
            //Uri myUri = Uri.parse("file:///sdcard/mp3/example.mp3");
            //Uri myUri = Uri.parse("raw/audiofile.mp3");
//            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mPlayer.setDataSource(getApplicationContext(), myUri);
//            mPlayer.prepare();
            mPlayer.start();
//
//            String url = "http://........"; // your URL here
//            MediaPlayer mPlayer = new MediaPlayer();
//            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mPlayer.setDataSource(url);
//            mPlayer.prepare(); // might take long! (for buffering, etc)
//            mPlayer.start();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public void setReceivedMessage(String receivedMessage){
        Timber.d("Setting message");
        message = findViewById(R.id.message);
        Timber.d("Setting message 2 ");
        message.setText(receivedMessage);
    }
    @Override
    public void onError(String errorMessage) {
        Timber.d(errorMessage);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dashboardPresenter.unSubscribeMqttChannel(client);
        dashboardPresenter.disconnectMqtt(client);
    }
}
