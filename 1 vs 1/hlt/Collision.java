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
    
    public static Pair<Ship,Boolean> collisionInTransit (final Position start, final Position target,
    											final ArrayList<Move> currentMoves, final int maxThrust) 
	{
    	double rad = Math.PI / 180.0;
    	double distance = start.getDistanceTo(target);
    	double error =2 * Constants.SHIP_RADIUS + Constants.FORECAST_FUDGE_FACTOR;
    	
    	if (distance > maxThrust) distance = maxThrust;
    	
    	for (Move move : currentMoves) {
			if (move.getType() != Move.MoveType.Thrust) continue;
			
			ThrustMove moveShip = (ThrustMove) move;
			
			if (moveShip.getShip().getDistanceTo(start) > moveShip.getThrust() + distance + error) continue;
			
			double moveTargetDx = Math.cos(moveShip.getAngle()*rad) * moveShip.getThrust() ;
		    double moveTargetDy = Math.sin(moveShip.getAngle()*rad) * moveShip.getThrust() ;
		    Position moveTarget = new Position(moveShip.getShip().getXPos() + moveTargetDx, moveShip.getShip().getYPos() + moveTargetDy);
		    
		    if (Util.segmentToPointDistance(start, target, moveTarget) < error)
	        	return new  Pair<>(move.getShip(),true);
		    
			Position intersection = Util.getIntersectionPosition(start,target,move.getShip(),moveTarget);
			
			if (intersection == null) continue;
			
	        final double startX = start.getXPos();
	        final double startY = start.getYPos();
	        final double endX = target.getXPos();
	        final double endY = target.getYPos();
	        final double centerX = intersection.getXPos();
	        final double centerY = intersection.getYPos();
	        final double dx = endX - startX;
	        final double dy = endY - startY;

	        final double a = square(dx) + square(dy);

	        final double b = -2 * (square(startX) - (startX * endX)
	                            - (startX * centerX) + (endX * centerX)
	                            + square(startY) - (startY * endY)
	                            - (startY * centerY) + (endY * centerY));

	        if (a == 0.0) {
	            if (start.getDistanceTo(intersection) <= error) return new  Pair<>(move.getShip(),true);
	        }

	        final double t = Math.min(-b / (2 * a), 1.0);
	        if (t < 0) continue;

	        final double closestX = startX + dx * t;
	        final double closestY = startY + dy * t;
	        final double closestDistance = new Position(closestX, closestY).getDistanceTo(intersection);

	        if (closestDistance <= error) return new  Pair<>(move.getShip(),true);
		}
    	return new  Pair<>(null,false);
	}   
}