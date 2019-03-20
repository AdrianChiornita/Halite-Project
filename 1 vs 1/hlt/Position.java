package hlt;

import hlt.Navigation.TargetType;

public class Position {

    private final double xPos;
    private final double yPos;

    public Position(final double xPos, final double yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public double getXPos() {
        return xPos;
    }

    public double getYPos() {
        return yPos;
    }

    public double getDistanceTo(final Position target) {
        final double dx = xPos - target.getXPos();
        final double dy = yPos - target.getYPos();
        return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
    }

    public int orientTowardsInDeg(final Position target) {
        return Util.angleRadToDegClipped(orientTowardsInRad(target));
    }

    public double orientTowardsInRad(final Position target) {
        final double dx = target.getXPos() - xPos;
        final double dy = target.getYPos() - yPos;

        return Math.atan2(dy, dx) + 2 * Math.PI;
    }

    public Position getClosestPoint(final Entity target, TargetType type) {
    	double adding = 0;
    	
    	switch (type) {
    		case ENTITY:
    			adding = Constants.MIN_DISTANCE_FOR_CLOSEST_POINT;
    			break;
    		case U_PLANET:
    			break;
    		case A_PLANET:
    			adding = Constants.FORECAST_FUDGE_FACTOR;
    			break;
    		case E_PLANET:
    			adding = Constants.MIN_DISTANCE_FOR_CLOSEST_POINT;
    			break;
    		case E_SHIP:
    			adding = Constants.WEAPON_RADIUS - Constants.SHIP_RADIUS;
    			break;
    		case A_SHIP:
    			adding = 2*Constants.SHIP_RADIUS + Constants.FORECAST_FUDGE_FACTOR;
    			break;
    	}
    	
        final double radius = target.getRadius() + adding;
        final double angleRad = target.orientTowardsInRad(this);

        final double x = target.getXPos() + radius * Math.cos(angleRad);
        final double y = target.getYPos() + radius * Math.sin(angleRad);

        return new Position(x, y);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Position position = (Position) o;

        return (Double.compare(position.xPos, xPos) == 0) && (Double.compare(position.yPos, yPos) == 0);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(xPos);
        result = (int)(temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(yPos);
        result = 31 * result + (int)(temp ^ (temp >>> 32));

        return result;
    }

    @Override
    public String toString() {
        return "Position(" + xPos + ", " + yPos + ")";
    }
}
