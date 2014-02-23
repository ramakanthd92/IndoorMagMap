package in.ernet.iitr.puttauec.ui;


import in.ernet.iitr.puttauec.R;
import in.ernet.iitr.puttauec.algorithms.SensorLogger;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SensorLoggerActivity extends Activity {
	private Button mLoggingButton;
	private SensorLogger mSensorLogger;
	private WakeLock mWakeLock;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor_logger);
		System.out.println("Sensor Logger");
		Intent intent = getIntent();
		mSensorLogger = new SensorLogger(this);
		mLoggingButton = (Button) findViewById(R.id.logging_button);
		
		mLoggingButton.setOnClickListener(new OnClickListener() {
			private int state = 0;
			final String labelText[] = new String[] { "Stop Logging Data", "Start Logging Data" };
			
			@Override
			public void onClick(View v) {
				mLoggingButton.setText(labelText[state]);
				
				switch(state) {
				case 0: // start logging
					mSensorLogger.start();
					break;
				case 1:
					mSensorLogger.stop();
					break;
				default:
					throw new RuntimeException("Invalid button state!");
				}
				
				state = (state + 1)%2;
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mSensorLogger.resume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mSensorLogger.pause();
	}
	
}
