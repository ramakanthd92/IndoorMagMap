package in.ernet.iitr.puttauec.sensorutil;

import org.apache.commons.math3.analysis.interpolation.BicubicSplineInterpolatingFunction;
import org.apache.commons.math3.analysis.interpolation.BicubicSplineInterpolator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
<<<<<<< HEAD

public class MapGenerator implements Runnable{
	public int N = 4;                     // No. of measurement pathways in the Magnetic field  Experiment Setup.
	private int a = 2;                    // The instance specific magnetic field axes ID.  0 - x , 1 - y, 2- z
	private String json_obj,name;			
	public double [][] magnitudes;		  // Z = f(x,y) where Z is Magnetic field input for The interpolation function
	public double [] xs;                  // x-axes measurement co-ordinates
	public double [] ys = new double[51]; // y-axes measurement co-ordinates
	
	public BicubicSplineInterpolatingFunction f;   // Interpolation function for the estimation of magnetic field at any position (x,y)
	public BicubicSplineInterpolator interpolator = new BicubicSplineInterpolator();	
    private int j,k,len;
 	    	
	public MapGenerator(String json, int Nval, int k)
	{   // System.out.println("Map");
		 N = Nval;
		 a = k;
	     json_obj = json;
	    
	}	
	
	/** Loads the Magnetic field readings from the JSON object to the 2-D array. 	   
	 * @param the_json,  String JSON object
	 */
	public void parseProfilesJson (String the_json) {
		 try {	 JSONObject myjson   = new JSONObject(the_json);
		         JSONArray nameArray = myjson.names();
		         len = nameArray.length();
		         for(int i = 0; i < len; i++)
=======

import android.os.AsyncTask;

public class MapGenerator {
	public int N = 4;
	public double [][] magnitudesx,magnitudesy,magnitudesz;
	public double [] xs;
	public double [] ys = new double[51];
	public static BicubicSplineInterpolatingFunction fx,fy,fz; 
	public SmoothingPolynomialBicubicSplineInterpolator iterpolator = new SmoothingPolynomialBicubicSplineInterpolator();
	private String name;
    private int j,k,len;
 	    	
	public MapGenerator(String json_obj, int Nval)
	{    N = Nval;
	     magnitudesx = new double[N][51];
         magnitudesy = new double[N][51];
	     magnitudesz = new double[N][51];
	     xs = new double[N];
	     parseProfilesJson(json_obj);		    
	     fx = iterpolator.interpolate(xs, ys, magnitudesx);
         fy = iterpolator.interpolate(xs, ys, magnitudesy);
      	 fz = iterpolator.interpolate(xs, ys, magnitudesz);	    
      	 for(double i = 0.0; i < 15.0; i++)
      	 {for(double j = 0.0; j < 50.0; j++)
      	 	{ System.out.print(fz.value(i,j));
      	 	  System.out.print(" ");
      	 	}
      	     System.out.println("");
      	 }
	}	
	
	public void parseProfilesJson (String the_json) {
		 try {
		         JSONObject myjson   = new JSONObject(the_json);
		         JSONArray nameArray = myjson.names();
		         len = nameArray.length();
		         for(int i=0; i < len; i++)
>>>>>>> e6b5b8d6170e3d8c4fac755f4b9e4df97fa7f921
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
<<<<<<< HEAD
		              magnitudes[j][k] = json_array.getDouble(a);
=======
		              magnitudesz[j][k] = json_array.getDouble(2);
		              magnitudesy[j][k] = json_array.getDouble(1);
		              magnitudesx[j][k] = json_array.getDouble(0);		              
>>>>>>> e6b5b8d6170e3d8c4fac755f4b9e4df97fa7f921
		            //hard coded form of data input from the JSON 3rd column
		      	     }    
		          for(int i = 0; i < 51; i++)
		            { ys[i] = i;
		      	    }    
		          if(N != 10)
		            {  for(int i=0; i < N; i++)
		                   { xs[i] = i;
		      	           } 
		            }
		          else
<<<<<<< HEAD
		            {   xs[0] = 0;  xs[1] =1; xs[2]=2;
		          	    xs[3] = 6;  xs[4] =7; xs[5]=8;
		                xs[6] = 12; xs[7]=13; xs[8]=14; xs[9]=15;
=======
		            {   xs[0] = 0; xs[1] =1; xs[2]=2;
		          	    xs[3] = 6; xs[4] =7; xs[5]=8;
		                xs[6] = 12; xs[7] =13; xs[8]=14; xs[9]=15;
>>>>>>> e6b5b8d6170e3d8c4fac755f4b9e4df97fa7f921
		            }
		       } catch (JSONException e) {
		                e.printStackTrace();
		       }		      
		   }
<<<<<<< HEAD
	
	/**  Generate Interpolation Function for the instance which will be used in the particle filter for estimating the particles 
	 *  Magnetic field at some location (x,y) 
	 */
	public void run()
	{    android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
	     magnitudes = new double[N][51];
    	 xs = new double[N];
    	 parseProfilesJson(json_obj);		    
    	 f = interpolator.interpolate(xs, ys, magnitudes);
    	 magnitudes = null;  
	}
=======
>>>>>>> e6b5b8d6170e3d8c4fac755f4b9e4df97fa7f921
}
	
		  		

