import math 
import numpy as np
#from scipy import interpolate
from matplotlib.mlab import griddata
import matplotlib.pyplot as plt
import json
import pylab

json_data_0=open('C:\Users\Ramakanth\Dropbox\Thesis-Docs\library_samples\data-west-6.json')

data0 = json.load(json_data_0)

json_data_0.close()

M1,M,N = 9,15,50  # The shape of the data array

z0 =[]
x0 = []
y0 = []
k = data0.keys()
j = 0
for i in k:
   z0.append(data0[i][0])
   x = (int(i)-1)/27
   y = (int(i)-1)%27
   x0.append(x)
   y0.append(y)

xi = np.linspace(0,14,300)
yi = np.linspace(0,26,500)
zi = griddata(x0,y0,z0,xi,yi,interp ='nn')

plt.subplot(2, 2, 1)
plt.contourf(xi,yi,zi,30)
plt.ylabel('total map')
plt.colorbar()
plt.title('Magnetic field - X-axis')

z0 =[]
x0 = []
y0 = []
k = data0.keys()
j = 0
for i in k:
   z0.append(data0[i][1])
   x = (int(i)-1)/27
   y = (int(i)-1)%27
   x0.append(x)
   y0.append(y)

xi = np.linspace(0,14,300)
yi = np.linspace(0,26,500)
zi = griddata(x0,y0,z0,xi,yi,interp ='nn')

plt.subplot(2, 2, 2)
plt.contourf(xi,yi,zi,30)
plt.ylabel('total map')
plt.colorbar()
plt.title('Magnetic field - Y-axis')

z0 =[]
x0 = []
y0 = []
k = data0.keys()
j = 0
for i in k:
   z0.append(data0[i][2])
   x = (int(i)-1)/27
   y = (int(i)-1)%27
   x0.append(x)
   y0.append(y)

xi = np.linspace(0,14,300)
yi = np.linspace(0,26,500)
zi = griddata(x0,y0,z0,xi,yi,interp ='nn')

plt.subplot(2, 2, 3)
plt.contourf(xi,yi,zi,30)
plt.ylabel('total map')
plt.colorbar()
plt.title('Magnetic field - Z-axis')

z0 =[]
x0 = []
y0 = []
k = data0.keys()
j = 0
for i in k:
   z0.append(data0[i][3])
   x = (int(i)-1)/27
   y = (int(i)-1)%27
   x0.append(x)
   y0.append(y)

xi = np.linspace(0,14,300)
yi = np.linspace(0,26,500)
zi = griddata(x0,y0,z0,xi,yi,interp ='nn')

plt.subplot(2, 2, 4)
plt.contourf(xi,yi,zi,30)
plt.ylabel('total map')
plt.colorbar()
plt.title('Magnetic field - Magnitude')

plt.show() 





               
