import numpy as np
import matplotlib.pyplot as plt
import decimal
#gx,gy,gz = np.loadtxt('C:\Users\Ramakanth\Dropbox\Thesis-Docs\ParticleFiltering\Magnitude\Loc_c2_2014-03-23-04-36-52.sensor.csv',delimiter=',', usecols=(10,11,12), unpack=True)
#px,py,mx,my,mz = np.loadtxt('C:\Users\Ramakanth\Dropbox\Thesis-Docs\ParticleFiltering\Magnitude\pfLog.2014-03-19-09-03-42.mmse.csv',delimiter=',', usecols=(4,5,7,8,9), unpack=True)

src  = 'C:\Users\Ramakanth\Dropbox\Thesis-Docs\ParticleFiltering'
s = '\pfah_ve_mp'
s1 = '\Magnitude'
h = '\pfLog.2014-03-24-'
a = '\pfPathLog.2014-03-24-13-50-34.csv'

gt,ta,npa,oi= np.loadtxt( src + s1 + h +'01-47-39.mmse.csv',delimiter=',', usecols=(8,9,10,11), unpack=True)
ss,ra = np.loadtxt(src + s1 + h +'01-47-39.pfsteps.csv',delimiter=',', usecols=(2,3), unpack=True)
xx,yy = np.loadtxt(src + s1 + a ,delimiter=',', usecols=(0,1), unpack=True)
step ,sen, turn = np.loadtxt(src + s1 + h + '01-43-55.noise.csv',delimiter=',', usecols=(0,1,2), unpack=True)
#cx,cy = np.loadtxt('C:\Users\Ramakanth\Dropbox\Thesis-Docs\ParticleFiltering\Magnitude\drLog.2014-03-11-06-10-11.steps.csv',delimiter=',', usecols=(2,3), unpack=True)
#l = len(mx2)
#mx3 = mx2[l-1:0:-1]
#my3 = my2[l-1:0:-1]
#mz3 = mz2[l-1:0:-1]

step = decimal.Decimal(step)
sen = decimal.Decimal(sen)
turn = turn * (180/np.pi)
turn = decimal.Decimal(turn)
step = round(step,2)
sen = round(sen,2)
turn = round(turn,2)
k = s + a
s = "Step-" + str(step) + " *0.6 m  " + "Sense-" + str(sen) + " uT " + "Turn-" + str(turn) + " deg \n"
cons = 180/np.pi
plt.subplot(1,1,1)
plt.title('Particle Filter- Avg Orietation,Rad Angle') 
plt.ylabel('Avg. orientation')
plt.xlabel('Steps')

#plt.plot(np.array(gx),'g-', label = 'X-angle')
#plt.plot(np.array(gy),'r-', label = 'Y-angle')
#plt.plot(np.array(gz),'b-', label = 'Z-angle')

#plt.plot(np.array(oi)*cons,'c+-', label = 'Avg orientation')
#plt.plot(np.array(npa),'b+-', label = 'particle Number')
#plt.plot(np.array(ta),'k-', label = 'Turn Adjustment')
#plt.plot(np.array(ss),'g-', label = 'Step_Size')
plt.plot(np.array(ra),'r-', label = 'Rad_Angle',linewidth = 2)

#mag = np.sqrt(mx**2 + my**2 + mz**2)


legend = plt.legend(loc='best', shadow=True)
frame = legend.get_frame()
frame.set_facecolor('0.90')

a = np.arange(2,26,1)
fb = np.arange(0,14,1)
f = np.arange(2,14,1)
g = np.arange(13,26,1)
h = np.arange(13,1,-1)
i = np.arange(25,12,-1)
b = np.arange(1,5,1)
c = np.arange(4,8,1)
d = np.arange(7,11,1)
e = np.arange(10,14,1)
def rep(x,l):
    k = [x]*l
    return k
#plt.subplot(1,2,2)
#plt.title('Particle Filter based path Estimate' + "\n" + s  + k)
#plt.plot(xx,yy,'ro-',linewidth=2)

#plt.xlabel('x')
#plt.ylabel('y')
#plt.xlim(0,16)
#plt.ylim(0,26)
#plt.title(s + k ) 
#plt.xlabel('magnetic field')
#plt.ylabel('readings')
#plt.plot(np.array(oi)*cons,'m+-', label = 'com-y')
#legend = plt.legend(loc='best', shadow=True)
#frame = legend.get_frame()
#frame.set_facecolor('0.90')

plt.show()
