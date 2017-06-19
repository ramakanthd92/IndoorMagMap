import numpy as np
import matplotlib.pyplot as plt
import decimal
#xd0,yd0 = np.loadtxt('C:\Users\Ramakanth\Dropbox\Thesis-Docs\ParticleFiltering\Z-value-PF\Third\drPathLog.2014-03-01-15-32-01.csv',delimiter=',', usecols=(0,1), unpack=True)
xp0,yp0 = np.loadtxt('C:\Users\Ramakanth\Dropbox\Thesis-Docs\ParticleFiltering\Z-value-PF\Third\pfPathLog.2014-03-01-18-15-39.csv',delimiter=',', usecols=(0,1), unpack=True)
xp1,yp1 = np.loadtxt('C:\Users\Ramakanth\Dropbox\Thesis-Docs\ParticleFiltering\Z-value-PF\Third\pfPathLog.2014-03-01-18-16-59.csv',delimiter=',', usecols=(0,1), unpack=True)

s,a = np.loadtxt('C:\Users\Ramakanth\Dropbox\Thesis-Docs\ParticleFiltering\Z-value-PF\Third\pfLog.2014-03-01-06-14-35.pfsteps.csv',delimiter=',', usecols=(2,3), unpack=True)
s1,a1 = np.loadtxt('C:\Users\Ramakanth\Dropbox\Thesis-Docs\ParticleFiltering\Z-value-PF\Third\pfLog.2014-03-01-06-16-02.pfsteps.csv',delimiter=',', usecols=(2,3), unpack=True)

step ,sen, turn = np.loadtxt('C:\Users\Ramakanth\Dropbox\Thesis-Docs\ParticleFiltering\Z-value-PF\Third\pfLog.2014-03-01-06-14-32.noise.csv',delimiter=',', usecols=(0,1,2), unpack=True)
step1 ,sen1, turn1 = np.loadtxt('C:\Users\Ramakanth\Dropbox\Thesis-Docs\ParticleFiltering\Z-value-PF\Third\pfLog.2014-03-01-06-16-00.noise.csv',delimiter=',', usecols=(0,1,2), unpack=True)


step = decimal.Decimal(step)
sen = decimal.Decimal(sen)
turn = turn * (180/np.pi)
turn = decimal.Decimal(turn)
step = round(step,2)
sen = round(sen,2)
turn = round(turn,2)

step1 = decimal.Decimal(step1)
sen1 = decimal.Decimal(sen1)
turn1 = turn1 * (180/np.pi)
turn1 = decimal.Decimal(turn1)
step1 = round(step1,2)
sen1 = round(sen1,2)
turn1 = round(turn1,2)


s0 =  "Sense = "+str(sen)+"uT "
t0 = "Step = " + str(step)+"*0.3 m \n"
p0 = "Turn = " + str(turn)+"deg"

r0 = s0+p0
s = "Z-Magnetic Field based PF \n" + "Initial Location not known \n" + "Step Noise = " + str(step) + " *0.3 m \n"+ s0 + " microT\n" +"Turn Nosie = " + str(turn) + " deg \n"

s1 =  "Sense = "+str(sen1)+"uT "
t1 = "Step = " + str(step1)+"*0.3 m \n"
p1 = "Turn = " + str(turn1)+"deg "
r1 =s1+p1


#plt.subplot(1,2, 1)
#plt.title('Dead Reckoning - Known Initial Locaiton (1,50)')
#plt.xlabel('x')
#plt.plot(xd0,yd0,'bo-')

plt.subplot(1,2,1)
plt.title('Particle Filtered Reckoning - Initial Location Not known') 
plt.xlabel('x')
plt.ylabel('y')
#plt.text(7,10, s)
plt.plot(xp0,yp0,'b+-', label = r0)
plt.plot(xp1,yp1,'r+-', label = r1)
legend = plt.legend(loc='best', shadow=True)
frame = legend.get_frame()
frame.set_facecolor('0.90')


#plt.subplot(1,3,2)
#plt.title('MMSE - Initial Location Not known') 
#plt.ylabel('MMSE')
#plt.xlabel('steps')
#plt.plot(mp0,'b+-')
#plt.text(4,10, s)

plt.subplot(1,2,2)
plt.ylabel('Angle')
plt.xlabel('steps')
plt.plot(a,'b*-', label = r0)
plt.plot(a1,'r*-', label = r1)

legend = plt.legend(loc='best', shadow=True)
frame = legend.get_frame()
frame.set_facecolor('0.90')

plt.show()

