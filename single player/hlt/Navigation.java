package hlt;

import java.util.ArrayList;

public class Navigation {

    public static ThrustMove navigateShipToDock (final GameMap gameMap, final Ship ship, final Entity dockTarget,
            										final int maxThrust, final ArrayList<Move> currentMoves)
    {
        final int maxCorrections = Constants.MAX_NAVIGATION_CORRECTIONS;
        final boolean avoidObstacles = true;
        final double angularStepRad = Math.PI/180.0;
        final Position targetPos = ship.getClosestPoint(dockTarget);

        return navigateShipTowardsTarget(gameMap, ship, targetPos, maxThrust, avoidObstacles, maxCorrections, angularStepRad, currentMoves);
    }

    public static ThrustMove navigateShipTowardsTarget (final GameMap gameMap, final Ship ship, final Position targetPos, final int maxThrust,
            			final boolean avoidObstacles, final int maxCorrections, final double angularStepRad, final ArrayList<Move> currentMoves)
    {
        if (maxCorrections <= 0) {
            return null;
        }

        final double distance = ship.getDistanceTo(targetPos);
        final double angleRad = ship.orientTowardsInRad(targetPos);
        final int angleDeg = Util.angleRadToDegClipped(angleRad);
        
        
        if (avoidObstacles && !gameMap.objectsBetween(ship, targetPos, currentMoves).isEmpty()) {
            
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