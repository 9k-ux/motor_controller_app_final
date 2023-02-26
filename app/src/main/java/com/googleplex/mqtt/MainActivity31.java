package com.googleplex.mqtt;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class MainActivity31 extends AppCompatActivity {

    EditText brokerAddress, textToSend, topicToSend,topic_to_send2, text_to_send2,subscriptionTopic,subscriptionTopic2;
    Button connectButton, sendButton,send_button2, subscribeButton,subscribeButton2;
    TextView receivedMessage,receivedMessage2, connectionStatus,textView;

    MqttAndroidClient client;

    String serverURL = "tcp://13.233.147.62:1883";

    String topic = "foo/bar";

    String sTopic = "ram",sTopic2="";


    boolean connectionFlag = false;


    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main31);
        connectToBroker();
        subscribeToTopic("ram2");
        subscribeToTopic2("ram2");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setLogo(getDrawable(R.drawable.mqtt_icon));
        }
        /////////////////////////////////////////////////////////////////
        brokerAddress = findViewById(R.id.broker_address);




        textToSend = findViewById(R.id.text_to_send);




        text_to_send2 = findViewById(R.id.text_to_send2);
        textView = findViewById(R.id.textView);


        topicToSend = findViewById(R.id.topic_to_send);
        topic_to_send2 = findViewById(R.id.topic_to_send2);

        subscriptionTopic = findViewById(R.id.subscription_topic);
        subscriptionTopic2 = findViewById(R.id.subscription_topic2);

        subscribeButton = findViewById(R.id.subscribe_button);
        subscribeButton2 = findViewById(R.id.subscribe_button2);

        connectButton = findViewById(R.id.connect_to_broker_button);


        sendButton = findViewById(R.id.send_button);




        send_button2 = findViewById(R.id.send_button2);

        receivedMessage = findViewById(R.id.received_message);
        receivedMessage2 =findViewById(R.id.received_message2);

        connectionStatus = findViewById(R.id.connection_status);
        /////////////////////////////////////////////////////////////////
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                serverURL = "tcp://" + brokerAddress.getText().toString() + ":1883";
                connectToBroker();

            }
        });
        /////////////////////////////////////////////////////////////////
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topic = topicToSend.getText().toString();
                sendMessage(topic);
            }
        });

        send_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageTwo(topic_to_send2.getText().toString());
            }
        });

        /////////////////////////////////////////////////////////////////
        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sTopic = subscriptionTopic.getText().toString();
//                sTopic = "ram";
                subscribeToTopic("ram");
            }
        });

        subscribeButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sTopic2 = subscriptionTopic2.getText().toString();

                subscribeToTopic("ram");
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    void connectToBroker() {
        String clientId = MqttAsyncClient.generateClientId();
        Log.w("ClientID",clientId);
        client = new MqttAndroidClient(this.getApplicationContext(), serverURL, clientId);
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    connectionStatus.setText("Connected To " + serverURL);
                    connectionFlag = true;
                    sendButton.setEnabled(true);
                    subscribeButton.setEnabled(true);
                    subscribeToTopic("ram");
                    subscribeToTopic2("ram2");
                    subscribeToTopic2("ram3");

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                    Log.w("Exception",exception);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    void sendMessage(String topic) {
        String payload = textToSend.getText().toString();
        byte[] encodedPayload;
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
            Toast.makeText(getApplicationContext(), "Sent", Toast.LENGTH_SHORT).show();
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }

    void sendMessageTwo(String topic) {
        String payload = text_to_send2.getText().toString();
        byte[] encodedPayload;
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
            Toast.makeText(getApplicationContext(), "Sent", Toast.LENGTH_SHORT).show();
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void subscribeToTopic(String topic) {
        try {
            if (client.isConnected()) {
                client.subscribe(topic, 0);
                Toast.makeText(getApplicationContext(), "Subscribed", Toast.LENGTH_SHORT).show();
                client.setCallback(new MqttCallback() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void connectionLost(Throwable cause) {
                        connectionStatus.setText("Connection Failed");
                        connectionFlag = false;
                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
//                        textView.setText(message.toString());
                        if("ram2".equals(topic))
                        {
                            receivedMessage.setText(message.toString());
//                            textView.setText(message.toString());
                            Toast.makeText(MainActivity31.this, message.toString(), Toast.LENGTH_SHORT).show();
                        }

                        else
                        {
                            receivedMessage2.setText(message.toString());
                        }

                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {
                    }
                });
            }
        } catch (Exception ignored) {
        }
    }


    public void subscribeToTopic2(String topic) {
        try {
            if (client.isConnected()) {
                client.subscribe(topic, 0);
                Toast.makeText(getApplicationContext(), "Subscribed", Toast.LENGTH_SHORT).show();
                client.setCallback(new MqttCallback() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void connectionLost(Throwable cause) {

                        connectionStatus.setText("Connection Failed");
                        connectionFlag = false;
                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        if("ram2".equals(topic))
                        {
//                            receivedMessage.setText(message.toString());
                            Toast.makeText(MainActivity31.this, message.toString(), Toast.LENGTH_SHORT).show();
                            textView.setText((message.toString()));
                        }
                        if("ram3".equals(topic))
                        {
//                            receivedMessage.setText(message.toString());
                            Toast.makeText(MainActivity31.this, message.toString(), Toast.LENGTH_SHORT).show();
                            textView.setText((message.toString()));
                        }
                        else
                        {
//                            receivedMessage2.setText(message.toString());
                            Toast.makeText(MainActivity31.this, message.toString(), Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {
                    }
                });
            }
        } catch (Exception ignored) {
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
}
