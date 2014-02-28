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
	public static int N = 4;
	public static double [][] magnitudes;
	public static double [] xs;
	public static double [] ys = new double[51];
	public static BicubicSplineInterpolatingFunction f; 
	public static SmoothingPolynomialBicubicSplineInterpolator iterpolator = new SmoothingPolynomialBicubicSplineInterpolator();
	    
	
	public MapGenerator(String json_obj, int Nval)
	{    N = Nval;
	     magnitudes = new double[N][51];
	     xs = new double[N];
	     parseProfilesJson(json_obj);		    
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
		        	   if(j >= 6 && j <= 8)
		        	   		{ j -=3;
		        	   		}
		        	   else if(j >= 12 && j <=15)
		        	   		{ j -= 6;		        		   
		        	   		}
		              magnitudes[j][k] = json_array.getDouble(2);       //hard coded form of data input from the JSON 3rd column
		      	     }    
		          for(int i=0; i < 51; i++)
		            { ys[i] = i;
		      	    }    
		        if(N != 10)
		          {  for(int i=0; i < N; i++)
		              { xs[i] = i;
		      	      } 
		          }
		        else
		          {  xs[0] = 0; xs[1] =1; xs[2]=2;
		          	 xs[3] = 6; xs[4] =7; xs[5]=8;
		             xs[6] = 12; xs[7] =13; xs[8]=14; xs[9]=15;
		          }
		       } catch (JSONException e) {
		                e.printStackTrace();
		       }
		      
		    }
	
}
	
		  		

