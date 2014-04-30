package in.ernet.iitr.puttauec.ui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import in.ernet.iitr.puttauec.algorithms.AHRS;
import in.ernet.iitr.puttauec.algorithms.KalmanFilter;
import in.ernet.iitr.puttauec.R;
import in.ernet.iitr.puttauec.algorithms.ParticleFiltering;
import in.ernet.iitr.puttauec.algorithms.DeadReckoning;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class ParticleFilteringActivity extends Activity {
   
	private ParticleFiltering mDeadReckoning;
	private static final String TAG = "ParticleFilterReckoningActivity";
	
	private static final String MAP_POINT = "MapPoint";
	private static final String KEY_QR_TYPE = "Type";
	private static final int DEAD_RECKONING_TRAINING_CONSTANT = Menu.FIRST;
	private static final int PARFIL_RECKONING_TRAINING_CONSTANT = Menu.FIRST+1;
	private static final int PARFIL_RECKONING_RESTART = Menu.FIRST + 2;
	private static final int PARFIL_RECKONING_STARTPOS = Menu.FIRST + 3;
	private static final int PARFIL_RECKONING_LOG_STEP_DATA = Menu.FIRST + 4;
	private static final int PARFIL_RECKONING_STEP_SIZE_ESTIMATE = Menu.FIRST + 5;
	private static final int PARFIL_RECKONING_LOG_PATH = Menu.FIRST + 6;
	
	public static final int PF_RECKONING_AHRS = 1;
	public static final int PF_RECKONING_KALMAN = 2;
	public static final int PF_RECKONING_VECTOR = 3;
	public static final int PF_RECKONING_MAGNITUDE = 4;
	public static final int PF_RECKONING_MAP_USED = 5;
	public static final int PF_RECKONING_MAP_NOT_USED = 6;
	
	public static final String KEY_ANGLE_METHOD = "KeyReckoningMethod";
	public static final String KEY_MAGNETIC_FIELD_METHOD = "KeyMagneticFieldMethod";
	public static final String KEY_MAP_METHOD = "KeyMapMethod";

	// Constants for QRCode data
	private static final String KEY_VERSION = "Version";
	private static final int MIN_QRCODE_VERSION = 0x1;
	private Intent intent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_particle_filtering);
	    int reckon_method = getIntent().getIntExtra(KEY_ANGLE_METHOD, PF_RECKONING_AHRS);		
	    int field_method = getIntent().getIntExtra(KEY_MAGNETIC_FIELD_METHOD, PF_RECKONING_MAGNITUDE);
	    int map_method = getIntent().getIntExtra(KEY_MAP_METHOD, PF_RECKONING_MAP_USED);
	    
	    Log.i(TAG, "Starting PFReckoningActivity with angle estimation method: " + reckon_method);
	    Log.i(TAG, "Starting PFReckoningActivity with field estimation method: " + field_method);
	    Log.i(TAG, "Starting PFReckoningActivity with map estimation method: " + map_method);
		switch(reckon_method) {		
		case PF_RECKONING_AHRS:
			mDeadReckoning = new ParticleFiltering(this, new AHRS());
			break;
		case PF_RECKONING_KALMAN:	
			mDeadReckoning = new ParticleFiltering(this, new KalmanFilter());
			break;		
		}			
	    intent = new Intent(this, BroadcastService.class);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
	
	 private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	       	updateUI(intent);       
	        }
	    };  
	   
	  private void updateUI(Intent intent) {
	    	float mstartx = mDeadReckoning.getmStartX();
	    	float mstarty = mDeadReckoning.getmStartY();  
	    	float[] mlocation = mDeadReckoning.getLocation();		
	    	float mcurrentx = mlocation[0];  
	    	float mcurrenty = mlocation[1];
	    	int msteps = mDeadReckoning.getStepCount();
	    	double mMMSE_err = mDeadReckoning.getMMSE();
	        
	    	Log.d(TAG,Float.toString(mstartx));
	    	Log.d(TAG,Float.toString(mstarty));
	    	Log.d(TAG,Float.toString(mcurrentx));
	    	Log.d(TAG,Float.toString(mcurrenty));
	    	Log.d(TAG,Integer.toString(msteps));
	    	Log.d(TAG,Double.toString(mMMSE_err));
	    	
	    	TextView startx = (TextView) findViewById(R.id.startx);  	
	    	TextView starty = (TextView) findViewById(R.id.starty);
	    	TextView currentx = (TextView) findViewById(R.id.currentx);  	
	    	TextView currenty = (TextView) findViewById(R.id.currenty);
	    	TextView stepcount = (TextView) findViewById(R.id.stepcount);  	
	    	TextView mmse_err = (TextView) findViewById(R.id.mmse_error);
	    	
	    	startx.setText(Float.toString(mstartx));
	    	starty.setText(Float.toString(mstarty));
	    	currentx.setText(Float.toString(mcurrentx));
	    	currenty.setText(Float.toString(mcurrenty));
	    	stepcount.setText(Integer.toString(msteps));
	    	mmse_err.setText(Double.toString(mMMSE_err));
	   }
	  
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, DEAD_RECKONING_TRAINING_CONSTANT, 0, "Modify Training Constant");
		menu.add(0, PARFIL_RECKONING_TRAINING_CONSTANT, 0, "Particle Filter Control");		
		menu.add(0, PARFIL_RECKONING_RESTART, 0, "Restart Particle Filter Reckoning");
		menu.add(0, PARFIL_RECKONING_STARTPOS, 0, "Scan Location");
		if(mDeadReckoning.isLogging()) {
			menu.add(0, PARFIL_RECKONING_LOG_STEP_DATA, 0, "Stop Path Data Logging");
		} else {
			menu.add(0, PARFIL_RECKONING_LOG_STEP_DATA, 0, "Start Path Data Logging");
		}
		menu.add(0, PARFIL_RECKONING_STEP_SIZE_ESTIMATE, 0, "Estimate Step Size");
		menu.add(0, PARFIL_RECKONING_LOG_PATH, 0, "Log current displayed path");
		return result;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case DEAD_RECKONING_TRAINING_CONSTANT:
			Intent launchIntent = new Intent(this, DeadReckoningTrainingActivity.class);
			launchIntent.putExtra(DeadReckoningTrainingActivity.KEY_THRESHOLD_VALUE, mDeadReckoning.getAccelThreshold());
			launchIntent.putExtra(DeadReckoningTrainingActivity.KEY_TRAINING_VALUE, mDeadReckoning.getTrainingConstant());
			startActivityForResult(launchIntent , DEAD_RECKONING_TRAINING_CONSTANT);
			break;
		case PARFIL_RECKONING_TRAINING_CONSTANT:
			Intent pfIntent = new Intent(this, ParticlFilterControlActivity.class);
			pfIntent.putExtra(ParticlFilterControlActivity.KEY_PARTICLE_COUNT_VALUE, mDeadReckoning.getParticleCount());
			pfIntent.putExtra(ParticlFilterControlActivity.KEY_STEP_NOISE_VALUE, mDeadReckoning.getStepNoise());
			pfIntent.putExtra(ParticlFilterControlActivity.KEY_SENSE_NOISE_VALUE, mDeadReckoning.getSenseNoise());
			pfIntent.putExtra(ParticlFilterControlActivity.KEY_TURN_NOISE_VALUE, mDeadReckoning.getTurnNoise());
			startActivityForResult(pfIntent , PARFIL_RECKONING_TRAINING_CONSTANT);
			break;
		case PARFIL_RECKONING_RESTART:
			mDeadReckoning.restart();
			break;
		case PARFIL_RECKONING_STARTPOS:
			Intent intent = new Intent(this,ScanActivity.class);
	        startActivityForResult(intent, PARFIL_RECKONING_STARTPOS);
			break;
			
		case PARFIL_RECKONING_LOG_STEP_DATA:
			if(mDeadReckoning.isLogging()) {
				mDeadReckoning.stopLogging();
				Toast.makeText(this, "Step logging stopped", Toast.LENGTH_SHORT).show();
			} else {
				mDeadReckoning.startLogging();
				Toast.makeText(this, "Step logging started", Toast.LENGTH_SHORT).show();
			}
			break;
			
		case PARFIL_RECKONING_STEP_SIZE_ESTIMATE:
			Intent scanIntent = new Intent(this, ScanActivity.class);
	        startActivityForResult(scanIntent, PARFIL_RECKONING_STEP_SIZE_ESTIMATE);
			break;
		case PARFIL_RECKONING_LOG_PATH:
			Date now = new Date(System.currentTimeMillis());
			try {
				FileWriter pathLoggingFile = new FileWriter(new File(Environment.getExternalStorageDirectory() + File.separator + "samples", "pfPathLog." + DateFormat.format("yyyy-MM-dd-kk-mm-ss", now) + ".csv"));
				ArrayList<float[]> path = mDeadReckoning.getmPath();
				for(float[] p : path) {
					pathLoggingFile.write(p[0] + "," + p[1] + "\n");
				}
				pathLoggingFile.flush();
				pathLoggingFile.close();
				Toast.makeText(this, "Path logged successfully!", Toast.LENGTH_SHORT).show();
			} catch(IOException e) {
				e.printStackTrace();
				Log.e(TAG, "Couldn't log Path to file!", e);
				throw new RuntimeException(e);
			}
			break;
        default:
			throw new RuntimeException("Invalid Menu Option!");
		}
		boolean result = super.onOptionsItemSelected(item);
		return result;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mDeadReckoning.resume();
		startService(intent);
   		registerReceiver(broadcastReceiver, new IntentFilter(BroadcastService.BROADCAST_ACTION));
	}

	@Override
	protected void onPause() {
		super.onPause();
		mDeadReckoning.pause();
		unregisterReceiver(broadcastReceiver);
   		stopService(intent); 		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode) {
			case DEAD_RECKONING_TRAINING_CONSTANT:
				switch(resultCode) {
					case RESULT_OK:
						// The PFReckoningTrainingActivity returns the new values selected using sliders.
						mDeadReckoning.setTrainingConstant(data.getFloatExtra(DeadReckoningTrainingActivity.KEY_TRAINING_VALUE, DeadReckoning.DEFAULT_TRAINING_CONSTANT/1000.f));
						mDeadReckoning.setAccelThreshold(data.getFloatExtra(DeadReckoningTrainingActivity.KEY_THRESHOLD_VALUE, DeadReckoning.DEFAULT_ACCEL_THRESHOLD/1000.f));										
						break;
					default:
						throw new RuntimeException("Unexpected Activity Return value!");
				}
				break;				
				
			case PARFIL_RECKONING_TRAINING_CONSTANT:
				switch(resultCode) {
					case RESULT_OK:
						// The PFReckoningTrainingActivity returns the new values selected using sliders.
						mDeadReckoning.setParticleCount(data.getFloatExtra(ParticlFilterControlActivity.KEY_PARTICLE_COUNT_VALUE,ParticleFiltering.DEFAULT_PARTICLE_COUNT));
						mDeadReckoning.setStepNoise(data.getFloatExtra(ParticlFilterControlActivity.KEY_STEP_NOISE_VALUE, ParticleFiltering.DEFAULT_STEP_NOISE_THRESHOLD/1000.f));
						mDeadReckoning.setSenseNoise(data.getFloatExtra(ParticlFilterControlActivity.KEY_SENSE_NOISE_VALUE, ParticleFiltering.DEFAULT_SENSE_NOISE_THRESHOLD/1000.f ));
						mDeadReckoning.setTurnNoise(data.getFloatExtra(ParticlFilterControlActivity.KEY_TURN_NOISE_VALUE, ParticleFiltering.DEFAULT_TURN_NOISE_THRESHOLD/1000.f ));
						break;
					default:
						throw new RuntimeException("Unexpected Activity Return value!");
				}
				break;
				
				
			case PARFIL_RECKONING_STEP_SIZE_ESTIMATE:
			case PARFIL_RECKONING_STARTPOS:
				switch(resultCode) {
				case RESULT_OK:
				    String contents = data.getStringExtra("SCAN_RESULT");
		            String format = data.getStringExtra("SCAN_RESULT_FORMAT");
		            Toast.makeText(this, "QRCode: " + contents + " format: " + format, Toast.LENGTH_LONG).show();
		            if(format.equals("QR_CODE")) {
		            try { 
		            	   	JSONObject scanQRCode = new JSONObject(contents);
		            		if(scanQRCode.has(KEY_VERSION) && scanQRCode.getInt(KEY_VERSION) >= MIN_QRCODE_VERSION && scanQRCode.has(KEY_QR_TYPE) && scanQRCode.getString(KEY_QR_TYPE).equalsIgnoreCase(MAP_POINT)) {
		            			float x = (float)scanQRCode.getDouble("X");
		            			float y = (float)scanQRCode.getDouble("Y");
		            			float[] location = mDeadReckoning.getLocation();
		            			
		            			if(requestCode == PARFIL_RECKONING_STEP_SIZE_ESTIMATE) {
		            				int numSteps = mDeadReckoning.getStepCount();
		            				double actualDistance = Math.sqrt(Math.pow(x - mDeadReckoning.getmStartX(), 2) + Math.pow(y - mDeadReckoning.getmStartY(), 2));
		            				double estimatedDistance = Math.sqrt(Math.pow(location[0] - mDeadReckoning.getmStartX(), 2) + Math.pow(location[1] - mDeadReckoning.getmStartY(), 2));
		            				double distancePerStep = (actualDistance/numSteps);
		            				double trainingConstant = mDeadReckoning.getTrainingConstant() * actualDistance/estimatedDistance;
		            				
		            				Date now = new Date(System.currentTimeMillis());
		            				try {
		            					FileWriter stepDistance = new FileWriter(new File(Environment.getExternalStorageDirectory() + File.separator + "samples", "stepDistance." + DateFormat.format("yyyy-MM-dd-kk-mm-ss", now)));
			            				stepDistance.write("" + now.getTime() + "," + + numSteps + "," + actualDistance + "," + estimatedDistance + "," + distancePerStep + "," + trainingConstant + "\n");
			            				stepDistance.flush();
										stepDistance.close();
										Toast.makeText(this, "Training Constant: " + trainingConstant + " written to file.", Toast.LENGTH_SHORT).show();
									} catch (IOException e) {
										Log.e(TAG, "Writing to stepDistance file failed!", e);
										e.printStackTrace();
										throw new RuntimeException(e);
									}
		            			} else if(requestCode == PARFIL_RECKONING_STARTPOS) {
			            			// TODO Remove this hack
			            				mDeadReckoning.restart();
			            				mDeadReckoning.setStartPos(x, y);
			            				mDeadReckoning.setLocation(x, y);
		            			}
		            		} else {
		            			Log.i(TAG, "Missing some pre-conditions!");
		            			Toast.makeText(this, "The QRCode that you've scanned doesn't seem to be of the correct type. Please recheck and scan again", Toast.LENGTH_LONG).show();		            			
		            		}
		            	} catch(JSONException e) {
		            		Log.e(TAG, "JsonException!!!", e);
		            		Toast.makeText(this, "The QRCode that you've scanned doesn't seem to be of the correct type. Please recheck and scan again", Toast.LENGTH_LONG).show();
		            	}
		            } else {
		            	Toast.makeText(this, "Invalid QRCODE format!", Toast.LENGTH_LONG).show();
		            }
		            // Handle successful scan
					break; 
					
			  case RESULT_CANCELED:
					Toast.makeText(this, "QRCode selection cancelled.", Toast.LENGTH_LONG).show();
					// Cancelled, do nothing.
					break;
				}
				break;
			default:
				throw new RuntimeException("Unexpected request code!");
		}
	}
	
	public void onSensorUpdate(float[] accel, float[] velocity,
			float[] displacement, float[] angularVelocity, float[] angles) {
		return;
	}

}
