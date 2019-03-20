package hlt;

import java.util.ArrayList;

public class Collision {
    /**
     * Test whether a given line segment intersects a circular area.
     *
     * @param start  The start of the segment.
     * @param end    The end of the segment.
     * @param circle The circle to test against.
     * @param fudge  An additional safety zone to leave when looking for collisions. Probably set it to ship radius.
     * @return true if the segment intersects, false otherwise
     */
    public static boolean segmentCircleIntersect(final Position start, final Position end, final Entity circle, final double fudge) {
        // Parameterize the segment as start + t * (end - start),
        // and substitute into the equation of a circle
        // Solve for t
    	
        final double circleRadius = circle.getRadius();
        final double startX = start.getXPos();
        final double startY = start.getYPos();
        final double endX = end.getXPos();
        final double endY = end.getYPos();
        final double centerX = circle.getXPos();
        final double centerY = circle.getYPos();
        final double dx = endX - startX;
        final double dy = endY - startY;

        final double a = square(dx) + square(dy);

        final double b = -2 * (square(startX) - (startX * endX)
                            - (startX * centerX) + (endX * centerX)
                            + square(startY) - (startY * endY)
                            - (startY * centerY) + (endY * centerY));

        if (a == 0.0) {
            // Start and end are the same point
            return start.getDistanceTo(circle) <= circleRadius + fudge;
        }

        // Time along segment when closest to the circle (vertex of the quadratic)
        final double t = Math.min(-b / (2 * a), 1.0);
        if (t < 0) {
            return false;
        }

        final double closestX = startX + dx * t;
        final double closestY = startY + dy * t;
        final double closestDistance = new Position(closestX, closestY).getDistanceTo(circle);

        return closestDistance <= circleRadius + fudge;
    }
    
    public static double square(final double num) {
        return num * num;
    }
    
    public static boolean collisionInTransit (final Position start, final Position target,
    											final ArrayList<Move> currentMoves) 
	{
    	final double angle = start.orientTowardsInRad(target);
    	final double distance = 2*((Entity) start).getRadius() + Constants.FORECAST_FUDGE_FACTOR;
    	
    	//Position newStart = Util.getRelativeWithRadius(start, angle);
    	Position newTarget = Util.getRelativeWithRadius(target, angle, distance);
    	
    	double angleRad;
    	
    	angleRad  = (angle < (3.0/2.0) * Math.PI ) ? (angle + (1.0/2.0) * Math.PI ) : (2.0 * Math.PI - angle);
        
        
        double newADx = Math.cos(angleRad) * distance;
        double newADy = Math.sin(angleRad) * distance;
    	
    	Position firstBoundStart = new Position(start.getXPos() + newADx, start.getYPos() + newADy);
    	Position firstBoundTarget = new Position(newTarget.getXPos() + newADx, newTarget.getYPos() + newADy);
    	
    	angleRad  = (angleRad > (1.0/2.0) * Math.PI ) ? (angleRad - (1.0/2.0) * Math.PI ) : ((3.0/2.0) * Math.PI - angleRad);
        
        newADx = Math.cos(angleRad) * distance;
        newADy = Math.sin(angleRad) * distance;
        
    	Position secondBoundStart = new Position(start.getXPos() + newADx, start.getYPos() + newADy);;
    	Position secondBoundTarget = new Position(newTarget.getXPos() + newADx, newTarget.getYPos() + newADy);
    	
    	double rad = Math.PI / 180.0;
    	
    	
    	for (Move move : currentMoves) {
			if (move.getType() != Move.MoveType.Thrust) continue;
		
			ThrustMove moveShip = (ThrustMove) move;
			
			double moveTargetDx = Math.cos(moveShip.getAngle()*rad) * moveShip.getThrust() ;
		    double moveTargetDy = Math.sin(moveShip.getAngle()*rad) * moveShip.getThrust() ;
		    
            Position moveTarget = new Position(moveShip.getShip().getXPos() + moveTargetDx, moveShip.getShip().getYPos() + moveTargetDy);
			
			if (Util.intersect(firstBoundStart, firstBoundTarget, moveShip.getShip(), moveTarget) || 
					Util.intersect(secondBoundStart, secondBoundTarget, moveShip.getShip(), moveTarget)) {							
				return true; 
			}
		}
		
		return false;
	}
    
    public static Entity getCollisionShipInTransit (final Position start, final Position target,
														final ArrayList<Move> currentMoves) 
	{		
    	final double angle = start.orientTowardsInRad(target);
    	final double distance = 2*((Entity) start).getRadius() + Constants.FORECAST_FUDGE_FACTOR;
    	
    	//Position newStart = Util.getRelativeWithRadius(start, angle);
    	Position newTarget = Util.getRelativeWithRadius(target, angle, distance);
    	
    	double angleRad;
    	
    	angleRad  = (angle < (3.0/2.0) * Math.PI ) ? (angle + (1.0/2.0) * Math.PI ) : (2.0 * Math.PI - angle);
        
        
        double newADx = Math.cos(angleRad) * distance;
        double newADy = Math.sin(angleRad) * distance;
    	
    	Position firstBoundStart = new Position(start.getXPos() + newADx, start.getYPos() + newADy);
    	Position firstBoundTarget = new Position(newTarget.getXPos() + newADx, newTarget.getYPos() + newADy);
    	
    	angleRad  = (angleRad > (1.0/2.0) * Math.PI ) ? (angleRad - (1.0/2.0) * Math.PI ) : ((3.0/2.0) * Math.PI - angleRad);
        
        newADx = Math.cos(angleRad) * distance;
        newADy = Math.sin(angleRad) * distance;
        
    	Position secondBoundStart = new Position(start.getXPos() + newADx, start.getYPos() + newADy);;
    	Position secondBoundTarget = new Position(newTarget.getXPos() + newADx, newTarget.getYPos() + newADy);
    	
    	double rad = Math.PI / 180.0;
    	
    	
    	for (Move move : currentMoves) {
			if (move.getType() != Move.MoveType.Thrust) continue;
		
			ThrustMove moveShip = (ThrustMove) move;
			
			double moveTargetDx = Math.cos(moveShip.getAngle()*rad) * moveShip.getThrust() ;
		    double moveTargetDy = Math.sin(moveShip.getAngle()*rad) * moveShip.getThrust() ;
		    
            Position moveTarget = new Position(moveShip.getShip().getXPos() + moveTargetDx, moveShip.getShip().getYPos() + moveTargetDy);
			
			if (Util.intersect(firstBoundStart, firstBoundTarget, moveShip.getShip(), moveTarget) || 
					Util.intersect(secondBoundStart, secondBoundTarget, moveShip.getShip(), moveTarget)) {							
				return moveShip.getShip(); 
			}
		}
		
		return null;
	}
}