package obstacle;

import gamestage.Config;
import gamestage.GameStage;
import gamestage.GameTimer;
import javafx.scene.image.Image;
import player.Player;
import sprite.Sprite;

public class Button extends Sprite{
	private static final double BUTTON_SIZE = 32;
	private static final double INITIAL_X = Config.INITIAL_BUTTON_LOCATION[GameStage.getLevel()][0];
	private static final double INITIAL_Y = Config.INITIAL_BUTTON_LOCATION[GameStage.getLevel()][1];;
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
			if (!GameStage.SINGLE_PLAYER_GAME) GameTimer.client.sendMessage("button;clicked");
			
			/* 
			TODO: add button functionality 
			[per level if conditional for different functions? (player who steps on button is dead, door is visible, etc.?)]
			*/ 
		}
	}

	public void setClicked(){
		this.clicked = true;
		this.loadImage(BUTTON_CLICKED);
	}



}
