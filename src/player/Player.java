package player;

import gamestage.GameStage;
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
import objects.Key;
import tile.TileManager;
import sprite.Sprite;

public class Player extends Sprite{
	private final static String[] COLORS = {"green", "red", "yellow", "orange"};
	private final static String[] DIRECTIONS = {"left", "right"};
	private final static String[] ACTIONS = {"idle", "walk", "jump", "push", "dead"};

	//Initial player spawn location
	public final static int INITIAL_X = 50;
	public final static int INITIAL_Y = 594;

	private final static int INITIAL_INDEX = 0; // increment this for multiplayer.
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

	// Player image
	public final Image INITIAL_IMAGE = new Image("assets/" + INITIAL_COLOR + "_character/" + INITIAL_DIRECTION + "_" + INITIAL_ACTION + ".png", PLAYER_WIDTH, PLAYER_HEIGHT, true, false);

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

	//for player jump
	private final static double JUMP_HEIGHT = 12;
	private final static double JUMP_ACCELERATION = 0.8;
	private final static double FALL_SPEED = 6;
	private double jumpSpeed;
	private double jumpYPos;
	private boolean jumping;
	private boolean falling;
	private Key key;
	private TileManager tileManager;

	//Player constructor
	public Player(String name, Scene scene, TileManager tileManager){
		super(INITIAL_X, INITIAL_Y);
		this.loadImage(INITIAL_IMAGE);
		this.name = name;
		this.key = null;
		this.scene = scene;
		this.spriteNum = 0;
		this.spriteCounter = 0;
		this.jumpSpeed = 0;
		this.jumpYPos = 0;
		this.jumping = false;
		this.falling = false;
		this.tileManager = tileManager;
		this.color = INITIAL_COLOR;
		this.direction = INITIAL_DIRECTION;
		this.action = INITIAL_ACTION;
		this.prepareActionHandlers();
	}

	//For getting user input for player movement
	// *Down is not included since there is no jump in Pico Park
	private void prepareActionHandlers() {
	    this.scene.setOnKeyPressed(new EventHandler<KeyEvent>(){
	    	public void handle(KeyEvent e){
	    		KeyCode code = e.getCode();
	    		if(code.equals(KeyCode.SPACE) && !jumping)jump();
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
		//Reset dx and dy value
		this.dx = 0;
		this.dy = 0;

		if (this.left){
			this.dx = -SPEED;
			this.direction = DIRECTIONS[0];
		} else if (this.right){
			this.dx = SPEED;
			this.direction = DIRECTIONS[1];
		}

		if (this.xPos + this.dx >= 0 && this.xPos + this.dx + this.width <= GameStage.WINDOW_SIZE){
			this.xPos += dx;
		}

		if (this.yPos + this.dy >= 0 && this.yPos + this.height + this.dy <= GameStage.WINDOW_SIZE){
			this.yPos += dy;
		}

		if (!this.jumping){
			this.falling = true;
		}

		if (this.jumping){
			// update vertical position based on jump
			this.yPos += this.jumpSpeed;
			this.jumpSpeed += JUMP_ACCELERATION;

			//check if the player landed
			if (this.yPos >= this.jumpYPos){
				this.yPos = this.jumpYPos;
				this.jumping = false;
			}
		} else if (this.falling) {
			this.jumpSpeed = 0;
			this.yPos += FALL_SPEED;
		}

		//Check for collisions with platforms
		for (Sprite platform : tileManager.getPlatforms()) {
		    if (this.collidesWith(platform)) {
				//Check for vertical collisions with the platform
				// *Iterates over the width of the player
				// *Makes sure that each player's vertical position is checked for collisions
		        for (int x = (int) this.xPos; x < this.xPos + this.width; x++) {
					int bottom = tileManager.getTile((int) (this.xPos + this.width / 2), (int) (this.yPos + this.height));
		            int top = tileManager.getTile(x, (int) (this.yPos));

			        if (this.falling && bottom == 1) {
			            this.yPos = platform.getYPos() - this.height;
			            this.jumping = false;
			            this.falling = false;
			            break;
			        } else if (this.falling && top == 1) {
			            this.yPos = platform.getYPos() + platform.getHeight();
			            this.jumpSpeed = 0;
			            this.falling = true;
			            break;
			        }
		        }

				//Check for horizontal collisions with the platform
				// *Iterates over the height of the player
				// *Makes sure that each player's horizontal position is checked for collisions
		        for (int y = (int) this.yPos; y < this.yPos + this.height; y++) {
		            int left = tileManager.getTile((int) (this.xPos - 1), y);
		            int right = tileManager.getTile((int) (this.xPos + this.width), y);

		            if (!this.falling && left == 1) {
		                this.xPos = platform.getXPos() + platform.getHeight();
		                break;
		            } else if (!this.falling && right == 1) {
		                this.xPos = platform.getXPos() - platform.getHeight();
		                break;
		            }
		        }
		    }
		}

		this.animatePlayer();

		//For sprite animation
		this.spriteCounter++;
		if (this.spriteCounter > MAX_FRAME){
			this.spriteNum = (this.spriteNum % MAX_SPRITE) + 1; // wrapping sprite number using modulo.
			this.spriteCounter = 0;
		}

	}

	//Initiates a jump for the player
	// *Sets the player's state to jumping
	// *Assigns an initial upward jump speed
	// *Records the current vertical position of the player
	private void jump(){
		if(!this.falling){
			this.jumping = true;
			this.jumpSpeed = -JUMP_HEIGHT;
			this.jumpYPos = this.yPos;
			this.falling = true;
		}
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
	  } else {
	      this.action = ACTIONS[0]; // idle
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

	public void removeKey(){
		this.key = null;

	}

	@Override
	public void render(GraphicsContext gc, double xPos, double yPos){
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
