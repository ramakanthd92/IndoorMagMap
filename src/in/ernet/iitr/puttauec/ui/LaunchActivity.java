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
	private static final int SENSOR_LOGGER_ACTIVITY = 1;
	private static final String TAG = "LaunchActivity";
	public static double [][] magnitudes = new double[51][4];
	public static double [] xs = new double[4];
	public static double [] ys = new double[51];
	public static BicubicSplineInterpolatingFunction f; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);
		loadJSONFromAsset(this);
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
			System.out.println("Dead");
			startActivityForResult(launchIntent, DEAD_RECKONING_ACTIVITY);
			break;
			
		case SENSOR_LOGGER_ACTIVITY:
			System.out.println("Sensor");
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
	
	public static void loadJSONFromAsset(Context context) {
	        String json = null;
	        try {	        	
	        	InputStream is = context.getAssets().open("w0.json");            
	        	int size = is.available();
	            byte[] buffer = new byte[size];
	            is.read(buffer);
	            is.close();
	            json = new String(buffer, "UTF-8");
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }
	        parseProfilesJson(json);
	    }
	
    public static void parseProfilesJson(String the_json){
		    try {
		           JSONObject myjson   = new JSONObject(the_json);
		           JSONArray nameArray = myjson.names();
		           String name;
		           int j,k;
		           for(int i=0; i < nameArray.length(); i++)
		            {  name = nameArray.getString(i);
		        	   JSONArray json_array  = myjson.getJSONArray(name);
		        	   j = (Integer.valueOf(name)-1) / 51;
		      	       k = (Integer.valueOf(name)-1) % 51; 		   
		               magnitudes[j][k] = json_array.getDouble(3);
		      	     }     
		       } catch (JSONException e) {
		                e.printStackTrace();
		       }
		    InterpolationFuntion();
		    }
    
   public static void InterpolationFuntion() 
   {     SmoothingPolynomialBicubicSplineInterpolator iterpolator = new SmoothingPolynomialBicubicSplineInterpolator();
         f = iterpolator.interpolate(xs, ys, magnitudes);
   }
   public static double getMagneticField(double x, double y)
   {     return f.value(x,y);   	   
   }
}
