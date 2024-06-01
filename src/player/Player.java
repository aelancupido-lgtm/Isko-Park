package player;

import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.util.Duration;
import gamestage.Config;
import gamestage.GameStage;
import gamestage.GameTimer;
import objects.Key;
import obstacle.Box;
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

	public final static int PLAYER_WIDTH = 43;
	public final static int PLAYER_HEIGHT = 48;
	private final static double SPEED = 2.0;

	// User for sprite animation.
	private final static int MAX_FRAME = 9;
	private final static int MAX_SPRITE = 4;

	private static final double USERNAME_FONTSIZE = 15;
	private boolean sentIdleCheck;
	private boolean sentDeadCheck;

	// Player image
	private final Image INITIAL_IMAGE = new Image("assets/" + INITIAL_COLOR + "_character/" + INITIAL_DIRECTION + "_" + INITIAL_ACTION + ".png", PLAYER_HEIGHT, PLAYER_HEIGHT, true, false);

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
	private boolean pushing;
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
		this.pushing = false;
		this.tileManager = GameTimer.getTileManager();
		this.direction = INITIAL_DIRECTION;
		this.action = INITIAL_ACTION;

		// TODO: make color constant for all players [currently, player 1 (one who input identifier is green)]
		this.color = INITIAL_COLOR;
		Player.INITIAL_INDEX++;

		if (this.name.equals(GameStage.getIdentifier())){
			this.prepareActionHandlers();
			if (!GameStage.isSinglePlayer()){
				this.sentDeadCheck = false;
				this.sentIdleCheck = false;
			}
		}
	}

	//For getting user input for player movement
	// *Down is not included since there is no jump in Pico Park
	private void prepareActionHandlers() {
	    this.scene.setOnKeyPressed(new EventHandler<KeyEvent>(){
	    	public void handle(KeyEvent e){
	    		KeyCode code = e.getCode();
				if (isAlive){
					if(code.equals(KeyCode.SPACE) && !jumping && !falling) jump();
					if(code.equals(KeyCode.A)) left = true;
					if(code.equals(KeyCode.D)) right = true;
				}
	    	}
	    });

	    this.scene.setOnKeyReleased(new EventHandler<KeyEvent>(){
			public void handle(KeyEvent e){
				KeyCode code = e.getCode();
				if (isAlive){
					if(code.equals(KeyCode.A)) left = false;
					if(code.equals(KeyCode.D)) right = false;
				}
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
	
		if (!GameStage.isSinglePlayer()) this.sendPlayerCoordinates();
		if (this.pushing) this.pushing = false;
		this.checkBoxCollision(prevXPos);
		this.checkBoxCollisionY();
		this.checkPlatformCollisionX(prevXPos);
		this.checkPlatformCollisionY();

		this.checkOnPlatform();
	
			//For sprite animation
		this.spriteCounter++;
		if (this.spriteCounter > MAX_FRAME){
			this.spriteNum = (this.spriteNum % MAX_SPRITE) + 1; // wrapping sprite number using modulo.
			this.spriteCounter = 0;
		}
	
	}

	// send player coordinates to server
	public void sendPlayerCoordinates(){
		if ((this.dx != 0 || this.dy != 0) && !this.action.equals("idle")){
			String message = "coor;" + name + ";" +  this.getXPos() + ";" + this.getYPos() + ";" + this.action + ";" + this.direction + ";" + this.left + ";" + this.right + ";" + this.spriteNum + ";";
			GameStage.client.sendMessage(message);
			this.sentIdleCheck = false;
		} else if (this.dx == 0 && this.dy == 0 && !this.sentIdleCheck){
			String message = "coor;" + name + ";" +  this.getXPos() + ";" + this.getYPos() + ";idle;" + this.direction + ";" + this.left + ";" + this.right + ";";
			GameStage.client.sendMessage(message);
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
    }

	private void checkOnPlatform(){
		ArrayList<Sprite> platforms = tileManager.getPlatforms();
		ArrayList<Box> boxes = GameTimer.getBoxes();

        boolean onPlatform = this.isOnPlatform(platforms);
		boolean onBox = this.isOnBox(boxes);

		if (!onPlatform && !onBox){
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

	private void checkBoxCollision(double prevXPos){
		ArrayList<Box> boxes = GameTimer.getBoxes();
		for (Box box: boxes){
			// System.out.println(this.falling);
			if (this.collidesWith(box) && !this.jumping && !this.falling){
				box.move(this);
				this.xPos = prevXPos + 0.5;
				this.dx = 0;
				this.pushing = true;
				break;
			}
		}
	}

	private boolean isOnBox(ArrayList<Box> boxes){
		this.yPos += 1;
        boolean onBox = false;
        for (Box box : boxes) {
            if (this.collidesWith(box)){
                onBox = true;
                break;
            }
        }
        this.yPos -= 1;
        return onBox;
	}

	private void checkBoxCollisionY() {
		ArrayList<Box> boxes = GameTimer.getBoxes();
        for (Box box: boxes){
            if (this.collidesWith(box)){
                if (this.dy > 0){  //falling
                    this.yPos = box.getYPos() - this.height;
                    this.dy = 0;
                    this.jumping = false;
                    this.falling = false;
                } else if (this.dy < 0){  //jumping
                    this.yPos = box.getYPos() + box.getHeight();
                    this.dy = 0;
                }
            }
        }
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

		if(GameStage.getIdentifier().equals(this.name) && !GameStage.isSinglePlayer() && !this.sentDeadCheck) {
			GameStage.client.sendMessage("dead;" + this.name);
			this.sentDeadCheck = true;
		};

		// vanish player after 2 seconds
		Timeline vanishTL = new Timeline(
			new KeyFrame(
				Duration.seconds(2),
				event -> {
					this.vanish();
					this.setXPos(Player.INITIAL_X);
					this.setYPos(Player.INITIAL_Y);
				}
            )
		);
        vanishTL.setCycleCount(1);
        vanishTL.play();

		this.respawn();
	}

	public void respawn() {
		// respawn player after 5 seconds
			// set player visibility to true 5 seconds after respawning (player flickers otherwise)
			Timeline respawnTL = new Timeline(
				new KeyFrame(
					Duration.seconds(5),
					event -> {
						this.setAction(Player.INITIAL_ACTION);
						this.setDirection(Player.INITIAL_DIRECTION);
						this.isAlive = true;
						this.falling = false;
						if (!GameStage.isSinglePlayer()) this.sentDeadCheck = false;
						Timeline appearTL = new Timeline(
							new KeyFrame(
								Duration.seconds(5),
								e -> {
									this.appear();
									if (this.name.equals(GameStage.getIdentifier()) && !GameStage.isSinglePlayer()) this.sendPlayerCoordinates();
								}
							)
						);
						appearTL.setCycleCount(1);
						appearTL.play();
					}
				)
			);
			respawnTL.setCycleCount(1);
			respawnTL.play();
	}

	// TODO: fix player push bug
	//For sprite animation
	private void animatePlayer() {
		Image img = null;

		if (this.visible && (this.left || this.right)) {
			if (this.pushing) {
				this.action = ACTIONS[3];
				img = new Image("assets/" + this.color + "_character/" + this.direction + "_" + this.action + "_" + this.spriteNum + ".png", PLAYER_HEIGHT, PLAYER_HEIGHT, true, false);
			} else{
				this.action = ACTIONS[1]; // walk
				img = new Image("assets/" + this.color + "_character/" + this.direction + "_" + this.action + "_" + this.spriteNum + ".png", PLAYER_HEIGHT, PLAYER_HEIGHT, true, false);
			}
		} else if (this.visible && this.jumping) {
			this.action = ACTIONS[2]; // jump
			img = new Image("assets/" + this.color + "_character/" + this.direction + "_" + this.action + ".png", PLAYER_HEIGHT, PLAYER_HEIGHT, true, false);
		} else if (!this.isAlive) {
			this.action = ACTIONS[4]; 	// dead
			img = new Image("assets/" + this.color + "_character/" + this.action + ".png", PLAYER_HEIGHT, PLAYER_HEIGHT, true, false);
		} else {
			this.action = ACTIONS[0]; 	// idle
			img = new Image("assets/" + this.color + "_character/" + this.direction + "_" + this.action + ".png", PLAYER_HEIGHT, PLAYER_HEIGHT, true, false);
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

	public boolean isPushing(){
		return this.pushing;
	}

	public void setPushing(){
		this.pushing = true;
	}

	public void stopPushing(){
		this.pushing = false;
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

	    double centerXPos = this.getXPos() + (PLAYER_HEIGHT / 2);	//get the horizontal center point of the player
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
