package testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import clientserver.Server;
import clientserver.ServerController;
import core.Location;
import core.Board;
import core.GameSystem.Direction;
import gameobjects.Player;
import junit.framework.AssertionFailedError;
import tile.Tile;
import util.Position;

public class LocationTesting {

	/**
	 * Test that correct direction is returned via oppositedir method
	 */
	public @Test void testOppositeDir(){
		assertEquals(Location.oppositeDir(Direction.NORTH),Direction.SOUTH);
		assertEquals(Location.oppositeDir(Direction.SOUTH),Direction.NORTH);
		assertEquals(Location.oppositeDir(Direction.WEST),Direction.EAST);
		assertEquals(Location.oppositeDir(Direction.EAST),Direction.WEST);
	}

	/**
	 * Test that correct direction is returned via counter clockwise method
	 */
	public @Test void testCounterClockDir(){
		assertEquals(Location.counterClockwiseDir(Direction.NORTH),Direction.WEST);
		assertEquals(Location.counterClockwiseDir(Direction.SOUTH),Direction.EAST);
		assertEquals(Location.counterClockwiseDir(Direction.WEST),Direction.SOUTH);
		assertEquals(Location.counterClockwiseDir(Direction.EAST),Direction.NORTH);
	}

	/**
	 * Test that correct direction is returned via clockwise method
	 */
	public @Test void testClockDir(){
		assertEquals(Location.clockwiseDir(Direction.NORTH),Direction.EAST);
		assertEquals(Location.clockwiseDir(Direction.SOUTH),Direction.WEST);
		assertEquals(Location.clockwiseDir(Direction.WEST),Direction.NORTH);
		assertEquals(Location.clockwiseDir(Direction.EAST),Direction.SOUTH);
	}

	/**
	 * Test that correct direction is return via relative direction method
	 */
	public @Test void testRelativeDirection(){
		assertEquals(Location.getRelativeDirection(Direction.NORTH, Direction.SOUTH),Direction.SOUTH);
		assertEquals(Location.getRelativeDirection(Direction.NORTH, Direction.EAST),Direction.EAST);
		assertEquals(Location.getRelativeDirection(Direction.NORTH, Direction.WEST),Direction.WEST);
		assertEquals(Location.getRelativeDirection(Direction.NORTH, Direction.NORTH),Direction.NORTH);

		assertEquals(Location.getRelativeDirection(Direction.EAST, Direction.SOUTH),Direction.WEST);
		assertEquals(Location.getRelativeDirection(Direction.EAST, Direction.EAST),Direction.SOUTH);
		assertEquals(Location.getRelativeDirection(Direction.EAST, Direction.WEST),Direction.NORTH);
		assertEquals(Location.getRelativeDirection(Direction.EAST, Direction.NORTH),Direction.EAST);


	}

	/**
	 * Test that correct direction is return via other relative direction method
	 */
	public @Test void testOtherRelativeDirection(){
		assertEquals(Location.getOtherRelativeDirection(Direction.NORTH, Direction.SOUTH),Direction.SOUTH);
		assertEquals(Location.getOtherRelativeDirection(Direction.NORTH, Direction.EAST),Direction.WEST);
		assertEquals(Location.getOtherRelativeDirection(Direction.NORTH, Direction.WEST),Direction.EAST);
		assertEquals(Location.getOtherRelativeDirection(Direction.NORTH, Direction.NORTH),Direction.NORTH);

		assertEquals(Location.getOtherRelativeDirection(Direction.EAST, Direction.SOUTH),Direction.WEST);
		assertEquals(Location.getOtherRelativeDirection(Direction.EAST, Direction.EAST),Direction.NORTH);
		assertEquals(Location.getOtherRelativeDirection(Direction.EAST, Direction.WEST),Direction.SOUTH);
		assertEquals(Location.getOtherRelativeDirection(Direction.EAST, Direction.NORTH),Direction.EAST);

	}

	/**
	 * Test get direction of tile method in location
	 */
	public @Test void testGetDirfTile(){
		Board b = new ServerController(new Server(1000)).requestBoard();
		Location l = b.getLocationById(0);
		Tile t = l.getTileAtPosition(new Position(5,5));
		assertEquals(l.getDirOfTile(t.getPos(), l.getTileAtPosition(new Position(6,5))),Direction.EAST);
		assertEquals(l.getDirOfTile(t.getPos(), l.getTileAtPosition(new Position(4,5))),Direction.WEST);
		assertEquals(l.getDirOfTile(t.getPos(), l.getTileAtPosition(new Position(5,4))),Direction.NORTH);
		assertEquals(l.getDirOfTile(t.getPos(), l.getTileAtPosition(new Position(5,6))),Direction.SOUTH);
	}

	/**
	 * Test getTilAtPosInLoc method
	 */
	public @Test void testGetTilInLoc(){
		Board b = new ServerController(new Server(1000)).requestBoard();
		Location l = b.getLocationById(0);
		Tile t = l.getTileAtPosition(new Position(5,5));

		assertEquals(l.getTileAtPositionInLoc(new Position(5,5)),t);
	}

	/**
	 * Test getTilAtPos method, NOTE: different to one above in that this will work cross location
	 */
	public @Test void testGetTileAtPosition(){
		Board b = new ServerController(new Server(1000)).requestBoard();
		Location l = b.getLocationById(0);
		Tile t = l.getTileAtPosition(new Position(11,5));
		assertEquals(b.getLocationById(9).getTileAtPositionInLoc(new Position(1,5)),t);

	}

	/**
	 * Test getPositionOfTil method
	 */
	public @Test void testGetPosOfTile(){
		Board b = new ServerController(new Server(1000)).requestBoard();
		Location l = b.getLocationById(0);
		Tile t = l.getTileAtPosition(new Position(5,5));
		assertEquals(t.getPos().getX(),l.getPositionOfTile(t).getX());
		assertEquals(t.getPos().getY(),l.getPositionOfTile(t).getY());
	}

	/**
	 * Test withinBounds location method
	 */
	public @Test void testWithinBound(){
		Board b = new ServerController(new Server(1000)).requestBoard();
		Location l = b.getLocationById(0);
		assertTrue(l.withinBounds(new Position(0,0)));
		assertTrue(l.withinBounds(new Position(9,9)));
		assertTrue(l.withinBounds(new Position(5,5)));
		assertTrue(!l.withinBounds(new Position(10,10)));
		assertTrue(!l.withinBounds(new Position(-1,-1)));
		assertTrue(!l.withinBounds(new Position(15,15)));

	}

	/**
	 * Tests get Tile in Direction
	 */
	public @Test void testGetTileIndirection(){
		Board b = new ServerController(new Server(1000)).requestBoard();
		Location l = b.getLocationById(0);
		assertEquals(l.getTileInDirection(new Position(5,5), Direction.NORTH),l.getTileAtPosition(new Position(5,4)));;
		assertEquals(l.getTileInDirection(new Position(5,5), Direction.SOUTH),l.getTileAtPosition(new Position(5,6)));;
		assertEquals(l.getTileInDirection(new Position(5,5), Direction.EAST),l.getTileAtPosition(new Position(6,5)));;
		assertEquals(l.getTileInDirection(new Position(5,5), Direction.WEST),l.getTileAtPosition(new Position(4,5)));;

	}


}
