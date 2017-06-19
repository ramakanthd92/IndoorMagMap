import numpy as np
import matplotlib.pyplot as plt
import json

a0,a1,a2,r0,r1,r2,r3,r4,r5,r6,r7,r8 = np.loadtxt('C:\Users\RP\Dropbox\Thesis-Docs\ParticleFiltering\All 3 axis\Rotation\Loc_a_2014-03-05-09-56-56.magnet.csv',delimiter=',', usecols=(2,3,4,5,6,7,9,10,11,13,14,15), unpack=True) 
ba0,ba1,ba2,br0,br1,br2,br3,br4,br5,br6,br7,br8 = np.loadtxt('C:\Users\RP\Dropbox\Thesis-Docs\ParticleFiltering\All 3 axis\Rotation\Loc_a2_2014-03-05-09-58-18.magnet.csv',delimiter=',', usecols=(2,3,4,5,6,7,9,10,11,13,14,15), unpack=True) 
ca0,ca1,ca2,cr0,cr1,cr2,cr3,cr4,cr5,cr6,cr7,cr8 = np.loadtxt('C:\Users\RP\Dropbox\Thesis-Docs\ParticleFiltering\All 3 axis\Rotation\Loc_a3_2014-03-05-10-00-01.magnet.csv',delimiter=',', usecols=(2,3,4,5,6,7,9,10,11,13,14,15), unpack=True) 
da0,da1,da2,dr0,dr1,dr2,dr3,dr4,dr5,dr6,dr7,dr8 = np.loadtxt('C:\Users\RP\Dropbox\Thesis-Docs\ParticleFiltering\All 3 axis\Rotation\Loc_a4_2014-03-05-10-01-06.magnet.csv',delimiter=',', usecols=(2,3,4,5,6,7,9,10,11,13,14,15), unpack=True) 
ea0,ea1,ea2,er0,er1,er2,er3,er4,er5,er6,er7,er8 = np.loadtxt('C:\Users\RP\Dropbox\Thesis-Docs\ParticleFiltering\All 3 axis\Rotation\Loc_b1_2014-03-05-10-02-29.magnet.csv',delimiter=',', usecols=(2,3,4,5,6,7,9,10,11,13,14,15), unpack=True) 
fa0,fa1,fa2,fr0,fr1,fr2,fr3,fr4,fr5,fr6,fr7,fr8 = np.loadtxt('C:\Users\RP\Dropbox\Thesis-Docs\ParticleFiltering\All 3 axis\Rotation\Loc_b2_2014-03-05-10-03-41.magnet.csv',delimiter=',', usecols=(2,3,4,5,6,7,9,10,11,13,14,15), unpack=True) 
ga0,ga1,ga2,gr0,gr1,gr2,gr3,gr4,gr5,gr6,gr7,gr8 = np.loadtxt('C:\Users\RP\Dropbox\Thesis-Docs\ParticleFiltering\All 3 axis\Rotation\Loc_b3_2014-03-05-10-05-08.magnet.csv',delimiter=',', usecols=(2,3,4,5,6,7,9,10,11,13,14,15), unpack=True) 
ha0,ha1,ha2,hr0,hr1,hr2,hr3,hr4,hr5,hr6,hr7,hr8 = np.loadtxt('C:\Users\RP\Dropbox\Thesis-Docs\ParticleFiltering\All 3 axis\Rotation\Loc_c1_2014-03-05-10-06-09.magnet.csv',delimiter=',', usecols=(2,3,4,5,6,7,9,10,11,13,14,15), unpack=True) 
ia0,ia1,ia2,ir0,ir1,ir2,ir3,ir4,ir5,ir6,ir7,ir8 = np.loadtxt('C:\Users\RP\Dropbox\Thesis-Docs\ParticleFiltering\All 3 axis\Rotation\Loc_c2_2014-03-05-10-07-02.magnet.csv',delimiter=',', usecols=(2,3,4,5,6,7,9,10,11,13,14,15), unpack=True) 
ja0,ja1,ja2,jr0,jr1,jr2,jr3,jr4,jr5,jr6,jr7,jr8 = np.loadtxt('C:\Users\RP\Dropbox\Thesis-Docs\ParticleFiltering\All 3 axis\Rotation\Loc_c3_2014-03-05-10-07-49.magnet.csv',delimiter=',', usecols=(2,3,4,5,6,7,9,10,11,13,14,15), unpack=True) 

#mx2,my2,mz2 = np.loadtxt('C:\Users\RP\Dropbox\Thesis-Docs\ParticleFiltering\Loc_c2__2014-03-04-07-23-05.magnet.csv',delimiter=',', usecols=(2,3,4), unpack=True)

ra0,ra1,ra2 = np.median(r0), np.median(r1) ,np.median(r2)
ra3,ra4,ra5 = np.median(r3), np.median(r4) ,np.median(r5)
ra6,ra7,ra8 =np.median(r6), np.median(r7) ,np.median(r8)

rb0,rb1,rb2 = np.median(br0), np.median(br1) ,np.median(br2)
rb3,rb4,rb5 = np.median(br3), np.median(br4) ,np.median(br5)
rb6,rb7,rb8 = np.median(br6), np.median(br7) ,np.median(br8)

rc0,rc1,rc2 = np.median(cr0), np.median(cr1) ,np.median(cr2)
rc3,rc4,rc5 = np.median(cr3), np.median(cr4) ,np.median(cr5)
rc6,rc7,rc8 = np.median(cr6), np.median(cr7) ,np.median(cr8)


