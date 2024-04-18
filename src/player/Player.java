package player;

import gamestage.GameStage;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import sprite.Sprite;

public class Player extends Sprite{
	//Initial player spawn location
	private final static int INITIAL_X = 50;
	private final static int INITIAL_Y = 600;

	//Player height must be greater than width by 5 pixels
	private final static int PLAYER_WIDTH = 47;
	private final static int PLAYER_HEIGHT = 52;
	private final static double SPEED = 2.0;

	// User for sprite animation.
	private final static int MAX_FRAME = 9;
	private final static int MAX_SPRITE = 4;

	// Player images
	public final static Image PLAYER_LEFT_IDLE = new Image("assets/green_character/green_left_idle_1.png", PLAYER_WIDTH, PLAYER_HEIGHT, true, false);

	public final static Image PLAYER_LEFT_WALK_1 = new Image("assets/green_character/green_left_walk_1.png", PLAYER_WIDTH, PLAYER_HEIGHT, true, false);
	public final static Image PLAYER_LEFT_WALK_2 = new Image("assets/green_character/green_left_walk_2.png", PLAYER_WIDTH, PLAYER_HEIGHT, true, false);
	public final static Image PLAYER_LEFT_WALK_3 = new Image("assets/green_character/green_left_walk_3.png", PLAYER_WIDTH, PLAYER_HEIGHT, true, false);
	public final static Image PLAYER_LEFT_WALK_4 = new Image("assets/green_character/green_left_walk_4.png", PLAYER_WIDTH, PLAYER_HEIGHT, true, false);

	public final static Image PLAYER_LEFT_PUSH_1 = new Image("assets/green_character/green_left_push_1.png", PLAYER_WIDTH, PLAYER_HEIGHT, true, false);
	public final static Image PLAYER_LEFT_PUSH_2 = new Image("assets/green_character/green_left_push_2.png", PLAYER_WIDTH, PLAYER_HEIGHT, true, false);
	public final static Image PLAYER_LEFT_PUSH_3 = new Image("assets/green_character/green_left_push_3.png", PLAYER_WIDTH, PLAYER_HEIGHT, true, false);
	public final static Image PLAYER_LEFT_PUSH_4 = new Image("assets/green_character/green_left_push_4.png", PLAYER_WIDTH, PLAYER_HEIGHT, true, false);

	public final static Image PLAYER_RIGHT_IDLE = new Image("assets/green_character/green_right_idle_1.png", PLAYER_WIDTH, PLAYER_HEIGHT, true, false);

	public final static Image PLAYER_RIGHT_WALK_1 = new Image("assets/green_character/green_right_walk_1.png", PLAYER_WIDTH, PLAYER_HEIGHT, true, false);
	public final static Image PLAYER_RIGHT_WALK_2 = new Image("assets/green_character/green_right_walk_2.png", PLAYER_WIDTH, PLAYER_HEIGHT, true, false);
	public final static Image PLAYER_RIGHT_WALK_3 = new Image("assets/green_character/green_right_walk_3.png", PLAYER_WIDTH, PLAYER_HEIGHT, true, false);
	public final static Image PLAYER_RIGHT_WALK_4 = new Image("assets/green_character/green_right_walk_4.png", PLAYER_WIDTH, PLAYER_HEIGHT, true, false);

	public final static Image PLAYER_RIGHT_PUSH_1 = new Image("assets/green_character/green_right_push_1.png", PLAYER_WIDTH, PLAYER_HEIGHT, true, false);
	public final static Image PLAYER_RIGHT_PUSH_2 = new Image("assets/green_character/green_right_push_2.png", PLAYER_WIDTH, PLAYER_HEIGHT, true, false);
	public final static Image PLAYER_RIGHT_PUSH_3 = new Image("assets/green_character/green_right_push_3.png", PLAYER_WIDTH, PLAYER_HEIGHT, true, false);
	public final static Image PLAYER_RIGHT_PUSH_4 = new Image("assets/green_character/green_right_push_4.png", PLAYER_WIDTH, PLAYER_HEIGHT, true, false);

