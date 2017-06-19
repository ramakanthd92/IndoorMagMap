import glob
import numpy as np
import matplotlib.pyplot as plt
import re

west = glob.glob('C:\Users\RP\Dropbox\Thesis-Docs\library_samples\orientation\magnetic_field\*_lt1_*.csv')
east = glob.glob('C:\Users\RP\Dropbox\Thesis-Docs\library_samples\orientation\magnetic_field\*_lb1_*.csv')

dxe = {}
dye = {}
dze = {}
dzw = {}
dxw = {}
dyw = {}

for i in range(len(west)):
    a = int(re.split('\D+',west[i])[2])
    xa,ya,za = np.loadtxt(west[i],delimiter=',', skiprows = 5, usecols=(2,3,4), unpack=True)
    dxw[a] = xa
    dyw[a] = ya
    dzw[a] = za
    
for i in range(len(east)):
    a= int(re.split('\D+',east[i])[2])
    xb,yb,zb = np.loadtxt(east[i],delimiter=',', skiprows = 5, usecols=(2,3,4), unpack=True)
    dxe[a] = xb
    dye[a] = yb
    dze[a] = zb

xoff =[]
yoff = []

mage = []
magw = []

for i in range(1,17):
    xoff.append((np.mean(dxe[i])+np.mean(dxw[i]))/2)
    yoff.append((np.mean(dye[i])+np.mean(dyw[i]))/2)
   
#plt.plot(xoff,'b',yoff,'r')
#plt.show()

for i in range(1,2):
    mage.append(np.sqrt(np.mean(dxe[i])**2+ np.mean(dye[i])**2+np.mean(dze[i])**2))
    magw.append(np.sqrt(np.mean(dxw[i])**2+ np.mean(dyw[i])**2+np.mean(dzw[i])**2))
    
#plt.plot(mage,'b',magw,'r')
#plt.show()
wz = []
ez = []
for i in range(1,17):
    wz.append(np.sqrt(np.mean(dxw[i]**2+dyw[i]**2)))
    ez.append(np.sqrt(np.mean(dxe[i]**2+dye[i]**2)))

plt.plot(wz,'b',ez,'r')
plt.show()
    
print "X-off ",np.mean(xoff),"Y-off ", np.mean(yoff)

    
          
