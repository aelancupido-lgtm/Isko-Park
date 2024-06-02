package gamestage;

import javafx.scene.image.Image;

public class Config {
    public final static int WINDOW_SIZE = 800;
	public final static int TILE_SIZE = 16;
	public final static int MAX_ROW = WINDOW_SIZE/TILE_SIZE;

	public final static int CHAT_SIZE = 200;
	public final static int CHAT_INPUT_SIZE = 35;
	public final static int SCREEN_SIZE = WINDOW_SIZE + CHAT_SIZE;

	public final static int MAX_PLAYERS = 2;

	public final static String[] RESERVED_KEYWORDS = {"button;clicked", "coor;", "init;", "player;", "key;", "door;", "kill;", "dead;", "box;", "userNum;"};

	public final static Image BACKGROUND_IMAGE = new Image("assets/white.png", TILE_SIZE, TILE_SIZE, false, false);
	public final static Image TILE_IMAGE = new Image("assets/orange.png", TILE_SIZE, TILE_SIZE, false, false);

	public final static int SERVER_PORT_NUMBER = 8080;

    public final static String[] INITIAL_MAPS = {"matrix_1", "matrix_2"};

    public final static double[][] INITIAL_PLAYER_LOCATION = {{50, 592}, {64, 702}};
    public final static double[][] INITIAL_DOOR_LOCATION = {{650, 576}, {615, 576}};
    public final static double[][] INITIAL_KEY_LOCATION = {{100, 118}, {453, 350}};

	// If no button on level, set level index to {} and if conditional for rendering [set to button.vanish()]
	private final static double[][] INITIAL_BUTTON_LOCATION = {{450, 632}, {}};
	private final static double[][] INITIAL_SPIKE_LOCATION = {{400, 538}, {150, 715}};
	private final static double[][] INITIAL_BOX_LOCATION = {{280, 608}, {}};
	private final static double[][] INITIAL_PORTAL_LOCATION = {{560, 400, 100, 450}, {}};
	private final static double[][] INITIAL_PLATFORM_LOCATION = {{220, 420, 600, 120}};


	// for portal warping
	// contains indexes of portals
	private final static int[][] PORTAL_WARPS = {{1, 0}, {}};

	// order button, spike, box, portal, platform 
	private final static int[][] OBSTACLE_NUMBERS = {{1, 1, 1, 2, 2}, {0, 1, 0, 0, 0}};
	public final static int MAX_LEVEL = 2;

	// gets the obstacle location based on object number and return xPos or yPos
	// allows for multiple obstacles of the same type per stage
	public static double obstacleLocation(String type, int obstacleNumber, boolean isXPos){
		int index;
		if (isXPos){
			index = obstacleNumber * 2;
		} else {
			index = (obstacleNumber * 2) + 1;
		}

		switch(type){
			case "BUTTON":
				return INITIAL_BUTTON_LOCATION[GameStage.getLevel()][index];
			case "SPIKE":
				return INITIAL_SPIKE_LOCATION[GameStage.getLevel()][index];
			case "BOX":
				return INITIAL_BOX_LOCATION[GameStage.getLevel()][index];
			case "PORTAL":
				return INITIAL_PORTAL_LOCATION[GameStage.getLevel()][index];
			case "PLATFORM":
				return INITIAL_PLATFORM_LOCATION[GameStage.getLevel()][index];
			default:
				return -1;
		}	
	}

	public static int obstacleCount(String type){
		switch(type){
			case "BUTTON":
				return OBSTACLE_NUMBERS[GameStage.getLevel()][0];
			case "SPIKE":
				return OBSTACLE_NUMBERS[GameStage.getLevel()][1];
			case "BOX":
				return OBSTACLE_NUMBERS[GameStage.getLevel()][2];
			case "PORTAL":
				return OBSTACLE_NUMBERS[GameStage.getLevel()][3];
			case "PLATFORM":
				return OBSTACLE_NUMBERS[GameStage.getLevel()][4];
			default:
				return -1;
		}
	}

	public static int getWarpPortalIndex(int portalIndex){
		return PORTAL_WARPS[GameStage.getLevel()][portalIndex];
	}

	public static double getInitialPlayerLocation(boolean isXPos){
		if (isXPos){
			return INITIAL_PLAYER_LOCATION[GameStage.getLevel()][0];
		} else {
			return INITIAL_PLAYER_LOCATION[GameStage.getLevel()][1];
		}
	}

	public static double getInitialKeyLocation(boolean isXPos){
		if (isXPos){
			return INITIAL_KEY_LOCATION[GameStage.getLevel()][0];
		} else {
			return INITIAL_KEY_LOCATION[GameStage.getLevel()][1];
		}
	}

	public static double getInitialDoorLocation(boolean isXPos){
		if (isXPos){
			return INITIAL_DOOR_LOCATION[GameStage.getLevel()][0];
		} else {
			return INITIAL_DOOR_LOCATION[GameStage.getLevel()][1];
		}
	}
}

