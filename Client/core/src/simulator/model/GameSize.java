package simulator.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This enum is used to determine game size and the amount of boats given by the
 * board size.
 */
public enum GameSize {

//							tablero	(1)				(2)			(3)			(4)				(5)				(6)
//	   						#size   #PatrolBoat   	#Destroyer  #Submarine	#Cruiser 		#Battleship		#Carrier
//Tablero reducido:        	6        	2 	      	 2        	1        	1				0				0
//Clasico:        			10        	2       	 1        	2       	1				1				1
//Insane:      				14        	3       	 3      	3        	2				2				2

	REDUCED(6, 2, 2, 1, 1, 0, 0), CLASSIC(10, 2, 1, 2, 1, 1, 1), INSANE(14, 3, 3, 3, 2, 2, 2);

	/**
	 * Para testear mas rapido
	 */
	// TEST(3, 1, 0, 0, 0, 0, 0);

	private int size;
	private int numPatrol;
	private int numDestroyer;
	private int numSubmarine;
	private int numCruiser;
	private int numBattleship;
	private int numCarrier;
	private int totalNumShips;

	/**
	 * Constructor for the GameSize
	 * 
	 * @param size
	 * @param numPatrolBoat
	 * @param numDestroyer
	 * @param numSubmarine
	 * @param numCruiser
	 * @param numBattleship
	 * @param numCarrier
	 */
	private GameSize(int size, int numPatrolBoat, int numDestroyer, int numSubmarine, int numCruiser, int numBattleship,
			int numCarrier) {
		this.size = size;
		this.numPatrol = numPatrolBoat;
		this.numDestroyer = numDestroyer;
		this.numSubmarine = numSubmarine;
		this.numCruiser = numCruiser;
		this.numBattleship = numBattleship;
		this.numCarrier = numCarrier;
		totalNumShips = numPatrol + numDestroyer + numSubmarine + numCruiser + numBattleship + numCarrier;
	}

	public List<Integer> getShipsSizeList() {
		List<Integer> list = new ArrayList<Integer>();
		list.add(0); // pos 0
		list.add(numPatrol); // pos 1 size 1
		list.add(numDestroyer); // pos 2 size 2
		list.add(numSubmarine); // pos 3 size 3
		list.add(numCruiser); // pos 4 size 4
		list.add(numBattleship); // pos 5 size 5
		list.add(numCarrier); // pos 6 size 6
		return list;
	}

	public int getSize() {
		return size;
	}

	public int getTotalNumShips() {
		return totalNumShips;
	}

	/**
	 * PatrolBoat size (1)
	 * 
	 * @return size
	 */
	public int getNumPatrolBoat() {
		return numPatrol;
	}

	/**
	 * Destroyer size (2)
	 * 
	 * @return size
	 */
	public int getNumDestroyer() {
		return numDestroyer;
	}

	/**
	 * Submarine size (3)
	 * 
	 * @return size
	 */
	public int getNumSubmarine() {
		return numSubmarine;
	}

	/**
	 * Cruiser size (4)
	 * 
	 * @return size
	 */
	public int getNumCruiser() {
		return numCruiser;
	}

	/**
	 * Battleship size (5)
	 * 
	 * @return size
	 */
	public int getNumBattleship() {
		return numBattleship;
	}

	/**
	 * Carrier size (6)
	 * 
	 * @return size
	 */
	public int getNumCarrier() {
		return numCarrier;
	}

	public static String[] getNames() {
		String[] names = new String[GameSize.values().length];
		int i = 0;
		for (GameSize env : GameSize.values()) {
			names[i] = env.name();
			i++;
		}
		return names;
	}

	/**
	 * para utilizar en el game report
	 */
	public String paint() {
		return "GameSize: " + this.toString();
	}

}
