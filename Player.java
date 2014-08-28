import java.util.*;
import java.io.*;
import java.math.*;

class Dir implements Comparable<Dir>{

    int code;
    Player player;

    public Dir(int theCode, Player thePlayer) {
        code = theCode;
        player = thePlayer;
    }

    public int compareTo(Dir d) {

        if ( code == d.code) return 0;

        // Check if direction is safe
        if (player.isSafe(code) != player.isSafe(d.code)){
            if(player.isSafe(code)) return 1;
            else return -1;
        }


        // go to direction that has maximum free points
        if (player.futureful(code) != player.futureful(d.code)) {
            return player.futureful(code) - player.futureful(d.code);
        }

        // go to the area where there are more free points
        if (player.selfish(code) != player.selfish(d.code)) {
            return player.selfish(code) - player.selfish(d.code);
        }



        // at last, if all is equal, keep the order: LEFT > RIGHT > UP > DOWN
        return d.code - code;
    }
    
    public String toString(){
        String s = "";
        switch(code){
            case Player.LEFT: s =  "LEFT";break;
            case Player.RIGHT: s = "RIGHT";break;
            case Player.UP:  s = "UP";break;
            case Player.DOWN: s = "DOWN";break;
        }
        return s;
    }
}

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
    boolean[] dead;
    int[] weight = new int[4];  // hold weight value for each position
    int[][] playGround = new int[30][20];  // hold the play ground
    int[] opponentPositions = new int[8]; // hold opponentCurrentPosition
    Map<Integer, LinkedHashSet<String>> map;
    Dir[] directions = new Dir[]{new Dir(LEFT, this), 
                                 new Dir(RIGHT, this), 
                                 new Dir(UP, this),
                                 new Dir(DOWN, this)};

    //indexes
    int players;
    int myId;

    int counter;
    int totalPointsPerRound;
    int currentXPosition;
    int currentYPosition;
    int currentX;
    int currentY;
    
    // split ground into 4 zones, each have 150 points at the beginning
    int remainingPoints[] = new int[] {300, 300, 300, 300};
    int leftUp = 150;
    int leftDown = 150;
    int rightUp = 150;
    int rightDown = 150;

    int toLeft = 0;
    int toRight = 0;
    int toTop = 0;
    int toBottom = 0;

    public void initConfigurations(int p, int id) {

        players = p;
        myId = id;

        dead = new boolean[players];

        counter = 2;
        totalPointsPerRound = players*4 + 2;
        currentXPosition = 2 + 4 * myId + 2;
        currentYPosition = 2 + 4 * myId + 3;
        currentX = 0;
        currentY = 0;

        map = new HashMap<Integer, LinkedHashSet<String>>();
        for (int i = 0; i < players; i++) {
            map.put(i, new LinkedHashSet<String>());
        }

    }

    public int selfish(int code) {

        return remainingPoints[code];

    }

    public int futureful(int code) {
        int num = 0;
        switch(code){
            case LEFT: num = toLeft;break;
            case RIGHT: num = toRight;break;
            case UP: num = toTop;break;
            case DOWN: num = toBottom;break;
        }
        return num;
    }

    public boolean isSafe(int code){

        int x = currentX;
        int y = currentY;

        switch(code){
            case LEFT: x--;break;
            case RIGHT: x++;break;
            case UP: y--;break;
            case DOWN: y++;break;
        }

        if (x < XMIN || x > XMAX) return false;
        if (y < YMIN || y > YMAX) return false;

        if(playGround[x][y] == 0) return true;
        else return false;
    }

    // Compute distance between two position
    // This represents the minimum step needed to reach that point
    public int computeDistance(int x, int y, int currentX, int currentY) {
        int distance = Math.abs(x-currentX) + Math.abs(y-currentY);
        // System.err.print(distance + " ");
        return distance;
    }

    // Compute how many points left in each area
    public void computeLeftPoints(int x, int y){

        if (x < 15) remainingPoints[LEFT]--;
        else remainingPoints[RIGHT]--;

        if (y < 10) remainingPoints[UP]--;
        else remainingPoints[DOWN]--;

        if(x < 15 && y < 10) leftUp--;
        else if(x >= 15 && y < 10) rightUp--;
        else if(x < 15 && y >= 10) leftDown--;
        else if(x >=15 && y >= 10) rightDown--;
    }

    public void computeAction(){
    

        int i = 0;
                
        // calculate space from neighbours
        toLeft = 0;
        i = currentX-1;
        while(i>=0 && playGround[i][currentY]==0){
            toLeft++;
            i--;
        }
        
        toRight = 0;
        i = currentX+1;
        while(i<=29 && playGround[i][currentY]==0){
            toRight++;
            i++;
        }
        
        toTop = 0;
        i = currentY-1;
        while(i>=0 && playGround[currentX][i]==0){
            toTop++;
            i--;
        }
        
        toBottom = 0;
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
        if(currentX < XMIN + 3) weight[RIGHT] += BOUND_COEFF;
        if(currentX > XMAX - 3) weight[LEFT] += BOUND_COEFF;
        if(currentY < YMIN + 2) weight[DOWN] += BOUND_COEFF;
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
        
        // test walls
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
            System.err.println("weight "+ i +" is " + weight[i]);
            if (weight[i] > maxWeight) {
                step = i; 
                maxWeight = weight[i];
            }
            weight[i] = 0;
        }

        //sort directions
        System.err.println("");
        System.err.println("Direction sorting:");
        Arrays.sort(directions);
        for(int d = 3; d >= 0; d--){
            System.err.print(directions[d] + " > ");
        }

        step = directions[3].code;
        
        //print action to output
        switch(step){
            case LEFT:System.out.println("LEFT");break;
            case RIGHT:System.out.println("RIGHT");break;
            case UP:System.out.println("UP");break;
            case DOWN:System.out.println("DOWN");break;
        }
    }

    public void resetDeadPoints(int id){

        Set<String> points = map.get(id);
        Iterator<String> i = points.iterator();
        String p = "";
        String[] ps = new String[2];
        int x = 0;
        int y = 0;
        while(i.hasNext()) {
            p = i.next();
            ps = p.split("\\s");
            x = Integer.parseInt(ps[0]);
            y = Integer.parseInt(ps[1]);
            playGround[x][y] = 0;
        }

        map.put(id, null);

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

                currentPlayerId = (position - 2)/4;
                
                if (position == currentXPosition) {
                    currentX = n;
                } else if(position == currentYPosition) {
                    currentY = n;
                } else if(position%4 == 0) { //opponent current x position
                    opponentPositions[currentPlayerId*2] = n;
                } else if(position%4 == 1) { // opponent current y position
                    opponentPositions[currentPlayerId*2+1] = n;
                }
                
                // store points into playground and player's path
                if (position%2 == 0) x = n; // is x point
                else if(position%2 == 1) { // is y point
                
                    if (n < 0) {
                        if(!dead[currentPlayerId]) {
                            resetDeadPoints(currentPlayerId);
                            dead[currentPlayerId] = true;
                        }
                        
                    } else {
                        if(position%4 == 0 || position%4 == 1) {

                            //store the newest position in playGround
                            playGround[x][n] = 1;
                        
                            Set<String> points = map.get(currentPlayerId);
                            points.add(x + " " + n);

                            computeLeftPoints(x, n);

                        }
                    }
                }
            }
            
            counter++;

            // Write action to standard output if it's the end
            if(counter%totalPointsPerRound == 0) {

                // compute distances between me and other points
                for(int p = 0; p < players; p++) {
                    int xPosition = opponentPositions[p*2];
                    int yPosition = opponentPositions[p*2+1];
                    System.err.println("");
                    if(xPosition != -1) {
                        for(int i = 0; i < XMAX; i++) {
                            for(int j = 0; j < YMAX; j++) {
                                if(playGround[i][j] == 0) {
                                    computeDistance(i, j, xPosition, yPosition);
                                }
                            }
                        }    
                    }
                        
                }
                
                // compute scores and print action
                computeAction();

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