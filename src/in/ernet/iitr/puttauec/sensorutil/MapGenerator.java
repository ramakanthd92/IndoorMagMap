package in.ernet.iitr.puttauec.sensorutil;

import org.apache.commons.math3.analysis.interpolation.BicubicSplineInterpolatingFunction;
import org.apache.commons.math3.analysis.interpolation.SmoothingPolynomialBicubicSplineInterpolator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
		              magnitudesz[j][k] = json_array.getDouble(2);
		              magnitudesy[j][k] = json_array.getDouble(1);
		              magnitudesx[j][k] = json_array.getDouble(0);		              
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
		            {   xs[0] = 0; xs[1] =1; xs[2]=2;
		          	    xs[3] = 6; xs[4] =7; xs[5]=8;
		                xs[6] = 12; xs[7] =13; xs[8]=14; xs[9]=15;
		            }
		       } catch (JSONException e) {
		                e.printStackTrace();
		       }		      
		   }
}
	
		  		

