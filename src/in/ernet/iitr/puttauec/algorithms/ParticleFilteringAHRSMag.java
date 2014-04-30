package in.ernet.iitr.puttauec.algorithms;

import in.ernet.iitr.puttauec.R;
import in.ernet.iitr.puttauec.sensorutil.MapGenerator;
import in.ernet.iitr.puttauec.sensorutil.RandomSingleton;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Random;

import Jama.Matrix;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.text.format.DateFormat;
import android.util.Log;


public class ParticleFilteringAHRSMag extends DeadReckoning {
	   //constants
	   private static final String TAG = "PaicleFilterReckoning";
	   public static final int DEFAULT_PARTICLE_COUNT = 100; //1000 //2000
	   public static final int DEFAULT_STEP_NOISE_THRESHOLD = 200; // 400  //600 //800 //1000 //1500 
	   public static final int DEFAULT_SENSE_NOISE_THRESHOLD = 4000; //2000 //10000  //15000
	   public static final int DEFAULT_TURN_NOISE_THRESHOLD = 90; //2000 //10000  //15000
	   private static final double INIT_SD_X = 0.4;
	   private static final double INIT_SD_Y = 0.4;	  
	   private static final double X_SD = 1.4;
	   private static final double Y_SD = 1.4;	   	  
	   private static final double  minX  = 0.0  ; 
	   private static  double  maxX  = 16.0 ;  
	   private static final double  minY  = 0.0 ;  
	   private static final double  maxY  = 26.0 ; 
	   private static final double mul =(180/Math.PI);
	  // private static final double xoffset = 12.8375610466;
	  // private static final double yoffset = -0.0999689574027;
	   
	   //Instance variables
	   private double sigma_2 = Math.pow(msenseNoise, 2.0);
	   private static final Random rand = RandomSingleton.instance;
	   private double[]  measurement  = {0.0,0.0,0.0};					// Magnetic field in Device Co-ordinate system
	   private double orien  = 0.0;                                      // avg orientation of the particles to be written to file on each step
	   private double [] mTrueMeasurement = {0.0,0.0,0.0};					// Magnetic field in Global co-ordinate system
	   private double position  = 0.0;						// Magnetic field vector at any position (x,y) of the particles estimated from Interpolation function.
	   protected MapGenerator magneticmapwm;    // magnetic field map instances for x,y,z axes readings
	   protected MapGenerator magneticmapem;
	   protected Particle[] particles ;									 // All particles after re-sampling or after moving
	   protected Particle[] inside_particles ;                           // particles that were not lost in indoor map
	   protected Particle[] oldParticles;						         // particles before movement
	   protected double[] weightSums ;									 // CDF of importance weights
	   private double magnitude;										 // magnitude to be written to Magnetic field log.
	   private double angle;                                             // angle updated by different particles
	   private double theta_adj;										 // adjustment angle based on the particles lost in the previosu step
	   
       //Control parameters        
	   private static int particleCount = DEFAULT_PARTICLE_COUNT;       
	   private static double msenseNoise = DEFAULT_SENSE_NOISE_THRESHOLD/1000.f;    // Step Noise for the particles used in dynamical equation.  Reduce this to a mimimum to reduce error in path length.  
	   private static double mstepNoise = DEFAULT_STEP_NOISE_THRESHOLD/1000.f;      // Sense noise used in Co-variance matrix of Vector Gaussian
	   private static double mturnNoise = DEFAULT_TURN_NOISE_THRESHOLD/1000.f;	    // Turn noise for particles used in dynamical equations
	   private static double mmse = 0.0;                                            // MMSE of the particles at each update location step from estimated position.

	   //log file writers
	   private static final double MAX_ACCEPTABLE_TRANSITION_COST = 1e-4;
	   protected Bitmap mFloorPlan;
	   private FileWriter mNoiseFileWriter;
	   private FileWriter mMagLogFileWriter;
	   private FileWriter mMMSEDistanceFileWriter;
	   
