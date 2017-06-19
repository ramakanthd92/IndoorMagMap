import numpy as np
import matplotlib.pyplot as plt
import re
from matplotlib import cm
from mpl_toolkits.mplot3d import axes3d
import glob


l0x ,l0y ,l0z  = np.loadtxt('C:\Users\Ramakanth\Desktop\library_samples\library_samples\Loc_394-r-0_w_2014-02-03-11-07-13.magnet.csv',delimiter=',',skiprows = 5, usecols=(2,3,4), unpack=True)
l1x, l1y, l1z = np.loadtxt('C:\Users\Ramakanth\Desktop\library_samples\library_samples\Loc_394-r-1_w_2014-02-03-11-07-24.magnet.csv',delimiter=',', skiprows = 5, usecols=(2,3,4), unpack=True)
l2x, l2y, l2z = np.loadtxt('C:\Users\Ramakanth\Desktop\library_samples\library_samples\Loc_394-r-2_w_2014-02-03-11-07-36.magnet.csv',delimiter=',', skiprows = 5, usecols=(2,3,4), unpack=True)
l3x, l3y ,l3z = np.loadtxt('C:\Users\Ramakanth\Desktop\library_samples\library_samples\Loc_394-r-3_w_2014-02-03-11-07-49.magnet.csv',delimiter=',', skiprows = 5, usecols=(2,3,4), unpack=True)
l4x, l4y, l4z = np.loadtxt('C:\Users\Ramakanth\Desktop\library_samples\library_samples\Loc_394-r-4_w_2014-02-03-11-08-04.magnet.csv',delimiter=',', skiprows = 5, usecols=(2,3,4), unpack=True)
l5x, l5y, l5z = np.loadtxt('C:\Users\Ramakanth\Desktop\library_samples\library_samples\Loc_394-r-5_w_2014-02-03-11-08-16.magnet.csv',delimiter=',', skiprows = 5, usecols=(2,3,4), unpack=True)
l6x, l6y, l6z = np.loadtxt('C:\Users\Ramakanth\Desktop\library_samples\library_samples\Loc_394-r-6_w_2014-02-03-11-08-33.magnet.csv',delimiter=',', skiprows = 5, usecols=(2,3,4), unpack=True)

m0 = []
m1 = []
m2 = []
m3 = []
m4 = []
m5 = []
m6 = []

mag = [m0,m1,m2,m3,m4,m5,m6]
j = 0
for a in [[l0x,l0y,l0z],[l1x,l1y,l1z],[l2x,l2y,l2z],[l3x,l3y,l3z],[l4x,l4y,l4z],[l5x,l5y,l5z],[l6x,l6y,l6z]]:
    for i in range(len(a[0])):
        mag[j].append(np.sqrt(a[0][i]**2+a[1][i]**2+a[2][i]**2))
    j += 1

dt = 1
t = np.arange(0,36,dt)

print len(range(len(l0x))),len(mag[0])

plt.plot(t,mag[0],'b--',label ='ground')
plt.plot(t,mag[1],'r--',label ='first')
plt.plot(t,mag[2],'g--',label ='second')
plt.plot(t,mag[3],'y--',label ='third')
plt.plot(t,mag[4],'k--',label ='fourth')
plt.plot(t,mag[5],'c--',label ='fifth')
plt.plot(t,mag[6],'m--',label ='sixth')

legend = plt.legend(loc='best', shadow=True)
frame = legend.get_frame()
frame.set_facecolor('0.90')

plt.xlabel("time")
plt.ylabel("magnitude")
plt.show()
