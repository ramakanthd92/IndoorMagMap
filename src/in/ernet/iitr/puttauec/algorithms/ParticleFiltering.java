package in.ernet.iitr.puttauec.algorithms;

import in.ernet.iitr.puttauec.sensorutil.MapGenerator;
import in.ernet.iitr.puttauec.sensorutil.RandomSingleton;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.math3.analysis.interpolation.BicubicSplineInterpolatingFunction;

import android.content.Context;
//import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import Jama.*; 

public class ParticleFiltering extends DeadReckoning {
	   //constants
       private static final String TAG = "PaicleFilterReckoning";
	   public static final int DEFAULT_PARTICLE_COUNT = 200; //1000 //2000
	   public static final int DEFAULT_STEP_NOISE_THRESHOLD = 700; // 400  //600 //800 //1000 //1500 
	   public static final int DEFAULT_SENSE_NOISE_THRESHOLD = 5000; //2000 //10000  //15000
	   public static final int DEFAULT_TURN_NOISE_THRESHOLD = 300; //2000 //10000  //15000
	   private static final double INIT_SD_X = 1.0;
	   private static final double INIT_SD_Y = 1.0;	   
	   private static final double  minX  = 0.0  ; 
	   private static  double  maxX  = 3.0 ;  
	   private static final double  minY  = 0.0 ;  
	   private static final double  maxY  = 50.0 ; 
	   private static final double mul =(180/Math.PI);
	   //private static final double cons = Math.pow(2.0*Math.PI,1.5);
	   private static final double prb = Math.pow(10.0, -30);			// The constant probability for the particles going away from the measured map
	   private static final double kin = Math.pow(10.0, 10);             // The multiplier to account for too much less probability for the particles prediction.
	   
	   
	   public BicubicSplineInterpolatingFunction fz; 
	   
	   //Instance variables
	   private static final Random rand = RandomSingleton.instance;
	   private double[]  measurement  = {0.0,0.0,0.0};					// Magnetic field in Device Co-ordinate system
	   private double[][] R = {{0.0,0.0,0.0},{0.0,0.0,0.0},{0.0,0.0,0.0}};	      // Co-variance Matrix for Vector Gaussian
	   private double [] mTrueMeasurement = {0.0,0.0,0.0};					// Magnetic field in Global co-ordinate system
	   private float[] mRV = {0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f};	// Rotation Vector for local Rotation method used during each update location call.  
	   private double[] position  = {0.0,0.0,0.0};						// Magnetic field vector at any position (x,y) of the particles estimated from Interpolation function.
	   protected MapGenerator magneticmapx,magneticmapy,magneticmapz;    // magnetic field map instances for x,y,z axes readings
	   protected Particle[] particles ;									   // particles
	   protected double[] weightSums ;									// CDF of importance weights
	   private double magnitude;										 // magnitude to be written to Magnetic field log.
	   
	   //Control parameters 
	   private static int particleCount = DEFAULT_PARTICLE_COUNT;       
	   private static double msenseNoise = DEFAULT_SENSE_NOISE_THRESHOLD/1000.f;    // Step Noise for the particles used in dynamical equation.  Reduce this to a mimimum to reduce error in path length.  
	   private static double mstepNoise = DEFAULT_STEP_NOISE_THRESHOLD/1000.f;      // Sense noise used in Co-variance matrix of Vector Gaussian
	   private static double mturnNoise = DEFAULT_TURN_NOISE_THRESHOLD/1000.f;	    // Turn noise for particles used in dynamical equations
	   private static double mmse = 0.0;                                            // MMSE of the particles at each update location step from estimated position.

	   //log file writers	   
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
	   
  public class Pose
  { 	public double x;
    	public double y;
    	public double theta;
    	public Pose()
    		{ 	this.x = 0.0;
    			this.y = 0.0;
    			this.theta = 0.0;
    		}
    	public void set_pose(double x, double y, double theta)
    		{ 	this.x = x;
    			this.y = y;
    			this.theta = theta;	 
    		}
  }
  
