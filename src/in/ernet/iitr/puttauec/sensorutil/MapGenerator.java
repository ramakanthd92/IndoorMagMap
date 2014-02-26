package in.ernet.iitr.puttauec.sensorutil;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.math3.analysis.interpolation.BicubicSplineInterpolatingFunction;
import org.apache.commons.math3.analysis.interpolation.SmoothingPolynomialBicubicSplineInterpolator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class MapGenerator {
	
	public static double [][] magnitudes = new double[4][51];
	public static double [] xs = new double[4];
	public static double [] ys = new double[51];
	public static BicubicSplineInterpolatingFunction f; 
	public static SmoothingPolynomialBicubicSplineInterpolator iterpolator = new SmoothingPolynomialBicubicSplineInterpolator();
	    
	
	public MapGenerator(String json_obj)
	{    parseProfilesJson(json_obj);
	     f = iterpolator.interpolate(xs, ys, magnitudes);
	    
	}	
	public static void parseProfilesJson (String the_json) {
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
		          for(int i=0; i < 51; i++)
		            { ys[i] = i;
		      	    }    
		          for(int i=0; i < 4; i++)
		            { xs[i] = i;
		      	    }   
		       } catch (JSONException e) {
		                e.printStackTrace();
		       }
		      
		    }
	
}
	
		  		

