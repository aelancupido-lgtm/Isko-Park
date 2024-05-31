package player;

import java.util.ArrayList;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import gamestage.Config;
import gamestage.GameStage;
import gamestage.GameTimer;
import objects.Key;
import tile.TileManager;
import sprite.Sprite;

public class Player extends Sprite{
	private final static String[] COLORS = {"green", "gray", "violet", "orange"};
	private final static String[] DIRECTIONS = {"left", "right"};
	private final static String[] ACTIONS = {"idle", "walk", "jump", "push", "dead"};

	//Initial player spawn location
	public static double INITIAL_X = Config.INITIAL_PLAYER_LOCATION[GameStage.getLevel()][0];
	public static double INITIAL_Y = Config.INITIAL_PLAYER_LOCATION[GameStage.getLevel()][1];

	private static int INITIAL_INDEX = 0; // increment this for multiplayer.
	private final String INITIAL_COLOR = COLORS[INITIAL_INDEX]; // not static since this is an instance variable, not a class variable.
	private final static String INITIAL_DIRECTION = DIRECTIONS[1];
	private final static String INITIAL_ACTION = ACTIONS[0];

	//Player height must be greater than width by 5 pixels
	public final static int PLAYER_WIDTH = 43;
	public final static int PLAYER_HEIGHT = 48;
	private final static double SPEED = 2.0;

	// User for sprite animation.
	private final static int MAX_FRAME = 9;
	private final static int MAX_SPRITE = 4;

	private static final double USERNAME_FONTSIZE = 15;
	private boolean sentIdleCheck = false;

	// Player image
	private final Image INITIAL_IMAGE = new Image("assets/" + INITIAL_COLOR + "_character/" + INITIAL_DIRECTION + "_" + INITIAL_ACTION + ".png", PLAYER_WIDTH, PLAYER_HEIGHT, true, false);

	//direction input booleans
	private boolean left;
	private boolean right;

	private String name; //player username
	private Scene scene;
	private int spriteNum;
	private int spriteCounter;
	private String color;
	private String direction; //indicates if player is facing right or left
	private String action;

	private boolean isAlive;

	//for player jump
	private final static double JUMP_SPEED = 12;
	private final static double JUMP_GRAVITY = 0.6;
	private final static double FALL_GRAVITY = 0.3;
	private boolean jumping;
	private boolean falling;
	private Key key;
	private TileManager tileManager;

	//Player constructor
	public Player(String name){
		super(INITIAL_X, INITIAL_Y);
		this.loadImage(INITIAL_IMAGE);
		this.name = name;
		this.key = null;
		this.scene = GameTimer.getScene();
		this.spriteNum = 0;
		this.spriteCounter = 0;
		this.isAlive = true;
		this.jumping = false;
		this.falling = false;
		this.tileManager = GameTimer.getTileManager();
		this.direction = INITIAL_DIRECTION;
		this.action = INITIAL_ACTION;

		// TODO: make color constant for all players [currently, player 1 (one who input identifier is green)]
		this.color = INITIAL_COLOR;
		Player.INITIAL_INDEX++;

		if (this.name.equals(GameTimer.getPlayer())) this.prepareActionHandlers();
	}

	//For getting user input for player movement
	// *Down is not included since there is no jump in Pico Park
	private void prepareActionHandlers() {
	    this.scene.setOnKeyPressed(new EventHandler<KeyEvent>(){
	    	public void handle(KeyEvent e){
	    		KeyCode code = e.getCode();
	    		if(code.equals(KeyCode.SPACE) && !jumping && !falling) jump();
	    		if(code.equals(KeyCode.A)) left = true;
	    		if(code.equals(KeyCode.D)) right = true;
	    	}
	    });

	    this.scene.setOnKeyReleased(new EventHandler<KeyEvent>(){
			public void handle(KeyEvent e){
				KeyCode code = e.getCode();
	    		if(code.equals(KeyCode.A)) left = false;
	    		if(code.equals(KeyCode.D)) right = false;
	    	}
	    });
	}

	//For player movement
	// *Adds dx and dy to player movement
	// *Checks if player will reach boundary
	// *Animates player with animatePlayer method
	public void move(){
		double prevXPos = this.xPos;

		if (this.left){
			this.dx = -SPEED;
			this.direction = DIRECTIONS[0];
		} else if (this.right){
			this.dx = SPEED;
			this.direction = DIRECTIONS[1];
		} else {
			this.dx = 0;
		}

		if (this.xPos + this.dx >= 0 && this.xPos + this.dx + this.width <= Config.WINDOW_SIZE){
			this.xPos += dx;
		}

		if (jumping){
            this.yPos += this.dy;
            this.dy += JUMP_GRAVITY;
            if (this.dy > 0) {
                this.falling = true;
            }
        }
        else if (falling){
			this.yPos += this.dy;
			this.dy += FALL_GRAVITY;
			this.jumping = false;
        }

		if (!GameStage.SINGLE_PLAYER_GAME) this.sendPlayerCoordinates();

		this.checkPlatformCollisionX(prevXPos);
		this.checkPlatformCollisionY();

		//For sprite animation
		this.spriteCounter++;
		if (this.spriteCounter > MAX_FRAME){
			this.spriteNum = (this.spriteNum % MAX_SPRITE) + 1; // wrapping sprite number using modulo.
			this.spriteCounter = 0;
		}

	}

