package in.ernet.iitr.puttauec.algorithms;


import in.ernet.iitr.puttauec.algorithms.IAngleAlgorithm;
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
	protected static final String SAMPLES_DIR = Environment.getExternalStorageDirectory() + File.separator + "samples_exp";
	protected static final String STORAGE_DIR_A = DeadReckoning.SAMPLES_DIR + File.separator + "pfah_ve_mp";	
	protected static final String STORAGE_DIR_B = DeadReckoning.SAMPLES_DIR + File.separator + "pfah_mg_mp";
	protected static final String STORAGE_DIR_C = DeadReckoning.SAMPLES_DIR + File.separator + "pfkm";
	protected static final String STORAGE_DIR_D = DeadReckoning.SAMPLES_DIR + File.separator + "pfah_ve";
	protected static final String STORAGE_DIR_E = DeadReckoning.SAMPLES_DIR + File.separator + "pfah_mg";
	protected static final String STORAGE_DIR_F = DeadReckoning.SAMPLES_DIR + File.separator + "pfah_mp";
	     
	protected String STORAGE_DIR = SAMPLES_DIR + File.separator + "dr";
	private static final int DEFAULT_MAP_HEIGHT = 26;
	private static final int DEFAULT_MAP_WIDTH = 16;
	private static final int MAX_HISTORY_SIZE = 10;
	private static final String TAG = "DeadReckoning";
	private static final int PEAK_HUNT = 0;
	private static final int VALLEY_HUNT = 1;
    protected static double mmse;
	
	// These constants are expected to be divided by 1000 before use
	public static final int DEFAULT_TRAINING_CONSTANT = 770; // 5200; // 3300; // 1937;
	public static final int DEFAULT_ACCEL_THRESHOLD = 1300; // 1840; //1300 /1400 //1500 
	
	// Instance variables
	LinkedList<float[]> mAccelHistory;
	private float mLocation[];
	protected float mTrainingConstant = DEFAULT_TRAINING_CONSTANT/1000.f;
	protected float mAccelThreshold = DEFAULT_ACCEL_THRESHOLD/1000.f;
	private float mStartX; 
	private float mStartY;
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
	protected IAngleAlgorithm angle_algo = new AHRS();
	  
	protected boolean mIsLogging;
	protected FileWriter mAccelLogFileWriter;
	protected FileWriter mStepLogFileWriter;

	private static final float NS2S = 1.0f / 1000000000.0f;
	private float[] rotationMatrix = new float[9];     
    private float[] RVOrientation = {0.f,0.f,0.f};
    private static float beta = 0.075f;
 	protected double q0, q1, q2, q3 ;
 	private float[] gyroOri = new float[3];
 	private double prevAngle;  
 	private boolean initState = true;
    private float[] gyro = new float[3];
    private float[] magnet = new float[3];
    private float[] accel = new float[3];
      	
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
						
						double offset = Math.toRadians(5); // 5 degree offset because of map
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
						
						if(radAngle < -Math.PI)
							radAngle += 2*Math.PI;
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

	protected void updateLocation(double stepSize, double radAngle,double turnAngle) {
		mLocation[0] += Math.sin(radAngle)*stepSize;
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
		 angle_algo.update(gyro[0],gyro[1],gyro[2],accel[0],accel[1],accel[2],magnet[0],magnet[1],magnet[2],deltaT*NS2S);	
		 double[] qi = angle_algo.quaternion_values();
		 q0 = qi[0]; q1 = qi[1]; q2 = qi[2];  q3 = qi[3];
		 getAngleGD();	 
	}
	
	/* (non-Javadoc)
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
			System.out.println(STORAGE_DIR);
			mAccelLogFileWriter = new FileWriter(new File(STORAGE_DIR, logFileBaseName + ".accel.csv"));
			mStepLogFileWriter = new FileWriter(new File(STORAGE_DIR, logFileBaseName + ".steps.csv"));
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

  public void getAngleGD()
	{  gyroOri[0] = (float) ((Math.atan2(2.0 * (q1*q2 - q0*q3),(2*(q0*q0 + q1*q1)-1))));          
       gyroOri[1] = (float) (Math.asin(-2.0 * (q1*q3 + q0*q2)));
       gyroOri[2] = (float) ((Math.atan2(2.0 * (q2*q3 - q0*q1),(2*(q0*q0 + q3*q3)-1)))); 
	}
	

	@Override
	public void setParticleCount (float pc) {
	}
	    
	@Override
	public void setSenseNoise (float sen) {
	}
		
	@Override
	public void setStepNoise (float ste) {
	}
		
	@Override
	public void setTurnNoise (float tun) {
	}		
	
	@Override
	public float getParticleCount () {
	   return 0.f;
	}
  
	@Override
	public float getSenseNoise () {
		return 0.f;
	}
	
	@Override
	public float getStepNoise () {
		return 0.f;
	}
	
	@Override
	public float getTurnNoise () {
		return 0.f;
		
	}
	@Override
	public double getMMSE() {
		return  0.0;
	}
}

