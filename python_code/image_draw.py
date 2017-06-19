import Image, ImageDraw

im = Image.open('C:\Users\Ramakanth\Documents\Python Scripts\Empty-a.png')

consx = 280/14
consy = 520/26

draw = ImageDraw.Draw(im)

draw.rectangle([(0*consx,0*consy),(14*consx,26*consy)], outline = 'red',fill = 'white')
draw.rectangle([(2*consx,3*consy),(3*consx,12*consy)], outline = 'red',fill = 'red')
draw.rectangle([(2*consx,14.5*consy),(3*consx,24*consy)], outline = 'red',fill = 'red')
draw.rectangle([(5*consx,3*consy),(6*consx,12*consy)], outline = 'red',fill = 'red')
draw.rectangle([(5*consx,14.5*consy),(6*consx,24*consy)], outline = 'red',fill='red')
draw.rectangle([(8*consx,3*consy),(9*consx,12*consy)], outline = 'red',fill = 'red')
draw.rectangle([(8*consx,14.5*consy),(9*consx,24*consy)], outline = 'red',fill = 'red')
draw.rectangle([(11*consx,3*consy),(12*consx,12*consy)], outline = 'red',fill= 'red')
draw.rectangle([(11*consx,14.5*consy),(12*consx,24*consy)], outline = 'red',fill = 'red')
del draw

# write to stdout
im.save("C:\Users\Ramakanth\Documents\Python Scripts\library5.png")
