package com.synclovis;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import static android.content.Context.SENSOR_SERVICE;

public class RNWalkCounterModule extends ReactContextBaseJavaModule implements SensorEventListener, StepListener{

  private final ReactApplicationContext reactContext;
  private StepDetector simpleStepDetector;
  private SensorManager sensorManager;
  private Sensor accel;
  private int numSteps;

  public RNWalkCounterModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNWalkCounter";
  }

  @ReactMethod
  public void startCounter(){
    Toast.makeText(getReactApplicationContext(),"Step Started",Toast.LENGTH_LONG).show();
    numSteps = 0;
    initStepCounter();
    runStepCounter();
    this.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit("onStepStart",null);

  }

  public void initStepCounter(){
    sensorManager = (SensorManager) reactContext.getSystemService(SENSOR_SERVICE);
    accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    simpleStepDetector = new StepDetector();
    simpleStepDetector.registerListener(this);
  }

  public void runStepCounter(){
    sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);
  }

  public void onStepRunning(long newSteps){
    WritableMap params = Arguments.createMap();
    params.putString("steps", ""+newSteps);
    this.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit("onStepRunning",params);
  }

  @ReactMethod
  public void stopCounter(){
    sensorManager.unregisterListener(this);
  }
  @Override
  public void onAccuracyChanged(Sensor s, int i){}

  @Override
  public void onSensorChanged(SensorEvent event){
    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
      simpleStepDetector.updateAccel(
              event.timestamp, event.values[0], event.values[1], event.values[2]);
    }
  }


  @Override
  public void step(long timeNs) {
    numSteps++;
    onStepRunning(numSteps);
  }
}