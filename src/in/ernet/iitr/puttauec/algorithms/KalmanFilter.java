package in.ernet.iitr.puttauec.algorithms;

import in.ernet.iitr.puttauec.sensorutil.MatrixLib;
import java.util.ArrayList;
import java.util.Stack;
import java.lang.Double;
public class KalmanFilter implements IAngleAlgorithm
 {  
	public class Filter 
     {   private ArrayList<Double> a;
         private ArrayList<Double> b;

         //default Filter
          public Filter()
          {  b = new ArrayList<Double>();
             b.add(0.002899695497431);
             b.add(-0.006626465760968);
             b.add(0.004033620976099);
             b.add(0.004033620976099);
             b.add(-0.006626465760968);
             b.add(0.002899695497431);

             a = new ArrayList<Double>();
             a.add(1.000000000000000);
             a.add(-4.229081817661462);
             a.add(7.205853343227314);
             a.add(-6.177477993982333);
             a.add(2.662714482809827);
             a.add(-0.461394312968222);
         }

         public Filter(ArrayList<Double> a, ArrayList<Double> b)
         {
             this.a = a;
             this.b = b;
         }

         public ArrayList<Double> Applyfilter (ArrayList<Double> x)
         {
             int ord = a.size() - 1;
             int np = x.size() - 1;
             
             if (np < ord)
             {
                 for (int k = 0; k < ord - np; k++)
                     x.add(0.0);
                 np = ord;
             }
          
             ArrayList<Double> y = new ArrayList<Double>();
             
             for (int k = 0; k < np + 1; k++)
             {
                 y.add(0.0);
             }
             int i, j;
             
             y.set(0, b.get(0)*x.get(0));
             for (i = 1; i < ord + 1; i++)
             {
                 y.set(i,0.0);
                 for (j = 0; j < i + 1; j++)
                    {  
                        y.set(i, y.get(i) + b.get(j) * x.get(i - j));
                    }
                 for (j = 0; j < i; j++)
                   {
                	  y.set(i, y.get(i) - a.get(j + 1) * y.get(i - j - 1));
                   }
                     
             }
             /* end of initial part */
             for (i = ord + 1; i < np + 1; i++)
             {
            	 y.set(i,0.0);
                 for (j = 0; j < ord + 1; j++)
                	 y.set(i, y.get(i) + b.get(j) * x.get(i - j));
                 for (j = 0; j < ord; j++)
                	 y.set(i, y.get(i) - a.get(j + 1) * y.get(i - j - 1));
             }
             return y;
         }
     }

        private double gyroOffRoll = 0;//-0.3866;
        private double gyroOffPitch = 0;//-3.5042;
        private double gyroOffYaw = 0;//2.7279;
        private double gyroVarX = 10;//-0.3866;
        private double gyroVarY = 10;//-3.5042;
        private double gyroVarZ = 10;//2.7279;
        
     //   private Parameters param;
     //   private double dt;

        //private double sigmaRoll = Math.pow((0.5647 / 180 * Math.PI), 2);           //the variance of the roll measure
        //private double sigmaPitch = Math.pow((0.5674 / 180 * Math.PI), 2);          //the variance of the pitch measure
        //private double sigmaYaw = Math.pow((0.5394 / 180 * Math.PI), 2);            //the variance of the yaw measure

        //private double sigmaRoll = Math.pow((0.3 / 180 * Math.PI), 2);           //the variance of the roll measure
        //private double sigmaPitch = Math.pow((0.3 / 180 * Math.PI), 2);          //the variance of the pitch measure
        //private double sigmaYaw = Math.pow((0.3 / 180 * Math.PI), 2);            //the variance of the yaw measure

        public double q_filt1,q_filt2,q_filt3,q_filt4;       //quaternioni filtrati
        public int last;

        MatrixLib weOld;

        public ArrayList<Double> aAcc;
        public ArrayList<Double> bAcc;
        public ArrayList<Double> aMagn;
        public ArrayList<Double> bMagn;

        public ArrayList<Double> AccObservX;
        public ArrayList<Double> AccObservY;
        public ArrayList<Double> AccObservZ;

        public ArrayList<Double> AccFiltX;
        public ArrayList<Double> AccFiltY;
        public ArrayList<Double> AccFiltZ;

        public ArrayList<Double> MagnObservX;
        public ArrayList<Double> MagnObservY;
        public ArrayList<Double> MagnObservZ;

        public ArrayList<Double> MagnFiltX;
        public ArrayList<Double> MagnFiltY;
        public ArrayList<Double> MagnFiltZ;


        //int dim = 4;
        double w_x_old = 0;
        double w_y_old = 0;
        double w_z_old = 0;


        MatrixLib F = new MatrixLib(4, 4);
        MatrixLib H = new MatrixLib(MatrixLib.Identity(4));
        MatrixLib Q = new MatrixLib(4, 4);
        MatrixLib R = new MatrixLib(MatrixLib.Identity(4));

        //the state of the filter is composed of a quaternion, where the real part is in the first position
        MatrixLib state_filtered = new MatrixLib(4, 1);           //output of the filter. This is the update of the state
        MatrixLib state_predicted = new MatrixLib(4, 1);          //the predictiion of the state x(k|k-1), calculated from the gyro
        MatrixLib state_observed = new MatrixLib(4, 1);           //the observation calculated from the accelerometer and the magnetometer y(k). 

        MatrixLib P_predicted = new MatrixLib(4, 4);             //the variance of prediction of the state
        MatrixLib P_Update = new MatrixLib(4, 4);                 //the variance of the update of the state
        MatrixLib K = new MatrixLib(4, 4);                        //the gain of the filter

        Stack<double[]> MagnOsserv = new Stack<double[]>();
        Stack<double[]> AccOsserv = new Stack<double[]>();

        int countdata = 0;
        Filter magnFilter;
        Filter accFilter;

        public KalmanFilter()
        {
            //this.param = param;
            double sigmaRoll = Math.pow((gyroVarX / 180 * Math.PI), 2);           //the variance of the roll measure
            double sigmaPitch = Math.pow((gyroVarY / 180 * Math.PI), 2);          //the variance of the pitch measure
            double sigmaYaw = Math.pow((gyroVarZ / 180 * Math.PI), 2);            //the variance of the yaw measure
            
          //  dt = 1.0 / param.freq;  

            //Compute matrix Q (costant if the time interval is costant)
            Q.in_Mat[0][0] = sigmaRoll + sigmaPitch + sigmaYaw;
            Q.in_Mat[0][1] = -sigmaRoll + sigmaPitch - sigmaYaw;
            Q.in_Mat[0][2] = -sigmaRoll - sigmaPitch + sigmaYaw;
            Q.in_Mat[0][3] = sigmaRoll - sigmaPitch - sigmaYaw;
            Q.in_Mat[1][0] = -sigmaRoll + sigmaPitch - sigmaYaw;
            Q.in_Mat[1][1] = sigmaRoll + sigmaPitch + sigmaYaw;
            Q.in_Mat[1][2] = sigmaRoll - sigmaPitch - sigmaYaw;
            Q.in_Mat[1][3] = -sigmaRoll - sigmaPitch + sigmaYaw;
            Q.in_Mat[2][0] = -sigmaRoll - sigmaPitch + sigmaYaw;
            Q.in_Mat[2][1] = sigmaRoll - sigmaPitch - sigmaYaw;
            Q.in_Mat[2][2] = sigmaRoll + sigmaPitch + sigmaYaw;
            Q.in_Mat[2][3] = -sigmaRoll + sigmaPitch - sigmaYaw;
            Q.in_Mat[3][0] = sigmaRoll - sigmaPitch - sigmaYaw;
            Q.in_Mat[3][1] = -sigmaRoll - sigmaPitch + sigmaYaw;
            Q.in_Mat[3][2] = -sigmaRoll + sigmaPitch - sigmaYaw;
            Q.in_Mat[3][3] = sigmaRoll + sigmaPitch + sigmaYaw;

            //double scalar=System.Math.pow((dt/2),2);
            //Q=MatrixLib.ScalarMultiply(scalar,Q);

            //R = MatrixLib.ScalarMultiply(0.07, R);
            R = new MatrixLib(4, 4);
            R.in_Mat[0][0] = 0.1;
            R.in_Mat[0][1] = 0;
            R.in_Mat[0][2] = 0;
            R.in_Mat[0][3] = 0;

            R.in_Mat[1][0] = 0;
            R.in_Mat[1][1] = 0.1;
            R.in_Mat[1][2] = 0;
            R.in_Mat[1][3] = 0;

            R.in_Mat[2][0] = 0;
            R.in_Mat[2][1] = 0;
            R.in_Mat[2][2] = 0.1;
            R.in_Mat[2][3] = 0;

            R.in_Mat[3][0] = 0;
            R.in_Mat[3][1] = 0;
            R.in_Mat[3][2] = 0;
            R.in_Mat[3][3] = 0.1;

            //Initial conditions
            state_observed = newQuaternion(0.5, 0.5, 0.5, 0.5);
            state_filtered = newQuaternion(0.5, 0.5, 0.5, 0.5);
            state_predicted = newQuaternion(0.5, 0.5, 0.5, 0.5);
            P_Update = MatrixLib.ScalarMultiply(0.1,new MatrixLib(MatrixLib.Identity(4)));

            aAcc = new ArrayList<Double>();
            bAcc = new ArrayList<Double>();
            aMagn = new ArrayList<Double>();
            bMagn = new ArrayList<Double>();

            aAcc.add(1.0);
            aAcc.add(-2.9529);
            aAcc.add(2.9069);
            aAcc.add(-0.954);

            bAcc.add(0.000001597);
            bAcc.add(0.000004792);
            bAcc.add(0.000004792);
            bAcc.add(0.000001597);

            accFilter = new Filter(aAcc, bAcc);

            aMagn.add(1.0);
            aMagn.add(-1.73);
            aMagn.add(0.76);

            bMagn.add(0.0078);
            bMagn.add(0.0156);
            bMagn.add(0.0078);

            magnFilter = new Filter(aMagn, bMagn);

            AccObservX = new ArrayList<Double>();
            AccObservY = new ArrayList<Double>();
            AccObservZ = new ArrayList<Double>();
            AccFiltX = new ArrayList<Double>();
            AccFiltY = new ArrayList<Double>();
            AccFiltZ = new ArrayList<Double>();

            MagnObservX = new ArrayList<Double>();
            MagnObservY = new ArrayList<Double>();
            MagnObservZ = new ArrayList<Double>();
            MagnFiltX = new ArrayList<Double>();
            MagnFiltY = new ArrayList<Double>();
            MagnFiltZ = new ArrayList<Double>();

            weOld = new MatrixLib(4, 1);
            weOld = MatrixLib.ScalarMultiply(0, weOld);
     
        }

        public void update(double w_x, double w_y, double w_z, double a_x, double a_y, double a_z, double m_x, double m_y, double m_z, double dt)
        {
            double norm;
            MatrixLib temp;//=new MatrixLib(4,4);
            MatrixLib dq = new MatrixLib(4, 1);
            double mu;

            // normalise the accelerometer measurement
            norm = Math.sqrt(a_x * a_x + a_y * a_y + a_z * a_z);
            a_x /= norm;
            a_y /= norm;
            a_z /= norm;

            // normalise the magnetometer measurement
            norm = Math.sqrt(m_x * m_x + m_y * m_y + m_z * m_z);
            m_x /= norm;
            m_y /= norm;
            m_z /= norm;

            if (AccObservX.size() < 10)
            {
                AccObservX.add(a_x);
                AccObservY.add(a_y);
                AccObservZ.add(a_z);
            }
            else
            {
                AccObservX.remove(0);
                AccObservY.remove(0);
                AccObservZ.remove(0);

                AccObservX.add(a_x);
                AccObservY.add(a_y);
                AccObservZ.add(a_z);
            }
            if (MagnObservX.size() < 10)
            {
                MagnObservX.add(m_x);
                MagnObservY.add(m_y);
                MagnObservZ.add(m_z);
            }
            else
            {
                MagnObservX.remove(0);
                MagnObservY.remove(0);
                MagnObservZ.remove(0);
                MagnObservX.add(m_x);
                MagnObservY.add(m_y);
                MagnObservZ.add(m_z);

                //Filter stabilization
                AccFiltX = accFilter.Applyfilter(AccObservX);
                AccFiltY = accFilter.Applyfilter(AccObservY);
                AccFiltZ = accFilter.Applyfilter(AccObservZ);

                MagnFiltX = magnFilter.Applyfilter(MagnObservX);
                MagnFiltY = magnFilter.Applyfilter(MagnObservY);
                MagnFiltZ = magnFilter.Applyfilter(MagnObservZ);
            }

            if (countdata > 10)
            {   last = AccFiltX.size();
                a_x = AccFiltX.get(last-1);
                a_y = AccFiltY.get(last-1);
                a_z = AccFiltZ.get(last-1);
                last = MagnFiltX.size();
                m_x = MagnFiltX.get(last-1);
                m_y = MagnFiltY.get(last-1);
                m_z = MagnFiltZ.get(last-1);

                // normalise the accelerometer measurement
                norm = Math.sqrt(a_x * a_x + a_y * a_y + a_z * a_z);
                a_x /= norm;
                a_y /= norm;
                a_z /= norm;

                // normalise the magnetometer measurement
                norm = Math.sqrt(m_x * m_x + m_y * m_y + m_z * m_z);
                m_x /= norm;
                m_y /= norm;
                m_z /= norm;
            }

            countdata++;
            //dtt = DateTime.Now;

            //w_x = w_x ; //- param.gyroOffX;
            //w_y = w_y ;//+ param.gyroOffY;
            //w_z = w_z ;//- param.gyroOffZ;

            w_x = w_x * Math.PI / 180;
            w_y = w_y * Math.PI / 180;
            w_z = w_z * Math.PI / 180;

            w_x = (w_x + w_x_old) / 2;
            w_y = (w_y + w_y_old) / 2;
            w_z = (w_z + w_z_old) / 2;

            /*if (countdata > 11)
            {
                MatrixLib qUpdateOld = newQuaternion(q_filt1][q_filt2][q_filt3][q_filt4);
                MatrixLib dq1 = MatrixLib.ScalarMultiply(0.5, QuaternionProduct(qUpdateOld, newQuaternion(0][w_x, w_y, w_z)));
                MatrixLib we = MatrixLib.ScalarMultiply(2][QuaternionProduct(qUpdateOld, dq1));
                we = (we*dt + weOld);
                weOld = we;
                w_x = w_x - we[1][0];
                w_y = w_y - we[2][0];
                w_z = w_z - we[3][0];
            }*/


            //calculate the observation via the Gradient Descent method
            dq = MatrixLib.ScalarMultiply(0.5,(QuaternionProduct(state_observed, newQuaternion(0.0, w_x, w_y, w_z))));
            norm = Math.sqrt(dq.in_Mat[0][0] * dq.in_Mat[0][0] + dq.in_Mat[1][0] * dq.in_Mat[1][0] + dq.in_Mat[2][0] * dq.in_Mat[2][0] + dq.in_Mat[3][0] * dq.in_Mat[3][0]);
            //dq.in_Mat[0,0]/=norm;
            //dq.in_Mat[1,0]/=norm;
            //dq.in_Mat[2,0]/=norm;
            //dq.in_Mat[3,0]/=norm;
            mu = 10 * norm * dt;
            state_observed = GradientDescent(a_x, a_y, a_z, m_x, m_y, m_z, mu);

            //KALMAN FILTERING

            //F matrix computing
            F.in_Mat[0][0] = 1;
            F.in_Mat[0][1] = -dt / 2 * w_x;
            F.in_Mat[0][2] = -dt / 2 * w_y;
            F.in_Mat[0][3] = -dt / 2 * w_z;
            F.in_Mat[1][0] = dt / 2 * w_x;
            F.in_Mat[1][1] = 1;
            F.in_Mat[1][2] = dt / 2 * w_z;
            F.in_Mat[1][3] = -dt / 2 * w_y;
            F.in_Mat[2][0] = dt / 2 * w_y;
            F.in_Mat[2][1] = -dt / 2 * w_z;
            F.in_Mat[2][2] = 1;
            F.in_Mat[2][3] = dt / 2 * w_x;
            F.in_Mat[3][0] = dt / 2 * w_z;
            F.in_Mat[3][1] = dt / 2 * w_y;
            F.in_Mat[3][2] = -dt / 2 * w_x;
            F.in_Mat[3][3] = 1;

            //state prediction
            //state_filtered was computed at the previous step(if step is the first, then are the intial values).
            state_predicted = MatrixLib.Multiply(F, state_filtered);

            //calculate the variance of the prediction
            P_predicted = MatrixLib.Multiply(F, P_Update);
            P_predicted = MatrixLib.Add (MatrixLib.Multiply(P_predicted, MatrixLib.Transpose(F)), Q);

            //compute the gain of the filter K
            temp = MatrixLib.Add(MatrixLib.Multiply(MatrixLib.Multiply(H, P_predicted), MatrixLib.Transpose(H)), R);
            temp = MatrixLib.Inverse(temp);
            K = MatrixLib.Multiply(MatrixLib.Multiply(P_predicted, MatrixLib.Transpose(H)), temp);

            //update the state of the system (this is the output of the filter)
            temp = MatrixLib.Subtract(state_observed, MatrixLib.Multiply(H, state_predicted));
            state_filtered = MatrixLib.Add(state_predicted, MatrixLib.Multiply(K, temp));

            //compute the variance of the state filtered
            temp = MatrixLib.Subtract((new MatrixLib(MatrixLib.Identity(4))), MatrixLib.Multiply(K, H));
            P_Update = MatrixLib.Multiply(temp, P_predicted);

            norm = Math.sqrt(state_filtered.in_Mat[0][0] * state_filtered.in_Mat[0][0] + state_filtered.in_Mat[1][0] * state_filtered.in_Mat[1][0] + state_filtered.in_Mat[2][0] * state_filtered.in_Mat[2][0] + state_filtered.in_Mat[3][0] * state_filtered.in_Mat[3][0]);
            state_filtered = MatrixLib.ScalarDivide(norm, state_filtered);
            q_filt1 = state_filtered.in_Mat[0][0];
            q_filt2 = state_filtered.in_Mat[1][0];
            q_filt3 = state_filtered.in_Mat[2][0];
            q_filt4 = state_filtered.in_Mat[3][0];

            //TimeSpan duration = DateTime.Now - dtt;
            //time = duration.Milliseconds;

            //Console.WriteLine(time);
            w_x_old = w_x;
            w_y_old = w_y;
            w_z_old = w_z;
        }

        private MatrixLib GradientDescent(double a_x, double a_y, double a_z, double m_x, double m_y, double m_z, double mu)
        {
            int i = 0;
            double q1,q2,q3,q4;
            MatrixLib h = new MatrixLib(4, 1);     //magnetic field
            MatrixLib f_obb = new MatrixLib(6, 1);
            MatrixLib Jacobian = new MatrixLib(6, 4);
            MatrixLib temp;
            MatrixLib Df = new MatrixLib(4, 1);
            double norm;
            double bx, bz, by;
            MatrixLib result = new MatrixLib(4, 1);
            //m_x = m_x + 0.32;
            //m_y = m_y + 0.1;
            //oss=sf*reading+offset
          //  m_x = (param.magnSFX * m_x) + param.magnOffX;
          //  m_y = (param.magnSFY * m_y) + param.magnOffY;
          // m_z = (param.magnSFZ * m_z) + param.magnOffZ;

            q1 = state_observed.in_Mat[0][0];
            q2 = state_observed.in_Mat[1][0];
            q3 = state_observed.in_Mat[2][0];
            q4 = state_observed.in_Mat[3][0];

            while (i < 1)
            {
                //compute the direction of the magnetic field
                MatrixLib quaternion = newQuaternion(q1,q2,q3,q4);
                MatrixLib quaternion_conjugate = newQuaternion(q1,-q2,-q3,-q4);

                //magnetic field compensation
                temp = QuaternionProduct(quaternion, newQuaternion(0.0,m_x, m_y, m_z));
                h = QuaternionProduct(temp, quaternion_conjugate);
                bx = Math.sqrt((h.in_Mat[1][0] * h.in_Mat[1][0] + h.in_Mat[2][0] * h.in_Mat[2][0]));
                bz = h.in_Mat[3][0];


                //bx = h.in_Mat[1][0];
                //by = h.in_Mat[2][0];
                //bz = h.in_Mat[3][0];

                norm = Math.sqrt(bx * bx + bz * bz);
                bx /= norm;
                by = 0;
                bz /= norm;


                //compute the objective functions
                f_obb.in_Mat[0][0] = 2 * (q2 * q4 - q1 * q3) - a_x;
                f_obb.in_Mat[1][0] = 2 * (q1 * q2 + q3 * q4) - a_y;
                f_obb.in_Mat[2][0] = 2 * (0.5 - q2 * q2 - q3 * q3) - a_z;
                f_obb.in_Mat[3][0] = 2 * bx * (0.5 - q3 * q3 - q4 * q4) + 2 * by * (q1 * q4 + q2 * q3) + 2 * bz * (q2 * q4 - q1 * q3) - m_x;
                f_obb.in_Mat[4][ 0] = 2 * bx * (q2 * q3 - q1 * q4) + 2 * by * (0.5 - q2 * q2 - q4 * q4) + 2 * bz * (q1 * q2 + q3 * q4) - m_y;
                f_obb.in_Mat[5][ 0] = 2 * bx * (q1 * q3 + q2 * q4) + 2 * by * (q3 * q4 - q1 * q2) + 2 * bz * (0.5 - q2 * q2 - q3 * q3) - m_z;

                //compute the jacobian
                Jacobian.in_Mat[0][0] = -2 * q3;
                Jacobian.in_Mat[0][1] = 2 * q4;
                Jacobian.in_Mat[0][2] = -2 * q1;
                Jacobian.in_Mat[0][3] = 2 * q2;
                Jacobian.in_Mat[1][0] = 2 * q2;
                Jacobian.in_Mat[1][1] = 2 * q1;
                Jacobian.in_Mat[1][2] = 2 * q4;
                Jacobian.in_Mat[1][3] = 2 * q3;
                Jacobian.in_Mat[2][0] = 0;
                Jacobian.in_Mat[2][1] = -4 * q2;
                Jacobian.in_Mat[2][2] = -4 * q3;
                Jacobian.in_Mat[2][3] = 0;

                Jacobian.in_Mat[3][0] = 2 * by * q4 - 2 * bz * q3;
                Jacobian.in_Mat[3][1] = 2 * by * q3 + 2 * bz * q4;
                Jacobian.in_Mat[3][2] = -4 * bx * q3 + 2 * by * q2 - 2 * bz * q1;
                Jacobian.in_Mat[3][3] = -4 * bx * q4 + 2 * by * q1 + 2 * bz * q2;
                Jacobian.in_Mat[4][0] = -2 * bx * q4 + 2 * bz * q2;
                Jacobian.in_Mat[4][1] = 2 * bx * q3 - 4 * by * q2 + 2 * bz * q1;
                Jacobian.in_Mat[4][2] = 2 * bx * q2 + 2 * bz * q4;
                Jacobian.in_Mat[4][3] = -2 * bx * q1 - 4 * by * q4 + 2 * bz * q3;
                Jacobian.in_Mat[5][0] = 2 * bx * q3 - 2 * by * q2;
                Jacobian.in_Mat[5][1] = 2 * bx * q4 - 2 * by * q1 - 4 * bz * q2;
                Jacobian.in_Mat[5][2] = 2 * bx * q1 + 2 * by * q4 - 4 * bz * q3;
                Jacobian.in_Mat[5][3] = 2 * bx * q2 + 2 * by * q3;

                Df = MatrixLib.Multiply(MatrixLib.Transpose(Jacobian), f_obb);
                norm = Math.sqrt(Df.in_Mat[0][0] * Df.in_Mat[0][0] + Df.in_Mat[1][0] * Df.in_Mat[1][0] + Df.in_Mat[2][0] * Df.in_Mat[2][0] + Df.in_Mat[3][0] * Df.in_Mat[3][0]);
                Df = MatrixLib.ScalarDivide(norm, Df);

                result = MatrixLib.Subtract(quaternion , MatrixLib.ScalarMultiply (mu, Df));

                q1 = result.in_Mat[0][0];
                q2 = result.in_Mat[1][0];
                q3 = result.in_Mat[2][0];
                q4 = result.in_Mat[3][0];

                norm = Math.sqrt(q1 * q1 + q2 * q2 + q3 * q3 + q4 * q4);
                result = MatrixLib.ScalarDivide(norm, result);

                q1 = result.in_Mat[0][0];
                q2 = result.in_Mat[1][0];
                q3 = result.in_Mat[2][0];
                q4 = result.in_Mat[3][0];

                i = i + 1;
            }

            return result;
        }

        private MatrixLib QuaternionProduct(MatrixLib quaternion, MatrixLib matrix)
        {
            double a1 = quaternion.in_Mat[0][0];
            double a2 = quaternion.in_Mat[1][0];
            double a3 = quaternion.in_Mat[2][0];
            double a4 = quaternion.in_Mat[3][0];
            double b1 = matrix.in_Mat[0][0];
            double b2 = matrix.in_Mat[1][0];
            double b3 = matrix.in_Mat[2][0];
            double b4 = matrix.in_Mat[3][0];

            MatrixLib result = new MatrixLib(4, 1);
            result.in_Mat[0][0] = a1 * b1 - a2 * b2 - a3 * b3 - a4 * b4;
            result.in_Mat[1][0] = a1 * b2 + a2 * b1 + a3 * b4 - a4 * b3;
            result.in_Mat[2][0] = a1 * b3 - a2 * b4 + a3 * b1 + a4 * b2;
            result.in_Mat[3][0] = a1 * b4 + a2 * b3 - a3 * b2 + a4 * b1;

            return result;

        }

        private MatrixLib newQuaternion(double p, double m_x, double m_y, double m_z)
        {
            MatrixLib quaternion = new MatrixLib(4, 1);
            quaternion.in_Mat[0][0] = p;
            quaternion.in_Mat[1][0] = m_x;
            quaternion.in_Mat[2][0] = m_y;
            quaternion.in_Mat[3][0] = m_z;
            return quaternion;
        }
        
        public double[] quaternion_values()
        { double[] quat = {q_filt1,q_filt2,q_filt3,q_filt4};
          return quat;
        }

 }
