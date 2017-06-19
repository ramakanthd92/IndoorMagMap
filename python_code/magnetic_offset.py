import numpy as np
import matplotlib.pyplot as plt
mx,my,mz = np.loadtxt('C:\Users\RP\Dropbox\Thesis-Docs\ParticleFiltering\All 3 axis\SensorLog\sLog.RV-2014-03-04-11-18-21.csv',delimiter=',', usecols=(2,3,4), unpack=True)
px,py,pz = np.loadtxt('C:\Users\RP\Dropbox\Thesis-Docs\ParticleFiltering\All 3 axis\SensorLog\sLog.mag-2014-03-04-11-18-37.csv',delimiter=',', usecols=(2,3,4), unpack=True)

#l = len(mx2)
#mx3 = mx2[l-1:0:-1]
#my3 = my2[l-1:0:-1]
#mz3 = mz2[l-1:0:-1]


plt.subplot(1,1,1)
plt.title('Particle Filtered Reckoning - magnetic offset') 
plt.xlabel('magnetic field')
plt.ylabel('readings')

plt.plot(mx,'b+-', label = 'forward-x')
plt.plot(my,'g+-', label = 'forward-y')
plt.plot(mz,'r+-', label = 'forward-z')
#plt.plot(mx3,'c+-', label = 'backward-x')
#plt.plot(my3,'m+-', label = 'backward-y')
#plt.plot(mz3,'k+-', label = 'backward-z')

legend = plt.legend(loc='best', shadow=True)
frame = legend.get_frame()
frame.set_facecolor('0.90')
plt.show()
