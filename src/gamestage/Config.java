package gamestage;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Config {
    public final static int WINDOW_SIZE = 800;
	public final static int TILE_SIZE = 16;
	public final static int MAX_ROW = WINDOW_SIZE/TILE_SIZE;
	public final static int CHAT_SIZE = 200;
	public final static int CHAT_INPUT_SIZE = 35;
	public final static int SCREEN_SIZE = WINDOW_SIZE + CHAT_SIZE;

	public final static Color BACKGROUND_COLOR = Color.WHITE;
	public final static Color TILE_COLOR = Color.ORANGE;

	public final static Image BACKGROUND_IMAGE = new Image("assets/white.png", TILE_SIZE, TILE_SIZE, false, false);
	public final static Image TILE_IMAGE = new Image("assets/orange.png", TILE_SIZE, TILE_SIZE, false, false);

	public final static int SERVER_PORT_NUMBER = 8080;
    public final static String[] INITIAL_MAPS = {"matrix_1.txt"};

    public final static double[][] INITIAL_PLAYER_LOCATION = {{50, 592}};
    public final static double[][] INITIAL_DOOR_LOCATION = {{650, 576}};
    public final static double[][] INITIAL_KEY_LOCATION = {{100, 118}};
}
