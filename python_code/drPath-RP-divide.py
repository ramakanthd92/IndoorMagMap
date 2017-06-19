import numpy as np
import matplotlib.pyplot as plt
import decimal
xd0,yd0 = np.loadtxt('C:\Users\Ramakanth\Dropbox\Thesis-Docs\ParticleFiltering\Z-value-PF\debug\drPathLog.2014-03-06-16-25-05.csv',delimiter=',', usecols=(0,1), unpack=True)
xp0,yp0 = np.loadtxt('C:\Users\Ramakanth\Dropbox\Thesis-Docs\ParticleFiltering\Magnitude\pfPathLog.2014-03-19-21-06-27.csv',delimiter=',', usecols=(0,1), unpack=True)
step ,sen, turn = np.loadtxt('C:\Users\Ramakanth\Dropbox\Thesis-Docs\ParticleFiltering\Magnitude\pfLog.2014-03-19-09-37-59.noise.csv',delimiter=',', usecols=(0,1,2), unpack=True)
#p0,p1,p2,m0,m1,m2 =  np.loadtxt('C:\Users\Ramakanth\Dropbox\Thesis-Docs\ParticleFiltering\Magnitude\pfPathLog.2014-03-18-14-59-52.csv',delimiter=',', usecols=(4,5,6,7,8,9), unpack=True)

step = decimal.Decimal(step)
sen = decimal.Decimal(sen)
turn = turn * (180/np.pi)
turn = decimal.Decimal(turn)
step = round(step,2)
sen = round(sen,2)
turn = round(turn,2)
s1 = "DR"
s = "PF "+ "Step-" + str(step) + " *0.3m  " + "Sense-" + str(sen) + " uT " + "Turn-" + str(turn) + " dg \n"
kn = "Initial Loncation known (1,0)"
mth = "Magnitude based particle filter"
ph = "   (1,0) - >(1,25) ->(9,25) ->(9,2)->(1,2) mostly"
par_count = 2000

j = 1
k = 0
l = 56
for i in range((len(xp0)/l) +1): 
    xpa = xp0[k:k+l]
    ypa = yp0[k:k+l]
    if j > 4 :
        break
    plt.subplot(2,2,j)
    plt.xlabel('x')
    plt.ylabel('y')
    plt.plot(xpa,ypa,'r*-')
    plt.xlim(-6.0,15.0)
    plt.ylim(0.0,26.0)
    j+=1
    k += l
    print j

plt.suptitle(mth + "\n" +kn  + ph + "\n" + str(int(par_count)) +" particles " + s)
#plt.subplot(3,3,9)
#plt.title('Particle Filtered Reckoning -'+kn) 
#plt.xlabel('x')
#plt.plot(xd0,yd0,'bo-')
#plt.xlim(-6.0,20.0)
#plt.ylim(0.0,26.0)
#plt.plot(xp0,yp0,'r*-')

#plt.plot(xd0,yd0,'go-')
plt.show()

