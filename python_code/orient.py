import numpy as np
import matplotlib.pyplot as plt
ao,go,fo = np.loadtxt('C:\Users\Ramakanth\Dropbox\Thesis-Docs\ParticleFiltering\Magnitude\Loc_small_2014-03-14-11-06-25.orient.csv',delimiter=',', usecols=(0,3,6), unpack=True)
cx,cy,cz = np.loadtxt('C:\Users\Ramakanth\Dropbox\Thesis-Docs\ParticleFiltering\Magnitude\Loc_small_2014-03-14-11-06-25.sensor.csv',delimiter=',', usecols=(7,8,9), unpack=True)
#l = len(mx2)
#mx3 = mx2[l-1:0:-1]
#my3 = my2[l-1:0:-1]
#mz3 = mz2[l-1:0:-1]

cons = 180/np.pi
plt.subplot(1,1,1)
plt.title('Particle Filtered Reckoning - magnetic readings') 
plt.xlabel('magnetic field')
plt.ylabel('readings')

plt.plot(np.array(ao)*cons,'g+-', label = 'Acceleration')
plt.plot(np.array(go)*cons,'r+-', label = 'Gyroscope')
plt.plot(np.array(fo)*cons,'b+-', label = 'Fused')
plt.plot(np.array(cx)*cons,'k+-', label = 'gyroscope')
plt.plot(np.array(cy)*cons,'c+-', label = 'gyroscope')
plt.plot(np.array(cz)*cons,'m+-', label = 'gyroscope')

#mag = np.sqrt(mx**2 + my**2 + mz**2)
#plt.plot(np.array(gt)*cons,'r+-',label = 'gyroTurn')

legend = plt.legend(loc='best', shadow=True)
frame = legend.get_frame()
frame.set_facecolor('0.90')

#plt.title('Particle Filtered Reckoning - position readings') 
#plt.xlabel('magnetic field')
#plt.ylabel('readings')
#plt.plot(np.array(oi)*cons,'m+-', label = 'com-y')
#legend = plt.legend(loc='best', shadow=True)
#frame = legend.get_frame()
#frame.set_facecolor('0.90')

plt.show()