	   //some variables
	   private static int len = 0;
       private static double sel = 0.0;
       private static int index = 0;
       private static double totalSum = 0.0;
       private static double sum = 0.0;
       private static double total = 0.0;
       private static double value = 0.0;
       private static double dx = 0.0, dy = 0.0;
	   private static double err = 0.0; 
	   private static double currentWeight = 0.0;
	   private static double best = 0.0;
	   private static double xp = 0.0;
	   private static double yp = 0.0;
	   private static double den = 0.0;
	   private static double step_act = 0.0;
	   private float Cost = 0.f;	
	   
public class Particle
  { 	public double x;
    	public double y;
    	public double theta_noise; 
    	public double importance_weight;
    	public Particle()
    		{  	x  = minX + (maxX-minX)*rand.nextDouble() ; 		    
    			y  = minY + (maxY-minY)*rand.nextDouble() ;
    			theta_noise = mturnNoise*rand.nextGaussian();
    			importance_weight = 1.0;
    		}	 
    	public Particle(Particle P)
    		{  	x  = P.x ; 
    			y  = P.y ; 
    			theta_noise = P.theta_noise;
    			importance_weight = P.importance_weight;
    		}	
    	public Particle(double x_co, double y_co, double imp)
		    {   x  = x_co ;
			    y  = y_co ; 
			    theta_noise = mturnNoise*rand.nextGaussian();
			    importance_weight = imp;
		    }	
    	public void set_pos(double x_co, double y_co)
    		{  	x = x_co;
    			y = y_co;
    		}	 
    	public void move(double step_size, double rad_angle,double theta_a)
    		{   step_act = step_size + mstepNoise*rand.nextGaussian();
    		 	angle = rad_angle + theta_noise + theta_a;
                angle %= (Math.PI*2);
    		 	x = x + (step_act* Math.sin(angle));                         											 
    			y = y + (step_act* Math.cos(angle));                  
    		}
    	public void normalizeImportanceWeight(double sum)
    		{   if (sum == 0) {
    		    		throw new IllegalArgumentException("Argument 'divisor' is 0");
    				}
    			 importance_weight /= sum;    
    		}
    	@Override
    	public String toString() 
    	    {
    		  return "Particle [ x =" + x + "]" + "[ y =" + y + "]" +  "[ imp =" + importance_weight + "]" ; 			    	
            }
  }
  
    /**
    * Constructor for particle filtering class. 
    * Generates the magnetic field maps x,y,z axes using separate threads.     
    * @param context
    */  
		/**
		 * @param context
		 * @param algorithm
		 */
		public ParticleFilteringAHRSMag(Context ctx, IAngleAlgorithm algorithm) {
			super(ctx);   
			//Load the indoor Map
			String json_obj_0 = loadJSONFromAsset(ctx,"data-west-6.json");
		    String json_obj_1 = loadJSONFromAsset(ctx,"data-east-6.json");
    		
		    //Load the Magnetic Map
		    magneticmapwm = new MapGenerator(json_obj_0, 17,3);
    		magneticmapem = new MapGenerator(json_obj_1, 17,3);
 			magneticmapwm.run();
			magneticmapem.run();
	        
	        //set the Angle Algorithm 
	        angle_algo = algorithm; 
	     }
	
		/**
		 * Loads the magnetic field readings from the JSON file in assets to the string object.		 * 
		 * @param context
		 * @param filename     the asset JSON file that has the raw magnetic field readings. (x,y,z,magnitude) 
		 * @return             JSON string object 
		 */
		
		public String loadJSONFromAsset(Context context,String filename) {
	        String json = null;
	        try {	        	
	        	InputStream is = context.getAssets().open(filename);            
	        	int size = is.available();
	            byte[] buffer = new byte[size];
	            is.read(buffer);
	            is.close();
	            json = new String(buffer, "UTF-8");
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }
	        return json;
	    }
	   
		/**
		 *  Gaussian Function for |m| that gives the probability of the particle magnetic magnitude (mu) and respective magnetic field observation.(Z)
		 *                _
		 *            |  /|\              Gaussian probability using the measurement and particle magnetic field.
		 *          __|_/ | \_|__
		 * -----------|---|---|--------
		 *               mu   Z
		 * @param mu             particle magnetic field estimate
		 * @param sigma          sense noise
		 * @param x              measurement 
		 * @return               importance weight
		 */
	   public double Gaussian(double mu,double sigma,double x)
	   {    double sigma_2 = Math.pow(sigma, 2.0);
		    double mu_x_2 = Math.pow((x-mu),2.0); 		
	        return Math.exp(-(mu_x_2 / (sigma_2*2.0))) + 0.0001 ;
	   }
	   
	   
	   /**  init for the particle filter. Initialising the particles and weights CDF
	    *   Normalise the weights. Log the noise parameters.	     
	    */
	   @Override
	   protected void init() {
	        super.init();
			this.particles = new Particle[particleCount];
			this.inside_particles = new Particle[particleCount];
			this.weightSums = new double[particleCount + 1];
		    len = particleCount;
			for (int i = 0 ; i < len ; i++) {
				particles[i] = new Particle();
			}
			for (int i = 0 ; i < len ; i++) {
				inside_particles[i] = particles[i];
			}
			normalizeWeights(len);
			try {
					String r = (String) (DateFormat.format("yyyy-MM-dd-hh-mm-ss", new java.util.Date()) );
					String logFileBaseName = "pfLog." + r;
					mNoiseFileWriter = new FileWriter(new File(STORAGE_DIR_E, logFileBaseName + ".noise.csv"));
					mNoiseFileWriter.write(""+ mstepNoise + ","+ msenseNoise +","+ mturnNoise + "," + mAccelThreshold + "," + mTrainingConstant + "\n");
					mNoiseFileWriter.flush();
					mNoiseFileWriter.close();					
				} catch (IOException e) {
					Log.e(TAG, "Log file write Noise failed!!!\n", e);
					e.printStackTrace();
					throw new RuntimeException(e);
				}
		}
			    
