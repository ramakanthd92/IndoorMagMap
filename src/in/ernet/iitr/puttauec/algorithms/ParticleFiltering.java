package in.ernet.iitr.puttauec.algorithms;

import in.ernet.iitr.puttauec.sensorutil.MapGenerator;
import in.ernet.iitr.puttauec.sensorutil.RandomSingleton;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;

public class ParticleFiltering extends DeadReckoning {
	   private static final Random rand = RandomSingleton.instance;
	   private static double  minX  = 0.0  ;  //Double.MAX_VALUE; 
	   private static double  maxX  = 3.0 ;   //-Double.MAX_VALUE;
	   private static double  minY  = 0.0 ;   //Double.MAX_VALUE;
	   private static double  maxY  = 50.0 ;  //-Double.MAX_VALUE;
	   private static double  measurement  = 0.0;
	   private static final String TAG = "ParticleFilterReckoning";
	   
	   public static int DEFAULT_PARTICLE_COUNT = 700; //1000 //2000
	   public static int DEFAULT_STEP_NOISE_THRESHOLD = 700; // 400  //600 //800 //1000 //1500 
	   public static int DEFAULT_SENSE_NOISE_THRESHOLD = 5000; //2000 //10000  //15000
			 
       protected MapGenerator magneticmap ; 
	   protected Particle[] particles ;
	   protected Particle[] sortedParticles;
	   protected double[] weightSums ;
		
	   private static int particleCount = DEFAULT_PARTICLE_COUNT;       
	   private static double msenseNoise = DEFAULT_STEP_NOISE_THRESHOLD/1000.f;       
	   private static double mstepNoise = DEFAULT_SENSE_NOISE_THRESHOLD/1000.f;        
	   private static double mturnNoise = 0.50;	  
	   
	   private static final double INIT_SD_X = 1.0;
	   private static final double INIT_SD_Y = 1.0;
	   private static String file = "0";  
	   private double mmse = 0.0;
	
	   
	   public static int count = 0;
	
	   private FileWriter mNoiseFileWriter;
	   private FileWriter mMagLogFileWriter;
	   private FileWriter mMMSEDistanceFileWriter;
	   
	   //@Override
		public ParticleFiltering(Context ctx) {
			super(ctx);                                                  //TODO: Incorporate the Barcode Scanner to identify the path way. 
		/*	synchronized(this)
			{String file_name = new String();
			 int Nval = 0;
			switch(file)
			 {case 0: 
			  { setmaxX(3.0);
			    Nval =4;
			    file_name = new String("w0.json");
			    System.out.print("0");
			    break;
			  }
			 case 1: 
			  { setmaxX(2.0);
			    Nval =3;
			    file_name = new String("w1.json");
			    break;
			  } 
			 case 2:
			   { setmaxX(2.0);
			     Nval =3;
		         file_name = new String("w2.json");
		         break;
		       }
			 case 3:
			  { Nval =10;
				file_name = new String("w012.json");
			    setmaxX(15.0);
			    break;
			  }
			 default:
			  { file_name = "w012.json";
			  }
			}
			System.out.print(maxX);*/
		    String json_obj =loadJSONFromAsset(ctx,"w012.json");
			magneticmap = new MapGenerator(json_obj, 10);	
		 }
		/*	for(double i= 0.0; i <15.0; i+=0.5)
				{for(double j= 0.0; j <50.0; j+=0.5)
				   {System.out.print(magneticmap.f.value(i,j));
				    System.out.print("   ");
				   }	
				  System.out.println("");
				}*/
		//}
		
