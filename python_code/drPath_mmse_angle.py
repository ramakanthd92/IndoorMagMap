import numpy as np
import matplotlib.pyplot as plt
import decimal

#xd0,yd0 = np.loadtxt('C:\Users\Ramakanth\Dropbox\Thesis-Docs\ParticleFiltering\Z-value-PF\Third\drPathLog.2014-03-01-15-32-01.csv',delimiter=',', usecols=(0,1), unpack=True)
par,mp0,xp0,yp0 = np.loadtxt('C:\Users\RP\Dropbox\Thesis-Docs\ParticleFiltering\All 3 axis\Randomwalks\pfLog.2014-03-04-10-45-16.mmse.csv',delimiter=',', usecols=(0,1,2,3), unpack=True)
#xp1,yp1 = np.loadtxt('C:\Users\RP\Dropbox\Thesis-Docs\ParticleFiltering\All 3 axis\pfPathLog.2014-03-01-17-48-59.csv',delimiter=',', usecols=(0,1), unpack=True)

mx,my,mz,mm = np.loadtxt('C:\Users\RP\Dropbox\Thesis-Docs\ParticleFiltering\All 3 axis\Randomwalks\pfLog.2014-03-04-10-45-16.mag.csv',delimiter=',', usecols=(2,3,4,5), unpack=True) 
s,a = np.loadtxt('C:\Users\RP\Dropbox\Thesis-Docs\ParticleFiltering\All 3 axis\Randomwalks\pfLog.2014-03-04-10-45-16.pfsteps.csv',delimiter=',', usecols=(2,3), unpack=True)
step ,sen, turn = np.loadtxt('C:\Users\RP\Dropbox\Thesis-Docs\ParticleFiltering\All 3 axis\Randomwalks\pfLog.2014-03-04-10-45-08.noise.csv',delimiter=',', usecols=(0,1,2), unpack=True)
#step1 ,sen1, turn1 = np.loadtxt('C:\Users\Ramakanth\Dropbox\Thesis-Docs\ParticleFiltering\Z-value-PF\Third\pfLog.2014-03-01-05-47-46.noise.csv',delimiter=',', usecols=(0,1,2), unpack=True)

step = decimal.Decimal(step)
sen = decimal.Decimal(sen)
turn = turn * (180/np.pi)
turn = decimal.Decimal(turn)
step = round(step,2)
sen = round(sen,2)
turn = round(turn,2)

s0 = "Sense Noise = " + str(sen)
kn = "Initial Loncation known (1,0)"
ph = "(1,0) - >(1,50) ->(13,50) ->(13,0)->(1,0)"
par_count = par[0]

s = "3 axis-Magnetic Field based PF("+str(int(par_count))+ " particles) \n" + kn +"\n" + "Step Noise = " + str(step) + " *0.3 m \n"+ s0 + " microT\n" +"Turn Nosie = " + str(turn) + " deg \n" + ph

#s1 =  "Sense Noise = " + str(sen1)
#plt.subplot(1,2, 1)
#plt.title('Dead Reckoning - Known Initial Locaiton (1,50)')
#plt.xlabel('x')
#plt.plot(xd0,yd0,'bo-')

plt.subplot(2,2,1)
plt.title('Particle Filtered Reckoning - ' + kn ) 
plt.xlabel('x')
plt.ylabel('y')
plt.text(min(xp0),max(yp0)/2, s)
plt.plot(xp0,yp0,'ro-', label = s0)
#plt.plot(xp1,yp1,'bo-', label = s1)

plt.subplot(2,2,2)
plt.title('MMSE -' + kn ) 
plt.ylabel('MMSE')
plt.xlabel('steps')
plt.plot(mp0,'b+-')
plt.text(len(mp0)/2,max(mp0)/2, s)

plt.subplot(2,2,3)
plt.ylabel('MMSE')
plt.xlabel('steps')
plt.plot(a,'g*-', label = 'angle')

plt.subplot(2,2,4)
plt.ylabel('MagneticField')
plt.xlabel('steps')
plt.plot(mx,'b-', label = 'x')
plt.plot(my,'r-', label = 'y')
plt.plot(mz,'g-', label = 'z')
plt.plot(mm,'m-', label = 'mag')
legend = plt.legend(loc='best', shadow=True)
frame = legend.get_frame()
frame.set_facecolor('0.90')

plt.show()

