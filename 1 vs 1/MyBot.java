import hlt.*;


import java.util.ArrayList;
import java.util.Map;



public class MyBot {
	
	static final Networking networking = new Networking();
	static final GameMap gameMap = networking.initialize("C0deWarr1ors");
	static final ArrayList<Move> moveList = new ArrayList<>();
	
	public enum Tactics { CONQUER, RUSH, ATTACK, DEFFEND, RABBIT}
	//CONCQUER si o varianta de RUSH le-am implementat -> restul vor urma
	
	public static void populateTheMap () {
		
		for (final Ship ship : gameMap.getMyPlayer().getShips().values()) {
	        
	    	if (ship.getDockingStatus() != Ship.DockingStatus.Undocked) continue;
	    	if (ship.getIgnoringStatus()) continue;
	        
	        Map<Double, Planet> sortedPlanets = gameMap.nearbyPlanetsByDistance(ship);
	        
	        for (final Planet planet : sortedPlanets.values()) {
	        	
	        	if (planet.isOwned()) continue;
	        	if (planet.getIgnoringStatus()) continue;
	        	
	            if (ship.canDock(planet)) {
	                moveList.add(new DockMove(ship, planet));
	                break;
	            }
	            
	            final ThrustMove newThrustMove = Navigation.navigateShipToDock(gameMap, ship, planet, Constants.MAX_SPEED, moveList);
	            
	            if (newThrustMove != null) {
	            	ship.setIgnore(true);
	            	planet.setIgnore(true);
	            	moveList.add(newThrustMove);
	            }
	            
	            break;
	        }
	    }
	}
	
	public static void simpleRush () {
		
		for (final Ship ship : gameMap.getMyPlayer().getShips().values()) {
	        
	    	if (ship.getDockingStatus() != Ship.DockingStatus.Undocked) continue;
	    	if (ship.getIgnoringStatus()) continue;
	        
	        Map<Double, Ship> enemies = gameMap.nearbyEnemiesByDistance(ship);
	        
	        for (final Ship enemy : enemies.values()) {
	        	
	            final ThrustMove newThrustMove = Navigation.navigateShipToAttackShip(gameMap, ship, enemy, Constants.MAX_SPEED, moveList);
	            
	            if (newThrustMove != null) {
	            	ship.setIgnore(true);
	            	moveList.add(newThrustMove);
	            	break;
	            } 
	            
	            break;
	        }
	    }
	}

    public static void main(final String[] args) {
        
        while (true) {
            moveList.clear();
            networking.updateMap(gameMap);
          
            // runda 2 -> simple rush
            simpleRush();
            
            Networking.sendMoves(moveList);
        }
    }
}
