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
   g = 'w'+ str(i)
   h = 'e'+ str(i)
   setsw = glob.glob('C:\Users\Ramakanth\Dropbox\Thesis-Docs\ParticleFiltering\Map-data\*' + g + '_*.magnet.csv')
   setse = glob.glob('C:\Users\Ramakanth\Dropbox\Thesis-Docs\ParticleFiltering\Map-data\*' + h + '_*.magnet.csv')
   for j in range(len(setsw)):
      xw,yw,zw =  np.loadtxt(setsw[j],delimiter=',', skiprows = 5, usecols=(2,3,4), unpack=True)
      xe,ye,ze =  np.loadtxt(setse[j],delimiter=',', skiprows = 5, usecols=(2,3,4), unpack=True)
      magw = np.sqrt(xw**2 + yw**2 + zw**2)
      mage = np.sqrt(xe**2 + ye**2 + ze**2)
      ze = ze[len(ze)-1:0:-1]
      ye = ye[len(ye)-1:0:-1]
      xe = xe[len(xe)-1:0:-1]
      mage = mage[len(mage)-1:0:-1]
      plt.subplot(4,3,i+1)
      plt.title("path " + str(i) + " West + East")
      plt.plot(xw,'b-',label = 'x-w')
      plt.plot(yw,'g-',label = 'y-w')
      plt.plot(xe,'b--',label = 'x-e')
      plt.plot(ye,'g--',label = 'y-e')
      plt.plot(zw,'r-',label = 'z-w')
      plt.plot(ze,'r--',label = 'z-e')
      plt.plot(magw,'k-',label = 'mag-w')
      plt.plot(mage,'k--',label = 'mag-e')     
legend = plt.legend(loc='best', shadow=True)
frame = legend.get_frame()
frame.set_facecolor('0.90')
plt.show()

    
