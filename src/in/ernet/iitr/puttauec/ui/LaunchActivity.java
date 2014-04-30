package in.ernet.iitr.puttauec.ui;

import in.ernet.iitr.puttauec.ui.DeadReckoningActivity;
import in.ernet.iitr.puttauec.ui.ParticleFilteringActivity;
import in.ernet.iitr.puttauec.R;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LaunchActivity extends ListActivity {
	
	private static final int DEAD_RECKONING_ACTIVITY = 0;
	private static final int PARTICLE_FILTER_RECKONING_ACTIVITY_AHRS_VECTOR_MAP = 1;
	private static final int PARTICLE_FILTER_RECKONING_ACTIVITY_AHRS_MAGNITUDE_MAP = 2;
	private static final int PARTICLE_FILTER_RECKONING_ACTIVITY_KALMAN = 3;
	private static final int PARTICLE_FILTER_RECKONING_ACTIVITY_AHRS_MAGNITUDE = 4;
	private static final int PARTICLE_FILTER_RECKONING_ACTIVITY_AHRS_VECTOR = 5;
	private static final int PARTICLE_FILTER_RECKONING_ACTIVITY_AHRS_MAP = 6;
	private static final int SENSOR_LOGGER_ACTIVITY = 7;
	private static final String TAG = "LaunchActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.activities));
		getListView().setAdapter(adapter);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Intent launchIntent = new Intent(this, DeadReckoningActivity.class);
		
		switch(position) {
		case DEAD_RECKONING_ACTIVITY:			
			startActivityForResult(launchIntent, DEAD_RECKONING_ACTIVITY);
			break;
		
	    case PARTICLE_FILTER_RECKONING_ACTIVITY_AHRS_VECTOR_MAP:
	    	launchIntent = new Intent(this, ParticleFilteringActivity.class);
	    	launchIntent.putExtra(ParticleFilteringActivity .KEY_RECKONING_METHOD, ParticleFilteringActivity.PF_RECKONING_AHRS_VECTOR_MAP);	
	    	startActivityForResult(launchIntent, PARTICLE_FILTER_RECKONING_ACTIVITY_AHRS_VECTOR_MAP);
			break;			

	    case PARTICLE_FILTER_RECKONING_ACTIVITY_AHRS_MAGNITUDE_MAP:
	    	launchIntent = new Intent(this, ParticleFilteringActivity.class);
	    	launchIntent.putExtra(ParticleFilteringActivity .KEY_RECKONING_METHOD, ParticleFilteringActivity.PF_RECKONING_AHRS_MAGNITUDE_MAP);	
	    	startActivityForResult(launchIntent, PARTICLE_FILTER_RECKONING_ACTIVITY_AHRS_MAGNITUDE_MAP);
			break;			
		
	    case PARTICLE_FILTER_RECKONING_ACTIVITY_AHRS_VECTOR:
	    	launchIntent = new Intent(this, ParticleFilteringActivity.class);
	    	launchIntent.putExtra(ParticleFilteringActivity .KEY_RECKONING_METHOD, ParticleFilteringActivity.PF_RECKONING_AHRS_VECTOR);	
	    	startActivityForResult(launchIntent, PARTICLE_FILTER_RECKONING_ACTIVITY_AHRS_VECTOR);
			break;			
	    
	    case PARTICLE_FILTER_RECKONING_ACTIVITY_AHRS_MAGNITUDE:
	    	launchIntent = new Intent(this, ParticleFilteringActivity.class);
	    	launchIntent.putExtra(ParticleFilteringActivity .KEY_RECKONING_METHOD, ParticleFilteringActivity.PF_RECKONING_AHRS_MAGNITUDE);	
	    	startActivityForResult(launchIntent, PARTICLE_FILTER_RECKONING_ACTIVITY_AHRS_MAGNITUDE);
			break;			

	    case PARTICLE_FILTER_RECKONING_ACTIVITY_AHRS_MAP:
	    	launchIntent = new Intent(this, ParticleFilteringActivity.class);
	    	launchIntent.putExtra(ParticleFilteringActivity .KEY_RECKONING_METHOD, ParticleFilteringActivity.PF_RECKONING_AHRS_MAP);	
	    	startActivityForResult(launchIntent, PARTICLE_FILTER_RECKONING_ACTIVITY_AHRS_MAP);
			break;			
		
	    case PARTICLE_FILTER_RECKONING_ACTIVITY_KALMAN:
	    	launchIntent = new Intent(this, ParticleFilteringActivity.class);
	    	launchIntent.putExtra(ParticleFilteringActivity.KEY_RECKONING_METHOD, ParticleFilteringActivity.PF_RECKONING_KALMAN);
	    	startActivityForResult(launchIntent, PARTICLE_FILTER_RECKONING_ACTIVITY_KALMAN);
	    	break;
			
		case SENSOR_LOGGER_ACTIVITY:
			launchIntent = new Intent(this, SensorLoggerActivity.class);
			startActivity(launchIntent);
			break;
			
		default:
			throw new RuntimeException("Unexpected position: " + position);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// TODO: Do we need to check the result? 
		// We will assume all results are RESULT_OK
	}
}
