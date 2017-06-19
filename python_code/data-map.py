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
for i in range(0,17):
   g = 'w'+str(i)
   sets = glob.glob('C:\Users\Ramakanth\Dropbox\Thesis-Docs\New-Data\*' + g + '_*.magnet.csv')
   for j in range(len(sets)):
      e = re.split('\D+',sets[j])[2]
      x,y,z =  np.loadtxt(sets[j],delimiter=',', skiprows = 5, usecols=(2,3,4), unpack=True)
      a =  np.mean(x)
      b =  np.mean(y)
      c =  np.mean(z)
      d =  np.sqrt(a**2 + b**2 + c**2)
      h = [a,b,c,d]
      dct[(16-i)*27 + int(e)] = h

f = open('C:\Users\Ramakanth\Dropbox\Thesis-Docs\library_samples\data-west-7.json','wb')
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

xi = np.linspace(0,16,17)
yi = np.linspace(0,26,27)
zi = griddata(x0,y0,z0,xi,yi,interp ='linear')

plt.subplot(2, 2, 1)
plt.contourf(xi,yi,zi,30)
plt.ylabel('total map')
plt.colorbar()
plt.title('Magnetic field -West- X-axis')

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

xi = np.linspace(0,16,17)
yi = np.linspace(0,26,27)
zi = griddata(x0,y0,z0,xi,yi,interp ='linear')

plt.subplot(2, 2, 2)
plt.contourf(xi,yi,zi,30)
plt.ylabel('total map')
plt.colorbar()
plt.title('Magnetic field -West- Y-axis')

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

xi = np.linspace(0,16,17)
yi = np.linspace(0,26,27)
zi = griddata(x0,y0,z0,xi,yi,interp ='linear')

plt.subplot(2, 2, 3)
plt.contourf(xi,yi,zi,30)
plt.ylabel('total map')
plt.colorbar()
plt.title('Magnetic field -West- Z-axis')

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

xi = np.linspace(0,16,17)
yi = np.linspace(0,26,27)
zi = griddata(x0,y0,z0,xi,yi,interp ='linear')

plt.subplot(2, 2, 4)
plt.contourf(xi,yi,zi,30)
plt.ylabel('total map')
plt.colorbar()
plt.title('Magnetic field -West- Magnitude')

plt.show() 





               
