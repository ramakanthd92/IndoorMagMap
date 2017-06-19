import numpy as np
import matplotlib.pyplot as plt
import decimal
xd0,yd0 = np.loadtxt('C:\Users\RP\Dropbox\Thesis-Docs\ParticleFiltering\Z-value-PF\debug\drPathLog.2014-03-06-17-23-06.csv',delimiter=',', usecols=(0,1), unpack=True)
a0, s0 =  np.loadtxt('C:\Users\RP\Dropbox\Thesis-Docs\ParticleFiltering\Z-value-PF\debug\Loc_c10_2014-03-06-05-18-07.magnet.csv',delimiter=',', usecols=(2,3), unpack=True)
a1, s1 =  np.loadtxt('C:\Users\RP\Dropbox\Thesis-Docs\ParticleFiltering\Z-value-PF\debug\Loc_c9_2014-03-06-05-17-47.magnet.csv',delimiter=',', usecols=(2,3), unpack=True)

plt.suptitle("Dead Reckoning")
plt.subplot(1,1,1)
plt.title('Dead Reckoning') 
plt.xlabel('x')
plt.plot(xd0,yd0,'go-')

#plt.subplot(1,2,2)
#plt.plot((180/np.pi)*a1,'b--')
#plt.plot((180/np.pi)*a0,'r--')

plt.show()
