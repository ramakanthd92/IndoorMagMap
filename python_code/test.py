import math 
import numpy 
from scipy import interpolate
import matplotlib.pyplot as plt
import json
import pylab

json_data_0=open('C:\Users\Ramakanth\Dropbox\Thesis-Docs\library_samples\w0.json')
json_data_1=open('C:\Users\Ramakanth\Dropbox\Thesis-Docs\library_samples\w1.json')
json_data_2=open('C:\Users\Ramakanth\Dropbox\Thesis-Docs\library_samples\w2.json')

data0 = json.load(json_data_0)
data1 = json.load(json_data_1)
data2 = json.load(json_data_2)

json_data_0.close()
json_data_1.close()
json_data_2.close()

M1,M2,N = 3,2,50  # The shape of the data array

y0,x0 = numpy.mgrid[0:N+1, 0:M1+1]
z0 =[]
for j in range(N+1):
   z0.append([])
   for i in range(M1+1):
       z0[j].append(data0[str(i*51 + j + 1)][2])

z0 = numpy.array(z0)       
# Interpolation
Y0,X0  = numpy.mgrid[0.0:N:0.1, 0.0: M1 + 0.1:0.1]
sp = interpolate.RectBivariateSpline(y0[:,0], x0[0,:], z0 , kx=2, ky=2)
Z0 = sp(Y0[:,0],X0[0,:])

y1,x1 = numpy.mgrid[0:N+1, 0:M2+1]
z1 =[]
for j in range(N+1):
   z1.append([])
   for i in range(M2+1):
       z1[j].append(data1[str(i*51 + j + 1)][2])

z1 = numpy.array(z1)       
# Interpolation
Y1,X1  = numpy.mgrid[0.0:N:0.1, 0.0: M2 + 0.1:0.1]
sp = interpolate.RectBivariateSpline(y1[:,0], x1[0,:], z1 , kx=2, ky=2)
Z1 = sp(Y1[:,0],X1[0,:])

y2,x2 = numpy.mgrid[0:N+1, 0:M2+1]
z2 =[]
for j in range(N+1):
   z2.append([])
   for i in range(M2+1):
       z2[j].append(data2[str(i*51 + j + 1)][2])

z2 = numpy.array(z2)       
# Interpolation
Y2,X2  = numpy.mgrid[0.0:N:0.1, 0.0: M2 + 0.1:0.1]
sp = interpolate.RectBivariateSpline(y2[:,0], x2[0,:], z2 , kx=2, ky=2)
Z2 = sp(Y2[:,0],X2[0,:])

crange = numpy.arange(-52.,-20., 1.0)

plt.subplot(3, 1, 1)
plt.contourf(Y2,X2,Z2,crange)
plt.ylabel('path-2')
plt.ylim(2.0,0.0)
plt.colorbar()

plt.subplot(3, 1, 2)
plt.contourf(Y1,X1,Z1,crange)
plt.ylabel('path-1')
plt.ylim(2.0,0.0)
plt.colorbar()

plt.subplot(3, 1, 3)
plt.contourf(Y0,X0,Z0,crange)
plt.ylabel('path-0')
plt.ylim(3.0,0.0)
plt.colorbar()

plt.suptitle('Magnetic field - Z-axis')

plt.show() 





               
