#!/usr/bin/python
# Author: Gustavo Noronha Silva <gustavo@noronha.eti.br>
# In the public domain, with the caveat bellow (the borrowed code).

import random

# This is the world; the robot may sense 1 or 0
world = [ 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0 ]

def plot(real_position, particles, measurement):
    """
    plot(real_position, particles, measurement)

    Plots the world map on the first line, the robot's real position
    along with its current measurement in brackets on the second line,
    and the current estimation the robot has (the number of particles
    multiplied by sum of the weights) on the third line.
    """

    ws = [('%5.1f' % (w)) for w in world]
    print 'Map:\t', ws

    real_world = ['  0.0'] * len(world)
    real_world[real_position] = 'R [%d]' % (measurement)
    print 'Real:\t', real_world

    # Create placeholders, just so our drawing is complete.
    weighted_positions = {}
    for p in range(len(world)):
        weighted_positions[p] = 0.0

    # Now sum the weights of the existing particles.
    for p in particles:
        weighted_positions[p[1]] += p[0]

    # Create placeholders, just so our drawing is complete.
    particles_in_positions = {}
    for p in range(len(world)):
        particles_in_positions[p] = 0.0

    # Now sum the weights of the existing particles.
    for p in particles:
        particles_in_positions[p[1]] += 1.0

    robots_view = []
    for k in sorted(particles_in_positions.keys()):
        robots_view.append('%5.1f' % (particles_in_positions[k] * weighted_positions[k]))
    print 'Robot:\t', robots_view

NUM_PARTICLES = 1000
def generate_particles(particles = [], position = 0):
    """
    generate_particles(particles = [], position = 0) -> new particles list

    Generates as many new particles in the possible range as necessary
    to have the number of particles we specified we want to use.

    A particle is a list of two items: the first item is the weight,
    and the second is an index in the world (the position).
    """
    for count in range(NUM_PARTICLES - len(particles)):
        pos = random.randrange(position, len(world))
        particles.append([0.1, pos])
    return particles

# This is the initial set of particles, all with the same weights
particles = generate_particles()

# This is the robot's sensor. It has a 90% chance of sensing correctly.
def sense(position):
    try:
        actual = bool(world[position])
        # add stochacity
        possible = [bool(actual)] * 9 + [not bool(actual)]
        return random.sample(possible, 1)[0]
    except IndexError:
        raise IndexError("You tried sensing %d but the world only goes from 0 to %d" % (position, len(world) - 1))

# Borrowed from:
# http://stackoverflow.com/questions/2140787/select-random-k-elements-from-a-list-whose-elements-have-weights/2149533#2149533
def weighted_sample(items):
    total = float(sum(w for w, v in items))
    i = 0
    w, v = items[0]

    x = total * (1 - random.random() ** (1.0 / 1))
    while x > w:
        x -= w
        i += 1
        w, v = items[i]
    return items[i]

# The actual filter.
def particles_filter(particles, measurement):
    new_particles = []
    total_weights = 0

    for i in range(len(particles)):
        # Sample a particle with replacement, with the probability of
        # it being picked being given by the associated weight.
        particle = weighted_sample(particles)

        # x is the position; we don't sample a probability because
        # movement is deterministic.
        x = particle[1]

        # w = P(measurement|x)
        # If our measurement matches the particle we picked, then its
        # weight is 0.9, otherwise it's 0.1.
        if measurement and world[x] or \
                not measurement and not world[x]:
            w = 0.9
        else:
            w = 0.1

        new_particles.append([w, x])
        total_weights += w

    # Normalize the weights.
    for i in range(len(new_particles)):
        new_particles[i][0] /= total_weights

    return new_particles

# This loop moves the robot from the beginning of the world to its end.
for robot_position in range(len(world)):
    # We have moved, so...
    if robot_position:
        # ...add noise,
        for p in particles:
            p[1] = p[1] + random.randrange(-1, 2)

        # ...move particles, following the movement,
        for p in particles:
            p[1] += 1

        # ...remove particles in positions which are now impossible,
        particles = [p for p in particles if p[1] >= robot_position and p[1] < (len(world) - 1)]

        # ...remove the 1/4 most unlikely particles,
        particles = sorted(particles, key=lambda p: p[0])[len(particles)/4:]

        # ...and finally generate new random particles to make up for
        # our losses.
        generate_particles(particles, robot_position)

    # Measurement.
    measurement = sense(robot_position)

    # New particles, after the measurement.
    particles = particles_filter(particles, measurement)

    print 'Iteration #%d' % (robot_position + 1)
    plot(robot_position, particles, measurement)
