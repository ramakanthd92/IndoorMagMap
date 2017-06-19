import numpy as np
import matplotlib.pyplot as plt
import decimal

#xd0,yd0 = np.loadtxt('C:\Users\Ramakanth\Dropbox\Thesis-Docs\ParticleFiltering\Z-value-PF\Third\dRamakanthathLog.2014-03-01-15-32-01.csv',delimiter=',', usecols=(0,1), unpack=True)
par,mp0,xp0,yp0,gt,oi = np.loadtxt('C:\Users\Ramakanth\Dropbox\Thesis-Docs\ParticleFiltering\Magnitude\pfLog.2014-03-19-09-38-06.mmse.csv',delimiter=',', usecols=(0,1,2,3,10,11), unpack=True)
#xp1,yp1 = np.loadtxt('C:\Users\Ramakanth\Dropbox\Thesis-Docs\ParticleFiltering\All 3 axis\pfPathLog.2014-03-01-17-48-59.csv',delimiter=',', usecols=(0,1), unpack=True)

mx,my,mz,mm = np.loadtxt('C:\Users\Ramakanth\Dropbox\Thesis-Docs\ParticleFiltering\Magnitude\pfLog.2014-03-19-09-38-06.mag.csv',delimiter=',', usecols=(2,3,4,5), unpack=True) 
s,a = np.loadtxt('C:\Users\Ramakanth\Dropbox\Thesis-Docs\ParticleFiltering\Magnitude\pfLog.2014-03-19-09-38-06.pfsteps.csv',delimiter=',', usecols=(2,3), unpack=True)
step ,sen, turn = np.loadtxt('C:\Users\Ramakanth\Dropbox\Thesis-Docs\ParticleFiltering\Magnitude\pfLog.2014-03-19-09-37-59.noise.csv',delimiter=',', usecols=(0,1,2), unpack=True)
#step1 ,sen1, turn1 = np.loadtxt('C:\Users\Ramakanth\Dropbox\Thesis-Docs\ParticleFiltering\Z-value-PF\Third\pfLog.2014-03-01-05-47-46.noise.csv',delimiter=',', usecols=(0,1,2), unpack=True)

step = decimal.Decimal(step)
sen = decimal.Decimal(sen)
turn = turn * (180/np.pi)
turn = decimal.Decimal(turn)
step = round(step,2)
sen = round(sen,2)
turn = round(turn,2)

s0 = "Sense Noise = " + str(sen)
kn = "Initial Loncation (7,0) known "
ph = "(7,0) - >(7,50) ->(4,50) ->(4,0)->(7,0) mostly"
par_count = par[0]
cons = (180./np.pi)

s =  str(int(par_count))+ " particles   " + kn +"\n" + "Step Noise = " + str(step) + " *0.3 m  "+ s0 + " microT " +"Turn Nosie = " + str(turn) + " deg \n " + ph

#s1 =  "Sense Noise = " + str(sen1)
#plt.subplot(1,2, 1)
#plt.title('Dead Reckoning - Known Initial Locaiton (1,50)')
#plt.xlabel('x')
#plt.plot(xd0,yd0,'bo-')
plt.suptitle(s)
plt.subplot(2,2,1)
plt.title('Particle Filtered Reckoning - ' + kn ) 
plt.xlabel('x')
plt.ylabel('y')
#plt.text(max(xp0)/2,max(yp0)/2, s)
plt.plot(xp0,yp0,'b*-', label = s0)
plt.xlim(-6,20)
plt.ylim(0,26)

#plt.plot(xp1,yp1,'bo-', label = s1)

plt.subplot(2,2,2)
plt.title('MMSE -' + kn ) 
plt.ylabel('MMSE')
plt.xlabel('steps')
plt.plot(mp0,'b+-')
#plt.text(len(mp0)/2,max(mp0)/2, s)

plt.subplot(2,2,3)
plt.ylabel('particles Orientation Average')
plt.xlabel('steps')
plt.plot(np.array(oi)*cons,'g*-', label = 'angle')

plt.subplot(2,2,4)
plt.ylabel('MagneticField')
plt.xlabel('steps')
#plt.plot(mx,'b-', label = 'x')
#plt.plot(my,'r-', label = 'y')
#plt.plot(mz,'g-', label = 'z')
plt.plot(mm,'m-', label = 'mag')
legend = plt.legend(loc='best', shadow=True)
frame = legend.get_frame()
frame.set_facecolor('0.90')

plt.show()

