import numpy as np
import matplotlib.pyplot as plt
import glob
import re
import decimal
import pylab

src = 'C:\Users\Ramakanth\Dropbox\Thesis-Docs\Experiments'
vp =  '\pfahvemp'
gp = '\pfahmgmp'
g = '\pfahmg'
p = '\pfahmp'
v = '\pfahve'
k = '\pfPathLog.2014-05-14-'
a = '\pfPathLog.2014-05-08-17-13-47.csv'

west2 = glob.glob('C:\Users\Ramakanth\Dropbox\Thesis-Docs\Experiments\pfahvemp\path_m3*.csv')
west3 = glob.glob('C:\Users\Ramakanth\Dropbox\Thesis-Docs\Experiments\pfahvemp\path_m2*.csv')
west = west2+ west3

totearr = []
l = ""
lr0 = ''
lrf = ''
for i in range(len(west)):
    a = re.split('\D+',west[i])
    l = vp + k + a[2] + '-' + a[3] + '-' + a[4] + '.csv'
    lr ='2014/5/14  ' +  a[2] + ':' + a[3] + ':' + a[4]
    if(i == 0):
        lr0 = lr
    if(i == len(west)-1):
        lrf = lr        
    xx,yy = np.loadtxt(src + l ,delimiter=',', usecols=(0,1), unpack=True)
    xa,ya = np.loadtxt(west[i],delimiter=',', usecols=(0,1), unpack=True)
    #plt.subplot(1,2,1)
    #plt.plot(xx,yy,'ro-',linewidth=2)
    #plt.plot(xa,ya,'bo-',linewidth=2)
    #plt.xlabel('x')
    #plt.ylabel('y')
    #plt.xlim(0,14)
    #plt.ylim(0,26)
    #plt.title(lr)
    xx = np.array(xx)*0.56
    yy = np.array(yy)*0.56
    xa = np.array(xa)*0.56
    ya = np.array(ya)*0.56
    earr = np.sqrt((xx-xa)**2 + (yy-ya)**2)
    totearr = totearr + earr.tolist()
    err = np.mean(earr)
    #plt.subplot(1,2,2)
    #plt.plot(earr,'b-')
    #plt.xlabel('steps')
    #plt.ylabel('Error')
    #plt.title('Mean Average Error :' + str(err))
    #plt.show()    
    #err = np.mean(np.sqrt(totearr))
num_bins = 100
n,bins,patches = plt.hist(totearr,num_bins,normed = 1,facecolor='green',alpha = 0.5)
plt.xlabel('Error (in m)',size = 18)
plt.ylabel('Frequency', size = 18)
plt.title("Vector + Indoor Map"  +'\n Avg Mean Error ' + str(np.mean(totearr)), size = 18)
plt.show()
    
n_counts,bin_edges = np.histogram(totearr,bins=50,normed=True) 
cdf = np.cumsum(n_counts)  # cdf not normalized, despite above
scale = 1.0/cdf[-1]
ncdf = scale * cdf    
pylab.plot(bin_edges[1:],ncdf ,linewidth = 2)
plt.xlabel('Error (in m)',size = 20)
plt.ylabel('Cumulative Distribution Function' ,size =20)
plt.title( "Vector + Indoor Map" +'\n Avg Mean Error ' + str(np.mean(totearr)), size = 20)

#legend = plt.legend(loc='best', shadow=True)
#frame = legend.get_frame()
#frame.set_facecolor('0.90')    
plt.show()    
