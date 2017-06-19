import math 
import numpy as np
#from scipy import interpolate
from matplotlib.mlab import griddata
import matplotlib.pyplot as plt
import json
import pylab
import glob
import re

dct = {}
for i in range(11):
   g = 'w'+str(i)
   sets = glob.glob('C:\Users\Ramakanth\Dropbox\Thesis-Docs\Data-Collect\*' + g + '_*.RV.csv')
   for j in range(len(sets)):
      e = re.split('\D+',sets[j])[2]
      xa,xb,xc,ya,yb,yc,za,zb,zc=  np.loadtxt(sets[j],delimiter=',', skiprows = 5, usecols=(5,6,7,9,10,11,13,14,15), unpack=True)
      r0 =  np.mean(xa)
      r1 =  np.mean(xb)
      r2 =  np.mean(xc)
      r3 =  np.mean(ya)
      r4 =  np.mean(yb)
      r5 =  np.mean(yc)
      r6 =  np.mean(za)
      r7 =  np.mean(zb)
      r8 =  np.mean(zc)
      h = [r0,r1,r2,r3,r4,r5,r6,r7,r8]
      dct[(10-i)*27 + int(e)] = h

f = open('C:\Users\Ramakanth\Dropbox\Thesis-Docs\library_samples\RV-West.json','wb')
json.dump(dct,f)
f.close()

z0 = []
x0 = []
y0 = []
k = dct.keys()
j = 0
for i in k:
   z0.append(dct[i][0])
   x = (int(i))/27
   y = (int(i))%27
   x0.append(x)
   y0.append(y)

xi = np.linspace(0,10,11)
yi = np.linspace(0,26,27)
zi = griddata(x0,y0,z0,xi,yi,interp ='linear')

plt.subplot(3, 3, 1)
plt.contourf(xi,yi,zi,30)
plt.ylabel('total map')
plt.colorbar()
plt.title('RV-0')

z0 = []
x0 = []
y0 = []
k = dct.keys()
j = 0
for i in k:
   z0.append(dct[i][1])
   x = (int(i))/27
   y = (int(i))%27
   x0.append(x)
   y0.append(y)

xi = np.linspace(0,10,11)
yi = np.linspace(0,26,27)
zi = griddata(x0,y0,z0,xi,yi,interp ='linear')

plt.subplot(3, 3, 2)
plt.contourf(xi,yi,zi,30)
plt.ylabel('total map')
plt.colorbar()
plt.title('RV-1')

z0 = []
x0 = []
y0 = []
k = dct.keys()
j = 0
for i in k:
   z0.append(dct[i][2])
   x = (int(i))/27
   y = (int(i))%27
   x0.append(x)
   y0.append(y)

xi = np.linspace(0,10,11)
yi = np.linspace(0,26,27)
zi = griddata(x0,y0,z0,xi,yi,interp ='linear')

plt.subplot(3, 3, 3)
plt.contourf(xi,yi,zi,30)
plt.ylabel('total map')
plt.colorbar()
plt.title('RV-2')

z0 = []
x0 = []
y0 = []
k = dct.keys()
j = 0
for i in k:
   z0.append(dct[i][3])
   x = (int(i))/27
   y = (int(i))%27
   x0.append(x)
   y0.append(y)

xi = np.linspace(0,10,11)
yi = np.linspace(0,26,27)
zi = griddata(x0,y0,z0,xi,yi,interp ='linear')

plt.subplot(3, 3, 4)
plt.contourf(xi,yi,zi,30)
plt.ylabel('total map')
plt.colorbar()
plt.title('RV-3')

z0 = []
x0 = []
y0 = []
k = dct.keys()
j = 0
for i in k:
   z0.append(dct[i][4])
   x = (int(i))/27
   y = (int(i))%27
   x0.append(x)
   y0.append(y)

xi = np.linspace(0,10,11)
yi = np.linspace(0,26,27)
zi = griddata(x0,y0,z0,xi,yi,interp ='linear')

plt.subplot(3, 3, 5)
plt.contourf(xi,yi,zi,30)
plt.ylabel('total map')
plt.colorbar()
plt.title('RV-4')

z0 = []
x0 = []
y0 = []
k = dct.keys()
j = 0
for i in k:
   z0.append(dct[i][5])
   x = (int(i))/27
   y = (int(i))%27
   x0.append(x)
   y0.append(y)

xi = np.linspace(0,10,11)
yi = np.linspace(0,26,27)
zi = griddata(x0,y0,z0,xi,yi,interp ='linear')

plt.subplot(3, 3, 6)
plt.contourf(xi,yi,zi,30)
plt.ylabel('total map')
plt.colorbar()
plt.title('RV-5')

z0 = []
x0 = []
y0 = []
k = dct.keys()
j = 0
for i in k:
   z0.append(dct[i][6])
   x = (int(i))/27
   y = (int(i))%27
   x0.append(x)
   y0.append(y)

xi = np.linspace(0,10,11)
yi = np.linspace(0,26,27)
zi = griddata(x0,y0,z0,xi,yi,interp ='linear')

plt.subplot(3, 3, 7)
plt.contourf(xi,yi,zi,30)
plt.ylabel('total map')
plt.colorbar()
plt.title('RV-6')

z0 = []
x0 = []
y0 = []
k = dct.keys()
j = 0
for i in k:
   z0.append(dct[i][7])
   x = (int(i))/27
   y = (int(i))%27
   x0.append(x)
   y0.append(y)

xi = np.linspace(0,10,11)
yi = np.linspace(0,26,27)
zi = griddata(x0,y0,z0,xi,yi,interp ='linear')

plt.subplot(3, 3, 8)
plt.contourf(xi,yi,zi,30)
plt.ylabel('total map')
plt.colorbar()
plt.title('RV-7')

z0 = []
x0 = []
y0 = []
k = dct.keys()
j = 0
for i in k:
   z0.append(dct[i][8])
   x = (int(i))/27
   y = (int(i))%27
   x0.append(x)
   y0.append(y)

xi = np.linspace(0,10,11)
yi = np.linspace(0,26,27)
zi = griddata(x0,y0,z0,xi,yi,interp ='linear')

plt.subplot(3, 3, 9)
plt.contourf(xi,yi,zi,30)
plt.ylabel('total map')
plt.colorbar()
plt.title('RV-8')

plt.show() 





               