  public class Particle
  { 	public double x;
    	public double y;
    	public double theta;
    	public double importance_weight;
    	public Particle()
    		{  	x  = minX + (maxX-minX)*rand.nextDouble() ; 		    
    			y  = minY + (maxY-minY)*rand.nextDouble() ; 
    			theta = 2*Math.PI*rand.nextDouble();
    			importance_weight = 1.0;
    		}	 
    	public Particle(Particle P)
    		{  	x  = P.x ; 
    			y  = P.y ; 
    			theta = P.theta;
    			importance_weight = P.importance_weight;
    		}	 
    	public void set_pos(double x_co, double y_co, double theta_co)
    		{  	x = x_co;
    			y = y_co;
    			theta = theta_co; 
    		}	 
    	public void move(double step_size, double rad_angle)
    		{   theta = rad_angle + mturnNoise*rand.nextGaussian();
    			theta %= (2*Math.PI);   
    			x = x + ((step_size + mstepNoise*(rand.nextGaussian())) * Math.sin(theta));                   // TODO : Use Particles Theta Value Here   // x %= maxX;     
    																										  // TODO : Use Map Bounds Here 
    			y = y + ((step_size + mstepNoise*(rand.nextGaussian())) * Math.cos(theta));                   // TODO : Use Velocity Dependent step_noise and turn_noise here                                       
    		}
    	public void SensorErrorModel(double[] Magnetic_Measurement)
    		{   importance_weight = 1.0;		      
    			if ((x > minX && x < maxX) && (y > minY && y < maxY))
    				{
    				    position[0] = magneticmapx.f.value(x,y);		// X-axis magnetic field of a particle look up from interpolation function. 
    					position[1] = magneticmapy.f.value(x,y);		// Y-axis magnetic field.
    					position[2] = magneticmapz.f.value(x,y);		// Z-axis magnetic field.
    					
    				//	Log.e(TAG,String.valueOf(position[0]));
    				//	Log.e(TAG,String.valueOf(position[1]));
    			    //	Log.e(TAG,String.valueOf(position[2]));
    			    	
    					//importance_weight *= Gaussian(position[0],msenseNoise,Magnetic_Measurement[0])*Gaussian(position[1],msenseNoise,Magnetic_Measurement[1])*Gaussian(position[2],msenseNoise, Magnetic_Measurement[2]);
    			    	importance_weight *= kin*VectorGaussian(position, msenseNoise, Magnetic_Measurement);       //Importance weight of the particle after measurement.
    				}  
    			else 
    				{ 	importance_weight = prb;		    	   
    				}
    		}  
    	public void normalizeImportanceWeight(double sum)
    		{   if (sum == 0) {
    		    			throw new IllegalArgumentException("Argument 'divisor' is 0");
    				}
    			 importance_weight /= sum;    
    		}
  }
   /**
    * Constructor for particle filtering class. 
    * Generates the magnetic field maps x,y,z axes using separate threads.
    * 
    * @param ctx
    */  
		public ParticleFiltering(Context ctx) {
			super(ctx);   
			//TODO: Incorporate the Barcode Scanner to identify the path way.
		    String json_obj = loadJSONFromAsset(ctx,"w012.json");
			magneticmapx = new MapGenerator(json_obj, 10,0);	
			magneticmapy = new MapGenerator(json_obj, 10,1);	
			magneticmapz = new MapGenerator(json_obj, 10,2);	
	        magneticmapx.run();
	        magneticmapy.run();
	        magneticmapz.run();
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
		    double mu_x_2 = Math.pow((mu-x),2.0); 		
	        return Math.exp(-(mu_x_2 / (sigma_2*2.0))) / Math.sqrt(2.0 * Math.PI * sigma_2) ;
	   }
	   
