package in.ernet.iitr.puttauec.sensors;

import in.ernet.iitr.puttauec.sensors.internal.IHWSensorEventCallback;

public interface ISensorCallback extends IHWSensorEventCallback {
	// Merge the 2 internal interfaces to provide a single interface
		// that any class desirous of registering for hardware + wifi sensor events must support.
		
		// Default (empty) implementations of the functions are provided in the
		// DefaultSensorCallbacks class.
}
