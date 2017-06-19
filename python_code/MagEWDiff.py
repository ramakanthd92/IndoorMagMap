import math 
import numpy as np
#from scipy import interpolate
from matplotlib.mlab import griddata
import matplotlib.pyplot as plt
import matplotlib.mlab as mlab
import json
import pylab

json_data_0=open('C:\Users\Ramakanth\Dropbox\Thesis-Docs\library_samples\data-west-6.json')
json_data_1=open('C:\Users\Ramakanth\Dropbox\Thesis-Docs\library_samples\data-east-6.json')
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
#data0= {}

mini = 0
maxi = 405
#for i in range(162,216):
#    data0[str(i)] = data2[str(i)]

def rotate(rv,vec):
    k = [0.0,0.0,0.0]
    k[0] = rv[0]*vec[0] + rv[1]*vec[1] + rv[2]*vec[2] 
    k[1] = rv[3]*vec[0] + rv[4]*vec[1] + rv[5]*vec[2]
    k[2] = rv[6]*vec[0] + rv[7]*vec[1] + rv[8]*vec[2]
    return k               

data4 = {}
data5 = {}

k = data0.keys()
l = np.array(k[:])
xoff = 0.0
yoff = 0.0

ka = k[0:81:1]
kb = k[81:162:1]

lx = []
mx = []
ly = []
my = []


for j in k:
    vecte = data0[j]
    vectw = data1[j]
    lx.append(vecte[0])
    mx.append(vectw[0])
    ly.append(vecte[1])
    my.append(vectw[1])
    xoff +=(vecte[0] + vectw[0])/2
    yoff += (vecte[1] + vectw[1])/2

xoff = xoff/len(k)
yoff = yoff/len(k)
print "xoff ", xoff, "yoff", yoff

for i in k:
    vecte = []
  #  vectw = []
    vecte.append(data0[i][0])
    vecte.append(data0[i][1])
    vecte.append(data0[i][2])
    #rote = data2[i]
#    vectw.append(data1[i][0]-xoff)
 #   vectw.append(data1[i][1]-yoff)
#    vectw.append(data1[i][2])
    data4[i] = vecte
 #   data5[i] = vectw
  #  data4[i] = rotate(rote,vecte)    
  #  data5[i] = rotate(rotw,vectw)
    data4[i].append(np.sqrt(vecte[0]**2 + vecte[1]**2 +vecte[2]**2))
  #  data5[i].append(np.sqrt(vectw[0]**2 + vectw[1]**2 +vectw[2]**2))

#f = open('C:\Users\Ramakanth\Dropbox\Thesis-Docs\library_samples\data-west-offset-2.json','wb')
#json.dump(data5,f)
#f.close()

#f = open('C:\Users\Ramakanth\Dropbox\Thesis-Docs\library_samples\data-east-offset-2.json','wb')
#json.dump(data4,f)
#f.close()

z0 = []
z1 = []
x0 = []
x1 = []
y0 = []
y1 = []
z2 = []
z3 = []
z4 = []
z5 = []
k = data0.keys()
k.sort()
j = 0
for i in range(mini,maxi):
 #  z0.append(data4[i][0])
 #  z1.append(data5[i][0])
   z2.append(data0[str(i)][0])
   z3.append(data1[str(i)][0])
 #  z4.append(data2[i][0])
 #  z5.append(data3[i][0])
   
plt.subplot(2, 2, 1)
#plt.plot(z0,'r+-',label ='east-offset')
#plt.plot(z1,'b+-',label ='west-offset')
plt.plot(z2,'g-',label ='west',linewidth =1.5)
plt.plot(z3,'m-',label ='east',linewidth =1.5)
#plt.plot(z4,'c+-',label ='east-wrong')
#plt.plot(z5,'k+-',label ='west-wrong')

plt.xlabel('Location Index')
plt.ylabel('X-value')
plt.ylim(-70,70)
plt.title('Magnetic field - X-axis')
legend = plt.legend(loc='best', shadow=True)
frame = legend.get_frame()
frame.set_facecolor('0.90')
print np.mean(np.array(z2)-np.array(z3))


