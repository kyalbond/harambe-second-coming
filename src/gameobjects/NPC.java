package gameobjects;

import NPCStrategies.CircleStrategy;
import NPCStrategies.RandomStrategy;
import core.GameSystem;
import core.Location;
import core.GameSystem.Direction;
import tile.WaterTile;
import util.Position;

/**
 * Class that holds all the information for an NPC within the game, using a
 * strategy interface it selects a strategy to use during the game
 * 
 * @author Jack Slater
 */
public class NPC extends GameObject {

	String strategyType;
	Direction facing = Direction.NORTH;
	NPC.Strategy strategy;

	private final String IMG_PRE = "assets/game_objects/npc/npc";
	private final String IMG_POST = ".png";

	/**
	 * Constructor that gives the required variables the correct values, and
	 * selects a strategy on which string was parsed in
	 * 
	 * @param s
	 *            - String holding the strategy type
	 * @param d
	 *            - Direction that NPC is currently facing
	 */
	public NPC(String s, Direction d) {
		this.strategyType = s;
		facing = d;
		if (strategyType.equals("random")) {
			this.strategy = new RandomStrategy();
		}
		if (strategyType.equals("circle")) {
			this.strategy = new CircleStrategy();
		}
	}

	/**
	 * Getter for the next direction for the NPC
	 * 
	 * @return Direction - next direction for the NPC
	 */
	public GameSystem.Direction getDirection() {
		return strategy.getNextDirection(this);
	}

	/**
	 * Getter for the direction that the NPC is facing
	 * 
	 * @return Direction - Direction that the NPC is currently facing
	 */
	public Direction getFacing() {
		return facing;
	}

	/**
	 * Setter for the direction that the NPC is facing
	 * 
	 * @param facing
	 *            - Direction that the NPC will face
	 */
	public void setFacing(Direction facing) {
		this.facing = facing;
	}

	public String toString() {
		return "NPC(" + strategyType + "," + facing.toString() + ")";
	}

	/**
	 * Gets an image file name depending on what the variables are
	 * 
	 * @param loc
	 *            - location of NPC
	 * @param pos
	 *            - Position of NPC
	 * @param viewingDir
	 *            - viewingDir of the renderer
	 * @return fname - File name of the object
	 */
	public String getImage(Location loc, Position pos, Direction viewingDir) {
		String fname = IMG_PRE;
		fname += Location.getOtherRelativeDirection(facing, viewingDir).toString() + IMG_POST;

		return fname;
	}
	
	/**
	 * Interface for the strategy that the interface uses, this will be defined as two strategies: a random strategy and a circle strategy
	 * 
	 * @author Jack Slater
	 */
	public interface Strategy {
		public GameSystem.Direction getNextDirection(NPC npc);
	}
}
