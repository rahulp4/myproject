package com.nxtgizmo.androidmqttdemo.dashboard;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nxtgizmo.androidmqttdemo.R;
import com.nxtgizmo.androidmqttdemo.mqtt_app.MqttApp;

import org.eclipse.paho.android.service.MqttAndroidClient;

import java.net.URI;

import javax.inject.Inject;

import timber.log.Timber;
import android.net.Uri;
import android.widget.Toast;

public class DashBoardActivity extends AppCompatActivity implements DashboardContract{

    @Inject
    MqttAndroidClient client;
    private TextView message;
    private boolean buzzerOn   =   false;

    private long firstBuzzerTimeStampInMillis   =   -1;

    private long resetInterval  =   30000;
    private boolean userActionTaken =   false;

    private DashboardPresenter dashboardPresenter;

    private EditText edittext1, edittext2;
    private Button buttonSum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        message = findViewById(R.id.message);

        addListenerOnButton();

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
        String tankStatusLabel  =   "Tank Status is ";
        int musicFileIn = 1;
                runOnUiThread(new Runnable() {
            @Override
            public void run() {
                message.setText(str);
                boolean playSound   =   false;
                if(str.equals("FULL")){
                    if(userActionTaken){
                        edittext2.setText("User has Switched Off");
                    } else {
                        edittext2.setText("User has Not Switched Off");
                    }
                    message.setText( "Tank status is full. Switch of motor");
                    if(buzzerOn==false){
                        Timber.d("Buzzer is false. This is first time");
                        //FIRST TIME
                        buzzerOn    =   true;
                        firstBuzzerTimeStampInMillis    =   System.currentTimeMillis();
                        playSound   =   true;
                    } else {
                        Timber.d("Buzzer is True.");
                        long currentTime    =   System.currentTimeMillis();
                        if(currentTime-firstBuzzerTimeStampInMillis>=resetInterval){
                            //RESET FLAG
                            Timber.d("Time to reset alamr");
                            buzzerOn    =   false;
                            playSound   =   true;
                            userActionTaken =   false;
                        } else {
                            Timber.d("Timeout is ot over");
                            if(userActionTaken==false){
                                Timber.d("No user input yet");
                                //NO USER INPUT
                                playSound   =   true;
                            } else {
                                Timber.d("User input to switch off done earlier");
                                playSound   =   false;
                            }
                        }
                    }


                    int musicFileIn =   R.raw.tankpull;

                    if(playSound){
                        playSound(musicFileIn);
                    }

                } else if(str.equals("THREEFROUTH")) {
                    message.setText( "Tank status is three rourth. About to be filled");
                    int musicFileIn =   R.raw.testfull;
                } else {
                    message.setText( "Tank status "+str);
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


    public void addListenerOnButton() {
        edittext1 = (EditText) findViewById(R.id.editText1);
        edittext2 = (EditText) findViewById(R.id.editText2);
        buttonSum = (Button) findViewById(R.id.button);

        buttonSum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userActionTaken=true;
//
//                String value1=edittext1.getText().toString();
//                String value2=edittext2.getText().toString();
//                int a=Integer.parseInt(value1);
//                int b=Integer.parseInt(value2);
//                int sum=a+b;
//                message.setText(String.valueOf(sum));
                edittext1.setText("Switch Of Alarm");
                //Toast.makeText(getApplicationContext(),String.valueOf(sum), Toast.LENGTH_LONG).show();

            }
        });
    }
}
