package testing;

import java.util.Scanner;

import org.junit.Test;

import core.Board;
import core.GameSystem.Direction;
import gameobjects.Building;
import gameobjects.Chest;
import gameobjects.Door;
import gameobjects.GameObject;
import gameobjects.NPC;
import gameobjects.Player;
import gameobjects.Tree;
import iohandling.BoardParser;
import iohandling.BoardWriter;
import items.Banana;
import items.FishingRod;
import items.Key;
import tile.DoorOutTile;
import tile.GrassTile;
import tile.Tile;
import tile.WaterTile;
import util.Position;

public class DataStorageTesting {

	final String PLAYER_STRING = "\n{\nLegendonger,1,2,3,4,NORTH,true,false,Inventory()}";
	final String DIRECTION_STRING = "NORTH";
	final String GRASSTILE_STRING = "(Grass)";
	final String WATERTILE_STRING = "(Water)";
	final String DOOROUT_STRING = "(18,2,7)";
	final String TREE_STRING = "Tree";
	final String CHEST_STRING = "Chest(0)";
	final String KEY_STRING = "Key(Key, 0)";
	final String NPC_STRING = "NPC(circle,EAST)";
	final String DOOR_STRING = "Door(0,16,5,9)";
	final String PLAYERONBOARD_STRING = "Player(Legendonger)";
	final String BANANA_STRING = "Banana";
	final String FISHINGROD_STRING = "FishingRod";

	/**
	 * helper method to load board
	 * @return
	 */
	private Board loadBoard() {
		return BoardParser.parseBoardFName("map-new.txt");
	}

	/**
	 * helper method to create scanner
	 * @param string - string to be scanned
	 * @return Scanner - scanner
	 */
	public Scanner createScanner(String string) {
		Scanner s = new Scanner(string);
		s.useDelimiter("\\s+|(?=[{}(),;])|(?<=[{}(),;])");
		return s;
	}

	/**
	 * Test writing and parsing boards
	 */
	public @Test void testWriteBoard() {
		Board board = loadBoard();
		String boardString = BoardWriter.writeBoardToString(board);
		Board loadedBoard = BoardParser.parseBoardString(boardString);
		assert boardString.equals(BoardWriter.writeBoardToString(loadedBoard));
	}

	/**
	 * Test parsing player
	 */
	public @Test void testParsePlayer() {
		Player player = BoardParser.parsePlayer(createScanner(PLAYER_STRING), loadBoard());
		assert (player.getUserName().equals("Legendonger"));
		assert (player.getNumOfBananas() == 1);
		assert (player.getLocation().getId() == 2);
		assert (player.getPosition().getX() == 3);
		assert (player.getPosition().getY() == 4);
		assert (player.isLoggedIn());
		assert (!player.getHasFloatingDevice());
		assert (player.getInventory().size() == 0);
	}

	/**
	 * Test parsing direction
	 */
	public @Test void testParseDirection() {
		Direction d = BoardParser.parseDirection(createScanner(DIRECTION_STRING));
		assert (d == Direction.NORTH);
	}

	/**
	 * Test parsing tile
	 */
	public @Test void testParseTile1() {
		Tile t = BoardParser.parseTile(createScanner(GRASSTILE_STRING), 0, 0, loadBoard());
		assert (t instanceof GrassTile);
	}

	/**
	 * Test parsing tile
	 */
	public @Test void testParseTile2() {
		Tile t = BoardParser.parseTile(createScanner(WATERTILE_STRING), 0, 0, loadBoard());
		assert (t instanceof WaterTile);
	}

	/**
	 * Test parsing DoorOutTile
	 */
	public @Test void testParseDoorOut() {
		DoorOutTile t = BoardParser.parseDoorOut(createScanner(DOOROUT_STRING), 0, 0);
		assert (t.getLocationID() == 18);
		assert (t.getDoorPos().getX() == 2);
		assert (t.getDoorPos().getY() == 7);
	}

	/**
	 * Test parsing GameObject
	 */
	public @Test void testParseGameObject() {
		GameObject t = BoardParser.parseGameObject(createScanner(TREE_STRING), loadBoard());
		assert (t instanceof Tree);
	}

	/**
	 * Test parsing Chest
	 */
	public @Test void testParseChest() {
		GameObject t = BoardParser.parseGameObject(createScanner(CHEST_STRING), loadBoard());
		assert (t instanceof Chest);
		Chest c = (Chest) t;
		assert (c.getContents() == null);
		assert (c.getCode() == 0);
	}

	/**
	 * Test parsing Key
	 */
	public @Test void testParseKey() {
		GameObject t = BoardParser.parseGameObject(createScanner(KEY_STRING), loadBoard());
		assert (t instanceof Key);
		Key k = (Key) t;
		assert (k.getCode() == 0);
	}

	/**
	 * Test parsing NPC
	 */
	public @Test void testParseNPC() {
		GameObject t = BoardParser.parseGameObject(createScanner(NPC_STRING), loadBoard());
		assert (t instanceof NPC);
		NPC n = (NPC) t;
	}

	/**
	 * Test parsing Door
	 */
	public @Test void testParseDoor() {
		GameObject t = BoardParser.parseGameObject(createScanner(DOOR_STRING), loadBoard());
		assert (t instanceof Door);
		Door d = (Door) t;
		assert (d.getDoorPosition().getX() == 5);
		assert (d.getDoorPosition().getY() == 9);
		assert (d.getLocationID() == 16);
	}

	/**
	 * Test parsing Banana
	 */
	public @Test void testParseBanana() {
		GameObject t = BoardParser.parseGameObject(createScanner(BANANA_STRING), loadBoard());
		assert (t instanceof Banana);
	}

	/**
	 * Test parsing Fishing rod
	 */
	public @Test void testParseFishingRod() {
		GameObject t = BoardParser.parseItem(createScanner(FISHINGROD_STRING));
		assert (t instanceof FishingRod);
	}

	/**
	 * Test saving player
	 */
	public @Test void testSavePlayer(){
		Player p = new Player("Legendonger", 0, new Position(0,0), loadBoard());
		String saveString = p.toSaveString();
		assert(saveString.equals("Player\n{\nLegendonger,0,0,0,0,SOUTH,false,false,Inventory()\n}"));
	}

	/**
	 * Test saving Building
	 */
	public @Test void testSavebuilding(){
		Building p = new Building();
		String saveString = p.toString();
		assert(saveString.equals("Building"));
	}

	/**
	 * Test saving Chest
	 */
	public @Test void testSaveChest(){
		Chest p = new Chest(new Banana("Banana"));
		String saveString = p.toString();
		assert(saveString.equals("Chest(0,Banana)"));
	}

	/**
	 * Test saving Door
	 */
	public @Test void testSaveDoor(){
		Door p = new Door(0, 2);
		String saveString = p.toString();
		assert(saveString.equals("Door(0,2,5,9)"));
	}

	/**
	 * Test saving NPC
	 */
	public @Test void testSaveNPC(){
		NPC p = new NPC("circle", Direction.NORTH);
		String saveString = p.toString();
		assert(saveString.equals("NPC(circle,NORTH)"));
	}

	/**
	 * Test saving Grass Tile
	 */
	public @Test void testSaveGrassTile(){
		Tile p = new GrassTile(new Position(0, 0), null);
		String saveString = p.toString();
		assert(saveString.equals("Grass"));
	}

	/**
	 * Test saving Key
	 */
	public @Test void testSaveKey(){
		Key p = new Key("key", 0);
		String saveString = p.toString();
		assert(saveString.equals("Key(key, 0)"));
	}

}