rd0,rd1,rd2 = np.median(dr0), np.median(dr1) ,np.median(dr2)
rd3,rd4,rd5 = np.median(dr3), np.median(dr4) ,np.median(dr5)
rd6,rd7,rd8 = np.median(dr6), np.median(dr7) ,np.median(dr8)

re0,re1,re2 =  np.mean([ra0,rb0,rc0,rd0]), np.mean([ra1,rb1,rc1,rd1]) ,np.mean([ra2,rb2,rc2,rd2])
re3,re4,re5 =  np.mean([ra3,rb3,rc3,rd3]), np.mean([ra4,rb4,rc4,rd4]),np.mean([ra5,rb5,rc5,rd5])
re6,re7,re8 = np.mean([ra6,rb6,rc6,rd6]),np.mean([ra7,rb7,rc7,rd7]),np.mean([ra8,rb8,rc8,rd8])

Ra = np.matrix([[re0,re1,re2],[re3,re4,re5],[re6,re7,re8]])


rf0,rf1,rf2 = np.median(er0), np.median(er1) ,np.median(er2)
rf3,rf4,rf5 = np.median(er3), np.median(er4) ,np.median(er5)
rf6,rf7,rf8 =np.median(er6), np.median(er7) ,np.median(er8)

rg0,rg1,rg2 = np.median(fr0), np.median(fr1) ,np.median(fr2)
rg3,rg4,rg5 = np.median(fr3), np.median(fr4) ,np.median(fr5)
rg6,rg7,rg8 = np.median(fr6), np.median(fr7) ,np.median(fr8)

rh0,rh1,rh2 = np.median(gr0), np.median(gr1) ,np.median(gr2)
rh3,rh4,rh5 = np.median(gr3), np.median(gr4) ,np.median(gr5)
rh6,rh7,rh8 = np.median(gr6), np.median(gr7) ,np.median(gr8)

ri0,ri1,ri2 =  np.mean([rf0,rg0,rh0]), np.mean([rf1,rg1,rh1]) ,np.mean([rf2,rg2,rh2])
ri3,ri4,ri5 =  np.mean([rf3,rg3,rh3]), np.mean([rf4,rg4,rh4]),np.mean([rf5,rg5,rh5])
ri6,ri7,ri8 =  np.mean([rf6,rg6,rh6]),np.mean([rf7,rg7,rh7]),np.mean([rf8,rg8,rh8])

Rb = np.matrix([[ri0,ri1,ri2],[ri3,ri4,ri5],[ri6,ri7,ri8]])

rj0,rj1,rj2 = np.median(rh0), np.median(rh1) ,np.median(rh2)
rj3,rj4,rj5 = np.median(rh3), np.median(rh4) ,np.median(rh5)
rj6,rj7,rj8 =np.median(rh6), np.median(rh7) ,np.median(rh8)

rk0,rk1,rk2 = np.median(ri0), np.median(ri1) ,np.median(ri2)
rk3,rk4,rk5 = np.median(ri3), np.median(ri4) ,np.median(ri5)
rk6,rk7,rk8 = np.median(ri6), np.median(ri7) ,np.median(ri8)

rl0,rl1,rl2 = np.median(rj0), np.median(rj1) ,np.median(rj2)
rl3,rl4,rl5 = np.median(rj3), np.median(rj4) ,np.median(rj5)
rl6,rl7,rl8 = np.median(rj6), np.median(rj7) ,np.median(rj8)

rm0,rm1,rm2 =  np.mean([ri0,rj0,rk0]), np.mean([ri1,rj1,rk1]) ,np.mean([ri2,rj2,rk2])
rm3,rm4,rm5 =  np.mean([ri3,rj3,rk3]), np.mean([ri4,rj4,rk4]),np.mean([ri5,rj5,rk5])
rm6,rm7,rm8 = np.mean([ri6,rj6,rk6]),np.mean([ri7,rj7,rk7]),np.mean([ri8,rj8,rk8])

Rc = np.matrix([[rm0,rm1,rm2],[rm3,rm4,rm5],[rm6,rm7,re8]])

print Ra
print Rb
print Rc

json_data_0=open('C:\Users\RP\Dropbox\Thesis-Docs\PR Results\w012.json','r')
data = json.load(json_data_0)
json_data_0.close()

data_rot = {}

k = data.keys()
j = 0

for i in k:
    if(int(i) >= 1 and int(i) <= 153):
        cm =  Rc*np.matrix([[data[i][0]],[data[i][1]],[data[i][2]]])
        data_rot[i] = [cm.item(0),cm.item(1),cm.item(2),np.sqrt(cm.item(0)**2 + cm.item(1)**2 + cm.item(2)**2)]
    if(int(i) >= 307 and int(i) <= 459):
        bm = Rb*np.matrix([[data[i][0]],[data[i][1]],[data[i][2]]])    
        data_rot[i] = [bm.item(0),bm.item(1),bm.item(2),np.sqrt(bm.item(0)**2 + bm.item(1)**2 + bm.item(2)**2)]
    if(int(i) >= 613 and int(i) <= 816):
        am = Ra*np.matrix([[data[i][0]],[data[i][1]],[data[i][2]]])
        data_rot[i] = [am.item(0),am.item(1),am.item(2),np.sqrt(am.item(0)**2 + am.item(1)**2 + am.item(2)**2)]
 
f = open('C:\Users\RP\Dropbox\Thesis-Docs\library_samples\w_rot.json', 'wb')
json.dump(data_rot,f)
f.close()
