import numpy as np
import matplotlib.pyplot as plt
#from mpl_toolkits.mplot3d import axes3d
import glob

walk = glob.glob('C:\Users\Ramakanth\Desktop\samples\*stairs*down*.csv')
x = []
y = []
z = []
mag =[]
for i in range(len(walk)):
    x,y,z = np.loadtxt(walk[i],delimiter=',',skiprows = 0, usecols=(2,3,4), unpack=True)
    mag =[]
    for i in range(len(x)):
        mag.append(np.sqrt(x[i]**2 + y[i]**2 + z[i]**2))
    print "mag-stddev:",np.std(mag),"x-stddev:",np.std(x), "     y-stddev:",np.std(y) , "       z-stddev:",np.std(z)


