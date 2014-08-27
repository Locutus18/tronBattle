import java.util.*;
import java.io.*;
import java.math.*;

//rank 130

class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

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
        
        // Set<String> points = new HashSet<String>();
        Map<Integer, LinkedHashSet<String>> map = new HashMap<Integer, LinkedHashSet<String>>();
        for (int i = 0; i < players; i++) {
            map.put(i, new LinkedHashSet<String>());
        }
        
        int x = 0;
        int currentPlayer = 0;
        
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
                }
                
                System.err.print(n + " ");
                if (position%2 == 0) x = n;
                else if(position%2 == 1) {
                    currentPlayer = (position - 2)/4;
                    if (n < 0) {
                        map.put(currentPlayer, null);
                    } else {
                        Set<String> points = map.get(currentPlayer);
                        points.add(x + " " + n);    
                    }
                }
            }
            
            counter++;
            
            // Write action to standard output if it's the end
            if(counter%totalPointsPerRound == 0) {
                // System.err.println("current x is " + currentX);
                // System.err.println("current y is " + currentY);
                
                boolean left = true;
                boolean right = true;
                boolean up = true;
                boolean down = true;
                
                
                // test boundries condition
                if (currentX == 0) left = false;
                if (currentX == 29) right = false;
                if (currentY == 0) up = false;
                if (currentY == 19) down = false;
                
                // test occupied neighbours
                for (int i = 0; i < players; i++) {
                    Set<String> points = map.get(i);
                    System.err.println(points);
                    if (points != null) {
                        if (points.contains((currentX - 1) + " " + currentY )) left = false;
                        if (points.contains((currentX + 1) + " " + currentY )) right = false;
                        if (points.contains(currentX + " " + (currentY - 1) )) up = false;
                        if (points.contains(currentX + " " + (currentY + 1) )) down = false;        
                    }
                }
                
                // Random ran = new Random();
                // int nextStep = ran.nextInt(4);
                
                // computing weight for each direction
                
                if (left) System.out.println("LEFT");
                else if (right) System.out.println("RIGHT");
                else if (down) System.out.println("DOWN");
                else if (up) System.out.println("UP");
                else System.out.println("LEFT");
                
            }
        }
        
        
    }
}