import numpy as np
import matplotlib.pyplot as plt
import glob

west = glob.glob('C:\Users\RP\Desktop\library_samples\orientation\magneticCF\*.csv')


for i in range(len(west)):
    xa,ya,za,xm,ym,zm,xf,yf,zf = np.loadtxt(west[i],delimiter=',', skiprows = 5, usecols=(0,1,2,3,4,5,6,7,8), unpack=True)
    plt.plot(xa,'b',xm,'r',xf,'k')
    plt.show()
    plt.plot(ya,'b',ym,'r',yf,'k')
    plt.show()
    plt.plot(za,'b',zm,'r',zf,'k')
    plt.show()
    
