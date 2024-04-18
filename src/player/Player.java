package player;

import gamestage.GameStage;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import sprite.Sprite;

public class Player extends Sprite{
	private final static String[] COLORS = {"green", "red", "yellow", "orange"};
	private final static String[] DIRECTIONS = {"left", "right"};
	private final static String[] ACTIONS = {"idle", "walk", "jump", "push", "dead"};

	//Initial player spawn location
	private final static int INITIAL_X = 50;
	private final static int INITIAL_Y = 600;

	private final static int INITIAL_INDEX = 0; // increment this for multiplayer.
	private final String INITIAL_COLOR = COLORS[INITIAL_INDEX]; // not static since this is an instance variable, not a class variable.
	private final static String INITIAL_DIRECTION = DIRECTIONS[1];
	private final static String INITIAL_ACTION = ACTIONS[0];

	//Player height must be greater than width by 5 pixels
	private final static int PLAYER_WIDTH = 47;
	private final static int PLAYER_HEIGHT = 52;
	private final static double SPEED = 2.0;

	// User for sprite animation.
	private final static int MAX_FRAME = 9;
	private final static int MAX_SPRITE = 4;

	// Player image
	public final Image INITIAL_IMAGE = new Image("assets/" + INITIAL_COLOR + "_character/" + INITIAL_DIRECTION + "_" + INITIAL_ACTION + ".png", PLAYER_WIDTH, PLAYER_HEIGHT, true, false);

	//direction input booleans
	private boolean left;
	private boolean right;
	private boolean up;

	private String name; //player username
	private boolean hasKey;
	private Image playerCharacter;
	private Scene scene;
	private int spriteNum;
	private int spriteCounter;
	private String color;
	private String direction; //indicates if player is facing right or left
	private String action;

	//for player jump
	private final static double JUMP_HEIGHT = 10;
	private final static double JUMP_ACCELERATION = 0.8;
	private double jumpSpeed;
	private double jumpYPos;
	private boolean isJumping;

	//Player constructor
	public Player(String name, Scene scene){
		super(INITIAL_X, INITIAL_Y);
		this.loadImage(INITIAL_IMAGE);
		this.name = name;
		this.hasKey = false;
		this.scene = scene;
		this.spriteNum = 0;
		this.spriteCounter = 0;
		this.jumpSpeed = 0;
		this.jumpYPos = 0;
		this.isJumping = false;
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
	    		String code = e.getCode().toString();
	    		if(code.equals("W") && !up && !isJumping){
	    			up = true;
	    			jump();
	    		}
	    		if(code.equals("A")) left = true;
	    		if(code.equals("D")) right = true;
	    	}
	    });

	    this.scene.setOnKeyReleased(new EventHandler<KeyEvent>(){
			public void handle(KeyEvent e){
	    		String code = e.getCode().toString();
	    		if(code.equals("W")) up = false;
	    		if(code.equals("A")) left = false;
	    		if(code.equals("D")) right = false;
	    	}
	    });
	}

	//For player movement
	// *Adds dx and dy to player movement
	// *Checks if player will reach boundary
	// *Animates player with animatePlayer method
	public void movePlayer(){
		//Reset dx and dy value
		this.dx = 0;
		this.dy = 0;

		if (this.up && !this.left && !this.right && !this.isJumping){
			jump();
		}

		if (this.left){
			this.dx = -SPEED;
			this.direction = DIRECTIONS[0];
		} else if (this.right){
			this.dx = SPEED;
			this.direction = DIRECTIONS[1];
		}

		if (this.xPos + this.dx >= 0 && this.xPos + this.dx + this.width <= GameStage.WINDOW_WIDTH){
			this.xPos += dx;
		}

		if (this.yPos + this.dy >= 0 && this.yPos + this.height + this.dy <= GameStage.WINDOW_WIDTH){
			this.yPos += dy;
		}

		if (isJumping){
			// update vertical position based on jump
			yPos += jumpSpeed;
			jumpSpeed += JUMP_ACCELERATION;
			
			//check if the player landed
			if (yPos >= jumpYPos){
				yPos = jumpYPos;
				isJumping = false;
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
		isJumping = true;
		jumpSpeed = -JUMP_HEIGHT;
		jumpYPos = yPos;
	}

	//For sprite animation
	// *Bug: if player stops moving, idle does not show
	private void animatePlayer() {
	  Image img = null;

	  if (this.left || this.right) {
	      this.action = ACTIONS[1]; // walk
	      img = new Image("assets/" + this.color + "_character/" + this.direction + "_" + this.action + "_" + this.spriteNum + ".png", PLAYER_WIDTH, PLAYER_HEIGHT, true, false);
	  } else if (this.up) {
	      this.action = ACTIONS[2]; // jump
	      img = new Image("assets/" + this.color + "_character/" + this.direction + "_" + this.action + ".png", PLAYER_WIDTH, PLAYER_HEIGHT, true, false);
	  } else {
	      this.action = ACTIONS[0]; // idle
	      img = new Image("assets/" + this.color + "_character/" + this.direction + "_" + this.action + ".png", PLAYER_WIDTH, PLAYER_HEIGHT, true, false);
	  }

	  this.loadImage(img);
	}

	//Getter methods
	private boolean checkKey(){
		return this.hasKey;
	}


	//Setter methods
	private void getKey(){
		this.hasKey = true;
	}

}

//Debugging Print.
// System.out.println("assets/" + this.color + "_character/" + this.direction + "_" + this.action + "_" + this.spriteNum + ".png");
