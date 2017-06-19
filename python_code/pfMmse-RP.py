import numpy as np
import matplotlib.pyplot as plt

ma,xa,ya = np.loadtxt('C:\Users\RP\Dropbox\Thesis-Docs\ParticleFiltering\pathatoback\pfLog.2014-02-28-05-23-16.mmse.csv',delimiter=',', usecols=(2,3,4), unpack=True)
mb,xb,yb = np.loadtxt('C:\Users\RP\Dropbox\Thesis-Docs\ParticleFiltering\pathbtoback\pfLog.2014-02-28-05-19-15.mmse.csv',delimiter=',', usecols=(2,3,4), unpack=True)
mc,xc,yc = np.loadtxt('C:\Users\RP\Dropbox\Thesis-Docs\ParticleFiltering\pathctoback\pfLog.2014-02-28-05-18-07.mmse.csv',delimiter=',', usecols=(2,3,4), unpack=True)

plt.subplot(1,2, 1)
plt.title('MMSE')
plt.xlabel('steps')
plt.ylabel('mmse')
plt.plot(ma,'bo-',label='path-a')
plt.plot(mb,'ro-',label='path-b')
plt.plot(mc,'go-',label='path-c')
legend = plt.legend(loc='best', shadow=True)
frame = legend.get_frame()
frame.set_facecolor('0.90')

plt.subplot(1, 2, 2)
plt.title('Path taken')
plt.xlabel('x')
plt.ylabel('y')
plt.plot(ya,xa,'bo-',label='path-a')
plt.plot(yb,xb,'ro-',label='path-b')
plt.plot(yc,xc,'go-',label='path-c')

legend = plt.legend(loc='best', shadow=True)
frame = legend.get_frame()
frame.set_facecolor('0.90')

plt.show()


