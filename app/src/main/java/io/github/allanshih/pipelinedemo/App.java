package io.github.allanshih.pipelinedemo;

import android.app.Application;
import android.content.Context;

import com.fuhu.pipeline.manager.MqttManager;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;

import io.github.allanshih.pipelinedemo.pipeline.MqttCallbackHandler;
import io.github.allanshih.pipelinedemo.pipeline.TestAPI;


public class App extends Application {
	private static final String TAG = App.class.getSimpleName();

	private static App instance;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;

        // initial MqttManager with client id.
        MqttCallback mqttCallback = new MqttCallbackHandler(this);
        String clientId = MqttClient.generateClientId();
        MqttManager.getInstance().init(this, TestAPI.getMqttUrl(), clientId, mqttCallback);
	}

	public static App getInstance() {
		return instance;
	}

	public static String getAppPackage() {
		return instance.getPackageName();
	}

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }
}