	   /**
	    * Vector Gaussian form for using with 'm' vector using the particle magnetic field along all 3-axes
	    * @param mu        particle magnetic field along 3 -axes. 
	    * @param sigma     sense noise for co-variance
	    * @param x         magnetic measurement vector
	    * @return          importance weight.
	    */
	   
	   public double VectorGaussian(double[] mu,double sigma,double[] x)
	   {    double sigma_2 = Math.pow(sigma, 2.0);
		    R[0][0] = sigma_2; 
	        R[1][1] = sigma_2;
	        R[2][2] = sigma_2;  
	        Matrix RM = new Matrix(R);
	        Matrix Rinv = RM.inverse();
	        double MU_ARR[][] = {{mu[0],mu[1],mu[2]}};
	        double Z_ARR[][] = {{x[0],x[1],x[2]}};
	        Matrix MU = new Matrix(MU_ARR);
	        Matrix Z = new Matrix(Z_ARR);
	      //double Rnorm = RM.det();
	        Matrix Z_MU = Z.minus(MU); 
	   
	      //Log.e(TAG,"posi[0] = " + String.valueOf(MU_ARR[0][0]) + "posi[1] = " + String.valueOf(MU_ARR[0][1]) + "posi[2] = " + String.valueOf(MU_ARR[0][2]));
	      //Log.e(TAG,"meas[0] = " + String.valueOf(Z_ARR[0][0]) + "meas[1] = " + String.valueOf(Z_ARR[0][1]) + "meas[2] = " + String.valueOf(Z_ARR[0][2])); 
	      //Log.e(TAG,"Z_MU[0] = " + String.valueOf(Z_ARR[0][0] - MU_ARR[0][0])+"Z_MU[1] = " + String.valueOf(Z_ARR[0][1] - MU_ARR[0][1]) + "Z_MU[2] = " + String.valueOf(Z_ARR[0][2] - MU_ARR[0][2]));
            
            Matrix exp_term = Z_MU.times(Rinv);
            Matrix exp_term_2  = exp_term.times(Z_MU.transpose());   
          //  Log.e(TAG,"Rnorm = " + String.valueOf(Rnorm));
           // Log.e(TAG,"exp_term_2 = " + String.valueOf(exp_term_2.det()));
              
	        return Math.exp(- 0.5 * exp_term_2.det()) ; // (Math.sqrt(Rnorm)*cons)  ;
	   }
	  
