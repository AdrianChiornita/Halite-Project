package hlt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class Util {
	
	private static ArrayList<Position> aux;

    public static int angleRadToDegClipped(final double angleRad) {
        final long degUnclipped = Math.round(Math.toDegrees(angleRad));
        // Make sure return value is in [0, 360) as required by game engine.
        return (int) (((degUnclipped % 360L) + 360L) % 360L);
    }
    
    //calculate the relative position of point C <-> [A,B]  (cross product AB x AC)
    public static double crossProduct(Position A, Position B, Position C) {
    	return (B.getXPos() - A.getXPos()) * (C.getYPos()- A.getYPos()) -
    			(C.getXPos() - A.getXPos()) * (B.getYPos()- A.getYPos());
    }
    
    //calculate the dot product AB . BC
    public static double dotProduct(Position A, Position B, Position C) {
        return (B.getXPos() - A.getXPos()) * ( C.getXPos() - B.getXPos()) + 
        		(B.getYPos() - A.getYPos()) * (C.getYPos() - B.getYPos());
    }
    
    // distance from AB to C
    public static double segmentToPointDistance(Position A, Position B, Position C) {
    	
    	double distance = crossProduct(A, B, C) / A.getDistanceTo(B);

        if (dotProduct(A, B, C) > 0) return B.getDistanceTo(C);

        if (dotProduct(B, A, C) > 0) return A.getDistanceTo(C);
        
        return Math.abs(distance);
    }
    
    //test if [A.B] and [C,D] intersects (orientation test)
    public static boolean intersect(Position A, Position B, Position C, Position D) {
    	
    	double deltaABC, deltaABD, deltaCDA, deltaCDB;
    	
    	deltaABC = crossProduct(A,B,C);
    	deltaABD = crossProduct(A,B,D);
    	deltaCDA = crossProduct(C,D,A);
    	deltaCDB = crossProduct(C,D,B);
    	
    	aux = new ArrayList<>(4);
    	
    	if (deltaABC == 0 && deltaABD == 0) {
    		ArrayList<Integer> indx = new ArrayList<>(4);
    		
    		aux.addAll(Arrays.asList(A,B,C,D));
    		indx.addAll(Arrays.asList(0,1,2,3));
    		
    		Collections.sort(indx, new Comparator<Integer>() {
    			
				@Override
				public int compare(Integer arg0, Integer arg1) {
					if (aux.get(arg0).getXPos() < aux.get(arg1).getXPos()) return 1;
					if (aux.get(arg0).getXPos() == aux.get(arg1).getXPos() &&
							aux.get(arg0).getYPos() < aux.get(arg1).getYPos()) return 1;
					return -1;
				}
    		});
    		
    		if (indx.get(0) + indx.get(3) == 1 && indx.get(1) + indx.get(2) == 5) return true;
    		if (indx.get(0) + indx.get(3) == 5 && indx.get(1) + indx.get(2) == 1) return true;
    		if (indx.get(0) + indx.get(2) == 1 && indx.get(1) + indx.get(3) == 5) return true;
    		if (indx.get(0) + indx.get(2) == 5 && indx.get(1) + indx.get(3) == 1) return true;
    		
    	}else if (deltaABC * deltaABD <= 0 && deltaCDA * deltaCDB <= 0) return true;
    	
    	return false;
    }
    
    //intersection point between [A,B] and [C,D]
    public static Position getIntersectionPosition(Position A, Position B, Position C, Position D) {
    	if (!intersect(A,B,C,D)) return null;
    	else {
    		double deltaABC, deltaABD;
        	
        	deltaABC = crossProduct(A,B,C);
        	deltaABD = crossProduct(A,B,D);
    		
        	if (deltaABC == 0 && deltaABD == 0) {
        		return null; //daca sunt pe aceeasi dreapta (implementare)
        	}else {
        		
        		double delta = (B.getXPos() - A.getXPos()) * (C.getYPos() - D.getYPos()) -
        				(C.getXPos() - D.getXPos()) * (B.getYPos() - A.getYPos());
        		double tau = (C.getXPos() - A.getXPos()) * (C.getYPos() - D.getYPos()) -
        				(C.getXPos() - D.getXPos()) * (C.getYPos() - A.getYPos());
        		
        		double alpha = tau/delta;
        		
        		return new Position((1 - alpha) * A.getXPos() + alpha * B.getXPos(), 
        				(1 - alpha) * A.getYPos() + alpha * B.getYPos());
        	}
    	}
    }
    
    public static Position getRelativeWithRadius (Position A, double angleRad, double distance) {

        final double newADx = Math.cos(angleRad) * distance;
        final double newADy = Math.sin(angleRad) * distance;
        final Position newStart = new Position(A.getXPos() + newADx, A.getYPos() + newADy);
    	
		return newStart;
    }
}
