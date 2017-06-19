import matplotlib.pyplot as plt
import numpy as np
import glob
import re
consx = 320/16
consy = 520/26

src  = 'C:\Users\Ramakanth\Dropbox\Thesis-Docs\Experiments'
d = '\dr'
vp ='\pfah_ve_mp'
gp ='\pfahmgmp'
g ='\pfahmg'
p ='\pfahmp'
v ='\pfahve'

west = glob.glob('C:\Users\Ramakanth\Dropbox\Thesis-Docs\Experiments\pfahvemp\pfLog.2014-05-14*mmse*.csv')
k = '\pfPathLog.2014-05-09-'

for i in range(len(west)):
        #im = plt.imread('C:\Users\Ramakanth\Documents\Python Scripts\library5.png')
        #implot = plt.imshow(im)
        a = re.split('\D+',west[i])
        l = vp  + '   ' +  a[1] + '/' + a[2] + '/' + a[3] + '   ' +  a[4] + ':' + a[5] + ':' + a[6]        
        #xx,yy = np.loadtxt(west[i],delimiter=',', usecols=(0,1), unpack=True)
        #xa,ya = np.loadtxt(west[j][i],delimiter=',', usecols=(0,1), unpack=True)
        #px = []
        #py = []
        #for i in range(len(xx)):
         #   px.append( int(consx*float(xx[i])))
          #  py.append( int(consy*float(yy[i])))    
        #plt.title(l)
        #plt.plot(px,py,'bo-',linewidth=1)
        #plt.show()
        s,t = np.loadtxt(west[i] ,delimiter=',', usecols=(0,1), unpack=True)
       # print  l
       # print  'step   ' + str(s)
       # print  'sense  ' + str(n)
       # print  'turn   ' +  str(a*(180/np.pi))
        print l
        print "particles " + str(s[0])
