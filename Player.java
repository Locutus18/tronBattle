import java.util.*;
import java.io.*;
import java.math.*;

class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        // Read init information from standard input, if any
        
        int players = in.nextInt();
        int myNumber = in.nextInt();
        System.err.println("players is : " + players + " number is : " +myNumber);
        
        int counter = 2;
        int totalPointsPerRound = players*4 + 2;
        
        class Point {
            int x;
            int y;
            
            public Point(int x, int y) {
                x = x;
                y = y;
            }
            
            public boolean equals(Object o) {
                Point p = (Point)o;
                return p.x == x && p.y==y;
            }
            
            public int hashCode(){
                return x;
            }
            
            public String toString() {
                return x + " " + y;
            }
        }
        
        Map<String, Point> m = new HashMap<String, Point>();
        
        int x = 0;
        
        while (true) {
            
            // Read information from standard input
            int n = in.nextInt();
            
            int position = counter%totalPointsPerRound;
            
            if (position == 0) {
                System.err.println("Players is " + n);
            } else if (position == 1) {
                System.err.println("My number is " + n);
            } else {
                System.err.print(n + " ");
                if (position%2 == 0) x = n;
                else if(position%2 == 1) m.put(x + " " + n, new Point(x, n));
            }
            
            // Compute logic here
            // Store position information into a matrix
            
            // System.err.printf("The %dth number is %d\n", counter, n);
            
            counter++;
            
            // Write action to standard output if it's the end
            if(counter%10 == 0) {
                System.err.println(m.keySet());    
                //print action
                System.out.println("LEFT");
            }
        }
        
        
    }
}