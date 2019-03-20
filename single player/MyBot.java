import hlt.*;

import java.util.ArrayList;
import java.util.Map;

public class MyBot {

    public static void main(final String[] args) {
        final Networking networking = new Networking();
        final GameMap gameMap = networking.initialize("C0deWarr1ors");

        // We now have 1 full minute to analyse the initial map.
        final String initialMapIntelligence =
                "width: " + gameMap.getWidth() +
                "; height: " + gameMap.getHeight() +
                "; players: " + gameMap.getAllPlayers().size() +
                "; planets: " + gameMap.getAllPlanets().size();
        
        Log.log(initialMapIntelligence);

        final ArrayList<Move> moveList = new ArrayList<>();
        while (true) {
            moveList.clear();
            networking.updateMap(gameMap);
          
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
            Networking.sendMoves(moveList);
        }
    }
}
