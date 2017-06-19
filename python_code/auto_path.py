import numpy as np
import glob
import matplotlib.pyplot as plt
import csv
import re

folder = 'C:\Users\Ramakanth\Dropbox\Thesis-Docs\Experiments\pfahmp'
west = glob.glob('C:\Users\Ramakanth\Dropbox\Thesis-Docs\Experiments\pfahmp\pfPathLog.2014-05-14*.csv')

for i in range(len(west)):
    a = re.split('\D+',west[i])
    l = a[1] + '/' + a[2] + '/' + a[3] + '   ' +  a[4] + ':' + a[5] + ':' + a[6]
    xa = []
    ya = [] 
    xl = []
    yl = [] 
    xx,yy = np.loadtxt(west[i],delimiter=',', usecols=(0,1), unpack=True)
    for j in range(1,len(xx)):
        xa.append(xx[j]-xx[j-1])
        if(abs(xx[j]-xx[j-1]) > 0.6):
            xl.append(j)
        ya.append(yy[j]-yy[j-1])
        if(abs(yy[j]-yy[j-1]) < 0.3):
            yl.append(j)
    print xl       
    if (xx[0] == 13.0 and yy[0] == 0.0):
        j = 0
        p = []
        sx = [13.0,10.0,7.0,4.0,1.0]
        sy = [1.0,25.0]    
        p.append([1.0,0.0])
        rx = 0
        ry = 0
        l = len(xx)
        jl = len(xl)
        hr = 2
        while hr  < l-10:
            #hr += 1            
            if(j < jl):
                start = xl[j]-1
               # print xl[j]
                while  (j+1 < jl) and (xl[j+1]-xl[j]) < 6:
                    j += 1
                end = xl[j]-1
               # print xl[j]
            else:
                start = len(xx)            

            h = sy[ry]
            
            if(start-hr != 0):        
                dh = (sy[(ry+1)%2] - sy[ry])/(start-hr)

            ry = (ry + 1)%2        
            x = sx[rx]
            
            print str(start) + "  "+ str(hr)

            while (hr < start):
                p.append([x,h])
                h += dh   
                hr += 1
            p.append([x,h])
            print "a-" + str(hr)
            if(end-start!= 0):
                dx = (sx[(rx+1)%5]-sx[rx])/(end-start+1)                

            rx = (rx+1)%5        

            while(hr < end):
                x += dx
                p.append([x,h])
                hr += 1
            print "a-" + str(hr)
            hr += 1    
            j += 1
            
        with open(folder+ '\path_m_' + a[4] + '_' + a[5] + '_' + a[6]+'.csv', 'wb') as f:
            writer = csv.writer(f)
            writer.writerows(p)    