		@Override
		public void onMagneticFieldUpdate(float[] values, long deltaT, long timestamp) {
			super.onMagneticFieldUpdate(values, deltaT, timestamp);
			this.measurement[0] = values[0];
			this.measurement[1] = values[1];
			this.measurement[2] = values[2];
			this.magnitude= Math.sqrt(this.measurement[0]*this.measurement[0] + this.measurement[1]*this.measurement[1] + this.measurement[2]*this.measurement[2]);			
			if(this.isLogging()) {
				try {
					mMagLogFileWriter.write("" + timestamp + "," + deltaT + "," + values[0] + ","+ values[1] +","+ values[2] + "," + this.magnitude + "\n");
				} catch (IOException e) {
					Log.e(TAG, "Log file write for Magnetic Field failed!!!\n", e);
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		}
		
		/** The update location that updates the particles motion and importance weights from measurements. 
		 *  Re-samples all important particles. Predicts the current Location using the mean of the particle density. 
		 */
		@Override
		public void updateLocation(double step_size, double rad_angle, double turn_angle)
		{						
			System.out.println("update");
			inside_particles = new Particle[particles.length];
			oldParticles = new Particle[particles.length];					
			len = particles.length;
			double px, py ,est,act;	
			double max_weight = 0.0;						
			orien = 0.0;
			int in_len = 0;
			for(int i = 0; i < particleCount ; ++i) 
				{
					oldParticles[i] = new Particle(particles[i]);
				}
			for(int i = 0; i < particleCount ; ++i) 
				{							
					particles[i].move(step_size,rad_angle,theta_adj);
					orien += angle;
					px = particles[i].x;
					py = particles[i].y;		
					max_weight = 0.0;
					act = measurement[0]*measurement[0] + measurement[1]*measurement[1] +measurement[2]*measurement[2];			   	
					if(px >= 0.0 && px <= maxX && py >= 0.0 && py <= maxY)
						{ 
								inside_particles[in_len]  = particles[i];
								inside_particles[in_len].importance_weight = 1.0;
								if(rad_angle < (-3*Math.PI/4) || rad_angle > 3*Math.PI/4)
									{  	
										position = magneticmapem.f.value(px,py);		// X-axis magnetic field of a particle look up from interpolation function. 
										inside_particles[in_len].importance_weight *= Gaussian(position,msenseNoise,magnitude);    						
									}
								else if (rad_angle > (-Math.PI/4) && rad_angle < Math.PI/4)
									{   
										position = magneticmapwm.f.value(px,py);		// X-axis magnetic field of a particle look up from interpolation function. 
										inside_particles[in_len].importance_weight *= Gaussian(position,msenseNoise,magnitude);  						
									}
								else
									{   
										position = magneticmapem.f.value(px,py);		// X-axis magnetic field of a particle look up from interpolation function. 
										inside_particles[in_len].importance_weight *= Gaussian(position,msenseNoise,magnitude);
									}	
								max_weight = Math.max(max_weight,inside_particles[in_len].importance_weight);
								in_len++;					 					 
							}			   
					}											 
			orien /= len;
			if(in_len > 0) 
				{
					normalizeWeights(in_len);		
					for(int j = 0; j < particleCount; ++j) 
						{						
							particles[j] = selectParticleAndCopy(in_len);						
						}							
				}
			else 
				{
					// Re-sampling stage: don't go here unless you get lost!!!
					// Retry with lesser accuracy particles
					for(int i = 0; i < particleCount; ++i) {
						Particle disturbedParticle = new Particle(oldParticles[i].x + (X_SD*rand.nextGaussian()), oldParticles[i].y + (Y_SD*rand.nextGaussian()),oldParticles[i].importance_weight);
						px = disturbedParticle.x;
						py = disturbedParticle.y;
						while(!(px >= 0.0 && px <= maxX && py >= 0.0 && py <= maxY))
							{ disturbedParticle = new Particle(oldParticles[i].x + (X_SD*rand.nextGaussian()), oldParticles[i].y + (Y_SD*rand.nextGaussian()),oldParticles[i].importance_weight);							
							}
						particles[i] = disturbedParticle;
					}
				}		
			
			// adjusting particle motion to minimize the particle loss.
			theta_adj = 0;
			for(int j = 0; j < in_len; ++j) 
				{
				 	theta_adj += inside_particles[j].theta_noise; 
				}
			if(in_len != 0)
				{ 
					theta_adj /= in_len;
				}
			mmse = minimumMeanSquareDistance();			
			if(this.isLogging()) 
				{
					try {
							mMMSEDistanceFileWriter.write("" + particleCount + "," + mmse + "," + getLocation()[0]  + "," + getLocation()[1] +"," + position +"," + measurement[0] +"," +  measurement[1] +"," + measurement[2] + "," + turn_angle + "," + theta_adj + "," + in_len + "," + orien + "\n");			
							Log.d(TAG,"turn " + String.valueOf(turn_angle));
							Log.d(TAG,"orien " + String.valueOf(orien));
						}
					catch (IOException e) 
						{
							Log.e(TAG, "Log file write for MMSEDistance failed!!!\n", e);
							e.printStackTrace();
						}
				}
		}

	  /** Re-sampling the particles after measurement probability is used to weight all the particles and normalise all the particle weights.
		 *  It gives importance to most preferred particles. 
		 * @return   particle with replacement.
		 */
		private Particle selectParticleAndCopy(int len) {
			sel = rand.nextDouble();
			index = Arrays.binarySearch(weightSums,0,len,sel);
			if (index < 0) {
				index = - (index + 1);
			}
			if (index > 0) {
				index--;
			}
			return new Particle(inside_particles[index]);
		}
         	
		/** Helper function for Normalize weights. 
		 * @return
		 */
		private double calculateSums(int len) {
		    totalSum = 0.0;
			for (int i = 0; i < len; i++) {
				totalSum += inside_particles[i].importance_weight;
			}
			return totalSum;
		}
        
		/**  Normalize weights if the total sum is not zero or weight all particles to 1.0                   
		*/
		private void normalizeWeights(int len) {
	     sum = calculateSums(len);
	     if (sum == 0.0) {
			  System.out.println(" Sum of weights is zero. Regenerate all particles.");
			  for (int i = 0; i < len ; i++) {				   
				   inside_particles[i].importance_weight = 1.0;
				}
				normalizeWeights(len);
			}
		    for (int i = 0; i <len ; i++) {
				inside_particles[i].normalizeImportanceWeight(sum);
			}
			calculateFilterArray(len);
		}
		
		/**  weightSums CDF Generation.		  
		 */
		private void calculateFilterArray(int len) {
			this.weightSums[0] = 0.0;
		    value = 0.0;
			for (int i = 0; i < len; i++) {
			    value = this.inside_particles[i].importance_weight;
				this.weightSums[i + 1] = this.weightSums[i] + value;
			}
			total = this.weightSums[len];                       
			for (int i = 0 ; i < len+1; i++) {
				weightSums[i] /= total;
			}
		}
		
		/** calculates Minimum Mean Square distance of particles from predicted location. This shows the convergence of particles. 
		 * @return  MMSE of the particles from the estimated particles.
		 */
		public double minimumMeanSquareDistance()
		{  sum = 0.0; err = 0.0; dx = 0.0; dy = 0.0;		
		   Particle r = getParticleMedian();
		   len = particles.length;
		   for (int i = 0; i <len ; i++)  // calculate mean error
			{  dx = (particles[i].x - r.x);
	           dy = (particles[i].y - r.y);
	           err = Math.sqrt(dx * dx + dy * dy);
	           sum += err;
			}
		   return (sum/len);
		}
		
		/**	Predicts the most probable particle as the Current location Estimate. 
		 * @return  The particle with highest Importance weight.
		 */
		public Particle getBestMap(int len) 
		{
			Particle rc = particles[0];
			best = rc.importance_weight;
		    currentWeight = 0.0;
		    for (int i = 0; i < len ; i++) 
		    {
				currentWeight = inside_particles[i].importance_weight;
				if (currentWeight > best) 
					{
						rc = inside_particles[i];
						best = currentWeight;
					}
			}
			setLocation(rc.x,rc.y);
			return rc;
		}
		
		/** Calculate the mean of all particles based upon importance weights.  
		 * @return  The Importance weight based mean estimate of all the particles
		 */
		public Particle getParticleEstimate(int len)
		{
			currentWeight = 0.0; xp = 0.0 ; yp = 0.0;
			double totalWeight = 0.0;
			for (int i = 0; i < len ; i++) 
				{
					currentWeight = inside_particles[i].importance_weight;
					totalWeight += currentWeight;
					xp += inside_particles[i].x;
					yp += inside_particles[i].y;
				}
			xp /= len;
			yp /= len;			
		    Particle pe = new Particle();
		    pe.set_pos(xp,yp);
			setLocation(xp,yp);
			return pe;
		}

		public Particle getParticleMedian()
		{
			currentWeight = 0.0; xp = 0.0 ; yp = 0.0;
			len = particles.length;
			double[] xs = new double [len]; 
			double[] ys = new double [len];
			for (int i = 0; i <len ; i++) 	
				{
					xs[i] = particles[i].x;
					ys[i] = particles [i].y;
				}
			Arrays.sort(xs);
			Arrays.sort(ys);	
			xp = xs[len/2];
			yp = ys[len/2];
		    Particle pe = new Particle();
		    pe.set_pos(xp,yp);
			setLocation(xp,yp);
			return pe;
		}
		
		public int size() {
			return particles.length;
		}
		
		public Particle getparticle(int i) {
			return particles[i];
		}

		public void setmaxX(double mx) {
		    maxX = mx;
		}
		
		@Override
		public void startLogging() {
			
			try {
				String r = (String) (DateFormat.format("yyyy-MM-dd-hh-mm-ss", new java.util.Date()) );
				String logFileBaseName = "pfLog." + r;
				mAccelLogFileWriter = new FileWriter(new File(STORAGE_DIR_E, logFileBaseName + ".accel.csv"));
				mMMSEDistanceFileWriter = new FileWriter(new File(STORAGE_DIR_E, logFileBaseName + ".mmse.csv"));
				mMagLogFileWriter = new FileWriter(new File(STORAGE_DIR_E, logFileBaseName + ".mag.csv"));
				mStepLogFileWriter = new FileWriter(new File(STORAGE_DIR_E, logFileBaseName + ".pfsteps.csv"));
			} catch (IOException e) {
				Log.e(TAG, "Creating and opening log files failed!", e);
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			mIsLogging = true;
		}
		
		@Override
		public void stopLogging() {
			mIsLogging = false;			
			try {
				mMMSEDistanceFileWriter.flush();
				mMMSEDistanceFileWriter.close();
				mMagLogFileWriter.flush();
				mMagLogFileWriter.close();
				mStepLogFileWriter.flush();
				mStepLogFileWriter.close();
			} catch (IOException e) {
				Log.e(TAG, "Flushing and closing log files failed!", e);
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		
		/** Set all the particles to start from an Initial location.		  
		 */
		@Override
		public void setStartPos(float x, float y) {
			super.setStartPos(x, y);
		}	    
	
		@Override
		public void setmStartX(float mStartX) {
			super.setmStartX(mStartX);
			len = particles.length;
			for(int i = 0; i <len ; i++) {
				particles[i].x = mStartX + (float)(INIT_SD_X*rand.nextGaussian());
			}
		}
		
		@Override
		public void setmStartY(float mStartY) {
			super.setmStartY(mStartY);
			len =particles.length;
			for(int i = 0; i <len ; i++) {
				particles[i].y = mStartY + (float)(INIT_SD_Y*rand.nextGaussian());
			}
		}			
	
		public void setParticleCount (float pc) {
			 particleCount = (int) pc;
		}
		    
		public void setSenseNoise (float sen) {
			  msenseNoise = (double)sen;
		}
			
		public void setStepNoise (float ste) {
			  mstepNoise = (double)ste;
		}
			
		public void setTurnNoise (float tun) {
			  mturnNoise = (double)(tun/mul);
		}		
		public float getParticleCount () {
		   return ((float)particleCount);
		}
	    
		public float getSenseNoise () {
			return ((float)msenseNoise);
		}
		
		public float getStepNoise () {
			return ((float)mstepNoise);
		}
		
		public float getTurnNoise () {
			return ((float)(mul*mturnNoise));
			
		}
		
		public double getMMSE() {
			return  mmse;
		}
};