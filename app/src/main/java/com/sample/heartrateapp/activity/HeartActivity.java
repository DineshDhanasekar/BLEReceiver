package com.sample.heartrateapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sample.heartrateapp.R;
import com.sample.heartrateapp.util.TCPClient;

public class HeartActivity extends AppCompatActivity {

    private TCPClient mTCPClient;
    private EditText ip;
    private EditText port;
    private EditText value;
    private Button connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart);

        ip = findViewById(R.id.ip_editText);
        port = findViewById(R.id.port_editText);
        value = findViewById(R.id.value_editText);

        connect = findViewById(R.id.connect);
        final Button send = findViewById(R.id.button);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (connect.getText().equals("Connect")) {

                    String ipAddr = ip.getText().toString();
                    int portVal = Integer.parseInt(port.getText().toString());
                    new ConnectTask(ipAddr, portVal).execute("");
                } else {
                    if (mTCPClient != null) {
                        mTCPClient.stopClient();
                    }
                    connect.setText("Connect");
                }
            }
        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTCPClient != null) {
                    mTCPClient.sendMessage(value.getText().toString());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTCPClient != null) {
            mTCPClient.stopClient();
        }
    }

    public class ConnectTask extends AsyncTask<String, String, TCPClient> {

        String ipAddress;
        int portDate;
        public ConnectTask(String ipAddr, int portVal) {
            ipAddress = ipAddr;
            portDate = portVal;
        }

        @Override
        protected TCPClient doInBackground(String... message) {

            //we create a TCPClient object
            mTCPClient = new TCPClient(ipAddress, portDate, new TCPClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            });
            mTCPClient.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            //response received from server
            Log.d("test", "response " + values[0]);
            //process server response here....
            connect.setText("Disconnect");
        }
    }
}
