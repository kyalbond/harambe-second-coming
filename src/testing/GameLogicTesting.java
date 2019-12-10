package testing;

import static org.junit.Assert.*;

import org.junit.Test;

import clientserver.PlayerCommand;
import clientserver.Server;
import clientserver.ServerController;
import core.Board;
import core.GameSystem.Direction;
import exceptions.GameLogicException;
import gameobjects.Chest;
import gameobjects.NPC;
import gameobjects.Player;
import iohandling.BoardParser;
import items.*;
import tile.WaterTile;
import util.Position;

/**
 * Class that holds all the testing for the Game logic and server Controller
 *
 * @author Simon Glew
 */
public class GameLogicTesting {

	/**
	 * Test that makes a player and check it made it correctly
	 */
	public @Test void makePlayerCorrect(){
		Player p = makePlayerHelper("Simon", 0, new Position(5, 5));
		assertEquals(p.getUserName(), "Simon");
		assertTrue(p.getInventory().isEmpty());
	}

	/**
	 * Check that the amount of bananas given is okay
	 */
	public @Test void checkBananaCountGood(){
		Player p = makePlayerHelper("Simon", 0, new Position(5,5));
		p.increaseBananaCount(1);
		assertEquals(p.getNumOfBananas(), 1);
		p.increaseBananaCount(2);
		assertEquals(p.getNumOfBananas(), 3);
	}

	/**
	 * Checks that no more than 5 bananas can be given at one time
	 */
	public @Test void checkBananaCountBad1(){
		try{
			Player p = makePlayerHelper("Simon", 0, new Position(5,5));
			p.increaseBananaCount(6);
		}catch(GameLogicException e){
			fail();
		}
	}

	/**
	 * Checks that no more than 5 bananas can be given at one time
	 */
	public @Test void checkBananaCountBad2(){
		try{
			Player p = makePlayerHelper("Simon", 0,new Position(5,5));
			p.increaseBananaCount(10);
		}catch(GameLogicException e){
			fail();
		}
	}

	/**
	 * Checks that one player cannot have more than 5 bananas
	 */
	public @Test void checkAmountOfBananasBad(){
		try{
			Player p = makePlayerHelper("Simon", 0, new Position(5,5));
			p.increaseBananaCount(4);
			assertEquals(p.getNumOfBananas(), 4);
			p.increaseBananaCount(2);
		}catch(GameLogicException e){
			fail();
		}
	}

	/**
	 * Checks a movement of a player
	 */
	public @Test void checkMovePos(){
		Player p = makePlayerHelper("Simon", 0, new Position(5,5));
		p.setPosition(new Position(6,6));
		assertEquals(p.getPosition().getX(), 6);
		assertEquals(p.getPosition().getY(), 6);
	}

	/**
	 * Check if a player has a floaty device
	 */
	public @Test void checkFloatingDevice(){
		Player p = makePlayerHelper("Simon", 0, new Position(5,5));
		assertFalse(p.getHasFloatingDevice());
		p.setHasFloatingDevice(true);
		assertTrue(p.getHasFloatingDevice());
	}

	/**
	 * Checks that the player has a full inventory at 10 items
	 */
	public @Test void checkFullInventory(){
		Player p = makePlayerHelper("Simon", 0, new Position(5,5));
		for(int i = 0; i < 10; i ++){
			p.pickUpItem(new Fish("Fish"));
		}
		assertTrue(p.inventoryIsFull());
	}

	/**
	 * Checks that certain items are usable
	 */
	public @Test void checkUsableItem(){
		Item floaty = new FloatingDevice("float");
		Item banana = new Banana("banana");

		assertTrue(floaty.isUsable());
		assertFalse(banana.isUsable());
	}

	/**
	 * Checking that you cannot move without first logging in
	 */
	public @Test void checkParsingFalseMovingNoLogin(){
		ServerController s = new ServerController(new Server(1000));
		String parse = s.parseInput(new PlayerCommand("move Simon north"));
		assertEquals(parse, "false");
	}

	/**
	 * Checks that a player logs in correctly and puts them on the correct square
	 */
	public @Test void checkParsingLoginTrue(){
		ServerController s = new ServerController(new Server(1000));
		String parsep1 = s.parseInput(new PlayerCommand("login Simon"));
		assertEquals(parsep1, "true");
		assertFalse(s.requestBoard().getLocationById(0).getTileAtPosition(new Position(5,5)).getGameObject() == null);
		assertTrue(s.requestBoard().getLocationById(0).getTileAtPosition(new Position(4,5)).getGameObject() == null);
		assertTrue(s.requestBoard().getLocationById(0).getTileAtPosition(new Position(5,4)).getGameObject() == null);
		assertTrue(s.requestBoard().getLocationById(0).getTileAtPosition(new Position(4,4)).getGameObject() == null);
	}

