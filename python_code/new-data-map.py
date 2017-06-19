import math 
import numpy as np
#from scipy import interpolate
from matplotlib.mlab import griddata
import matplotlib.pyplot as plt
import json
import pylab
import glob
import re

json_data_0=open('C:\Users\Ramakanth\Dropbox\Thesis-Docs\library_samples\data-west-6.json')
dct = json.load(json_data_0)
json_data_0.close()

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

xi = np.linspace(0,14,15)
yi = np.linspace(0,26,27)
zi = griddata(x0,y0,z0,xi,yi,interp ='linear')

plt.contourf(xi,yi,zi,30)
plt.xlabel('x-axis (*0.6 m)',size = 15)
plt.ylabel('y-axis (*0.6 m)', size = 15 )
plt.colorbar()
plt.title('Magnetic field - West Mangitude' ,size = 15)
plt.show()
