import numpy as np
import matplotlib.pyplot as plt
import re
from mpl_toolkits.mplot3d import axes3d
import glob

walk = glob.glob('C:\Users\Ramakanth\Desktop\samples\*n_*.csv')
dct = {}
for i in range(len(walk)):
     a = re.split('\D+',walk[i])
     x,y,z = np.loadtxt(walk[i],delimiter=',', skiprows = 5, usecols=(2,3,4), unpack=True)
     xl  = np.mean(x)
     yl  = np.mean(y)
     zl  = np.mean(z)
     dct[int(a[1])] = np.sqrt(xl*xl + yl*yl + zl*zl)
     
fig = plt.figure()
ax = fig.add_subplot(111, projection='3d')
xl = []
yl = []    
zl = []
for i in range(1,len(dct)):
    xl.append(int((i-1)/13))
    yl.append(int((i-1)%13))  
    zl.append(dct[i])

ax.scatter(xl,yl,zl,c='b',marker ='o')
ax.set_xlabel("x")
ax.set_ylabel("y")
ax.set_zlabel("Magnitude")
plt.show()