	/**
	 * Checks that you cannot login twice with the same username while that username is logged in
	 */
	public @Test void checkParsingLoginFalseDoubleUsername(){
		ServerController s = new ServerController(new Server(1000));
		String parsep1 = s.parseInput(new PlayerCommand("login Simon"));
		assertEquals(parsep1, "true");
		String parsep2 = s.parseInput(new PlayerCommand("login Simon"));
		assertEquals(parsep2, "fail login");
		String parsep3 = s.parseInput(new PlayerCommand("login Jack"));
		assertEquals(parsep3, "true");
	}

	/**
	 * Checks that you are in the same position after relogging
	 */
	public @Test void checkParsingLoginTrueRelog(){
		ServerController s = new ServerController(new Server(1000));
		s.parseInput(new PlayerCommand("login Simon"));
		s.parseInput(new PlayerCommand("move Simon south"));
		s.getPlayerByUserName("Simon").setLoggedIn(false);
		s.parseInput(new PlayerCommand("login Simon"));
		assertTrue(s.requestBoard().getLocationById(0).getTileAtPosition(new Position(5,6)).getGameObject() != null);
	}

	/**
	 * Checking that you can only have 4 players in the game at a time
	 */
	public @Test void checkParsingLoginFalsePlayerLimit(){
		ServerController s = new ServerController(new Server(1000));
		String parsep1 = s.parseInput(new PlayerCommand("login Simon"));
		assertEquals(parsep1, "true");
		String parsep2 = s.parseInput(new PlayerCommand("login Jack"));
		assertEquals(parsep2, "true");
		String parsep3 = s.parseInput(new PlayerCommand("login Jonathan"));
		assertEquals(parsep3, "true");
		String parsep4 = s.parseInput(new PlayerCommand("login Kyal"));
		assertEquals(parsep4, "true");
		String parsep5 = s.parseInput(new PlayerCommand("login Tutor"));
		assertEquals(parsep5, "fail login");
	}

	/**
	 * Checking a correct move
	 */
	public @Test void checkParsingMovingCorrect(){
		ServerController s = new ServerController(new Server(1000));
		s.parseInput(new PlayerCommand("login Simon"));
		String parseMove = s.parseInput(new PlayerCommand("move Simon north"));
		assertEquals(parseMove, "true");
		assertTrue(s.requestBoard().getLocationById(0).getTileAtPosition(new Position(5,5)).getGameObject() == null);
	}

	/**
	 * Checking that you cannot move to another tile that has a player on it
	 */
	public @Test void checkParsingMovingFalseOtherPlayer(){
		ServerController s = new ServerController(new Server(1000));
		s.parseInput(new PlayerCommand("login Simon"));
		s.parseInput(new PlayerCommand("login Jack"));
		s.parseInput(new PlayerCommand("move Simon west"));
		assertTrue(s.requestBoard().getLocationById(0).getTileAtPosition(new Position(5,5)).getGameObject() != null);
	}

	/**
	 * Checking that you cannot move to another tile that has terrain (game object) on it
	 */
	public @Test void checkParsingMovingFalseTerrain(){
		ServerController s = new ServerController(new Server(1000));
		s.parseInput(new PlayerCommand("login Simon"));
		s.parseInput(new PlayerCommand("move Simon south"));
		s.parseInput(new PlayerCommand("move Simon east"));
		assertTrue(s.requestBoard().getLocationById(0).getTileAtPosition(new Position(5,6)).getGameObject() != null);
	}

	/**
	 * Checking a correct drop of an item
	 */
	public @Test void checkDroppingItemCorrect(){
		ServerController s = new ServerController(new Server(1000));
		s.parseInput(new PlayerCommand("login Simon"));
		s.getPlayerByUserName("Simon").getInventory().add(new Banana("Banana"));
		assertFalse(s.getPlayerByUserName("Simon").getInventory().isEmpty());
		s.parseInput(new PlayerCommand("drop Simon 0"));
		assertTrue(s.getPlayerByUserName("Simon").getInventory().isEmpty());
		assertTrue(s.requestBoard().getLocationById(0).getTileAtPosition(new Position(5,6)).getGameObject() != null);
	}

