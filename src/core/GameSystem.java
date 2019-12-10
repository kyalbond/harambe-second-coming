package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import clientserver.ServerController;
import gameobjects.Chest;
import gameobjects.Door;
import gameobjects.GameObject;
import gameobjects.NPC;
import gameobjects.Player;
import iohandling.BoardParser;
import items.Banana;
import items.Fish;
import items.FishingRod;
import items.FloatingDevice;
import items.Item;
import items.Key;
import items.Teleporter;
import tile.DoorOutTile;
import tile.Tile;
import tile.WaterTile;
import util.Position;

/**
 * Main class for running the game, stored server side and used to alter the
 * state of the game
 *
 * @author Jack Slater
 *
 */
public class GameSystem {

	private Board board;

	private ServerController serverController;

	private Map<NPC, Location> NPCs;

	private boolean nightTime = false;

	public final Integer WINNING_BANANA_COUNT = 5;
	public final Integer PLAYER_KEY_LIMIT = 3;

	/**
	 * Enum used for Directions throughout the game
	 *
	 * @author Jack Slater
	 *
	 *
	 */
	public enum Direction {
		NORTH, SOUTH, EAST, WEST
	}

	public GameSystem(ServerController serverController) {
		this.serverController = serverController;
		this.board = BoardParser.parseBoardFName("map-new.txt");
		generateCodes();
		storeNpcs();
	}

	/**
	 * Method to return the current board
	 *
	 * @return Board
	 */
	public Board getBoard() {
		return board;
	}

	/**
	 * Method used to store all of the NPCs from the board in a map
	 */
	public void storeNpcs() {
		NPCs = new HashMap<NPC, Location>();

		for (Location location : board.getLocations().values()) {
			for (Tile ta[] : location.getTiles()) {
				for (Tile t : ta) {
					if (t.getGameObject() instanceof NPC) {
						NPCs.put((NPC) t.getGameObject(), location);
					}
				}
			}
		}
	}

	/**
	 * Method used to randomize the codes for all of the cests and keys int he
	 * game so that every key and chest has a matching pair
	 */
	public void generateCodes() {
		ArrayList<Chest> chests = new ArrayList<Chest>();
		ArrayList<Key> keys = new ArrayList<Key>();

		for (Location location : board.getLocations().values()) {
			for (Tile ta[] : location.getTiles()) {
				for (Tile t : ta) {
					if (t.getGameObject() instanceof Chest) {
						chests.add((Chest) t.getGameObject());
					} else if (t.getGameObject() instanceof Key) {
						keys.add((Key) t.getGameObject());
					}
				}
			}
		}

		if (keys.size() != chests.size()) {
			throw new RuntimeException("must have same numer of keys and chests");
		}

		int num = keys.size();
		for (int i = 0; i < num; i++) {
			int randy = (int) (Math.random() * keys.size());
			int orton = (int) (Math.random() * keys.size());

			keys.get(randy).setCode(i);
			chests.get(orton).setCode(i);
			chests.get(orton).setContents(new Banana("Banana"));
			keys.remove(randy);
			chests.remove(orton);
		}

	}

	/**
	 * Method used to move a player in a given direction and returns whether the
	 * action was a success
	 *
	 * @param p - player to be moved
	 * @param d - direction to be moved too
	 * @return boolean - if move was successful
	 */
	public boolean movePlayer(Player p, Direction d) {

		Location playerLoc = p.getLocation();
		Tile playerTil = p.getTile();
		Position playerPos = playerTil.getPos();

		Tile newTile = null;

		switch (d) {

		case NORTH:
			newTile = playerLoc.getTileInDirection(playerPos, Direction.NORTH);
			p.setFacing(Direction.NORTH);
			break;
		case EAST:
			newTile = playerLoc.getTileInDirection(playerPos, Direction.EAST);
			p.setFacing(Direction.EAST);
			break;
		case WEST:
			newTile = playerLoc.getTileInDirection(playerPos, Direction.WEST);
			p.setFacing(Direction.WEST);
			break;
		case SOUTH:
			newTile = playerLoc.getTileInDirection(playerPos, Direction.SOUTH);
			p.setFacing(Direction.SOUTH);
			break;
		}

		if (newTile != null) {
			if (newTile.getGameObject() == null) {
				if (newTile instanceof WaterTile) {
					if (!p.getHasFloatingDevice()) {
						serverController.broadcastPlayerMessage(
								"It's deep blue and cold as ice, perhaps you require something to float on", p);
						return false;
					}
				}
				if (newTile instanceof DoorOutTile) {
					DoorOutTile dot = (DoorOutTile) newTile;

					if (board.getLocationById(dot.getOutLocationID()).getTileAtPosition(dot.getDoorPos())
							.getGameObject() != null) {
						return false;
					}
					playerTil.setGameObject(null);

					p.setLocation(dot.getOutLocationID());
					p.setTile(board.getLocationById(dot.getOutLocationID()).getTileAtPosition(dot.getDoorPos()));

					board.getLocationById(dot.getOutLocationID()).getTileAtPosition(dot.getDoorPos()).setGameObject(p);
					p.setFacing(Direction.SOUTH);
					return true;
				}
				playerTil.setGameObject(null);
				newTile.setGameObject(p);
				p.setTile(newTile);
				p.setLocation(board.getLocationById(newTile.getLocationID()));
				return true;
			} else {
				triggerInteraction(p, newTile);
				return true;
			}
		}

		return false;
	}

