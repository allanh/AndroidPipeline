package io.github.allanshih.pipelinedemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.fuhu.pipeline.component.HttpItem;
import com.fuhu.pipeline.manager.PipelineManager;
import com.fuhu.pipeline.mqtt.MqttAction;
import com.fuhu.pipeline.mqtt.MqttItem;
import com.fuhu.pipeline.mqtt.MqttQos;

import io.github.allanshih.pipelinedemo.pipeline.TestAPI;
import io.github.allanshih.pipelinedemo.pipeline.callback.ConnectMqttCallback;
import io.github.allanshih.pipelinedemo.pipeline.callback.DisconnectMqttCallback;
import io.github.allanshih.pipelinedemo.pipeline.callback.HttpBinGetIpCallback;
import io.github.allanshih.pipelinedemo.pipeline.callback.PublishMqttMessageCallback;
import io.github.allanshih.pipelinedemo.pipeline.callback.SubscribeMqttCallback;
import io.github.allanshih.pipelinedemo.pipeline.callback.UnsubscribeMqttCallback;
import io.github.allanshih.pipelinedemo.pipeline.object.HttpBinGetIpItem;
import io.github.allanshih.pipelinedemo.pipeline.taskList.BaseHttpTaskList;
import io.github.allanshih.pipelinedemo.pipeline.taskList.BaseMqttTaskList;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button mSubscribeButton, mUnsubscribeButton, mConnectMqttButton, mDisconnectMqttButton,
            mPublishMqttMessageButton, mHttpBinTestButton;
    private EditText mMqttTopicText;
    private Spinner mEventSpinner;
    private int currentEvent;
    private String [] events;

    private static final String MOCK_SERIAL_NUMBER = "12345678";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMqttTopicText = (EditText) findViewById(R.id.mqttTopicText);
        mMqttTopicText.setText(TestAPI.getEventTopic(MOCK_SERIAL_NUMBER));

        mEventSpinner = (Spinner) findViewById(R.id.eventSpinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.event_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        mEventSpinner.setAdapter(adapter);
        mEventSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentEvent = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        events = getResources().getStringArray(R.array.event_array);

        // MQTT
        mSubscribeButton = (Button) findViewById(R.id.subscribeButton);
        mSubscribeButton.setOnClickListener(mOnClickListener);
        mUnsubscribeButton = (Button) findViewById(R.id.unsubscribeButton);
        mUnsubscribeButton.setOnClickListener(mOnClickListener);
        mConnectMqttButton = (Button) findViewById(R.id.connectMqttButton);
        mConnectMqttButton.setOnClickListener(mOnClickListener);
        mDisconnectMqttButton = (Button) findViewById(R.id.disconnectMqttButton);
        mDisconnectMqttButton.setOnClickListener(mOnClickListener);
        mPublishMqttMessageButton = (Button) findViewById(R.id.publishMessageButton);
        mPublishMqttMessageButton.setOnClickListener(mOnClickListener);
        mHttpBinTestButton = (Button) findViewById(R.id.httpBinButton);
        mHttpBinTestButton.setOnClickListener(mOnClickListener);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.connectMqttButton:
                    connectMqtt();
                    break;
                case R.id.disconnectMqttButton:
                    disconnectMqtt();
                    break;
                case R.id.publishMessageButton:
                    publishMqttMessage();
                    break;
                case R.id.subscribeButton:
                    subscribeTopics();
                    break;
                case R.id.unsubscribeButton:
                    unsubscribeTopics();
                    break;
                case R.id.httpBinButton:
                    getIp();
                    break;
            }
        }
    };

    /**
     * Login into the application using Fuhu-Pipeline.
     */
    private void getIp() {
        HttpItem okHttpItem = new HttpItem.Builder(this)
                .url("https://httpbin.org/ip")
                .dataModel(HttpBinGetIpItem.class) // for response
                .timeout(5000L)
                .get();

        PipelineManager.getInstance().doPipeline(
                new BaseHttpTaskList(),
                okHttpItem,
                new HttpBinGetIpCallback());
    }

    /**
     * Connect to MQTT broker.
     */
    private void connectMqtt() {
        MqttItem mqttItem = new MqttItem.Builder(this)
                .actionType(MqttAction.CONNECT)
                .build();

        PipelineManager.getInstance().doPipeline(
                new BaseMqttTaskList(),
                mqttItem,
                new ConnectMqttCallback());
    }

    /**
     * Disconnect from MQTT broker.
     */
    private void disconnectMqtt() {
        MqttItem mqttItem = new MqttItem.Builder(this)
                .actionType(MqttAction.DISCONNECT)
                .build();

        PipelineManager.getInstance().doPipeline(
                new BaseMqttTaskList(),
                mqttItem,
                new DisconnectMqttCallback());
    }

    /**
     * Subscribe topics for device's events.
     */
    private void subscribeTopics() {
        MqttItem mqttItem = new MqttItem.Builder(this)
                .actionType(MqttAction.SUBSCRIBE)
                .topic(mMqttTopicText.getText().toString(), MqttQos.AT_LEAST_ONCE)
                .build();

        PipelineManager.getInstance().doPipeline(
                new BaseMqttTaskList(),
                mqttItem,
                new SubscribeMqttCallback());
    }

    /**
     * Unsubscribe topics for device's events.
     */
    private void unsubscribeTopics() {
        if (mMqttTopicText.getText() != null) {
            MqttItem mqttItem = new MqttItem.Builder(this)
                    .actionType(MqttAction.UNSUBSCRIBE)
                    .topic(mMqttTopicText.getText().toString())
                    .build();

            PipelineManager.getInstance().doPipeline(
                    new BaseMqttTaskList(),
                    mqttItem,
                    new UnsubscribeMqttCallback());
        }
    }

    /**
     * Publish a message to Mqtt broker.
     */
    private void publishMqttMessage() {
        MqttItem mqttItem = new MqttItem.Builder(this)
                .actionType(MqttAction.PUBLISH_MESSAGE)
                .topic(mMqttTopicText.getText().toString())
                .payload("TestMessage")
                .build();

        PipelineManager.getInstance().doPipeline(
                new BaseMqttTaskList(),
                mqttItem,
                new PublishMqttMessageCallback());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PipelineManager.release();
    }
}
