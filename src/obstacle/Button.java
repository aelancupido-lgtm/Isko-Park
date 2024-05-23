package obstacle;

import javafx.scene.image.Image;
import player.Player;
import sprite.Sprite;

/*
 * Button class
 * 	- currently does nothing when clicked
 * */

public class Button extends Sprite{
	private static final double BUTTON_SIZE = 32;
	private static final double INITIAL_X = 450;
	private static final double INITIAL_Y = 632;
	private static final Image BUTTON_UNCLICKED = new Image("assets/button_unclicked.png", BUTTON_SIZE, BUTTON_SIZE, true, false);
	private static final Image BUTTON_CLICKED = new Image("assets/button_clicked.png", BUTTON_SIZE, BUTTON_SIZE, true, false);

	private boolean clicked;

	public Button(){
		super(INITIAL_X, INITIAL_Y);
		this.loadImage(BUTTON_UNCLICKED);
		this.clicked = false;
	}


	//checks if player is at the center of the button
	// **returns true if yes, otherwise false
	public boolean playerAtCenter(Player player){
		//center position of player
	    double playerCenterX = player.getXPos() + (Player.PLAYER_WIDTH / 2);
	    double playerCenterY = player.getYPos() + (Player.PLAYER_HEIGHT / 2);

	    //center x position of key
	    double btnCenterX = this.getXPos() + (BUTTON_SIZE / 2);

	    double diffX = Math.abs(playerCenterX - btnCenterX);
	    double diffY = Math.abs(playerCenterY - this.getYPos());

	    return diffX <= Player.PLAYER_WIDTH / 2 && diffY <= Player.PLAYER_HEIGHT / 2;
	}

	public void checkCollision(Player player){
		if (this.playerAtCenter(player)){
			this.clicked = true;
			this.loadImage(BUTTON_CLICKED);
			// player dead
		}
	}



}
