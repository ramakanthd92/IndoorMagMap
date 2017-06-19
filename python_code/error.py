import numpy as np
import matplotlib.pyplot as plt
import glob
import re
import decimal
src  = 'C:\Users\Ramakanth\Dropbox\Thesis-Docs\Experiments'
s = '\pfah_ve_mp'
s1 = '\pfah_mg_mp'
s2 = '\pfah_mg'
s3 = '\pfah_ve'
h = '\pfLog.2014-05-08-'
a = '\pfPathLog.2014-05-08-17-13-47.csv'
b = '\path_g_17_13_47.csv'

xx,yy = np.loadtxt(src + s2 + a ,delimiter=',', usecols=(0,1), unpack=True)
xa,ya = np.loadtxt(src + s2+ b ,delimiter=',', usecols=(0,1), unpack=True)

plt.subplot(1,2,1)
plt.plot(xx,yy,'ro-',linewidth=2)
plt.plot(xa,ya,'bo-',linewidth=2)
plt.xlabel('x')
plt.ylabel('y')
plt.xlim(0,14)
plt.ylim(0,26)
plt.title(s +a)  

xx = np.array(xx)*0.56
yy = np.array(yy)*0.56
xa = np.array(xa)*0.56
ya = np.array(ya)*0.56
earr = np.sqrt((xx-xa)**2 + (yy-ya)**2)
err = np.mean(earr)

plt.subplot(1,2,2)
plt.plot(earr,'b-')
plt.xlabel('steps')
plt.ylabel('Error')
plt.title('Mean Average Error :' + str(err))
plt.show()

