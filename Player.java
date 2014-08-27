import java.util.*;
import java.io.*;
import java.math.*;

class Player {

    // Constants
    static final int LEFT = 0;
    static final int RIGHT = 1;
    static final int UP = 2;
    static final int DOWN = 3;
    static final int DEAD = -1;

    static final int XMIN = 0;
    static final int XMAX = 29;
    static final int YMIN = 0;
    static final int YMAX = 19;

    // Configurations
    static final int SPACE_COEFF = 20;
    static final int BOUND_COEFF = 20;

    //data holders
    int[] weight = new int[4];  // hold weight value for each position
    int[][] playGround = new int[30][20];  // hold the play ground
    Map<Integer, LinkedHashSet<String>> map;

    //indexes
    int players;
    int myId;

    int counter;
    int totalPointsPerRound;
    int currentXPosition;
    int currentYPosition;
    int currentX;
    int currentY;
    int oppoX;
    int oppoY;
    
    // split ground into 4 zones, each have 150 points at the beginning
    int leftUp = 150;
    int leftDown = 150;
    int rightUp = 150;
    int rightDown = 150;

    public void initConfigurations(int p, int id) {

        players = p;
        myId = id;

        counter = 2;
        totalPointsPerRound = players*4 + 2;
        currentXPosition = 2 + 4 * myId + 2;
        currentYPosition = 2 + 4 * myId + 3;
        currentX = 0;
        currentY = 0;
        oppoX = 0;
        oppoY = 0;

        map = new HashMap<Integer, LinkedHashSet<String>>();
        for (int i = 0; i < players; i++) {
            map.put(i, new LinkedHashSet<String>());
        }

    }

