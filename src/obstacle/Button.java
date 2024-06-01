package obstacle;

import gamestage.Config;
import gamestage.GameStage;
import gamestage.GameTimer;
import javafx.animation.PauseTransition;
import javafx.scene.image.Image;
import javafx.util.Duration;
import player.Player;
import sprite.Sprite;

public class Button extends Sprite{
	public static final double BUTTON_SIZE = 32;
	private static int BUTTON_NUMBER = 0;

	private static final Image BUTTON_UNCLICKED = new Image("assets/button_unclicked.png", BUTTON_SIZE, BUTTON_SIZE, true, false);
	private static final Image BUTTON_CLICKED = new Image("assets/button_clicked.png", BUTTON_SIZE, BUTTON_SIZE, true, false);

	private boolean clicked;
	private boolean sentServerMessage;
	private int btnNum;

	public Button(){
		super(Config.obstacleLocation("BUTTON", BUTTON_NUMBER, true), Config.obstacleLocation("BUTTON", BUTTON_NUMBER, false));
		this.loadImage(BUTTON_UNCLICKED);
		this.clicked = false;
		this.sentServerMessage = false;
		this.btnNum = Button.BUTTON_NUMBER;
		Button.BUTTON_NUMBER++;
	}


	//checks if player is at the center of the button
	// **returns true if yes, otherwise false
	public boolean playerAtCenter(Player player){
		//center position of player
	    double playerCenterX = player.getXPos() + (Player.PLAYER_HEIGHT / 2);
	    double playerCenterY = player.getYPos() + (Player.PLAYER_HEIGHT / 2);

	    //center x position of key
	    double btnCenterX = this.getXPos() + (BUTTON_SIZE / 2);

	    double diffX = Math.abs(playerCenterX - btnCenterX);
	    double diffY = Math.abs(playerCenterY - this.getYPos());

	    return diffX <= Player.PLAYER_HEIGHT / 2 && diffY <= Player.PLAYER_HEIGHT / 2;
	}

	public void checkCollision(Player player){
		if (this.playerAtCenter(player) && !this.clicked){
			this.setClicked();
			player.die();

			if (!GameStage.isSinglePlayer() && !this.sentServerMessage) GameTimer.client.sendMessage("button;clicked;" + this.btnNum );
			this.sentServerMessage = true;

		} else {
			this.sentServerMessage = false;
		}
	}

	public void setClicked(){
		this.clicked = true;
		this.loadImage(BUTTON_CLICKED);

		PauseTransition transition = new PauseTransition(Duration.seconds(5));
            transition.setOnFinished(event -> {
				this.setUnclicked();
            });
            transition.play();
	}

	public void setUnclicked(){
		this.clicked = false;
		this.loadImage(BUTTON_UNCLICKED);
	}

	public static void resetButtonCount(){
		Button.BUTTON_NUMBER = 0;
	}

}
