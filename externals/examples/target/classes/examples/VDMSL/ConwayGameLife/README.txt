Conway's Game of Life
The universe of the Game of Life is an infinite two-dimensional orthogonal grid of square cells,
each of which is in one of two possible states, alive or dead. Every cell interacts with its 
eight neighbours, which are the cells that are horizontally, vertically, or diagonally adjacent.
At each step in time, the following transitions occur:

*   Any live cell with fewer than two live neighbours dies, as if caused by under-population.
*   Any live cell with two or three live neighbours lives on to the next generation.
*   Any live cell with more than three live neighbours dies, as if by overcrowding.
*   Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.

The initial pattern constitutes the seed of the system. The first generation is created by 
applying the above rules simultaneously to every cell in the seed-births and deaths occur 
simultaneously, and the discrete moment at which this happens is sometimes called a tick 
(in other words, each generation is a pure function of the preceding one). The rules continue 
to be applied repeatedly to create further generations.
#******************************************************
#  AUTOMATED TEST SETTINGS
#------------------------------------------------------
#AUTHOR=Nick Battle, Peter Gorm Larsen and Claus Ballegaard Nielsen (animation)
#LANGUAGE_VERSION=vdm10
#INV_CHECKS=true
#POST_CHECKS=true
#PRE_CHECKS=true
#DYNAMIC_TYPE_CHECKS=true
#ENTRY_POINT=Conway`periodN(PULSAR,3)
#ENTRY_POINT=Conway`tests()
#ENTRY_POINT=Conway`generations(130,DIEHARD)
#ENTRY_POINT=gui_Graphics`generations_animate(130,DIEHARD)
#SUPPRESS_WARNINGS=false
#EXPECTED_RESULT=NO_ERROR_TYPE_CHECK
#******************************************************