    public void play(Scanner in) {

        initConfigurations(in.nextInt(), in.nextInt());

        int x = 0;
        int currentPlayerId = 0;

        while (true) {
            
            // Read information from standard input
            int n = in.nextInt();
            
            int position = counter%totalPointsPerRound;
            
            if (position == 0) {
                // System.err.println("Players is " + n);
            } else if (position == 1) {
                // System.err.println("My number is " + n);
            } else {
                
                if (position == currentXPosition) {
                    currentX = n;
                } else if(position == currentYPosition) {
                    currentY = n;
                } else if(position%4 == 0) { //opponent current x position
                    oppoX = n;
                } else if(position%4 == 1) { // opponent current y position
                    oppoY = n;
                }
                
                // store points into playground and player's path
                if (position%2 == 0) x = n; // is x point
                else if(position%2 == 1) { // is y point
                
                    currentPlayerId = (position - 2)/4;
                    if (n < 0) {
                        map.put(currentPlayerId, null);
                    } else {
                        
                        //store in playGround
                        playGround[x][n] = 1;
                        
                        Set<String> points = map.get(currentPlayerId);
                        points.add(x + " " + n);
                        
                        if(position%4 == 0 || position%4 == 1) {
                            if(x < 15 && n < 10) leftUp--;
                            else if(x >= 15 && n < 10) rightUp--;
                            else if(x < 15 && n >= 10) leftDown--;
                            else if(x >=15 && n >= 10) rightDown--;    
                        }
                    }
                }
            }
            
            counter++;

            // Write action to standard output if it's the end
            if(counter%totalPointsPerRound == 0) {
                
                int i = 0;
                
                // calculate space from neighbours
                int toLeft = 0;
                i = currentX-1;
                while(i>=0 && playGround[i][currentY]==0){
                    toLeft++;
                    i--;
                }
                
                int toRight = 0;
                i = currentX+1;
                while(i<=29 && playGround[i][currentY]==0){
                    toRight++;
                    i++;
                }
                
                int toTop = 0;
                i = currentY-1;
                while(i>=0 && playGround[currentX][i]==0){
                    toTop++;
                    i--;
                }
                
                int toBottom = 0;
                i = currentY+1;
                while(i<=19 && playGround[currentX][i]==0){
                    toBottom++;
                    i++;
                }
                
                System.err.println("toLeft" + toLeft);
                System.err.println("toRight" + toRight);
                System.err.println("toTop" + toTop);
                System.err.println("toBottom" + toBottom);
                
                // add more points to the fastest neighbour
                weight[LEFT] += toLeft;
                weight[RIGHT] += toRight;
                weight[UP] += toTop;
                weight[DOWN] += toBottom;
                
                // add space coefficients
                // always move to a area where there are more points left
                weight[LEFT] += (leftUp/SPACE_COEFF + leftDown/SPACE_COEFF)/2;
                weight[RIGHT] += (rightUp/SPACE_COEFF + rightDown/SPACE_COEFF)/2;
                weight[UP] += (leftUp/SPACE_COEFF + rightUp/SPACE_COEFF)/2;
                weight[DOWN] += (leftDown/SPACE_COEFF + rightDown/SPACE_COEFF)/2;
                
                // Compute weight according to current location
                // If it's near the boundry, it's better to go to the other two sides
                if(currentX < XMIN + 4) weight[RIGHT] += BOUND_COEFF;
                if(currentX > XMAX - 3) weight[LEFT] += BOUND_COEFF;
                if(currentY < YMIN + 3) weight[DOWN] += BOUND_COEFF;
                if(currentY > YMAX - 2) weight[UP] += BOUND_COEFF;
                
                if (currentX == XMIN+1) {weight[DOWN] += BOUND_COEFF; weight[UP] += BOUND_COEFF;}
                if (currentX == XMAX-1) {weight[DOWN] += BOUND_COEFF; weight[UP] += BOUND_COEFF;}
                if (currentY == YMIN+1) {weight[LEFT] += BOUND_COEFF; weight[RIGHT] += BOUND_COEFF;}
                if (currentY == YMAX-1) {weight[LEFT] += BOUND_COEFF; weight[RIGHT] += BOUND_COEFF;}
                
                // test boundries condition
                // If it's wall, don't go there
                if (currentX == XMIN) weight[LEFT] = DEAD;
                if (currentX == XMAX) weight[RIGHT] = DEAD;
                if (currentY == YMIN) weight[UP] = DEAD;
                if (currentY == YMAX) weight[DOWN] = DEAD;
                
                // test occupied neighbours
                // If it's wall, don't go there (null means the player is dead)
                for (i = 0; i < players; i++) {
                    Set<String> points = map.get(i);
                    // System.err.println(points);
                    if (points != null) {
                        if (points.contains((currentX - 1) + " " + currentY )) weight[LEFT] = DEAD;
                        if (points.contains((currentX + 1) + " " + currentY )) weight[RIGHT] = DEAD;
                        if (points.contains(currentX + " " + (currentY - 1) )) weight[UP] = DEAD;
                        if (points.contains(currentX + " " + (currentY + 1) )) weight[DOWN] = DEAD;
                    }
                }
                
                // Random ran = new Random();
                // int nextStep = ran.nextInt(4);
                
                // find the direction who has the maximum weight
                int step = 0;
                int maxWeight = -1;
                for(i= 0; i < weight.length; i++) {
                    System.err.println("weight i is " + weight[i]);
                    if (weight[i] > maxWeight) {
                        step = i; 
                        maxWeight = weight[i];
                    }
                    weight[i] = 0;
                }
                
                //print action to output
                switch(step){
                    case LEFT:System.out.println("LEFT");break;
                    case RIGHT:System.out.println("RIGHT");break;
                    case UP:System.out.println("UP");break;
                    case DOWN:System.out.println("DOWN");break;
                }
                
            }
        }

    }

    public static void main(String args[]) {

        // Read init information from standard input, if any
        Scanner in = new Scanner(System.in);

        // init player and play
        Player p = new Player();
        p.play(in);
        
    }
}