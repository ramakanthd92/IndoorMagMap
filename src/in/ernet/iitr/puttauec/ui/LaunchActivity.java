package in.ernet.iitr.puttauec.ui;

import in.ernet.iitr.puttauec.R;
import org.apache.commons.math3.analysis.interpolation.SmoothingPolynomialBicubicSplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.BicubicSplineInterpolatingFunction;

import java.io.IOException;
import java.io.InputStream;
import java.lang.Float;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LaunchActivity extends ListActivity {
	
	private static final int DEAD_RECKONING_ACTIVITY = 0;
	private static final int PARTICLE_FILTER_RECKONING_ACTIVITY = 1;
	private static final int SENSOR_LOGGER_ACTIVITY = 2;
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
		
		Intent launchIntent = new Intent(this, DeadReckoningActivity.class);;
		System.out.println("a");
		switch(position) {
		case DEAD_RECKONING_ACTIVITY:			
			startActivityForResult(launchIntent, DEAD_RECKONING_ACTIVITY);
			break;
		
	    case PARTICLE_FILTER_RECKONING_ACTIVITY:
	    	launchIntent = new Intent(this, InputActivity.class);
	    	startActivityForResult(launchIntent, PARTICLE_FILTER_RECKONING_ACTIVITY);
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
