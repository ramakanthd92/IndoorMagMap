package in.ernet.iitr.puttauec.sensorutil;

import org.apache.commons.math3.analysis.interpolation.BicubicSplineInterpolatingFunction;
import org.apache.commons.math3.analysis.interpolation.BicubicSplineInterpolator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapGenerator implements Runnable{
	public int N = 11;
	private int a = 2;
	private String json_obj,name;
	public double [][] magnitudes;
	public double [] xs;
	public double [] ys = new double[27];
	public BicubicSplineInterpolatingFunction f;
	public BicubicSplineInterpolator interpolator = new BicubicSplineInterpolator();	
    private int j,k,len;
 	    	
	public MapGenerator(String json, int Nval, int k)
	{    System.out.println("Map");
		 N = Nval;
		 a = k;
	     json_obj = json;
	    
	}	
	public void parseProfilesJson (String the_json) {
		 try {	 JSONObject myjson   = new JSONObject(the_json);
		         JSONArray nameArray = myjson.names();
		         len = nameArray.length();
		         for(int i = 0; i < len; i++)
		            {  name = nameArray.getString(i);
		        	   JSONArray json_array  = myjson.getJSONArray(name);
		        	   j = (Integer.valueOf(name)) / 27;
		        	   k = (Integer.valueOf(name)) % 27;
		        	   
		              magnitudes[j][k] = json_array.getDouble(a);
		            //hard coded form of data input from the JSON 3rd column
		      	     }    
		          for(int i = 0; i < 27; i++)
		            { ys[i] = i;
		      	    }    
		          for(int i=0; i < N; i++)
		                   { xs[i] = i;
		      	           } 
		       } catch (JSONException e) {
		                e.printStackTrace();
		       }		      
		   }
	
	public void run()
	{    android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
	     magnitudes = new double[N][27];
    	 xs = new double[N];
    	 parseProfilesJson(json_obj);		    
    	 f = interpolator.interpolate(xs, ys, magnitudes);
    	 magnitudes = null;  
	}
}
	
		  		

