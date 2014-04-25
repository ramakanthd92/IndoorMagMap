package in.ernet.iitr.puttauec.algorithms;

public interface IAngleAlgorithm {
	   public void update(double w_x, double w_y, double w_z, double a_x, double a_y, double a_z, double m_x, double m_y, double m_z, double deltat);
	   public double[] quaternion_values();
}
