package in.ernet.iitr.puttauec.algorithms;


import in.ernet.iitr.puttauec.sensors.DefaultSensorCallbacks;
import in.ernet.iitr.puttauec.sensors.SensorLifecycleManager;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

public class DeadReckoning extends DefaultSensorCallbacks implements IAlgorithm, IReckoningMethod {
	// Constants
	protected static final String SAMPLES_DIR = Environment.getExternalStorageDirectory() + File.separator + "samples";
	private static final int DEFAULT_MAP_HEIGHT = 480;
	private static final int DEFAULT_MAP_WIDTH = 640;
	private static final int MAX_HISTORY_SIZE = 10;
	private static final String TAG = "DeadReckoning";
	private static final int PEAK_HUNT = 0;
	private static final int VALLEY_HUNT = 1;

	// These constants are expected to be divided by 1000 before use
	public static final int DEFAULT_TRAINING_CONSTANT = 770; // 5200; // 3300; // 1937;
	public static final int DEFAULT_ACCEL_THRESHOLD = 1300; // 1840; //1300 /1400 //1500 
	
	// Instance variables
	LinkedList<float[]> mAccelHistory;
	private float mLocation[];
	protected float mTrainingConstant = DEFAULT_TRAINING_CONSTANT/1000.f;
	protected float mAccelThreshold = DEFAULT_ACCEL_THRESHOLD/1000.f;
	private float mStartX; // on a 0-1 scale based on the map
	private float mStartY;
	private boolean initState = true;
	private int mMapWidth = DEFAULT_MAP_WIDTH;
	private int mMapHeight = DEFAULT_MAP_HEIGHT;
	
	// Raw path obtained by Dead Reckoning
	private ArrayList<float[]> mPath;	
	private int mStepCount;
	private float mMinAccel;
	private float mMaxAccel;
	private int mState;
	private long prevSteptimestamp;
	
	// Reference to the Sensor Lifecycle Manager used to get the sensor data
	protected SensorLifecycleManager mSensorLifecycleManager;

	protected boolean mIsLogging;
	protected FileWriter mAccelLogFileWriter;
	protected FileWriter mStepLogFileWriter;
 
	
	 private static final float NS2S = 1.0f / 1000000000.0f;
	 private float [] deltaQuaternion = {0.f,0.f,0.f,0.f};
     private float[] gyroMatrix = new float[9];
     private float[] rotationMatrix = new float[9];     
     private float[] RVOrientation = {0.f,0.f,0.f};
    
     private static final double EPSILON = 0.1f;
     private double gyroscopeRotationVelocity = 0;	    
     private static float[] gyroAngle = {0.f,0.f,0.f};
     private boolean positionInitialised = false;
     
     private static float beta = 0.075f;
 	 private float q0, q1, q2, q3 ;
 	//private float sampleFreq = 512.0f ;	    // sample frequency in Hz
 	 private SensorManager mSensorManager;
 	 private long old_time_stamp = 0;  	
     private float[] gyroOri = new float[3];
 	 private double prevAngle;  
 	// angular speeds from gyro
     private float[] gyro = new float[3];
     // magnetic field vector
     private float[] magnet = new float[3];
     // accelerometer vector
     private float[] accel = new float[3];
     
    private String location;
 	private long mLastGyroTimestamp= 0;
 	
     public DeadReckoning(Context ctx) {
		init();		
		mSensorLifecycleManager = SensorLifecycleManager.getInstance(ctx);
	    q0 = 1.0f; q1 = 0.0f; q2 = 0.0f; q3 = 0.0f;	// quaternion of sensor frame relative to auxiliary frame
	}

	protected void init() {  
		mAccelHistory = new LinkedList<float[]>();
		mAccelHistory.add(new float[3]); // Added to avoid bounds checks while accessing
		mAccelHistory.add(new float[3]); // indices [i-1] and [i-2]		
		mPath = new ArrayList<float[]>();		
		mLocation = new float[2];
		mStepCount = 0;
		mMaxAccel = 0.f;
		mMinAccel = 0.f;		
		mStartX = 0.f;
		mStartY = 0.f;
		mState = VALLEY_HUNT; // Used for detection of peaks and valleys from the accelerometer data.		
		mIsLogging = false;
	}
	