		public static String loadJSONFromAsset(Context context,String filename) {
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
	   		
	   public class Pose
	   { private double x;
	     private double y;
	     private double theta;
	     public Pose()
	     { this.x = 0.0;
	       this.y = 0.0;
	       this.theta = 0.0;
	     }
	     public void set_pose(double x, double y, double theta)
	     { this.x = x;
	       this.y = y;
	       this.theta = theta;	 
	     }
	     public double get_X()
	     { return x;	 
	     }
	     public double get_Y()
	     { return y;
	     }
	     public double get_Theta()
	     { return theta; 
	     }
	   }
	   
	   public class Path
	   {	private Pose current;
			private Path previous;
		    private double measurement;
			public Path(Pose pose, Path prev, double meas)
		    { this.current = pose;
		      this.previous = prev;
		      this.measurement = meas;
		    }
			public Path previousPath() {
				return previous;
			}
			public final Pose[] getPoseArray() {
				ArrayList<Pose> poseList = new ArrayList<Pose>();
				Path curr = this;
				while(curr != null) {
					 poseList.add(current);
					 curr = curr.previous;
				}
				Pose[] rc = new Pose[poseList.size()];
				poseList.toArray(rc);
				return rc;
			}   
			
			public Pose getLatestPose()
		     {  return current; 
		     }
	   }	   
	   public class Particle
	   { private static final String KEY_X = "X";
	     private static final String KEY_Y = "Y";  
	     private double x;
	     private double y;
	     private double theta;
		 private double importance_weight;
		 public Particle()
		 {  x  = minX + (maxX-minX)*rand.nextDouble() ; 		    
		    y  = minY + (maxY-minY)*rand.nextDouble() ; 
		    theta = 2*Math.PI*rand.nextDouble();
		    importance_weight = 1.0;
		 }
		 
		 public Particle(Particle P)
		 {  x  = P.x ; 
		    y  = P.y ; 
		    theta = P.theta;
		    importance_weight = P.importance_weight;
		 }
		 
		 public void set_pos(double x_co, double y_co, double theta_co)
		 {  x = x_co;
		    y = y_co;
		    theta = theta_co; 
		 }
		 
		 
		 public void move(double step_size, double rad_angle)
		 {    theta = rad_angle + mturnNoise*rand.nextGaussian();
		 	  theta %= (2*Math.PI);   
			  x = x + ((step_size + mstepNoise*(rand.nextGaussian())) * Math.sin(theta));                   // TO DO : Use Particles Theta Value Here   // x %= maxX;     
			  																								// To DO : Use Map Bounds Here 
		      y = y + ((step_size + mstepNoise*(rand.nextGaussian())) * Math.cos(theta));                   // TO DO : Use Velocity Dependent step_noise and turn_noise here                     
                       
         }
		 
		 public void SensorErrorModel(double Magnetic_Measurement)
		 {     importance_weight = 1.0;		      
		       if ((x > minX && x < maxX) && (y > minY && y < maxY))
		       {  double position_magnitude = magneticmap.f.value(x,y);	
		       	  importance_weight *= Gaussian(position_magnitude,msenseNoise,Magnetic_Measurement);
		       }  
		       else 
		       { importance_weight = 0.001;		    	   
		       }
		 } 
		 public double getImportanceWeight()
		 {   return importance_weight;}
		
		 public void setImportanceWeight(double d)
		 {   importance_weight = d;}
		
		 public void normalizeImportanceWeight(double sum)
		 {   importance_weight /= sum;}
		 
		 public Pose getPose()
		 {   Pose pose = new Pose();
		     pose.set_pose(x,y,theta);  
			 return pose; }
		 public double get_X()
	     { return x;	 
	     }
	     public double get_Y()
	     { return y;
	     }
	     public void set_X(double X)
	     { x = X;	 
	     }
	     public void set_Y(double Y)
	     { y = Y;
	     }
	     public double get_Theta()
	     { return theta; 
	     }
	   }
	   
	   public double Gaussian(double mu,double sigma,double x)
	   {      
		    double sigma_2 = Math.pow(sigma, 2.0);
		    double mu_x_2 = Math.pow((mu-x),2.0); 		
	        return Math.exp(-(mu_x_2 / (sigma_2*2.0))) / Math.sqrt(2.0 * Math.PI * sigma_2) ;
	   }
	  
	   @Override
	   protected void init() {
	       super.init();
			this.particles = new Particle[particleCount];
			this.weightSums = new double[particleCount + 1];
			for (int i = 0; i < particles.length; i++) {
				particles[i] = new Particle();
			}
			normalizeWeights();
			sortParticles();
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
		public void setStartPos(float x, float y) {
			super.setStartPos(x, y);
		}
		
		@Override
		public void onMagneticFieldUpdate(float[] values, long deltaT,
				long timestamp) {
			super.onMagneticFieldUpdate(values, deltaT, timestamp);
			//this.measurement = Math.sqrt(values[0]*values[0] + values[1]*values[1] + values[2]*values[2]);
			this.measurement = values[2];
			if(this.isLogging()) {
				try {
					mMagLogFileWriter.write("" + timestamp + "," + deltaT + "," + values[0] + ","+ values[1] +","+ values[2] + "," + measurement + "\n");
				} catch (IOException e) {
					Log.e(TAG, "Log file write for Magnetic Field failed!!!\n", e);
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		}

		@Override
		public void updateLocation(double step_size, double rad_angle) {
			// localLandmarks are with respect to the robot
			System.out.println("update");
			Particle[] next = new Particle[size()];
			for (int i = 0; i < next.length; i++) {
				// select with a preference for better paths 
				double rsel;
				next[i] = selectParticleAndCopy();
				double lastWeight = next[i].getImportanceWeight();
				Pose currentPose = next[i].getPose();
				/* action.selectActionHypothesis(rand);
				 TODO: should combine the prevAction and the supplied action (and maybe include the association action)
                 ActionModel averageAction = prevAction.average(action);
                */   						
				next[i].move(step_size,rad_angle);                                 // TODO: Should order move and sense  
			    next[i].SensorErrorModel(measurement);
			//  next[i].updateMap( nextPose, observations, sensorImageGen, dt, sensorErrorModel, processError, // assoc,//useKnownDataAssociations,lmGen);
			}
			particles = next;
			normalizeWeights();
		//	sortParticles();
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
        
		public double minimumMeanSquareDistance()
		{  double dx = 0.0, dy = 0.0;
		   double sum = 0.0, err = 0.0; 
		   Particle r = getParticleEstimate(); 
		   for ( int i = 0; i < particles.length; i++)  // calculate mean error
			{  dx = (particles[i].get_X() - r.get_X());
	           dy = (particles[i].get_Y() - r.get_Y());
	           err = Math.sqrt(dx * dx + dy * dy);
	           sum += err;
			}
		   return (sum/particles.length);
		}
		public Particle getMedian() {
			Particle rc = particles[0];
			double best = rc.getImportanceWeight();
			for (int i = 1; i < particles.length; i++) {
				double currentWeight = particles[i].getImportanceWeight();
				if (currentWeight > best) {
					rc = particles[i];
					best = currentWeight;
				}
			}
			setLocation(rc.get_X(),rc.get_Y());
			return rc;
		}
		
		public Particle getParticleEstimate(){
			double xp = 0.0;
			double yp = 0.0;
			for (int i = 1; i < particles.length; i++) {
				double currentWeight = particles[i].getImportanceWeight();
                  xp += currentWeight*particles[i].get_X();
                  yp += currentWeight*particles[i].get_Y();
				}
		    Particle pe = new Particle();
		    pe.set_pos(xp,yp,0.0);
			setLocation(xp,yp);
			return pe;
		}

		public int size() {
			return particles.length;
		}
		
		public Particle getparticle(int i) {
			return particles[i];
		}

		public Particle getSortedParticle(int i) {
			return sortedParticles[i];
		}

		private Particle selectParticleAndCopy() {
			double sel = rand.nextDouble();
			int index = Arrays.binarySearch(weightSums, sel);
			if (index < 0) {
				index = - (index + 1);
			}
			if (index > 0) {
				index--;
			}
			return new Particle(particles[index]);
		}

		private double calculateSums() {
			double totalSum = 0.0;
			for (int i = 0; i < this.particles.length; i++) {
				totalSum += this.particles[i].getImportanceWeight();
			}
			return totalSum;
		}
        
		private void normalizeWeights() {
			double sum = calculateSums();
		  if (sum == 0.0) {
				System.out.println(" Sum of weights is zero. Treat all particles equally.");
			  for (int i = 0; i < this.particles.length; i++) {
					this.particles[i].setImportanceWeight(1.0);
				}
				normalizeWeights();
			}
			
			for (int i = 0; i < this.particles.length; i++) {
				this.particles[i].normalizeImportanceWeight(sum);
			}
			calculateFilterArray();
		}
		
		private void calculateFilterArray() {
			this.weightSums[0] = 0.0;
			for (int i = 0; i < this.particles.length; i++) {
				double value = this.particles[i].getImportanceWeight();
				this.weightSums[i + 1] = this.weightSums[i] + value;
			}
			final double total = this.weightSums[this.weightSums.length-1];                        // TO DO : use Dependance on previous Filter weight too..
			for (int i = 0; i < this.weightSums.length; i++) {
				this.weightSums[i] /= total;
			}
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
		
		
		@Override
		public void setmStartX(float mStartX) {
			super.setmStartX(mStartX);
			for(int i = 0; i < particleCount; ++i) {
				particles[i].x = mStartX + (float)(INIT_SD_X*rand.nextGaussian());
			}
		}
		
		@Override
		public void setmStartY(float mStartY) {
			super.setmStartY(mStartY);
			for(int i = 0; i < particleCount; ++i) {
				particles[i].y = mStartY + (float)(INIT_SD_Y*rand.nextGaussian());
			}
		}			
		public int getParticleCount () {
		   return particleCount;
		}
	    
		public double getSenseNoise () {
			return msenseNoise;
		}
		
		public double getStepNoise () {
			return  mstepNoise;
		}
		
		public double getMMSE() {
			return  mmse;
		}
		
		public void setParticleCount (double pc) {
			   particleCount = (int) pc;
			}
		    
			public void setSenseNoise (double sen) {
				 msenseNoise = sen;
			}
			
			public void setStepNoise (double ste) {
				mstepNoise = ste;
			}
	
	private static final Comparator mapFitness = new Comparator() {
			/** 
			 * Fitness is an approximation of p(z|x,z,m)
			 * Larger values are better.
			 * Values are normalized and selection using monte-carlo selection.
			 */
			public int compare(Object o1, Object o2) {
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
      
		
}
;