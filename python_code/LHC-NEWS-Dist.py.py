import numpy as np
import matplotlib.pyplot as plt
import re
from mpl_toolkits.mplot3d import axes3d
import glob

west = glob.glob('C:\Users\Ramakanth\Desktop\samples\*w_*.csv')
south = glob.glob('C:\Users\Ramakanth\Desktop\samples\*s_*.csv')
east = glob.glob('C:\Users\Ramakanth\Desktop\samples\*e_*.csv')
north = glob.glob('C:\Users\Ramakanth\Desktop\samples\*n_*.csv')
dcta = {}
dctb = {}
dctc = {}
dctd = {}
for i in range(min(len(west),len(south),len(north),len(east))):
     a = []
     a.append(re.split('\D+',west[i])[1])
     a.append(re.split('\D+',south[i])[1])
     a.append(re.split('\D+',east[i])[1])
     a.append(re.split('\D+',north[i])[1])
     xa,ya,za = np.loadtxt(west[i],delimiter=',', skiprows = 5, usecols=(2,3,4), unpack=True)
     xb,yb,zb = np.loadtxt(south[i],delimiter=',', skiprows = 5, usecols=(2,3,4), unpack=True)
     xc,yc,zc = np.loadtxt(east[i],delimiter=',', skiprows = 5, usecols=(2,3,4), unpack=True)
     xd,yd,zd = np.loadtxt(north[i],delimiter=',', skiprows = 5, usecols=(2,3,4), unpack=True)
     i = 0
     dctlis = [dcta,dctb,dctc, dctd]
     for k in [[xa,ya,za],[xb,yb,zb],[xc,yc,zc],[xd,yd,zd]]:
          xl  = np.mean(k[0])
          yl  = np.mean(k[1])
          zl  = np.mean(k[2])
          dctlis[i][int(a[i])] = np.sqrt(xl*xl + yl*yl + zl*zl)
          i += 1

def list_pos(dct):
     xl = []
     yl = []    
     zl = []
     for i in range(1,len(dct)):
         xl.append(int((i-1)/13))
         yl.append(int((i-1)%13))  
         if(i in dct):
            zl.append(dct[i])
         else:
            zl.append(dct[i-1])
         print i
     return (xl,yl,zl)


fig = plt.figure()
ax = fig.add_subplot(111, projection='3d')

xw = []
yw = []
zw = []
xs = []
ys = []
zs = []
xe = []
ye = []
ze = []
xn = []
yn = []
zn = []

print dctlis[0],dctlis[1],dctlis[2],dctlis[3]
xw,yw,zw = list_pos(dctlis[0])
xs,ys,zs = list_pos(dctlis[1])
xe,ye,ze = list_pos(dctlis[2])
print zs,ze,zw,zn
xn,yn,zn = list_pos(dctlis[3])

print zs,ze,zw,zn
ax.scatter(xw,yw,zw,c='b',marker ='o')
ax.scatter(xs,ys,zs,c='r',marker ='*')
ax.scatter(xe,ye,ze,c='g',marker ='+')
ax.scatter(xn,yn,zn,c='y',marker ='^')

ax.set_xlabel("x")
ax.set_ylabel("y")
ax.set_zlabel("Magnitude")
plt.show()
