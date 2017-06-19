import matplotlib.pyplot as plt
import numpy as np
im = plt.imread('C:\Users\Ramakanth\Documents\Python Scripts\library5.png')
implot = plt.imshow(im)

consx = 320/16
consy = 520/26

src  = 'C:\Users\Ramakanth\Dropbox\Thesis-Docs\Experiments'
s = '\dr\drPathLog.2014-05-01-12-15-37'
s1 ='\pfah_ve_mp\pfPathLog.2014-05-10-18-09-31'
s2 ='\pfahmgmp\pfPathLog.2014-05-11-17-51-50'
s3 ='\pfahmg\path_m2_15_05_10'
s4 ='\pfah_mp\pfPathLog.2014-05-04-16-29-49'
s5 ='\pfah_ve\pfPathLog.2014-05-10-17-57-00'

hr = s3

xx,yy = np.loadtxt( src + hr + '.csv',delimiter=',', usecols=(0,1), unpack=True)

px = []
py = []
for i in range(len(xx)):
    px.append( int(consx*float(xx[i])))
    py.append( int(consy*float(yy[i])))
    
plt.title(hr)
plt.plot(px,py,'b-',linewidth=2)
plt.show()
