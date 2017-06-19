import Image, ImageDraw
import numpy as np
im = Image.open('C:\Users\Ramakanth\Documents\Python Scripts\library5.png')

consx = 280/16
consy = 520/26

xx,yy = np.loadtxt('C:\Users\Ramakanth\Dropbox\Thesis-Docs\ParticleFiltering\All 3 axis\pfPathLog.2014-04-25-15-47-08.csv',delimiter=',', usecols=(0,1), unpack=True)

plist = []
points = ""
for i in range(len(xx)):
    a = int(consx*float(xx[i]))
    b = int(consy*float(yy[i]))
    plist += [(a,b)]
    points += str(a) + "," + str(b) + " "

draw = ImageDraw.Draw(im)

c = plist[0]
#draw.point(plist[0],fill ='black')

for b in plist[1:]:
    draw.line([c,b],fill = 'blue')
  #  draw.point(b, fill = 'black')
    c = b
del draw

# write to stdout
im.save("C:\Users\Ramakanth\Dropbox\Thesis-Docs\PR Results\dp-5.png")
