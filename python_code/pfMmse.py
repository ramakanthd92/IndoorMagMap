import numpy as np
import matplotlib.pyplot as plt

ma,xa,ya = np.loadtxt('C:\Users\Ramakanth\Dropbox\Thesis-Docs\DeadReckoning\pfLog.2014-02-27-11-08-26.mmse.csv',delimiter=',', usecols=(3,4,5), unpack=True)

plt.subplot(1,2, 1)
plt.plot(ma,'')

plt.subplot(1, 2, 2)
plt.plot(ya,xa,'ro-')
plt.show()


