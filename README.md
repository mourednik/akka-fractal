Distributed fractal renderer.

An exercise in learning Scala and Akka.

Work in progress. 

Default behavior is to start a Master actor and GUI.
The Master will automatically spawn a local Worker.

Start remote workers with command line argument "client". 
The worker will connect to the master and request work, in a work-pulling pattern.

Specify the master IP and port in src/main/resources/reference.conf

GUI controls:
A: Zoom in.
Z: Zoom out.
S: Increase iterations.
X: Decrease iterations.
R: Redraw (re-render) buffer.
J: Julia set.
M: Mandelbrot set.