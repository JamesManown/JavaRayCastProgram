# Raycasted 3D Engine in Java
In this project, I used old-school methods to create a first-person perspective
3D program in Java using the 2D Java graphics library. The code was created with an object-oriented approach (after all, it *is* Java). The program is controlled using the arrow keys to move forward and turn. The spacebar can be pressed

![The 2D and 3D view of the same Perspective](https://i.imgur.com/4gziEeB.png)

## The 2D Perspective

To summarize how it works: Think of everything as a two-dimensional top down view (as that's how all of the logic is treated in the program). You (the player) are represented by a point in space that faces a certain direction. Around the direction you face, ray casts are sent out, each with an agular offset. This creates a cone of rays which is sent out and tries to find the first collision made. Every time a ray enters a new "cell" (explained in the classes section), the ray checks if the cell is either empty or is taken up by a solid wall. If it's a solid wall, the ray cast runs a loop to find the specific point that it collides with the wall. In the 2D view, these collisions are represented by a small blue dot. 

## The 3D Perspective

For the 3D view, the world is represented with a series of vertical bars that have a set width. The distance of a raycast's collision determines the height of a bar, with each ray cast having its own bar. However, the distance between the player and the cast collision is not what's used. Instead, the orthogonal distance between the raycast's collision and the straight line created by the player's POV's center is what's used to determine the height of each bar. This method helps minimize a warping fish-eye lens effect that is still somewhat visible in the finished product. Finally, a nice little effect is added to the bars where the shorter a bar is, the most discolored it comes. This effect helps make the 3D view more visible, otherwise everything would be a single flat color.

## The Classes Created and Used

The following components had their own classes created in the code:
1. Walls (a single side of a wall)
2. SolidWalls (a box made of four walls)
3. Raycasts (The rays which are emitted from the player)

The walls and solid walls are simple. The singular walls are lines that cover part of a "cell". "Cells" are what I use to refer to the broader coordinate system in the program. The program's window is approximately 500x500 pixels and this is the coordinate system used by the 2D graphics library. When referring to the location of walls and solid walls, the top-down screen space is divided into boxes which are 50x50 pixels large.

The raycasts act as explained previously. Each cast is ejected at it's own angle and tries to find the first collision made with the aforementioned solid walls.

## Video

Click below to watch a YouTube video of the program in action!

[![](http://img.youtube.com/vi/VYJg_PsuSOY/0.jpg)](http://www.youtube.com/watch?v=VYJg_PsuSOY "Raycasted Program Video")