	   /**  init for the particle filter. Initialising the particles and weights CDF
	    *   Normalise the weights. Log the noise parameters.	     
	    */
	   @Override
	   protected void init() {
	        super.init();
			this.particles = new Particle[particleCount];
			this.weightSums = new double[particleCount + 1];
		    len = particleCount;
			for (int i = 0 ; i < len ; i++) {
				particles[i] = new Particle();
			}
			System.out.println(particles.length);
			normalizeWeights();
			try {
					String r = (String) (DateFormat.format("yyyy-MM-dd-hh-mm-ss", new java.util.Date()) );
					String logFileBaseName = "pfLog." + r;
					mNoiseFileWriter = new FileWriter(new File(SAMPLES_DIR, logFileBaseName + ".noise.csv"));
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
			this.magnitude= Math.sqrt(values[0]*values[0] + values[1]*values[1] + values[2]*values[2]);
			this.measurement[0] = values[0];
			this.measurement[1] = values[1];
			this.measurement[2] = values[2];
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
		 *  Resamples all important particles. Predicts the current Location using the mean of the particle density. 
		 */
		@Override
		public void updateLocation(double step_size, double rad_angle) {
			System.out.println("update");
			Particle[] next = new Particle[particles.length];
			len = particles.length;
			mRV = mSensorLifecycleManager.getRotationMatrix();						
			updateTrueMag();
			
			for (int i = 0 ; i< len; i++) {
				 next[i] = selectParticleAndCopy();
				/*action.selectActionHypothesis(rand);
				 TODO: should combine the prevAction and the supplied action (and maybe include the association action)
                 ActionModel averageAction = prevAction.average(action);*/   				
				next[i].move(step_size,rad_angle);                        // TODO: Should order move and sense			
				next[i].SensorErrorModel(mTrueMeasurement);
			    //next[i].updateMap( nextPose, observations, sensorImageGen, dt, sensorErrorModel, processError, // assoc,//useKnownDataAssociations,lmGen);
			}			
			
			particles = next;
			normalizeWeights();
			mmse = minimumMeanSquareDistance();
			if(this.isLogging()) {
				try {
					mMMSEDistanceFileWriter.write("" + particleCount + "," + mmse + "," + getLocation()[0]  + "," + getLocation()[1] +"\n");
				} catch (IOException e) {
					Log.e(TAG, "Log file write for MMSEDistance failed!!!\n", e);
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}			
		}
        
		/** Resampling the particles after measurement probability is used to weight all the particles and normalise all the particle weights.
		 *  It gives importance to most preferred particles. 
		 * @return   particle with replacement.
		 */
		private Particle selectParticleAndCopy() {
			sel = rand.nextDouble();
		    index = Arrays.binarySearch(weightSums, sel);
			if (index < 0) {
				index = - (index + 1);
			}
			if (index > 0) {
				index--;
			}
			return new Particle(particles[index]);
		}

		/** Helper funtion for Normalize weights. 
		 * @return
		 */
		private double calculateSums() {
		    totalSum = 0.0;
		    len = particles.length;
			for (int i = 0; i < len; i++) {
				totalSum += particles[i].importance_weight;
			}
			return totalSum;
		}
        
		
		/**  Normalise weights if the total sum is not zero or weight all particles to 1.0                   
		*/
		private void normalizeWeights() {
	      sum = calculateSums();
		  if (sum == 0.0) {
			  System.out.println(" Sum of weights is zero. Treat all particles equally.");
			  len = particles.length;
			  for (int i = 0; i <len ; i++) {
				   particles[i].importance_weight = 1.0;
				}
				normalizeWeights();
			}
		     len = particles.length;
			for (int i = 0; i <len ; i++) {
				particles[i].normalizeImportanceWeight(sum);
			}
			calculateFilterArray();
		}
		
		/**  weightSums CDF Generation.		  
		 */
		private void calculateFilterArray() {
			this.weightSums[0] = 0.0;
		    len = particles.length;
		    value = 0.0;
			for (int i = 0; i < len; i++) {
			    value = this.particles[i].importance_weight;
				this.weightSums[i + 1] = this.weightSums[i] + value;
			}
			total = this.weightSums[this.weightSums.length-1];                        // TO DO : use Dependance on previous Filter weight too..
			len = weightSums.length;
			for (int i = 0 ; i < len; i++) {
				weightSums[i] /= total;
			}
		}
		
		/** calculates Minimum Mean Square distance of particles from predicted location. This shows the convergence of particles. 
		 * @return  MMSE of the particles from the estimated particles.
		 */
		public double minimumMeanSquareDistance()
		{  sum = 0.0; err = 0.0; dx = 0.0; dy = 0.0;		
		   Particle r = getParticleEstimate();
		   len = particles.length;
		   for (int i = 0; i <len ; i++)  // calculate mean error
			{  dx = (particles[i].x - r.x);
	           dy = (particles[i].y - r.y);
	           err = Math.sqrt(dx * dx + dy * dy);
	           sum += err;
			}
		   return (sum/particles.length);
		}
		
		/**	Predicts the most probable particle as the Current location Estimate. 
		 * @return  The particle with highest Importance weight.
		 */
		public Particle getBestMap() {
			Particle rc = particles[0];
			best = rc.importance_weight;
		    currentWeight = 0.0;
		    len = particles.length;
			for (int i = 0; i <len ; i++) {
				currentWeight = particles[i].importance_weight;
				if (currentWeight > best) {
					rc = particles[i];
					best = currentWeight;
				}
			}
			setLocation(rc.x,rc.y);
			return rc;
		}
		
		/** Calculate the mean of all particles based upon importance weights.  
		 * @return  The Importance weight based mean estimate of all the particles
		 */
		public Particle getParticleEstimate(){
			currentWeight = 0.0; xp =0.0 ; yp = 0.0;
			len = particles.length;
			for (int i = 0; i <len ; i++) {
				  currentWeight = particles[i].importance_weight;
                  xp += currentWeight*particles[i].x;
                  yp += currentWeight*particles[i].y;
				}
		    Particle pe = new Particle();
		    pe.set_pos(xp,yp,0.0);
			setLocation(xp,yp);
			return pe;
		}
		
		/** The True Magnetic field magnitude inGlobal co-ordinate system is calculated from Rotation Matrix obtained from Sensor life cycle manager. 
		 */
		private void updateTrueMag() {
		//	Log.e(TAG,"measurement[0]= " + String.valueOf(measurement[0]));
		//	Log.e(TAG,"measurement[1]= " + String.valueOf(measurement[1]));
		//	Log.e(TAG,"measurement[2]= " + String.valueOf(measurement[2])); 
		      
			mTrueMeasurement[0] = mRV[0] * measurement[0] + mRV[1] * measurement[1] + mRV[2] * measurement[2];
			mTrueMeasurement[1] = mRV[3] * measurement[0] + mRV[4] * measurement[1] + mRV[5] * measurement[2];
			mTrueMeasurement[2] = mRV[6] * measurement[0] + mRV[7] * measurement[1] + mRV[8] * measurement[2];
			
		//	Log.e(TAG,"TrueMeasurement[0]= " + String.valueOf(mTrueMeasurement[0]));
		//	Log.e(TAG,"TureMeasurement[1]= " + String.valueOf(mTrueMeasurement[1]));
		//	Log.e(TAG,"TrueMeasurement[2]= " + String.valueOf(mTrueMeasurement[2])); 

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
				mAccelLogFileWriter = new FileWriter(new File(SAMPLES_DIR, logFileBaseName + ".accel.csv"));
				mMMSEDistanceFileWriter = new FileWriter(new File(SAMPLES_DIR, logFileBaseName + ".mmse.csv"));
				mMagLogFileWriter = new FileWriter(new File(SAMPLES_DIR, logFileBaseName + ".mag.csv"));
				mStepLogFileWriter = new FileWriter(new File(SAMPLES_DIR, logFileBaseName + ".pfsteps.csv"));
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
			len =particles.length;
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
		
	
/*	private static final Comparator mapFitness = new Comparator() {
			/** 
			 * Fitness is an approximation of p(z|x,z,m)
			 * Larger values are better.
			 * Values are normalized and selection using monte-carlo selection.
			 */
/*			public int compare(Object o1, Object o2) {
				Particle map1 = (Particle) o1;
				Particle map2 = (Particle) o2;
				if (map1.getImportanceWeight() < map2.getImportanceWeight()) {
					return -1;
				} else if (map1.getImportanceWeight() > map2.getImportanceWeight()) {
					return 1;
				} else {
					return 0;
				}
			}
		};

		private void sortParticles() {
			System.out.println(particles.length);
			sortedParticles = new Particle[particles.length];
			System.arraycopy(particles, 0, sortedParticles, 0, particles.length);
//			 sortedParticles = (LandmarkMap[])particles.clone();
			Arrays.sort(sortedParticles, particleFitnessComparator);
			System.out.println(particles.length);
			System.out.println(weightSums.length);
			System.out.println(sortedParticles.length);
			
		}
	
		private static final Comparator<Particle> particleFitnessComparator = new Comparator<Particle>() {
			public int compare(Particle o1, Particle o2) {
				double w1 = o1.getImportanceWeight();
				double w2 = o2.getImportanceWeight();
				if( w1 == w2 ) return 0;
				return (w1 < w2)?1:-1;
			}
		};
      
		*/
}
;