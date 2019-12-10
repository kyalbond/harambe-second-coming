package NPCStrategies;

import core.GameSystem.Direction;
import gameobjects.NPC;

/**
 * Class implementing the NPC.Strategy interface providing a strategy for the NPC to walk in a circle based upon the NPCs current facing direction
 * @author Jack Slater
 *
 */
public class CircleStrategy implements NPC.Strategy {
	
	/**
	 * Implementation of the NPC.Strategy interface getNextDirection method which will return the clockwise direction to which the NPC is facing
	 */
	@Override
	public Direction getNextDirection(NPC npc) {
		if(npc.getFacing() == Direction.NORTH){
			return Direction.EAST;
		}
		if(npc.getFacing() == Direction.EAST){
			return Direction.SOUTH;
		}
		if(npc.getFacing() == Direction.SOUTH){
			return Direction.WEST;
		}
		if(npc.getFacing() == Direction.WEST){
			return Direction.NORTH;
		}
		return null;
	}

}
