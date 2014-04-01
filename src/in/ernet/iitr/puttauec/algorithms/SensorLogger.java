package in.ernet.iitr.puttauec.algorithms;

import in.ernet.iitr.puttauec.sensors.ISensorCallback;
import in.ernet.iitr.puttauec.sensors.SensorLifecycleManager;
//import in.iitr.ernet.puttauec.db.LocationFingerprint;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.content.Context;
//import android.net.wifi.WifiManager;
//import android.net.wifi.WifiManager.WifiLock;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

public class SensorLogger implements IAlgorithm, ISensorCallback {

	private static final String SAMPLES_DIR = Environment.getExternalStorageDirectory() + File.separator + "samples";
	private static final int STOPPED = 0;
	private static final int STARTED = 1;
	private static final int PAUSED = 2;
	private static final String TAG = "SensorLogger";
	
	private SensorLifecycleManager mSensorLifecycleManager;
	private int mState;
	private FileWriter mAccelFileWriter;
	private FileWriter mLinearAccelFileWriter;
	private FileWriter mRVFileWriter;
	private FileWriter mGyroFileWriter;
	private FileWriter mMagFileWriter;
	private FileWriter mAngleFileWriter;

	public SensorLogger(Context ctx) {
		mSensorLifecycleManager = SensorLifecycleManager.getInstance(ctx);
		mState = STOPPED;
	}
	
	public void start() {
		mLinearAccelFileWriter = getFile("sLog.linaccel", "csv");
		mAccelFileWriter = getFile("sLog.accel", "csv");
		mGyroFileWriter = getFile("sLog.gyro", "csv");
		mMagFileWriter = getFile("sLog.mag", "csv");
		mAngleFileWriter = getFile("sLog.angle", "csv");
		mRVFileWriter = getFile("sLog.RV", "csv");
		mSensorLifecycleManager.registerCallback(this, SensorLifecycleManager.SENSOR_ACCELEROMETER);
		mSensorLifecycleManager.registerCallback(this, SensorLifecycleManager.SENSOR_LINEAR_ACCELERATION);
		mSensorLifecycleManager.registerCallback(this, SensorLifecycleManager.SENSOR_ROTATION_VECTOR);
	    mSensorLifecycleManager.registerCallback(this, SensorLifecycleManager.SENSOR_GYROSCOPE);
		mSensorLifecycleManager.registerCallback(this, SensorLifecycleManager.SENSOR_MAGNETISM);
//		mSensorLifecycleManager.registerCallback(this, SensorLifecycleManager.SENSOR_GRAVITY);
	    mState = STARTED;
		
	}

	public void stop() {
//		mWifiLock.release();		
		mSensorLifecycleManager.unregisterCallback(this, SensorLifecycleManager.SENSOR_ACCELEROMETER);
		mSensorLifecycleManager.unregisterCallback(this, SensorLifecycleManager.SENSOR_LINEAR_ACCELERATION);
		mSensorLifecycleManager.unregisterCallback(this, SensorLifecycleManager.SENSOR_ROTATION_VECTOR);
		mSensorLifecycleManager.unregisterCallback(this, SensorLifecycleManager.SENSOR_GYROSCOPE);
		mSensorLifecycleManager.unregisterCallback(this, SensorLifecycleManager.SENSOR_MAGNETISM);
//		mSensorLifecycleManager.unregisterCallback(this, SensorLifecycleManager.SENSOR_GRAVITY);
//		mSensorLifecycleManager.unregisterCallback(this, SensorLifecycleManager.SENSOR_WIFI);
		
		try {
			mAccelFileWriter.flush();
			mAccelFileWriter.close();
			
			mLinearAccelFileWriter.flush();
			mLinearAccelFileWriter.close();
			
			mGyroFileWriter.flush();
	    	mGyroFileWriter.close();
			
			mMagFileWriter.flush();
			mMagFileWriter.close();
			
			mAngleFileWriter.flush();
	    	mAngleFileWriter.close();
			
			mRVFileWriter.flush();
	    	mRVFileWriter.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "Flushing and closing files failed!", e);
			throw new RuntimeException(e);
		}
		
		
		mState = STOPPED;
	}

	@Override
	public void resume() {
		// Started state -> Invalid!!!
		// Stopped state -> No Op
		// Paused state -> restart
		if(mState == PAUSED) { 
			start();
		}
	}

	@Override
	public void pause() {
		// Started state -> Paused
		// Stopped state -> No op
		// Paused state -> Invalid!!!
		if(mState == STARTED) {
			stop();
			mState = PAUSED;
		}
	}

	@Override
	public void onAccelUpdate(float[] values, long deltaT, long timestamp) {
		persistToFile(mAccelFileWriter, values, deltaT, timestamp);
	}
	
	@Override
	public void onLinearAccelUpdate(float[] values, long deltaT, long timestamp) {
		persistToFile(mLinearAccelFileWriter, values, deltaT, timestamp);
	}

	@Override
	public void onGravityUpdate(float[] values, long deltaT, long timestamp) {
		persistToFile(mAngleFileWriter, mSensorLifecycleManager.getOrientation(), deltaT, timestamp);
	}

	@Override
	public void onMagneticFieldUpdate(float[] values, long deltaT,
			long timestamp) {
		persistToFile(mMagFileWriter, values, deltaT, timestamp);
		persistToFile(mAngleFileWriter, mSensorLifecycleManager.getOrientation(), deltaT, timestamp);
	}

	@Override
	public void onGyroUpdate(float[] values, long deltaT, long timestamp) {
		persistToFile(mGyroFileWriter, values, deltaT, timestamp);
	}
	
		@Override
	public void onRotationVectorUpdate(float[] values, long deltaT, long timestamp) {
		persistToFile(mRVFileWriter, values, deltaT, timestamp);
		persistToFile(mRVFileWriter, mSensorLifecycleManager.getRotationVector(), deltaT, timestamp);
	}
	
	private FileWriter getFile(String dataType, String extension) {
		String r = (String) (DateFormat.format("yyyy-MM-dd-hh-mm-ss", new java.util.Date()) );
		FileWriter f = null;
		try {
			f = new FileWriter(new File(SAMPLES_DIR, dataType + "-" + r + "." + extension));
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "File couldn't be opened for writing!");
			throw new RuntimeException(e);
		}
		return f;
	}

	private void persistToFile(FileWriter fileWriter, float[] values, long deltaT, long timestamp) {
		try {
			fileWriter.write(timestamp + "," + deltaT + "," + values[0] + "," + values[1] + "," + values[2] + "\n");
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "Writing sensor data to file failed!", e);
			throw new RuntimeException(e);
		}
	}

}