	/**
	 * Method called by the server every second, it will decide whether or not
	 * to move and npc and call the required methods if it is not night time
	 *
	 * @param time - time of the server
	 */
	public void tick(int time) {

		if ((90 < time % 180 && time % 180 < 150)) {
			nightTime = true;
		}else{
			nightTime = false;
		}
		for (NPC n : NPCs.keySet()) {
			int randy = (int) (Math.random() * 4);
			if (randy == 0) {
				moveNPC(n);
			}
		}

	}

	/**
	 * Method called when a player is moving to an occupied tile to trigger an
	 * interaction between the player and the given object
	 *
	 * @param p - player that is triggering interaction
	 * @param newTile - tile that is used for interaction
	 */
	public void triggerInteraction(Player p, Tile newTile) {
		Tile playerTil = p.getTile();
		GameObject object = newTile.getGameObject();
		if (object instanceof Item) {
			if (!p.inventoryIsFull()) {
				if (object instanceof Key) {
					int keyCount = 0;
					for (Item i : p.getInventory()) {
						if (i instanceof Key) {
							keyCount++;
						}
					}

					if (keyCount >= PLAYER_KEY_LIMIT) {
						serverController.broadcastPlayerMessage(
								"You already have 3 keys, Harambe does not appreciate your greed, sharpen up soldier!",
								p);
						return;
					}
				}
				playerTil.setGameObject(null);
				p.pickUpItem((Item) object);
				newTile.setGameObject(p);
				p.setTile(newTile);
				p.setLocation(board.getLocationById(newTile.getLocationID()));
			}
		} else if (object instanceof Chest) {
			Chest c = (Chest) object;
			for (Item i : p.getInventory()) {
				if (i instanceof Key) {
					Key k = ((Key) i);
					if (k.getCode() == c.getCode()) {
						if (c.getContents() != null) {
							p.getInventory().remove(i);
							p.pickUpItem(c.getContents());
							c.setContents(null);
							serverController.broadcastPlayerMessage(
									"With the chest you recognise a distinct glow, a Banana!", p);
							return;
						}
					}
				}
			}
			serverController.broadcastPlayerMessage(
					"You don't have a key with the correct code to open this chest soldier!", p);

		} else if (object instanceof Door) {
			Door door = (Door) object;
			p.getTile().setGameObject(null);
			p.setLocation(door.getLocationID());
			p.setTile(p.getLocation().getTileAtPosition(door.getDoorPosition()));
			p.getTile().setGameObject(p);

		} else if (object instanceof NPC) {
			if (!nightTime) {
				for (Item i : p.getInventory()) {
					if (i instanceof Fish) {
						p.getInventory().remove(i);
						p.pickUpItem(new Banana("Banana"));
						serverController.broadcastPlayerMessage(
								"The Pretty Penguin was overwhelmed as you handed her the fish, in response she gave you a golden reward!",
								p);
						return;
					}
				}
				serverController.broadcastPlayerMessage(
						"The Pretty Penguin did not offer a response, consider offering her a present", p);
			}else{
				serverController.broadcastPlayerMessage(
						"Ssssh!, Harambe does not approve of your intentions to wake a sleeping woman", p);
			}
		}
	}

	/**
	 * Method called by the server controller to order a player to drop an item
	 * from their inventory
	 *
	 * @param p - player that is dropping the item
	 * @param i - item that is being dropped
	 */
	public void playerDropItem(Player p, Item i) {
		Tile tileInFront = p.getLocation().getTileInDirection(p.getPosition(), p.getFacing());
		if (tileInFront.getGameObject() == null) {
			tileInFront.setGameObject(i);
			p.getInventory().remove(i);
		}

	}

