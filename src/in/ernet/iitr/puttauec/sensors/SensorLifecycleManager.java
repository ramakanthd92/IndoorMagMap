package in.ernet.iitr.puttauec.sensors;

import in.ernet.iitr.puttauec.sensors.internal.HWSensorEventListener;
import android.content.Context;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;


public class SensorLifecycleManager {

	private static final String TAG = "SensorLifecycleManager";

	private Context mCtx;
	private SensorManager mSensorManager;
	private HWSensorEventListener mSensorEventListener;

	public static final int SENSOR_ACCELEROMETER = 1;
	public static final int SENSOR_GRAVITY = 2;
	public static final int SENSOR_MAGNETISM = 3;
	public static final int SENSOR_GYROSCOPE = 4;
	public static final int SENSOR_ROTATION_VECTOR = 5;
	public static final int SENSOR_WIFI = 100;

	// This is a Singleton class
	private static SensorLifecycleManager mInstance = null;

	public static SensorLifecycleManager getInstance(Context ctx) {
		if (mInstance == null) {
			mInstance = new SensorLifecycleManager(ctx);
		}
		return mInstance;
	}

	private SensorLifecycleManager(Context ctx) {
		mCtx = ctx.getApplicationContext();
		mSensorManager = (SensorManager) mCtx
				.getSystemService(Context.SENSOR_SERVICE);
		mSensorEventListener = new HWSensorEventListener();
	}

	private void resumeAll() {
		resumeHWEventListeners();
	
	}

	private void resumeHWEventListeners() {
		int SENSOR_DELAY = SensorManager.SENSOR_DELAY_NORMAL;
		mSensorManager.registerListener(mSensorEventListener, mSensorManager
				.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
				SENSOR_DELAY);
		mSensorManager.registerListener(mSensorEventListener, mSensorManager
				.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
				SENSOR_DELAY);
	/*	mSensorManager.registerListener(mSensorEventListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
				SENSOR_DELAY);  
				
   		mSensorManager.registerListener(mSensorEventListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
				SENSOR_DELAY);
		mSensorManager.registerListener(mSensorEventListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SENSOR_DELAY); */
	}

	@SuppressWarnings("unused")
	private void pauseAll() {
		pauseHWEventListeners();
	}

	private void pauseHWEventListeners() {
		mSensorManager.unregisterListener(mSensorEventListener);
	}

	public void registerCallback(ISensorCallback callback, int sensorEventType) {
		boolean resumeRequired = false;

		switch (sensorEventType) {
		case SENSOR_ACCELEROMETER:
		case SENSOR_GYROSCOPE:
		case SENSOR_GRAVITY:
		case SENSOR_MAGNETISM:
		case SENSOR_ROTATION_VECTOR:
	
			if (mSensorEventListener.callbackCount() == 0) {
				resumeRequired = true;
			}
			
			mSensorEventListener.registerCallback(callback);

			if (resumeRequired && mSensorEventListener.callbackCount() > 0) {
				resumeHWEventListeners();
			}

			break;
		default:
			throw new IllegalArgumentException("Invalid eventType " + sensorEventType
					+ " for SensorEvents");
		}
	}

	public void unregisterCallback(ISensorCallback callback, int sensorEventType) {
		switch (sensorEventType) {
		case SENSOR_ACCELEROMETER:
		case SENSOR_GYROSCOPE:
		case SENSOR_GRAVITY:
		case SENSOR_MAGNETISM:
		case SENSOR_ROTATION_VECTOR:
			mSensorEventListener.unregisterCallback(callback);
			if (mSensorEventListener.callbackCount() == 0) {
				pauseHWEventListeners();
			}
			break;
		default:
			throw new IllegalArgumentException("Invalid eventType "
					+ sensorEventType + " for SensorEvents");
		}
	}

	// Unregister from all possible callbacks.
	// Useful in the cases when you don't know what kind of callbacks
	// the object has registered itself.
	public void unregisterCallback(ISensorCallback callback) {
		unregisterCallback(callback, SENSOR_ACCELEROMETER);
		unregisterCallback(callback, SENSOR_GRAVITY);
		unregisterCallback(callback, SENSOR_GYROSCOPE);
		unregisterCallback(callback, SENSOR_MAGNETISM);
		unregisterCallback(callback, SENSOR_ROTATION_VECTOR);
	}
	
	public float[] getRotationMatrix() {
		return mSensorEventListener.getRotationMatrix();
	}
	
	public float[] getInclinationMatrix() {
		return mSensorEventListener.getInclinationMatrix();
	}
	
	public float[] getOrientation() {
		float[] values = new float[3];
		return SensorManager.getOrientation(getRotationMatrix(), values);
	}
	
	
	public float[] getRotationVector() {
		float[] values = new float[3];
		return mSensorEventListener.getRotationVector();
	}
}
