package hlt;

import java.util.ArrayList;

public class Navigation {
	
	 public enum TargetType {ENTITY, U_PLANET, A_PLANET, E_PLANET, E_SHIP, A_SHIP}
	
	private static int numberOfCorrections;
	private static Position initialTarget;
	
    public static ThrustMove navigateShipToDock (final GameMap gameMap, final Ship ship, final Entity dockTarget,
            										final int maxThrust, final ArrayList<Move> currentMoves)
    {
        final int maxCorrections = Constants.MAX_NAVIGATION_CORRECTIONS;
        final boolean avoidObstacles = true;
        final double angularStepRad = Math.PI/180.0;
        final Position targetPos = ship.getClosestPoint(dockTarget, TargetType.U_PLANET);
        numberOfCorrections  = maxCorrections;
        initialTarget = dockTarget;
        
        return navigateShipTowardsTarget(gameMap, ship, targetPos, maxThrust, avoidObstacles, maxCorrections, angularStepRad, currentMoves);
    }
    
    public static ThrustMove navigateShipToAttackShip (final GameMap gameMap, final Ship ship, final Entity other,
			final int maxThrust, final ArrayList<Move> currentMoves)
	{
		final int maxCorrections = Constants.MAX_NAVIGATION_CORRECTIONS;
		final boolean avoidObstacles = true;
		final double angularStepRad = Math.PI/180.0;
		final Position targetPos = ship.getClosestPoint(other, TargetType.E_SHIP);
		numberOfCorrections  = maxCorrections;
		initialTarget = other;
		 
		return navigateShipTowardsTarget(gameMap, ship, targetPos, maxThrust, avoidObstacles, maxCorrections, angularStepRad, currentMoves);
	}

    public static ThrustMove navigateShipTowardsTarget (final GameMap gameMap, final Ship ship, final Position targetPos, final int maxThrust,
            			final boolean avoidObstacles, final int maxCorrections, final double angularStepRad, final ArrayList<Move> currentMoves)
    {
    	if (maxThrust == 0) return null;
        if (maxCorrections <= 0) {
            return navigateShipTowardsTarget(gameMap, ship, initialTarget, maxThrust-1, true, numberOfCorrections, angularStepRad, currentMoves);
        }

        final double distance = ship.getDistanceTo(targetPos);
        final double angleRad = ship.orientTowardsInRad(targetPos);
        final int angleDeg = Util.angleRadToDegClipped(angleRad);
        
        
        if (avoidObstacles && !gameMap.objectsBetween(ship, targetPos, currentMoves, maxThrust).isEmpty()) {
            
        	final double newTargetDx = Math.cos(angleRad + angularStepRad) * distance;
            final double newTargetDy = Math.sin(angleRad + angularStepRad) * distance;
            final Position newTarget = new Position(ship.getXPos() + newTargetDx, ship.getYPos() + newTargetDy);

            return navigateShipTowardsTarget(gameMap, ship, newTarget, maxThrust, true, (maxCorrections-1), angularStepRad, currentMoves);
        }

        int thrust;
        
        if (distance < maxThrust) {
            thrust = (int) distance;
        }
        else {
            thrust = maxThrust;
        }

        return new ThrustMove(ship, angleDeg, thrust);
    }
} 