	/**
	 * Method called by the server controller to order a player to siphon a
	 * banana from their inventory
	 *
	 * @param p - player that is siphoning the banana
	 * @param b - banana to be siphoned
	 * @return boolean - if siphoning of the banana was successful
	 */
	public boolean playerSiphonBanana(Player p, Banana b) {
		if (p != null && b != null) {
			p.setNumOfBananas(p.getNumOfBananas() + 1);
			p.getInventory().remove(b);
			if (p.getNumOfBananas() == 1) {
				serverController.broadcastBarOnePlayer(
						p.getUserName() + " has siphoned " + p.getNumOfBananas() + " banana, step it up soldier!", p);
			} else {
				serverController.broadcastBarOnePlayer(
						p.getUserName() + " has siphoned " + p.getNumOfBananas() + " bananas, step it up soldier!", p);
			}
			serverController
					.broadcastPlayerMessage("You've siphoned a radiating banana, keep up the good work soldier!", p);
			return p.getNumOfBananas() == WINNING_BANANA_COUNT;
		}
		return false;
	}

	/**
	 * Method called to order a player to use an item from their inventory
	 *
	 * @param player - player that is using the item
	 * @param item - item that is being used
	 */
	public void playerUseItem(Player player, Item item) {
		if (item instanceof FloatingDevice) {
			player.setHasFloatingDevice(!player.getHasFloatingDevice());
		} else if (item instanceof Teleporter) {
			player.getTile().setGameObject(null);
			player.setLocation(0);
			Tile t;
			if (!(player.getLocation().getTileAtPosition(new Position(5, 5)).getGameObject() instanceof Player)) {
				player.setTile(player.getLocation().getTileAtPosition(new Position(5, 5)));
				player.getTile().setGameObject(player);
			} else if (!(player.getLocation().getTileAtPosition(new Position(4, 5))
					.getGameObject() instanceof Player)) {
				player.setTile(player.getLocation().getTileAtPosition(new Position(4, 5)));
				player.getTile().setGameObject(player);
			} else if (!(player.getLocation().getTileAtPosition(new Position(4, 4))
					.getGameObject() instanceof Player)) {
				player.setTile(player.getLocation().getTileAtPosition(new Position(4, 4)));
				player.getTile().setGameObject(player);
			} else if (!(player.getLocation().getTileAtPosition(new Position(5, 4))
					.getGameObject() instanceof Player)) {
				player.setTile(player.getLocation().getTileAtPosition(new Position(5, 4)));
				player.getTile().setGameObject(player);
			}
			player.getInventory().remove(item);

		} else if (item instanceof FishingRod) {
			if (player.getLocation().getTileInDirection(player.getTile().getPos(),
					player.getFacing()) instanceof WaterTile) {
				if (player.inventoryIsFull()) {
					serverController.broadcastPlayerMessage("You can't fish now, you have no room for the spoils",
							player);
					return;
				}
				int randy = (int) (Math.random() * 5);
				if (randy == 0) {
					serverController.broadcastPlayerMessage(
							"You caught a fish against all odds, sadly your rod was lost in the process", player);
					player.getInventory().remove(item);
					player.pickUpItem(new Fish("Fish"));
				} else {
					serverController.broadcastPlayerMessage(
							"A nibble felt, however sometimes we just aren't that lucky", player);

				}
			} else {
				serverController.broadcastPlayerMessage(
						"Harambe is disgusted with you incompetence, you can't fish on land fool!", player);
			}
		}
	}

	/**
	 * Method called to move an NPC in a direction which is gotten fromt he NPCs
	 * strategy
	 *
	 * @param npc - npc that is getting moved
	 */
	public void moveNPC(NPC npc) {
		if (!nightTime) {
			Tile npcTile = null;
			outer: for (Tile[] ta : NPCs.get(npc).getTiles()) {
				for (Tile t : ta) {
					if (t.getGameObject() != null && t.getGameObject().equals(npc)) {
						npcTile = t;
						break outer;
					}
				}
			}

			if (npcTile != null) {
				Direction dir = npc.getDirection();
				npc.setFacing(dir);
				Tile newTile = NPCs.get(npc).getTileInDirection(npcTile.getPos(), dir);
				if (newTile != null) {
					if (newTile.getGameObject() == null && !(newTile instanceof WaterTile)) {
						npcTile.setGameObject(null);
						newTile.setGameObject(npc);
						NPCs.put(npc, board.getLocationById(newTile.getLocationID()));
						serverController.broadcastBoard(board);
					}
				}
			}
		}

	}

}