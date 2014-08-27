import java.util.*;
import java.io.*;
import java.math.*;

//rank 127

class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);


        //contants
        final int LEFT = 0;
        final int RIGHT = 1;
        final int UP = 2;
        final int DOWN = 3;

        // Read init information from standard input, if any
        int players = in.nextInt();
        int myNumber = in.nextInt();
        // System.err.println("players is : " + players + " number is : " +myNumber);
        
        int counter = 2;
        int totalPointsPerRound = players*4 + 2;
        int currentXPosition = 2 + 4 * myNumber + 2;
        int currentYPosition = 2 + 4 * myNumber + 3;
        int currentX = 0;
        int currentY = 0;
        
        int oppoX = 0;
        int oppoY = 0;
        
        int leftUp = 150;
        int leftDown = 150;
        int rightUp = 150;
        int rightDown = 150;
        
        // Set<String> points = new HashSet<String>();
        Map<Integer, LinkedHashSet<String>> map = new HashMap<Integer, LinkedHashSet<String>>();
        for (int i = 0; i < players; i++) {
            map.put(i, new LinkedHashSet<String>());
        }
        
        int[][] matrix = new int[30][20];
        
        int x = 0;
        int currentPlayer = 0;
        
        int[] weight = new int[4];
        
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
                
                System.err.print(n + " ");
                if (position%2 == 0) x = n; // is x point
                else if(position%2 == 1) { // is y point
                
                    currentPlayer = (position - 2)/4;
                    if (n < 0) {
                        map.put(currentPlayer, null);
                    } else {
                        
                        //store in matrix
                        matrix[x][n] = 1;
                        
                        Set<String> points = map.get(currentPlayer);
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
                while(i>=0 && matrix[i][currentY]==0){
                    toLeft++;
                    i--;
                }
                
                int toRight = 0;
                i = currentX+1;
                while(i<=29 && matrix[i][currentY]==0){
                    toRight++;
                    i++;
                }
                
                int toTop = 0;
                i = currentY-1;
                while(i>=0 && matrix[currentX][i]==0){
                    toTop++;
                    i--;
                }
                
                int toBottom = 0;
                i = currentY+1;
                while(i<=19 && matrix[currentX][i]==0){
                    toBottom++;
                    i++;
                }
                
                // System.err.println("toLeft" + toLeft);
                // System.err.println("toRight" + toRight);
                // System.err.println("toTop" + toTop);
                // System.err.println("toBottom" + toBottom);
                
                // weight[LEFT] += toLeft/5;
                // weight[RIGHT] += toRight/5;
                // weight[UP] += toTop/5;
                // weight[DOWN] += toBottom/5;
                
                // System.err.println("current x is " + currentX);
                // System.err.println("current y is " + currentY);
                
                int spaceCoeff = 40;
                
                System.err.println("");
                System.err.println(leftUp);
                System.err.println(leftDown);
                System.err.println(rightUp);
                System.err.println(rightDown);
                System.err.println("");
                
                // weight[LEFT] += (leftUp/spaceCoeff + leftDown/spaceCoeff)/2;
                // weight[RIGHT] += (rightUp/spaceCoeff + rightDown/spaceCoeff)/2;
                // weight[UP] += (leftUp/spaceCoeff + rightUp/spaceCoeff)/2;
                // weight[DOWN] += (leftDown/spaceCoeff + rightDown/spaceCoeff)/2;
                
                //compute weight according to current location
                int pc = 3;
                if(currentX < 4) weight[RIGHT] += pc;
                if(currentX > 26) weight[LEFT] += pc;
                if(currentY < 3) weight[DOWN] += pc;
                if(currentY > 17) weight[UP] += pc;
                
                if (currentX == 1) {weight[DOWN] += pc; weight[UP] += pc;}
                if (currentX == 28) {weight[DOWN] += pc; weight[UP] += pc;}
                if (currentY == 1) {weight[LEFT] += pc; weight[RIGHT] += pc;}
                if (currentY == 18) {weight[LEFT] += pc; weight[RIGHT] += pc;}
                
                // test boundries condition
                if (currentX == 0) weight[LEFT] = -1;
                if (currentX == 29) weight[RIGHT] = -1;
                if (currentY == 0) weight[UP] = -1;
                if (currentY == 19) weight[DOWN] = -1;
                
                // test occupied neighbours
                for (i = 0; i < players; i++) {
                    Set<String> points = map.get(i);
                    System.err.println(points);
                    if (points != null) {
                        if (points.contains((currentX - 1) + " " + currentY )) weight[LEFT] = -1;
                        if (points.contains((currentX + 1) + " " + currentY )) weight[RIGHT] = -1;
                        if (points.contains(currentX + " " + (currentY - 1) )) weight[UP] = -1;
                        if (points.contains(currentX + " " + (currentY + 1) )) weight[DOWN] = -1;
                    }
                }
                
                // Random ran = new Random();
                // int nextStep = ran.nextInt(4);
                
                // find the max weight
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
                
                //print action
                switch(step){
                    case LEFT:System.out.println("LEFT");break;
                    case RIGHT:System.out.println("RIGHT");break;
                    case UP:System.out.println("UP");break;
                    case DOWN:System.out.println("DOWN");break;
                }
                
                // if (left) System.out.println("LEFT");
                // else if (right) System.out.println("RIGHT");
                // else if (down) System.out.println("DOWN");
                // else if (up) System.out.println("UP");
                // else System.out.println("LEFT");
                
            }
        }
        
        
    }
}