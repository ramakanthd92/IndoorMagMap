import numpy as np
import json
import glob
import re

w2 = glob.glob('C:\Users\RP\Dropbox\Thesis-Docs\library_samples\lib_samples\*_w2_*.csv')

dct0 = {}
dct1 = {}
dct2 = {}
dct3 = {}
dct = [dct0,dct1,dct2,dct3]

for i in range(len(w2)):
     x,y,z = np.loadtxt(w2[i],delimiter =',', usecols=(2,3,4),skiprows = 5,unpack=True)
     a = int(re.split('\D+',w2[i])[1])
     mag = np.sqrt(x**2 + y**2 + z**2)
     dct[(a-1)/51][(a-1)%51] = [np.mean(x), np.mean(y), np.mean(z), np.mean(mag)]

f = open('C:\Users\RP\Dropbox\Thesis-Docs\library_samples\w34.json', 'wb')
dcta = {}
n = 4
k = 0
for i in range(n):
    for j in range(len(dct[n-i-1])):
        dcta[j+k+1] = dct[n-i-1][j]
    k += 51
json.dump(dcta,f)
f.close()

