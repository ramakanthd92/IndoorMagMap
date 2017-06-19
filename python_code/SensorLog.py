import numpy as np
import matplotlib.pyplot as plt
import decimal
xa0,ya0,za0 = np.loadtxt('C:\Users\Ramakanth\Dropbox\Thesis-Docs\ParticleFiltering\Z-value-PF\Third\sLog.angle-2014-03-01-05-56-35.csv',delimiter=',', usecols=(2,3,4), unpack=True)
xm0,ym0,zm0 = np.loadtxt('C:\Users\Ramakanth\Dropbox\Thesis-Docs\ParticleFiltering\Z-value-PF\Third\sLog.mag-2014-03-01-05-56-35.csv',delimiter=',', usecols=(2,3,4), unpack=True)

s = " Moving from (7.0,0.0) to (7.0,50.0)"
xa0 = xa0*(180/np.pi) + 85
ya0 = ya0*(180/np.pi)
za0 = za0*(180/np.pi)

plt.subplot(1,2,1)
plt.title('MagneticField')
plt.xlabel('readings')
plt.ylabel('Magnetic Field')
plt.plot(xm0,'b-',label = 'x-axis')
#plt.text(3,10, s)
plt.plot(ym0,'r-', label = 'y-axis')
plt.plot(zm0,'g-', label = 'z-axis')

legend = plt.legend(loc='best', shadow=True)
frame = legend.get_frame()
frame.set_facecolor('0.90')

plt.subplot(1,2,2)
plt.title('Angle') 
plt.xlabel('readings')
plt.ylabel('Angle')
#plt.text(3,-40, s)
plt.plot(xa0,'b-', label = 'x-axis + 85 deg')
plt.plot(ya0,'r-', label = 'y-axis')
plt.plot(za0,'g-', label = 'z-axis')
legend = plt.legend(loc='best', shadow=True)
frame = legend.get_frame()
frame.set_facecolor('0.90')

plt.show()

