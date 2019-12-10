package NPCStrategies;

import core.GameSystem;
import core.GameSystem.Direction;
import gameobjects.NPC;

/**
 * Class implementing the NPC.Strategy interface providing a strategy for the
 * NPC to walk in a random direction
 * 
 * @author Jack Slater
 *
 */
public class RandomStrategy implements NPC.Strategy {

	/**
	 * Implementation of the NPC.Strategy interface getNextDirection method
	 * which will return a random direction
	 */
	@Override
	public Direction getNextDirection(NPC npc) {
		int randy = (int) (Math.random() * 4);

		if (randy == 0) {
			return GameSystem.Direction.NORTH;
		} else if (randy == 1) {
			return GameSystem.Direction.SOUTH;
		} else if (randy == 2) {
			return GameSystem.Direction.EAST;
		} else {
			return GameSystem.Direction.WEST;
		}
	}

}