	/**
	 * Checking that you cannot drop an item on a square with a game object on it
	 */
	public @Test void checkDroppingItemFalseObjectInWay(){
		ServerController s = new ServerController(new Server(1000));
		s.parseInput(new PlayerCommand("login Simon"));
		s.parseInput(new PlayerCommand("login Jack"));
		s.parseInput(new PlayerCommand("login Jonathan"));
		s.parseInput(new PlayerCommand("login Kyal"));
		s.getPlayerByUserName("Kyal").getInventory().add(new Banana("Banana"));
		assertFalse(s.getPlayerByUserName("Kyal").getInventory().isEmpty());
		s.parseInput(new PlayerCommand("drop Kyal 0"));
		assertFalse(s.getPlayerByUserName("Kyal").getInventory().isEmpty());
	}

	/**
	 * Checking correctly picking up an item
	 */
	public @Test void checkingPickingUpItem(){
		ServerController s = new ServerController(new Server(1000));
		s.parseInput(new PlayerCommand("login Simon"));
		s.requestBoard().getLocationById(0).getTileAtPosition(new Position(5,4)).setGameObject(new Banana("Banana"));
		assertTrue(s.getPlayerByUserName("Simon").getInventory().isEmpty());
		s.parseInput(new PlayerCommand("move Simon north"));
		assertFalse(s.getPlayerByUserName("Simon").getInventory().isEmpty());
	}

	/**
	 * Checking that you cannot pickup an item when inventory is full
	 */
	public @Test void checkingPickingUpItemFalseFullInventory(){
		ServerController s = new ServerController(new Server(1000));
		s.parseInput(new PlayerCommand("login Simon"));
		s.requestBoard().getLocationById(0).getTileAtPosition(new Position(5,4)).setGameObject(new Banana("Banana"));
		for(int i = 0; i < 10; i ++){
			s.getPlayerByUserName("Simon").getInventory().add(new Banana("Banana"));
		}
		s.parseInput(new PlayerCommand("move Simon north"));
		assertTrue(s.requestBoard().getLocationById(0).getTileAtPosition(new Position(5,5)).getGameObject() != null);
		assertTrue(s.requestBoard().getLocationById(0).getTileAtPosition(new Position(5,4)).getGameObject() != null);

	}

	/**
	 * Check of a correct siphon banana
	 */
	public @Test void checkSiphonBananaCorrect(){
		ServerController s = new ServerController(new Server(1000));
		s.parseInput(new PlayerCommand("login Simon"));
		s.getPlayerByUserName("Simon").getInventory().add(new Banana("Banana"));
		s.parseInput(new PlayerCommand("siphon Simon 0"));
		assertTrue(s.getPlayerByUserName("Simon").getInventory().isEmpty());
		assertTrue(s.getPlayerByUserName("Simon").getNumOfBananas() == 1);
	}

	/**
	 * Checking correct use of toggling the float device
	 */
	public @Test void checkUseFloatDevice(){
		ServerController s = new ServerController(new Server(1000));
		s.parseInput(new PlayerCommand("login Simon"));
		s.getPlayerByUserName("Simon").getInventory().add(new FloatingDevice("floaty"));
		s.parseInput(new PlayerCommand("use Simon 0"));
		assertTrue(s.getPlayerByUserName("Simon").getHasFloatingDevice());
		s.parseInput(new PlayerCommand("use Simon 0"));
		assertFalse(s.getPlayerByUserName("Simon").getHasFloatingDevice());
	}

	/**
	 * Checking correct use of teleporter and if it teleports you back to the correct square
	 */
	public @Test void checkUseTeleporter(){
		ServerController s = new ServerController(new Server(1000));
		s.parseInput(new PlayerCommand("login Simon"));
		s.getPlayerByUserName("Simon").getInventory().add(new Teleporter("tele"));
		s.parseInput(new PlayerCommand("move Simon south"));
		s.parseInput(new PlayerCommand("move Simon south"));
		s.parseInput(new PlayerCommand("use Simon 0"));
		assertTrue(s.requestBoard().getLocationById(0).getTileAtPosition(new Position(5,5)).getGameObject() != null);
		assertTrue(s.getPlayerByUserName("Simon").getInventory().isEmpty());
	}

	/**
	 * Checks correct/incorrect use of a fishing rod
	 */
	public @Test void checkUseFishingRod(){
		ServerController s = new ServerController(new Server(1000));
		s.parseInput(new PlayerCommand("login Simon"));
		s.getPlayerByUserName("Simon").getInventory().add(new FishingRod("fishy stick"));
		s.parseInput(new PlayerCommand("use Simon 0"));
		assertFalse(s.getPlayerByUserName("Simon").getInventory().isEmpty());
	}

