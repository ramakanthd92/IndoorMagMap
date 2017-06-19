import numpy as np
import matplotlib.pyplot as plt

src  = 'C:\Users\Ramakanth\Dropbox\Thesis-Docs\Experiments'
s = '\pfah_ve_mp'
h = '\pfPathLog.2014-05-01-20-39-51.csv'
a = '\pfLog.2014-05-01-08-38-34.mmse.csv'

#gx,gy,gz = np.loadtxt('C:\Users\Ramakanth\Dropbox\Thesis-Docs\ParticleFiltering\Magnitude\Loc_c2_2014-03-23-04-36-52.sensor.csv',delimiter=',', usecols=(10,11,12), unpack=True)
px,py,pz,mx,my,mz = np.loadtxt(src + s + a ,delimiter=',', usecols=(4,5,6,7,8,9), unpack=True)
#ss,ra = np.loadtxt('C:\Users\Ramakanth\Dropbox\Thesis-Docs\ParticleFiltering\Magnitude\drLog.2014-03-23-06-10-19.steps.csv',delimiter=',', usecols=(2,3), unpack=True)
xx,yy = np.loadtxt(src + s+ h,delimiter=',', usecols=(0,1), unpack=True)
#cx,cy = np.loadtxt('C:\Users\Ramakanth\Dropbox\Thesis-Docs\ParticleFiltering\Magnitude\drLog.2014-03-11-06-10-11.steps.csv',delimiter=',', usecols=(2,3), unpack=True)
#l = len(mx2)
#mx3 = mx2[l-1:0:-1]
#my3 = my2[l-1:0:-1]
#mz3 = mz2[l-1:0:-1]

cons = 180/np.pi
plt.subplot(1,1,1)
plt.title('Particle Filtered Reckoning - Avg Orietation') 
plt.ylabel('Avg. orientation')
plt.xlabel('Steps')

#plt.plot(np.array(ss),'g-', label = 'Step_Size')
#plt.plot(np.array(ra),'r-', label = 'Rad_Angle')

mag = np.sqrt(mx**2 + my**2 + mz**2)

plt.plot(np.array(px),'g-', label = 'est-x')
plt.plot(np.array(py),'r-', label = 'est-y')
plt.plot(np.array(pz),'b-', label = 'est-z')
plt.plot(np.array(mx),'g--', label = 'mes-x')
plt.plot(np.array(my),'r--', label = 'mes-y')
plt.plot(np.array(mz),'b--', label = 'mes-z')
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
