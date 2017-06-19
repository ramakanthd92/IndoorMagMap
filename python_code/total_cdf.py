import numpy as np
import matplotlib.pyplot as plt
import glob
import re
import decimal
import pylab

src  = 'C:\Users\Ramakanth\Dropbox\Thesis-Docs\Experiments'
vp =  '\pfahvemp'
gp = '\pfahmgmp'
g = '\pfahmg'
v = '\pfahve'
h = '\pfPathLog.2014-05-14-'
b = '\path_g_17_13_47.csv'

westmg2 = glob.glob('C:\Users\Ramakanth\Dropbox\Thesis-Docs\Experiments\pfahmg\path_m2_15*.csv')
westmg3 = glob.glob('C:\Users\Ramakanth\Dropbox\Thesis-Docs\Experiments\pfahmg\path_m3_15*.csv')
westmga = glob.glob('C:\Users\Ramakanth\Dropbox\Thesis-Docs\Experiments\pfahmg\path_m3_14*.csv')
westmg = westmg3 + westmga + westmg2

westmgmp2 = glob.glob('C:\Users\Ramakanth\Dropbox\Thesis-Docs\Experiments\pfahmgmp\path_m2*.csv')
westmgmp3 = glob.glob('C:\Users\Ramakanth\Dropbox\Thesis-Docs\Experiments\pfahmgmp\path_m3*.csv')

westmgmp = westmgmp3 + westmgmp2

westve2 = glob.glob('C:\Users\Ramakanth\Dropbox\Thesis-Docs\Experiments\pfahve\path_m2*.csv')
westve3 = glob.glob('C:\Users\Ramakanth\Dropbox\Thesis-Docs\Experiments\pfahve\path_m3*.csv')

westve = westve3 + westve2

westvemp2 = glob.glob('C:\Users\Ramakanth\Dropbox\Thesis-Docs\Experiments\pfahvemp\path_m2*.csv')
westvemp3 = glob.glob('C:\Users\Ramakanth\Dropbox\Thesis-Docs\Experiments\pfahvemp\path_m3*.csv')

westvemp = westvemp3 + westvemp2

east = []
west = [westmg,westmgmp,westve,westvemp]
color = ['k-','ko-','k--','k+-']
fol = [g,gp,v,vp]
hl = [h,h,h,h]
labels = ['MagMagnitude','MagMagnitude + Indoor map' , 'MagVector','MagVector + Indoor map']
totearr = [[],[],[],[]]

l = ""
for j in range(4):
    for i in range(len(west[j])):
        a = re.split('\D+',west[j][i])
        l = fol[j] + h + a[2] + '-' + a[3] + '-' + a[4] + '.csv'
        xx,yy = np.loadtxt(src + l ,delimiter=',', usecols=(0,1), unpack=True)
        xa,ya = np.loadtxt(west[j][i],delimiter=',', usecols=(0,1), unpack=True)
        #plt.subplot(1,2,1)
        #plt.plot(xx,yy,'ro-',linewidth=2)
        #plt.plot(xa,ya,'bo-',linewidth=2)
        #lt.xlabel('x')
        #plt.ylabel('y')
        #plt.xlim(0,14)
        #plt.ylim(0,26)
        #plt.title(l)
        if(len(xx) != len(xa)):
            print src + l
        
        xx = np.array(xx)*0.56
        yy = np.array(yy)*0.56
        xa = np.array(xa)*0.56
        ya = np.array(ya)*0.56
        earr = np.sqrt((xx-xa)**2 + (yy-ya)**2)
        totearr[j] = totearr[j] + earr.tolist()
        err = np.mean(earr)
        
        #plt.subplot(1,2,2)
        #plt.plot(earr,'b-')
        #plt.xlabel('steps')
        #plt.ylabel('Error')
        #plt.title('Mean Average Error :' + str(err))
        #plt.show()    
#err = np.mean(np.sqrt(totearr))
    #num_bins = 100
    #n,bins,patches = plt.hist(totearr,num_bins,normed = 1,facecolor='green',alpha = 0.5)
    #plt.xlabel('Error (in m)',size = 15)
    #plt.ylabel('Probability', size = 15)
    #plt.title('Localization Error Histogram,Avg Mean Error' + str(np.mean(totearr)), size = 18)
    
    #plt.show()
    
    n_counts,bin_edges = np.histogram(totearr[j],bins=50,normed=True) 
    cdf = np.cumsum(n_counts)  # cdf not normalized, despite above
    scale = 1.0/cdf[-1]
    ncdf = scale * cdf    
    pylab.plot(bin_edges[1:],ncdf, color[j], label = labels[j] ,linewidth = 2)
    plt.xlabel('Error (in m)',size = 18)
    plt.ylabel('Cumulative Distribution Function' ,size =18)
    plt.title('Localization Error CDF' ,size =20)

legend = plt.legend(loc='best', shadow=True)
frame = legend.get_frame()
frame.set_facecolor('0.90')    
plt.show()    