	/**
	 * Checks that you can correctly trade with an NPC
	 */
	public @Test void checkTradingWithNPC(){
		ServerController s = new ServerController(new Server(1000));
		s.parseInput(new PlayerCommand("login Simon"));
		s.getPlayerByUserName("Simon").getInventory().add(new Fish("fishy"));
		NPC npc = new NPC("random", Direction.SOUTH);
		s.requestBoard().getLocationById(0).getTileAtPosition(new Position(5,4)).setGameObject(npc);
		s.parseInput(new PlayerCommand("move Simon north"));
		assertTrue(s.requestBoard().getLocationById(0).getTileAtPosition(new Position(5,5)).getGameObject() != null);
		s.parseInput(new PlayerCommand("siphon Simon 0"));
		assertTrue(s.getPlayerByUserName("Simon").getInventory().isEmpty());
	}

	/**
	 * Checks opening a chest correctly
	 */
	public @Test void checkChestCorrect(){
		ServerController s = new ServerController(new Server(1000));
		s.parseInput(new PlayerCommand("login Simon"));
		Chest chest = new Chest(new Banana("Banana"));
		chest.setCode(0);
		s.requestBoard().getLocationById(0).getTileAtPosition(new Position(5,4)).setGameObject(chest);
		s.getPlayerByUserName("Simon").getInventory().add(new Key("key", 0));
		s.parseInput(new PlayerCommand("move Simon north"));
		assertEquals(s.getPlayerByUserName("Simon").getInventory().get(0).getName(), "Banana");
	}

	/**
	 * Checks opening a chest incorrectly
	 */
	public @Test void checkChestFalse(){
		ServerController s = new ServerController(new Server(1000));
		s.parseInput(new PlayerCommand("login Simon"));
		Chest chest = new Chest(new Banana("Banana"));
		chest.setCode(1);
		s.requestBoard().getLocationById(0).getTileAtPosition(new Position(5,4)).setGameObject(chest);
		s.getPlayerByUserName("Simon").getInventory().add(new Key("key", 0));
		s.parseInput(new PlayerCommand("move Simon north"));
		assertNotEquals(s.getPlayerByUserName("Simon").getInventory().get(0).getName(), "Banana");
	}

	/**
	 * Checks winning the game correctly after someone siphons 5 bananas
	 */
	public @Test void checkWinning(){
		String parse = "true";
		ServerController s = new ServerController(new Server(1000));
		s.parseInput(new PlayerCommand("login Simon"));
		for(int i = 0; i < 5; i ++){
			s.getPlayerByUserName("Simon").getInventory().add(new Banana("Banana"));
		}
		for(int i = 0; i < 5; i ++){
			parse = s.parseInput(new PlayerCommand("siphon Simon 0"));
		}
		assertTrue(s.getPlayerByUserName("Simon").getInventory().isEmpty());
		assertEquals(parse, "endgame");
	}

	/**
	 * Checking that you cannot move into water without a floaty on
	 */
	public @Test void checkMovingWaterNoFloaty(){
		ServerController s = new ServerController(new Server(1000));
		s.parseInput(new PlayerCommand("login Simon"));
		s.requestBoard().getLocationById(0).getTiles()[5][4] = new WaterTile(new Position(5,4), null);
		s.parseInput(new PlayerCommand("move Simon north"));
		assertTrue(s.requestBoard().getLocationById(0).getTileAtPosition(new Position(5,5)).getGameObject() != null);
	}

	/**
	 * Checking that you can move into water with a floaty on
	 */
	public @Test void checkMovingWaterFloaty(){
		ServerController s = new ServerController(new Server(1000));
		s.parseInput(new PlayerCommand("login Simon"));
		s.requestBoard().getLocationById(0).getTiles()[5][4] = new WaterTile(new Position(5,4), null);
		s.getPlayerByUserName("Simon").getInventory().add(new FloatingDevice("floaty"));
		s.parseInput(new PlayerCommand("use Simon 0"));
		s.parseInput(new PlayerCommand("move Simon north"));
		assertFalse(s.requestBoard().getLocationById(0).getTileAtPosition(new Position(5,5)).getGameObject() != null);
		assertTrue(s.requestBoard().getLocationById(0).getTileAtPosition(new Position(5,4)).getGameObject() != null);
	}

	/**
	 * Helper method for creating a player
	 *
	 * @param name - Name of player
	 * @param locationID - location of player
	 * @param pos - position of player
	 * @return Player - created player
	 */
	public Player makePlayerHelper(String name, int locationID, Position pos){
		return new Player(name, locationID, pos, BoardParser.parseBoardFName("map-new.txt"));
	}
}