	// send player coordinates to server
	private void sendPlayerCoordinates(){
		if (this.dx != 0 || this.dy != 0){
			String message = "coor;" + name + ";" +  this.getXPos() + ";" + this.getYPos() + ";" + this.action + ";" + this.direction + ";" + this.left + ";" + this.right + ";" + this.spriteNum + ";";
			GameTimer.client.sendMessage(message);
			this.sentIdleCheck = false;
		} else if (this.dx == 0 && this.dy == 0 && !this.sentIdleCheck){
			String message = "coor;" + name + ";" +  this.getXPos() + ";" + this.getYPos() + ";idle;" + this.direction + ";" + this.left + ";" + this.right + ";";
			GameTimer.client.sendMessage(message);
			this.sentIdleCheck = true;
		}

	}
    private void checkPlatformCollisionX(double prevXPos) {
        ArrayList<Sprite> platforms = tileManager.getPlatforms();

        for (Sprite platform : platforms){
            if (this.collidesWith(platform)){
				this.xPos = prevXPos;
				this.dx = 0;
				break;
            }
        }
    }

	private void checkPlatformCollisionY() {
        ArrayList<Sprite> platforms = tileManager.getPlatforms();

        for (Sprite platform : platforms){
            if (this.collidesWith(platform)){
                if (this.dy > 0){  //falling
                    this.yPos = platform.getYPos() - this.height;
                    this.dy = 0;
                    this.jumping = false;
                    this.falling = false;;
                } else if (this.dy < 0){  //jumping
                    this.yPos = platform.getYPos() + platform.getHeight();
                    this.dy = 0;
                }
            }
        }

        //check if player is on any platform
        if (!this.isOnPlatform(platforms)){
            this.falling = true;
        } else {
            this.falling = false;
        }
    }

    private boolean isOnPlatform(ArrayList<Sprite> platforms) {
        this.yPos += 1;
        boolean onPlatform = false;
        for (Sprite platform : platforms) {
            if (this.collidesWith(platform)){
                onPlatform = true;
                break;
            }
        }
        this.yPos -= 1;
        return onPlatform;
    }


	//Initiates a jump for the player
	// *Sets the player's state to jumping
	// *Sets falling to false
	// *Assigns an initial upward jump speed
    private void jump() {
        this.jumping = true;
        this.falling = false;
        this.dy = -JUMP_SPEED;
    }

	public void die() {
		this.setDX(0);
		this.setDY(0);
		this.setLeftRight(false, false);
		this.jumping = false;
		this.falling = false;
		this.isAlive = false;
	}

	public void respawn() {
		this.setXPos(Player.INITIAL_X);
		this.setYPos(Player.INITIAL_Y);
		this.setAction(Player.INITIAL_ACTION);
		this.setDirection(Player.INITIAL_DIRECTION);
		this.isAlive = true;
	}

	//For sprite animation
	private void animatePlayer() {
		Image img = null;

		if (this.left || this.right) {
			this.action = ACTIONS[1]; // walk
			img = new Image("assets/" + this.color + "_character/" + this.direction + "_" + this.action + "_" + this.spriteNum + ".png", PLAYER_WIDTH, PLAYER_HEIGHT, true, false);
		} else if (this.jumping) {
			this.action = ACTIONS[2]; // jump
			img = new Image("assets/" + this.color + "_character/" + this.direction + "_" + this.action + ".png", PLAYER_WIDTH, PLAYER_HEIGHT, true, false);
		} else if (!this.isAlive) {
			this.action = ACTIONS[4]; 	// dead
			img = new Image("assets/" + this.color + "_character/" + this.action + ".png", PLAYER_WIDTH, PLAYER_HEIGHT, true, false);
		} else {
			this.action = ACTIONS[0]; 	// idle
			img = new Image("assets/" + this.color + "_character/" + this.direction + "_" + this.action + ".png", PLAYER_WIDTH, PLAYER_HEIGHT, true, false);
		}

		this.loadImage(img);
	}

	//Getter methods
	public Key getKey(){
		return this.key;
	}

	public String getDirection(){
		return this.direction;
	}

	public String getUsername(){
		return this.name;
	}


	//Setter methods
	public void acquireKey(Key key){
		this.key = key;
	}

	public void setAction(String action){
		this.action = action;
	}

	public void setDirection(String direction){
		this.direction = direction;
	}

	public void setSpriteNum(int num){
		this.spriteNum = num;
	}

	public void setLeftRight(boolean left, boolean right){
		this.left = left;
		this.right = right;
	}

	public void removeKey(){
		this.key = null;
	}

	@Override
	public void render(GraphicsContext gc, double xPos, double yPos){
		this.animatePlayer();
		super.render(gc, xPos, yPos);

		//For player username
	    Text text = new Text(this.name);
	    text.setFont(Font.font("Verdana", FontWeight.BOLD, USERNAME_FONTSIZE));

	    double centerXPos = this.getXPos() + (PLAYER_WIDTH / 2);	//get the horizontal center point of the player
	    double textWidth = text.getLayoutBounds().getWidth();
	    double textXPos = centerXPos - (textWidth / 2);				//x-coordinate for positioning the text horizontally
	    double textYPos = this.yPos - 10;							//y-coordinate for positioning the text vertically

	    gc.setFont(Font.font("Verdana", FontWeight.BOLD, USERNAME_FONTSIZE));
	    gc.setFill(Color.BLACK);
		gc.fillText(this.name, textXPos, textYPos);
	}
}

// Issues:
// Restrict the characters for username

//Debugging Print.
// System.out.println("assets/" + this.color + "_character/" + this.direction + "_" + this.action + "_" + this.spriteNum + ".png");
