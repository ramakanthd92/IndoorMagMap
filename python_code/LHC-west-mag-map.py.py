import numpy as np
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import axes3d
import glob

walk = glob.glob('C:\Users\Ramakanth\Desktop\samples\*_w_*.csv')
    
xl = []
yl = []
zl = []
for i in range(len(walk)):
    x,y,z = np.loadtxt(walk[i],delimiter=',', skiprows = 5, usecols=(2,3,4), unpack=True)
    xl.append(np.mean(x))
    yl.append(np.mean(y))
    zl.append(np.mean(z))

fig = plt.figure()
ax = fig.add_subplot(111, projection='3d')
ax.scatter(xl,yl,zl,c='b',marker ='o')
ax.set_xlabel("Bx")
ax.set_ylabel("By")
ax.set_zlabel("Bz")
plt.show()
