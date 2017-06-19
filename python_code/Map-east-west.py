import math 
import numpy as np
#from scipy import interpolate
from matplotlib.mlab import griddata
import matplotlib.pyplot as plt
import json
import pylab

json_data_0=open('C:\Users\Ramakanth\Dropbox\Thesis-Docs\library_samples\data-west-2.json')
json_data_1=open('C:\Users\Ramakanth\Dropbox\Thesis-Docs\library_samples\data-east-2.json')
#json_data_2=open('C:\Users\Ramakanth\Dropbox\Thesis-Docs\library_samples\data-east-offset.json')
#json_data_3=open('C:\Users\Ramakanth\Dropbox\Thesis-Docs\library_samples\data-west-offset.json')

data0 = json.load(json_data_0)
data1 = json.load(json_data_1)
#data2 = json.load(json_data_2)
#data3 = json.load(json_data_3)

json_data_0.close()
json_data_1.close()
#json_data_2.close()
#json_data_3.close()

def rotate(rv,vec):
    k = [0.0,0.0,0.0]
    k[0] = rv[0]*vec[0] + rv[1]*vec[1] + rv[2]*vec[2] 
    k[1] = rv[3]*vec[0] + rv[4]*vec[1] + rv[5]*vec[2]
    k[2] = rv[6]*vec[0] + rv[7]*vec[1] + rv[8]*vec[2]
    return k               

data4 = {}
data5 = {}

k = data0.keys()
k.sort()
xoff = 0.0
yoff = 0.0

#for j in k:
 #   vecte = data0[j]
 #   vectw = data1[j]
 #   xoff += (vecte[0] + vectw[0])/2
 #   yoff += (vecte[1] + vectw[1])/2

#xoff = xoff/len(k)
#yoff = yoff/len(k)

#print "xoff ", xoff, "yoff", yoff

vecte = [[],[],[],[]]
vectw = [[],[],[],[]]
for i in k:    
    vecte[0].append(data1[i][0])
    vecte[1].append(data1[i][1])
    vecte[2].append(data1[i][2])
    vecte[3].append(data1[i][3])
    #rote = data2[i]
    vectw[0].append(data0[i][0])
    vectw[1].append(data0[i][1])
    vectw[2].append(data0[i][2])
    vectw[3].append(data0[i][3])
  #  data4[i] = vecte
  #  data5[i] = vectw
  #  data4[i] = rotate(rote,vecte)    
  #  data5[i] = rotate(rotw,vectw)
  #  data4[i].append(np.sqrt(vecte[0]**2 + vecte[1]**2 +vecte[2]**2))
  #  data5[i].append(np.sqrt(vectw[0]**2 + vectw[1]**2 +vectw[2]**2))

#f = open('C:\Users\Ramakanth\Dropbox\Thesis-Docs\library_samples\data-west-offset-2.json','wb')
#json.dump(data5,f)
#f.close()

#f = open('C:\Users\Ramakanth\Dropbox\Thesis-Docs\library_samples\data-east-offset-2.json','wb')
#json.dump(data4,f)
#f.close()


dct = {}
for i in range(11):
      xw = vectw[0][i*27: (i*27 + 26) :1]
      yw = vectw[1][i*27: (i*27 + 26) :1]
      zw = vectw[2][i*27: (i*27 + 26) :1]
      magw = vectw[3][i*27: (i*27 + 26) :1]

      xe = vecte[0][i*27: (i*27 + 26) :1]
      ye = vecte[1][i*27: (i*27 + 26):1]
      ze = vecte[2][i*27: (i*27 + 26) :1]
      mage = vecte[3][i*27: (i*27 + 26) :1]
      
      plt.subplot(4,3,i+1)
      plt.title("path " + str(i) + " West + East")
      plt.plot(xw,'b-',label = 'x-w')
      plt.plot(yw,'g-',label = 'y-w')
      plt.plot(xe,'b--',label = 'x-e')
      plt.plot(ye,'g--',label = 'y-e')
      plt.plot(zw,'r-',label = 'z-w')
      plt.plot(ze,'r--',label = 'z-e')
      plt.plot(magw,'k-',label = 'mag-w')
      plt.plot(mage,'k--',label = 'mag-e')     
legend = plt.legend(loc='best', shadow=True)
frame = legend.get_frame()
frame.set_facecolor('0.90')
plt.show()

