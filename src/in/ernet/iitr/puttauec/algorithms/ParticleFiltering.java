package in.ernet.iitr.puttauec.algorithms;

import in.ernet.iitr.puttauec.sensors.SensorLifecycleManager;
import in.ernet.iitr.puttauec.sensorutil.RandomSingleton;
import in.ernet.iitr.puttauec.algorithms.DeadReckoning;
import in.ernet.iitr.puttauec.ui.LaunchActivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;

public class ParticleFiltering extends DeadReckoning {
	   private static final Random rand = RandomSingleton.instance;
	   static double				minX  = 0  ;  //Double.MAX_VALUE;
	   static double				maxX  = 2 ;   //-Double.MAX_VALUE;
	   static double				minY  = 0 ;   //Double.MAX_VALUE;
	   static double				maxY  = 50 ;  //-Double.MAX_VALUE;
	   private static double  measurement  = 0.0;
	   private static final String TAG = "ParticleFilterReckoning";

	   private Particle[] particles = new Particle[0];
	   private Particle[] sortedParticles = new Particle[0];
	   private double[] weightSums = new double[0];
		
	   private static int particleCount = 200;        //500  //1000  
	   private FileWriter mNumCandidatesFile;
		
	   private static final double INIT_SD_X = 0.25;
	   private static final double INIT_SD_Y = 0.25;
	   
	   public static int count = 0;
		
	   private FileWriter mMagLogFileWriter;
	   
	   //@Override
		public ParticleFiltering(Context ctx) {
			super(ctx);
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
		 private double step_noise;
		 private double turn_noise;
		 private double sense_noise;
		 private double importance_weight;
		 public Particle()
		 {  x  = minX + (maxX-minX)*rand.nextDouble() ; 
		    y  = minY + (maxY-minY)*rand.nextDouble() ; 
		    theta = 2*Math.PI*rand.nextDouble();
		    step_noise = 0.0 ; 
		    turn_noise = 0.0; 
		    sense_noise = 0.0; 
		    importance_weight = 1.0;
		 }
		 
		 public Particle(Particle P)
		 {  x  = P.x ; 
		    y  = P.y ; 
		    theta = P.theta;
		    step_noise = P.step_noise; 
		    turn_noise = P.turn_noise; 
		    sense_noise = P.sense_noise ;
		    importance_weight = P.importance_weight;
		 }
		 
		 public void set_pos(double x_co, double y_co, double theta_co)
		 {  x = x_co;
		    y = y_co;
		    theta = theta_co; 
		 }
		 
		 public void set_noise(double step_no, double turn_no, double sense_no)
		 {  step_noise = step_no;
		    turn_noise = turn_no;
		    sense_noise = sense_no;
		 }
		 
		 public void move(double step_size, double rad_angle)
		 {    x = x + ((step_size + step_noise*(rand.nextGaussian())) * Math.cos(rad_angle));                   // TO DO : Use Particles Theta Value Here
		      y = y + ((step_size + step_noise*(rand.nextGaussian())) * Math.sin(rad_angle));                   // TO DO : Use Velocity Dependent step_noise and turn_noise here  
              theta = rad_angle + turn_noise*rand.nextGaussian();
              theta %= (2*Math.PI);                                                                             // To DO : Use Map Bounds Here
         }
		 
		 public void SensorErrorModel(double Magnetic_Measurement)
		 {     importance_weight = 1.0;
		       double position_magnitude = LaunchActivity.getMagneticField(x,y);
		       importance_weight *= Gaussian(position_magnitude,sense_noise,Magnetic_Measurement);
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
		    System.out.print("  pi ");
		    super.init();
			this.particles = new Particle[particleCount];
			System.out.println(particles.length);
			for (int i = 0; i < particles.length; i++) {
				particles[i] = new Particle();
			}
			this.weightSums = new double[particleCount + 1];
			normalizeWeights();
			sortParticles();
			//mRandom = new Random();
			try {
				if(mNumCandidatesFile != null) {
					mNumCandidatesFile.flush();
					mNumCandidatesFile.close();
					mNumCandidatesFile = null;
				}
			} catch(IOException e) {
				e.printStackTrace();
				Log.e(TAG, "Couldn't close numCandidates file!",e);
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
			this.measurement = Math.sqrt(values[0]*values[0] + values[1]*values[1] + values[2]*values[2]);
			if(this.isLogging()) {
				try {
					mMagLogFileWriter.write("" + timestamp + "," + deltaT + "," + values[0] + ","+ values[1] +","+ values[2] + "," + measurement + "\n");
				} catch (IOException e) {
					Log.e(TAG, "Log file write for acceleration failed!!!\n", e);
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		}

		@Override
		public void updateLocation(double step_size, double rad_angle) {
			// localLandmarks are with respect to the robot
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
				// TODO: Should order move and sense  
				next[i].move(step_size,rad_angle);
			    next[i].SensorErrorModel(measurement);		
			  //  next[i].updateMap( nextPose, observations, sensorImageGen, dt, sensorErrorModel, processError, // assoc,//useKnownDataAssociations,lmGen);
			}
			particles = next;
			// normalize the particle weights
			normalizeWeights();
			sortParticles();
			getBestMap();					
		}

		public void getBestMap() {
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
		    count++;
		    System.out.println(size());
			if (sum == 0.0) {
				// sum of weights is zero. Treat all particles equally.
				System.out.print(sum);
				System.out.println(" Sum of weights is zero. Treat all particles equally.");
				System.out.println(count);				
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
			sortedParticles = new Particle[particles.length];
			System.arraycopy(particles, 0, sortedParticles, 0, particles.length);
//			sortedParticles = (LandmarkMap[])particles.clone();
			Arrays.sort(sortedParticles, particleFitnessComparator);
		}
		
		private static final Comparator<Particle> particleFitnessComparator = new Comparator<Particle>() {
			public int compare(Particle o1, Particle o2) {
				double w1 = o2.getImportanceWeight();
				double w2 = o2.getImportanceWeight();
				if( w1 == w2 ) return 0;
				return (w1 < w2)?1:-1;
			}
		};
        
		@Override
		public void startLogging() {
			try {
				String r = (String) (DateFormat.format("yyyy-MM-dd-hh-mm-ss", new java.util.Date()) );
				String logFileBaseName = "pfLog." + r;
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
				mMagLogFileWriter.flush();
				mMagLogFileWriter.close();
				mStepLogFileWriter.flush();
				mStepLogFileWriter.close();
			} catch (IOException e) {
				Log.e(TAG, "Flushing and closing log files failed!", e);
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}@Override
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
}