	@Override
	public void onLinearAccelUpdate (float[] values, long deltaT, long timestamp) {
		// Remove low value sensor noise around 0
		// Also, threshold the sensor data so that only clean peaks are 
		// observed corresponding to steps
		if(this.isLogging()) {
			try {
				mAccelLogFileWriter.write("" + timestamp + "," + deltaT + "," + values[2] + "\n");
			} catch (IOException e) {
				Log.e(TAG, "Log file write for acceleration failed!!!\n", e);
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
        if(Math.abs(values[2]) < mAccelThreshold) {
			 values[0] = 0;
			 values[1] = 0;
			 values[2] = 0;
		}
		// Count local maxima
		synchronized(mAccelHistory) {
			float s0 = mAccelHistory.get(mAccelHistory.size()-2)[2], s1 = mAccelHistory.get(mAccelHistory.size()-1)[2], s2 = values[2];
			// Count peaks and valleys
			if((s2 - s1)*(s1 - s0) < 0) {
				if(s2 - s1 < 0 && s1 > 0) { // Peak Found
					if(mState == PEAK_HUNT) {
						// Count previous peak+valley pair and start off new counting. 
						if(timestamp-prevSteptimestamp > 100*1000)
							{ ++mStepCount;
						       prevSteptimestamp = timestamp; }
						else {return;}
 
						double stepSize = getStepSize();
						double radAngle = getAngleRadians();
						
						double offset = Math.toRadians(5); // 10 degree offset because of map
						radAngle -= offset;
						if(radAngle < -Math.PI)
							radAngle += 2*Math.PI;
						
						double turnAngle = (gyroOri[0]-prevAngle)%(2*Math.PI);
						prevAngle = gyroOri[0];
						Log.d(TAG,"turn Angle" + String.valueOf(turnAngle));
						turnAngle = (Math.abs(turnAngle) > (10./180.)*Math.PI) ? turnAngle : 0.0; 
				        	
						if(this.isLogging()) {
							try {
								mStepLogFileWriter.write("" + timestamp + "," + deltaT + "," + stepSize + "," + Math.toDegrees(radAngle) + "," + Math.toDegrees(turnAngle) +"\n");
							} catch (IOException e) {
								Log.e(TAG, "Writing to step log file failed!", e);
								e.printStackTrace();
								throw new RuntimeException(e);
							}
						}

						// TODO Remove this offset!
					//	double offset = Math.toRadians(85); // 85 degree offset because of map
					//	radAngle += offset;
						
						if(radAngle < -Math.PI)
							radAngle += 2*Math.PI;
						// Expected to set the new location of the person
						updateLocation(stepSize,radAngle,turnAngle);
						
						initState = true;		
						synchronized(mPath) {
							mPath.add(new float[] { mLocation[0], mLocation[1], (float)radAngle});
						}

						mMaxAccel = 0;
						mMinAccel = 0;
						
						mState = VALLEY_HUNT;
					}
					mMaxAccel = Math.max(mMaxAccel, s1);	
				} else if (s2 - s1 > 0 && s1 < 0) { // Valley Found
					mMinAccel = Math.min(mMinAccel, s1);
					if(mState == VALLEY_HUNT) {
						mState = PEAK_HUNT;
					}
				}
			}
				mAccelHistory.add(values);
			if(mAccelHistory.size() > MAX_HISTORY_SIZE) {
				mAccelHistory.removeFirst();
			}
		}
	}
	@Override
	public void onAccelUpdate (float[] values, long deltaT, long timestamp) {
		System.arraycopy(values,0,accel,0,3);
	}

	/* TODO Dead code. Remove if not needed 2 versions later.
	 * 
	 *  private double getAccelMagnitude(float[] rawAccel) {
		double netAccel = Math.sqrt(rawAccel[0]*rawAccel[0] + rawAccel[1]*rawAccel[1] + rawAccel[2]*rawAccel[2]);
		return netAccel;
	} */

	protected void updateLocation(double stepSize, double radAngle,double turnAngle) {
	/*	System.out.println("rad_ang" + String.valueOf(radAngle));
		System.out.println("step_size" + String.valueOf(stepSize));
		System.out.println("X" + String.valueOf(mLocation[0]));
		System.out.println("Y" + String.valueOf(mLocation[1]));
	*/	mLocation[0] += Math.sin(radAngle)*stepSize;
		mLocation[1] += Math.cos(radAngle)*stepSize; // negative sign due to image coordinate system v/s True North angle
	}

	/* (non-Javadoc)
	 * @see in.ernet.iitr.divyeuec.algorithms.IReckoningMethod#getStepSize()
	 */
	@Override
	public double getStepSize() {
		double stepSize = mTrainingConstant*Math.pow(mMaxAccel - mMinAccel, 0.25);
		return stepSize;
	}
	
	@Override
	public void onMagneticFieldUpdate(float[] values, long deltaT,
			long timestamp) {
		super.onMagneticFieldUpdate(values, deltaT, timestamp);
		System.arraycopy(values,0,magnet,0,3);
	}
	
	@Override
	public void onRotationVectorUpdate(float[] values, long deltaT, long timestamp) {
            SensorManager.getRotationMatrixFromVector(rotationMatrix,values);
	        SensorManager.getOrientation(rotationMatrix, RVOrientation);
	}
	
	@Override
	public void onGyroUpdate(float[] values, long deltaT, long timestamp) {
		 System.arraycopy(values,0,gyro,0,3);
		 MadgwickAHRSupdate(gyro[0],gyro[1],gyro[2],accel[0],accel[1],accel[2],magnet[0],magnet[1],magnet[2],deltaT*NS2S);							  
		/*if(initState)
	      {     gyroAngle[0] = 0.0f;
	      		gyroAngle[1] = 0.0f;
	      		gyroAngle[2] = 0.0f;

	        // initialise gyroMatrix with identity matrix
	      		gyroMatrix[0] = 1.0f; gyroMatrix[1] = 0.0f; gyroMatrix[2] = 0.0f;
	      		gyroMatrix[3] = 0.0f; gyroMatrix[4] = 1.0f; gyroMatrix[5] = 0.0f;
	      		gyroMatrix[6] = 0.0f; gyroMatrix[7] = 0.0f; gyroMatrix[8] = 1.0f;
	    	    initState = false; 
	      }
		  if (timestamp != 0) {
                final float dT = (deltaT) * NS2S;
                // Axis of the rotation sample, not normalized yet.
                float axisX = values[0];
                float axisY = values[1];
                float axisZ = values[2];

                // Calculate the angular speed of the sample
                gyroscopeRotationVelocity = Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);

                // Normalize the rotation vector if it's big enough to get the axis
                if (gyroscopeRotationVelocity > EPSILON) {
                    axisX /= gyroscopeRotationVelocity;
                    axisY /= gyroscopeRotationVelocity;
                    axisZ /= gyroscopeRotationVelocity;
                }

                // Integrate around this axis with the angular speed by the timestep
                // in order to get a delta rotation from this sample over the timestep
                // We will convert this axis-angle representation of the delta rotation
                // into a quaternion before turning it into the rotation matrix.
                double thetaOverTwo = gyroscopeRotationVelocity * dT / 2.0f;
                double sinThetaOverTwo = Math.sin(thetaOverTwo);
                double cosThetaOverTwo = Math.cos(thetaOverTwo);
                deltaQuaternion[0] = (float) (sinThetaOverTwo * axisX);
                deltaQuaternion[1] = (float) (sinThetaOverTwo * axisY);
                deltaQuaternion[2] = (float) (sinThetaOverTwo * axisZ);
                deltaQuaternion[3] = (float) cosThetaOverTwo;
               // Set the rotation matrix as well to have both representations
                
                float[] deltaRotationMatrix = new float[9];
                SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaQuaternion);
                gyroMatrix = matrixMultiplication(gyroMatrix, deltaRotationMatrix);
                SensorManager.getOrientation(gyroMatrix, gyroAngle);
	        }
	        */
	}
	
	 private float[] matrixMultiplication(float[] A, float[] B) {
	        float[] result = new float[9];
	     
	        result[0] = A[0] * B[0] + A[1] * B[3] + A[2] * B[6];
	        result[1] = A[0] * B[1] + A[1] * B[4] + A[2] * B[7];
	        result[2] = A[0] * B[2] + A[1] * B[5] + A[2] * B[8];
	     
	        result[3] = A[3] * B[0] + A[4] * B[3] + A[5] * B[6];
	        result[4] = A[3] * B[1] + A[4] * B[4] + A[5] * B[7];
	        result[5] = A[3] * B[2] + A[4] * B[5] + A[5] * B[8];
	     
	        result[6] = A[6] * B[0] + A[7] * B[3] + A[8] * B[6];
	        result[7] = A[6] * B[1] + A[7] * B[4] + A[8] * B[7];
	        result[8] = A[6] * B[2] + A[7] * B[5] + A[8] * B[8];
	     
	        return result;
	        
	    }/* (non-Javadoc)
	 * @see in.ernet.iitr.divyeuec.algorithms.IReckoningMethod#setStepDisplacement(float[])
	 */
	@Override
	public void setLocation(float[] location) {
		this.mLocation = location;
	}
	
	/* (non-Javadoc)
	 * @see in.ernet.iitr.divyeuec.algorithms.IReckoningMethod#setStepDisplacement(float[])
	 */
	@Override
	public void setLocation(double x, double y) { // 0-1 coordinate system
		setLocation(new float[] { (float)x, (float)y });
	}

	@Override
	public float[] getLocation() {
		return mLocation;
	}
	
	/* (non-Javadoc)
	 * @see in.ernet.iitr.divyeuec.algorithms.IReckoningMethod#getStepDisplacement()
	 */
	@Override
	public String getLocationJSON() { // Coordinate system: 0-1 on map
		JSONArray jsonArray = new JSONArray();
		
		try {
			jsonArray.put(mLocation[0]);
			jsonArray.put(mLocation[1]);
		} catch (JSONException e) {
			Log.e(TAG, "JSON serialization error", e);
			throw new RuntimeException(e);
		}
		return jsonArray.toString();
	}
	
	/* (non-Javadoc)
	 * @see in.ernet.iitr.divyeuec.algorithms.IReckoningMethod#getAngleRadians()
	 */
	@Override
	public double getAngleRadians() {
		return gyroOri[0];
		// return mRoughAngle;
	}
	
	/* (non-Javadoc)
	 * @see in.ernet.iitr.divyeuec.algorithms.IReckoningMethod#getAngle()
	 */
	@Override
	public double getAngle() {
		return getAngleRadians()*360/(2*Math.PI);
		
	}
	
	/* (non-Javadoc)
	 * @see in.ernet.iitr.divyeuec.algorithms.IReckoningMethod#getTrainingConstant()
	 */
	@Override
	public float getTrainingConstant() {
		return mTrainingConstant;
	}

	/* (non-Javadoc)
	 * @see in.ernet.iitr.divyeuec.algorithms.IReckoningMethod#setTrainingConstant(float)
	 */
	@Override
	public void setTrainingConstant(float mTrainingConstant) {
		this.mTrainingConstant = mTrainingConstant;
	}
	
	/* (non-Javadoc)
	 * @see in.ernet.iitr.divyeuec.algorithms.IReckoningMethod#getAccelHistory()
	 */
	@Override
	public String getAccelHistory() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		synchronized(mAccelHistory) {
			for(float[] value : mAccelHistory) {
				jsonObject.accumulate("accel", value[2]);
			}
		}
		return jsonObject.toString();
	}
	
	/* (non-Javadoc)
	 * @see in.ernet.iitr.divyeuec.algorithms.IReckoningMethod#getStepCount()
	 */
	@Override
	public int getStepCount() {
		return mStepCount;
	}

	/* (non-Javadoc)
	 * @see in.ernet.iitr.divyeuec.algorithms.IReckoningMethod#setStepCount(int)
	 */
	@Override
	public void setStepCount(int mStepCount) {
		this.mStepCount = mStepCount;
	}

	/* (non-Javadoc)
	 * @see in.ernet.iitr.divyeuec.algorithms.IReckoningMethod#getmPath()
	 */
	@Override
	public ArrayList<float[]> getmPath() {
		return mPath;
	}

	/* (non-Javadoc)
	 * @see in.ernet.iitr.divyeuec.algorithms.IReckoningMethod#setmPath(java.util.ArrayList)
	 */
	@Override
	public void setmPath(ArrayList<float[]> mPath) {
		synchronized(mPath) {
			this.mPath = mPath;
		}
	}

	/* (non-Javadoc)
	 * @see in.ernet.iitr.divyeuec.algorithms.IReckoningMethod#pause()
	 */
	@Override
	public void pause() {
		mSensorLifecycleManager.unregisterCallback(this, SensorLifecycleManager.SENSOR_ACCELEROMETER);
		mSensorLifecycleManager.unregisterCallback(this, SensorLifecycleManager.SENSOR_MAGNETISM);
		mSensorLifecycleManager.unregisterCallback(this, SensorLifecycleManager.SENSOR_GRAVITY);
		mSensorLifecycleManager.unregisterCallback(this, SensorLifecycleManager.SENSOR_GYROSCOPE);
		mSensorLifecycleManager.unregisterCallback(this, SensorLifecycleManager.SENSOR_ROTATION_VECTOR);
		
	}

	/* (non-Javadoc)
	 * @see in.ernet.iitr.divyeuec.algorithms.IReckoningMethod#resume()
	 */
	@Override
	public void resume() {
		mSensorLifecycleManager.registerCallback(this, SensorLifecycleManager.SENSOR_ACCELEROMETER);
		mSensorLifecycleManager.registerCallback(this, SensorLifecycleManager.SENSOR_MAGNETISM);
		mSensorLifecycleManager.registerCallback(this, SensorLifecycleManager.SENSOR_GRAVITY);
		mSensorLifecycleManager.registerCallback(this, SensorLifecycleManager.SENSOR_GYROSCOPE);
		mSensorLifecycleManager.registerCallback(this, SensorLifecycleManager.SENSOR_ROTATION_VECTOR);
	}

	/* (non-Javadoc)
	 * @see in.ernet.iitr.divyeuec.algorithms.IReckoningMethod#restart()
	 */
	@Override
	public void restart() {
		init();
	}

	/* (non-Javadoc)
	 * @see in.ernet.iitr.divyeuec.algorithms.IReckoningMethod#setAccelThreshold(float)
	 */
	@Override
	public void setAccelThreshold(float mAccelThreshold) {
		this.mAccelThreshold = mAccelThreshold;
	}

	/* (non-Javadoc)
	 * @see in.ernet.iitr.divyeuec.algorithms.IReckoningMethod#getAccelThreshold()
	 */
	@Override
	public float getAccelThreshold() {
		return mAccelThreshold;
	}

	/* (non-Javadoc)
	 * @see in.ernet.iitr.divyeuec.algorithms.IReckoningMethod#setmStartX(float)
	 */
	@Override
	public void setmStartX(float mStartX) {
		this.mStartX = mStartX;
		synchronized (mPath) {
			this.mPath.clear();
			this.mPath.add(new float[]{ this.mStartX, this.mStartY });	
		}
		
	}

	/* (non-Javadoc)
	 * @see in.ernet.iitr.divyeuec.algorithms.IReckoningMethod#getmStartX()
	 */
	@Override
	public float getmStartX() {
		return mStartX;
	}

	/* (non-Javadoc)
	 * @see in.ernet.iitr.divyeuec.algorithms.IReckoningMethod#setmStartY(float)
	 */
	@Override
	public void setmStartY(float mStartY) {
		this.mStartY = mStartY;
		synchronized (mPath) {
			this.mPath.clear();
			this.mPath.add(new float[]{ this.mStartX, this.mStartY });	
		}
		
	}

	/* (non-Javadoc)
	 * @see in.ernet.iitr.divyeuec.algorithms.IReckoningMethod#getmStartY()
	 */
	@Override
	public float getmStartY() {
		return mStartY;
	}

	/* (non-Javadoc)
	 * @see in.ernet.iitr.divyeuec.algorithms.IReckoningMethod#getStartPos()
	 */
	@Override
	public float[] getStartPos() {
		return new float[] { getmStartX(), getmStartY() };
	}
	
	/* (non-Javadoc)
	 * @see in.ernet.iitr.divyeuec.algorithms.IReckoningMethod#setStartPos(float, float)
	 */
	@Override
	public void setStartPos(float x, float y) {
		setmStartX(x);
		setmStartY(y);
	}
	
	/* (non-Javadoc)
	 * @see in.ernet.iitr.divyeuec.algorithms.IReckoningMethod#getmMapWidth()
	 */
	@Override
	public int getmMapWidth() {
		return mMapWidth;
	}

	/* (non-Javadoc)
	 * @see in.ernet.iitr.divyeuec.algorithms.IReckoningMethod#setmMapWidth(double)
	 */
	@Override
	public void setmMapWidth(int mMapWidth) {
		this.mMapWidth = mMapWidth;
	}

	/* (non-Javadoc)
	 * @see in.ernet.iitr.divyeuec.algorithms.IReckoningMethod#getmMapHeight()
	 */
	@Override
	public int getmMapHeight() {
		return mMapHeight;
	}

	/* (non-Javadoc)
	 * @see in.ernet.iitr.divyeuec.algorithms.IReckoningMethod#setmMapHeight(double)
	 */
	@Override
	public void setmMapHeight(int mMapHeight) {
		this.mMapHeight = mMapHeight;
	}

	@Override
	public String getmPathJSON() {
		synchronized(mPath) {
			try {
				JSONArray pathArray = new JSONArray();
				ArrayList<float[]> path = getmPath();
				
				for(float[] point : path) {
					JSONArray pathPoint = new JSONArray();
					pathPoint.put(point[0]);
					pathPoint.put(point[1]);
					pathArray.put(pathPoint);
				}
				String pathString = pathArray.toString();
				return pathString;
			} catch(JSONException e) {
				Log.e(TAG, "Error serializing path to JSON.", e);
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}
	
	@Override
	public boolean isLogging() {
		return mIsLogging;
	}
	
	@Override
	public void startLogging() {
		try {
			String r = (String) (DateFormat.format("yyyy-MM-dd-hh-mm-ss", new java.util.Date()) );
			String logFileBaseName = "drLog." + r;
			mAccelLogFileWriter = new FileWriter(new File(SAMPLES_DIR, logFileBaseName + ".accel.csv"));
			mStepLogFileWriter = new FileWriter(new File(SAMPLES_DIR, logFileBaseName + ".steps.csv"));
		} catch (IOException e) {
			Log.e(TAG, "Creating and opening log files failed!", e);
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		mIsLogging = true;
	}
	
	@Override
	public void stopLogging() {
		mIsLogging = false;
		
		try {
			mAccelLogFileWriter.flush();
			mAccelLogFileWriter.close();
			mStepLogFileWriter.flush();
			mStepLogFileWriter.close();
		} catch (IOException e) {
			Log.e(TAG, "Flushing and closing log files failed!", e);
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	void MadgwickAHRSupdate(float gx, float gy, float gz, float ax, float ay, float az, float mx, float my, float mz,float deltaT) {		
		float recipNorm;
		float s0, s1, s2, s3;
		float qDot1, qDot2, qDot3, qDot4;
		float hx, hy;
		float _2q0mx, _2q0my, _2q0mz, _2q1mx, _2bx, _2bz, _4bx, _4bz, _2q0, _2q1, _2q2, _2q3, _2q0q2, _2q2q3, q0q0, q0q1, q0q2, q0q3, q1q1, q1q2, q1q3, q2q2, q2q3, q3q3;

		// Use IMU algorithm if magnetometer measurement invalid (avoids NaN in magnetometer normalisation)
		if((mx == 0.0f) && (my == 0.0f) && (mz == 0.0f)) {
			MadgwickAHRSupdateIMU(gx, gy, gz, ax, ay, az,deltaT);
			return;
		}

		// Rate of change of quaternion from gyroscope
		qDot1 = 0.5f * (-q1 * gx - q2 * gy - q3 * gz);
		qDot2 = 0.5f * (q0 * gx + q2 * gz - q3 * gy);
		qDot3 = 0.5f * (q0 * gy - q1 * gz + q3 * gx);
		qDot4 = 0.5f * (q0 * gz + q1 * gy - q2 * gx);

		// Compute feedback only if accelerometer measurement valid (avoids NaN in accelerometer normalisation)
		if(!((ax == 0.0f) && (ay == 0.0f) && (az == 0.0f))) {

			// Normalise accelerometer measurement
			recipNorm = (float) Math.pow((ax * ax + ay * ay + az * az),-0.5); 
			ax *= recipNorm;
			ay *= recipNorm;
			az *= recipNorm;   

			// Normalise magnetometer measurement
			recipNorm = (float) Math.pow((mx * mx + my * my + mz * mz),-0.5);
			mx *= recipNorm;
			my *= recipNorm;
			mz *= recipNorm;

			// Auxiliary variables to avoid repeated arithmetic
			_2q0mx = 2.0f * q0 * mx;
			_2q0my = 2.0f * q0 * my;
			_2q0mz = 2.0f * q0 * mz;
			_2q1mx = 2.0f * q1 * mx;
			_2q0 = 2.0f * q0;
			_2q1 = 2.0f * q1;
			_2q2 = 2.0f * q2;
			_2q3 = 2.0f * q3;
			_2q0q2 = 2.0f * q0 * q2;
			_2q2q3 = 2.0f * q2 * q3;
			q0q0 = q0 * q0;
			q0q1 = q0 * q1;
			q0q2 = q0 * q2;
			q0q3 = q0 * q3;
			q1q1 = q1 * q1;
			q1q2 = q1 * q2;
			q1q3 = q1 * q3;
			q2q2 = q2 * q2;
			q2q3 = q2 * q3;
			q3q3 = q3 * q3;

			// Reference direction of Earth's magnetic field
			hx = mx * q0q0 - _2q0my * q3 + _2q0mz * q2 + mx * q1q1 + _2q1 * my * q2 + _2q1 * mz * q3 - mx * q2q2 - mx * q3q3;
			hy = _2q0mx * q3 + my * q0q0 - _2q0mz * q1 + _2q1mx * q2 - my * q1q1 + my * q2q2 + _2q2 * mz * q3 - my * q3q3;
			_2bx = (float) Math.sqrt(hx * hx + hy * hy);
			_2bz = -_2q0mx * q2 + _2q0my * q1 + mz * q0q0 + _2q1mx * q3 - mz * q1q1 + _2q2 * my * q3 - mz * q2q2 + mz * q3q3;
			_4bx = 2.0f * _2bx;
			_4bz = 2.0f * _2bz;

			// Gradient decent algorithm corrective step
			s0 = -_2q2 * (2.0f * q1q3 - _2q0q2 - ax) + _2q1 * (2.0f * q0q1 + _2q2q3 - ay) - _2bz * q2 * (_2bx * (0.5f - q2q2 - q3q3) + _2bz * (q1q3 - q0q2) - mx) + (-_2bx * q3 + _2bz * q1) * (_2bx * (q1q2 - q0q3) + _2bz * (q0q1 + q2q3) - my) + _2bx * q2 * (_2bx * (q0q2 + q1q3) + _2bz * (0.5f - q1q1 - q2q2) - mz);
			s1 = _2q3 * (2.0f * q1q3 - _2q0q2 - ax) + _2q0 * (2.0f * q0q1 + _2q2q3 - ay) - 4.0f * q1 * (1 - 2.0f * q1q1 - 2.0f * q2q2 - az) + _2bz * q3 * (_2bx * (0.5f - q2q2 - q3q3) + _2bz * (q1q3 - q0q2) - mx) + (_2bx * q2 + _2bz * q0) * (_2bx * (q1q2 - q0q3) + _2bz * (q0q1 + q2q3) - my) + (_2bx * q3 - _4bz * q1) * (_2bx * (q0q2 + q1q3) + _2bz * (0.5f - q1q1 - q2q2) - mz);
			s2 = -_2q0 * (2.0f * q1q3 - _2q0q2 - ax) + _2q3 * (2.0f * q0q1 + _2q2q3 - ay) - 4.0f * q2 * (1 - 2.0f * q1q1 - 2.0f * q2q2 - az) + (-_4bx * q2 - _2bz * q0) * (_2bx * (0.5f - q2q2 - q3q3) + _2bz * (q1q3 - q0q2) - mx) + (_2bx * q1 + _2bz * q3) * (_2bx * (q1q2 - q0q3) + _2bz * (q0q1 + q2q3) - my) + (_2bx * q0 - _4bz * q2) * (_2bx * (q0q2 + q1q3) + _2bz * (0.5f - q1q1 - q2q2) - mz);
			s3 = _2q1 * (2.0f * q1q3 - _2q0q2 - ax) + _2q2 * (2.0f * q0q1 + _2q2q3 - ay) + (-_4bx * q3 + _2bz * q1) * (_2bx * (0.5f - q2q2 - q3q3) + _2bz * (q1q3 - q0q2) - mx) + (-_2bx * q0 + _2bz * q2) * (_2bx * (q1q2 - q0q3) + _2bz * (q0q1 + q2q3) - my) + _2bx * q1 * (_2bx * (q0q2 + q1q3) + _2bz * (0.5f - q1q1 - q2q2) - mz);
			recipNorm = (float) Math.pow((s0 * s0 + s1 * s1 + s2 * s2 + s3 * s3),-0.5);  // normalise step magnitude
			s0 *= recipNorm;
			s1 *= recipNorm;
			s2 *= recipNorm;
			s3 *= recipNorm;

			// Apply feedback step
			qDot1 -= beta * s0;
			qDot2 -= beta * s1;
			qDot3 -= beta * s2;
			qDot4 -= beta * s3;
		}

		// Integrate rate of change of quaternion to yield quaternion
		q0 += qDot1 * deltaT;
		q1 += qDot2 * deltaT;
		q2 += qDot3 * deltaT;
		q3 += qDot4 * deltaT;
	// System.out.println("deltaT" + String.valueOf(deltaT));
			
		// Normalise quaternion
		recipNorm = (float) Math.pow((q0 * q0 + q1 * q1 + q2 * q2 + q3 * q3),-0.5);
		q0 *= recipNorm;
		q1 *= recipNorm;
		q2 *= recipNorm;
		q3 *= recipNorm;
		getAngleGD();
	}

	//---------------------------------------------------------------------------------------------------
	// IMU algorithm update

	void MadgwickAHRSupdateIMU(float gx, float gy, float gz, float ax, float ay, float az,float deltaT) {
		float recipNorm;
		float s0, s1, s2, s3;
		float qDot1, qDot2, qDot3, qDot4;
		float _2q0, _2q1, _2q2, _2q3, _4q0, _4q1, _4q2 ,_8q1, _8q2, q0q0, q1q1, q2q2, q3q3;
        
		// Rate of change of quaternion from gyroscope
		qDot1 = 0.5f * (-q1 * gx - q2 * gy - q3 * gz);
		qDot2 = 0.5f * (q0 * gx + q2 * gz - q3 * gy);
		qDot3 = 0.5f * (q0 * gy - q1 * gz + q3 * gx);
		qDot4 = 0.5f * (q0 * gz + q1 * gy - q2 * gx);

		// Compute feedback only if accelerometer measurement valid (avoids NaN in accelerometer normalisation)
		if(!((ax == 0.0f) && (ay == 0.0f) && (az == 0.0f))) {

			// Normalise accelerometer measurement
			recipNorm = (float) Math.pow((ax * ax + ay * ay + az * az),-0.5);
			ax *= recipNorm;
			ay *= recipNorm;
			az *= recipNorm;   

			// Auxiliary variables to avoid repeated arithmetic
			_2q0 = 2.0f * q0;
			_2q1 = 2.0f * q1;
			_2q2 = 2.0f * q2;
			_2q3 = 2.0f * q3;
			_4q0 = 4.0f * q0;
			_4q1 = 4.0f * q1;
			_4q2 = 4.0f * q2;
			_8q1 = 8.0f * q1;
			_8q2 = 8.0f * q2;
			q0q0 = q0 * q0;
			q1q1 = q1 * q1;
			q2q2 = q2 * q2;
			q3q3 = q3 * q3;

			// Gradient decent algorithm corrective step
			s0 = _4q0 * q2q2 + _2q2 * ax + _4q0 * q1q1 - _2q1 * ay;
			s1 = _4q1 * q3q3 - _2q3 * ax + 4.0f * q0q0 * q1 - _2q0 * ay - _4q1 + _8q1 * q1q1 + _8q1 * q2q2 + _4q1 * az;
			s2 = 4.0f * q0q0 * q2 + _2q0 * ax + _4q2 * q3q3 - _2q3 * ay - _4q2 + _8q2 * q1q1 + _8q2 * q2q2 + _4q2 * az;
			s3 = 4.0f * q1q1 * q3 - _2q1 * ax + 4.0f * q2q2 * q3 - _2q2 * ay;
			recipNorm =  (float) Math.pow((s0 * s0 + s1 * s1 + s2 * s2 + s3 * s3),-0.5); // normalise step magnitude
			s0 *= recipNorm;
			s1 *= recipNorm;
			s2 *= recipNorm;
			s3 *= recipNorm;

			// Apply feedback step
			qDot1 -= beta * s0;
			qDot2 -= beta * s1;
			qDot3 -= beta * s2;
			qDot4 -= beta * s3;
		}

		// Integrate rate of change of quaternion to yield quaternion
		q0 += qDot1 * deltaT;
		q1 += qDot2 * deltaT;
		q2 += qDot3 * deltaT;
		q3 += qDot4 * deltaT;
    //    System.out.println("deltaT" + String.valueOf(deltaT));
		// Normalise quaternion
		recipNorm = (float) Math.pow((q0 * q0 + q1 * q1 + q2 * q2 + q3 * q3),-0.5);
		q0 *= recipNorm;
		q1 *= recipNorm;
		q2 *= recipNorm;
		q3 *= recipNorm;
		getAngleGD();
	}

	public void getAngleGD()
	{  gyroOri[0] = (float) ((Math.atan2(2.0 * (q1*q2 - q0*q3),(2*(q0*q0 + q1*q1)-1))));          
       gyroOri[1] = (float) (Math.asin(-2.0 * (q1*q3 + q0*q2)));
       gyroOri[2] = (float) ((Math.atan2(2.0 * (q2*q3 - q0*q1),(2*(q0*q0 + q3*q3)-1)))); 
	}
	
}

