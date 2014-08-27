tronBattle
==========

AI for tron battle in codingame   

Play it here: http://www.codingame.com/cg/#!challenge:26   

## Algorithm


### Basic idea
Basically, the idea is to find the direction that is more likely to be safe.

The choice of direction is based on weight (scores).
There are four rules:

1. If there are walls or other players, move to another direction.
2. If I'm around the boundry, it's better to move to other direction.
3. Split playground into 4 area, move to the direction where there are more free points.
4. Calculate space from neibours, choose the direction to the fastest neighbour.

Basically, I add different points into each direction according to the 4 rules. The priority is:

	Rule 1 > Rule 2 > Rule 3 > Rule 4

### Consider dead players

If a player is dead, we consider that all its points are free.


## Implementation

First, I have a two dimension array (int[30][20]) of int to maintain the status of the playground. Every time a point(x,y) has been read, we set playground[x][y] equals to 1.

Secondly, I have a HashMap to track the path of each player. The key is player id, and the value is a LinkedHashSet to keep track of the points and the orders they are inserted. Points are store with String: "x y".

Thirdly, in every turn, the scores for each direction is recalculated and stored in an array of int.

If a player is dead, I will set its LinkedHashSet as null. Then, we no longer need to consider its points.


## Result
v1.0: 130   
v1.1: 127   
v1.2: 122   

To improve ... :(
    

## References

- [My Algo used in tron battle](http://localboyfrommadurai.blogspot.in/2014/03/my-ai-tron-bot-2014.html)
- [Tron battle : le debrief de ThomasNzO](http://blog.codingame.fr/2014/03/tron-battle-le-debrief-de-thomasnzo.html)
- [CodingGame - Challenge for Coders - Tron battle](https://github.com/bolilla/Tron-Battle)
- [Tools for making AI Players for Tron Battle](https://github.com/kvas-it/tron_battle)


## Author
Id: zhenyi   
Email:  zhenyi2697#gmail.com
