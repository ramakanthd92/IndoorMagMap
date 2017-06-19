import matplotlib.pyplot as plt
import numpy as np
import re
from matplotlib.mlab import griddata
from mpl_toolkits.mplot3d import axes3d
import glob

walk = glob.glob('C:\Users\Ramakanth\Desktop\library_samples\LHC\*w_*.csv')
dct = {}

for i in range(len(walk)):
     a = re.split('\D+',walk[i])
     x,y,z = np.loadtxt(walk[i],delimiter=',', skiprows = 5, usecols=(2,3,4), unpack=True)
     xl  = np.mean(x)
     yl  = np.mean(y)
     zl  = np.mean(z)
     dct[int(a[1])] = np.sqrt(xl*xl + yl*yl + zl*zl)
     
xl = []
yl = []    
zl = []
j = 1

print len(dct)

for i in range(1,len(dct)+1):
    xl.append(int((i-1)/13))
    yl.append(int((i-1)%13))  
    if i in dct:
         zl.append(dct[i])
         j = i
    else:
         print "i",i
         zl.append(dct[j])

xi = np.linspace(0,7,8)
yi = np.linspace(0,12,13)
zi = griddata(xl,yl,zl,xi,yi,interp='linear')
CS = plt.contour(xi,yi,zi,20,linewidths=0.5,colors='k')
CS = plt.contourf(xi,yi,zi,20)

plt.colorbar()
plt.scatter(xl,yl,marker='o',c='b',s=5,zorder=10)
plt.title('griddata test')
plt.show()

fig = plt.figure()
ax = fig.add_subplot(111, projection='3d')
ax.scatter(xl,yl,zl,c='b',marker ='o')
ax.set_xlabel("x")
ax.set_ylabel("y")
ax.set_zlabel("Magnitude")
plt.show()

print np.std(zl)

num_bins = 300
n,bins,patches = plt.hist(zl,num_bins,normed = 1,facecolor='green',alpha = 0.5)
plt.xlabel('Magnitude')
plt.ylabel('Probability')
plt.title('Histogram of Magnetic field distribution_east')
plt.show()