z0=[]
z1=[]
x0= []
x1= []
y0= []
y1=[]
z2= []
z3= []
z4 = []
z5 = []

k = data0.keys()
k.sort()
j = 0
for i in range(mini,maxi):
   #z0.append(data4[i][1])
   #z1.append(data5[i][1])
   z2.append(data0[str(i)][1])
   z3.append(data1[str(i)][1])
   #z4.append(data2[i][1])
   #z5.append(data3[i][1])
   
plt.subplot(2, 2, 2)
#plt.plot(z0,'r+-',label ='east-offset')
#plt.plot(z1,'b+-',label ='west-offset')
plt.plot(z2,'g-',label ='west',linewidth =1.5)
plt.plot(z3,'m-',label ='east',linewidth =1.5)
#plt.plot(z4,'c+-',label ='east-wrong')
#plt.plot(z5,'k+-',label ='west-wrong')

plt.xlabel('Location Index')
plt.ylabel('Y-value')
plt.title('Magnetic field - Y-axis')
legend = plt.legend(loc='best', shadow=True)
frame = legend.get_frame()
frame.set_facecolor('0.90')
print np.mean(np.array(z2)-np.array(z3))


z0 =[]
z1 =[]
x0 = []
x1 = []
y0 = []
y1 = []
z2 = []
z3 = []
z4 = []
z5 = []

k = data0.keys()
k.sort()
j = 0
for i in range(mini,maxi):
 #  z0.append(data4[i][2])
  # z1.append(data5[i][2])
   z2.append(data0[str(i)][2])
   z3.append(data1[str(i)][2])
  # z4.append(data2[i][2])
  # z5.append(data3[i][2])
   
plt.subplot(2, 2, 3)
#plt.plot(z0,'r+-',label ='east-offset')
#plt.plot(z1,'b+-',label ='west-offset')
plt.plot(z2,'g-',label ='west',linewidth =1.5)
plt.plot(z3,'m-',label ='east',linewidth =1.5)
#plt.plot(z4,'c+-',label ='east-wrong')
#plt.plot(z5,'k+-',label ='west-wrong')

plt.xlabel('Location Index')
plt.ylabel('Z-value')
plt.title('Magnetic field - Z-axis')

legend = plt.legend(loc='best', shadow=True)
frame = legend.get_frame()
frame.set_facecolor('0.90')
print np.mean(np.array(z2)-np.array(z3))

z0 =[]
z1 =[]
z2 = []
z3 = []
x0 = []
x1 = []
y0 = []
y1 = []
z4 = []
z5 = []

k = data0.keys()
k.sort()
j = 0
for i in range(mini,maxi):
 #  z0.append(data4[i][3])
 #  z1.append(data5[i][3])
   z2.append(data0[str(i)][3])
   z3.append(data1[str(i)][3])
 #  z4.append(data2[i][3])
 #  z5.append(data3[i][3])
   
plt.subplot(2,2,4)
#plt.plot(z0,'r+-',label ='east-offset')
#plt.plot(z1,'b+-',label ='west-offset')
plt.plot(z2,'g-',label ='west',linewidth =1.5)
plt.plot(z3,'m-',label ='east',linewidth =1.5)
#plt.plot(z4,'c+-',label ='east-wrong')
#plt.plot(z5,'k+-',label ='west-wrong')
print np.mean(np.array(z2)-np.array(z3))

plt.ylabel('Magnetic Field Magnitude')
plt.xlabel('Location Index')
plt.title('Magnetic field Magnitude')
legend = plt.legend(loc='best', shadow=True)
frame = legend.get_frame()
frame.set_facecolor('0.90')
plt.show() 

num_bins = 50
n, bins, patches = plt.hist(z2, num_bins, normed=1, facecolor='green', alpha=0.5)
# add a 'best fit' line
mu = np.mean(z2)
sigma = np.std(z2)
y = mlab.normpdf(bins, mu, sigma)
plt.plot(bins, y, 'r--')
plt.xlabel('Magnetic field ($\mu$T)', size = 20)
plt.ylabel('Probability', size = 20)
plt.title("Histogram of MF-west: $\mu="+str(mu)+"$, $\sigma=" + str(sigma)+"$" , size =20)

plt.show()