	public final static Image PLAYER_DEATH = new Image("assets/green_character/green_dead.png", PLAYER_WIDTH, PLAYER_HEIGHT, true, false);

	//direction input booleans
	private static boolean left;
	private static boolean right;
	private static boolean up;

	private String name; //player username
	private boolean hasKey;
	private Image playerCharacter;
	private Scene scene;
	private int spriteNum;
	private int spriteCounter;
	private String direction; //indicates if player is facing right or left

	//Player constructor
	public Player(String name, Scene scene){
		super(INITIAL_X, INITIAL_Y, PLAYER_RIGHT_IDLE);
		this.name = name;
		this.hasKey = false;
		this.scene = scene;
		this.prepareActionHandlers();

		this.spriteNum = 0;
		this.spriteCounter = 0;
		this.direction = "right";
	}

	//For getting user input for player movement
	// *Down is not included since there is no jump in Pico Park
	private void prepareActionHandlers() {
	    this.scene.setOnKeyPressed(new EventHandler<KeyEvent>(){
	    	public void handle(KeyEvent e){
	    		String code = e.getCode().toString();
	            if(code.equals("A")) {
	                left = true;
	                direction = "left";
	            }else if(code.equals("D")) {
	                right = true;
	                direction = "right";
	            }else if(code.equals("W")){
	                up = true;
	            }
	    	}
	    });

	    this.scene.setOnKeyReleased(new EventHandler<KeyEvent>(){
			public void handle(KeyEvent e){
	    		String code = e.getCode().toString();
	    		if(code.equals("A")) {
	                left = false;
	    		}else if(code.equals("D")) {
	                right = false;
	    		}else if(code.equals("W")){
	                up = false;
	    		}
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

		if (this.left){
			this.dx = -SPEED;
		} else if (this.right){
			this.dx = SPEED;
		}

		if (this.up){
			this.dy = -SPEED;
		}

		if (this.xPos + this.dx >= 0 && this.xPos + this.dx + this.width <= GameStage.WINDOW_WIDTH){
			this.xPos += dx;
		}

		if (this.yPos + this.dy >= 0 && this.yPos + this.height + this.dy <= GameStage.WINDOW_WIDTH){
			this.yPos += dy;
		}

		this.animatePlayer();

		//For sprite animation
		this.spriteCounter++;
		if (this.spriteCounter > MAX_FRAME){
			this.spriteNum = (this.spriteNum % MAX_SPRITE) + 1; // wrapping sprite number using modulo.
			this.spriteCounter = 0;
		}

	}

	//For sprite animation
	// *Bug: if player stops moving, idle does not show
	private void animatePlayer(){
		if (this.up){
			if (this.direction == "right"){
				this.loadImage(PLAYER_RIGHT_WALK_4);
			} else {
				this.loadImage(PLAYER_LEFT_WALK_4);
			}

		} else if (this.left){
			switch (spriteNum){
			case 1:
				this.loadImage(PLAYER_LEFT_WALK_1);
				break;
			case 2:
				this.loadImage(PLAYER_LEFT_WALK_2);
				break;
			case 3:
				this.loadImage(PLAYER_LEFT_WALK_3);
				break;
			case 4:
				this.loadImage(PLAYER_LEFT_WALK_4);
				break;
			}
		} else if (this.right){
			switch (spriteNum){
			case 1:
				this.loadImage(PLAYER_RIGHT_WALK_1);
				break;
			case 2:
				this.loadImage(PLAYER_RIGHT_WALK_2);
				break;
			case 3:
				this.loadImage(PLAYER_RIGHT_WALK_3);
				break;
			case 4:
				this.loadImage(PLAYER_RIGHT_WALK_4);
				break;
			}
		} else{
			if (this.direction == "right"){
				this.loadImage(PLAYER_RIGHT_IDLE);
			} else {
				this.loadImage(PLAYER_LEFT_IDLE);
			}
		}
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
