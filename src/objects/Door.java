package objects;

import gamestage.Config;
import gamestage.GameStage;
import gamestage.GameTimer;
import javafx.scene.image.Image;
import player.Player;
import sprite.Sprite;

public class Door extends Sprite{
	private final static double INITIAL_X = Config.INITIAL_DOOR_LOCATION[GameStage.stageLevel - 1][0];
	private final static double INITIAL_Y = Config.INITIAL_DOOR_LOCATION[GameStage.stageLevel - 1][1];
	private final static double DOOR_SIZE = 64;
	private final static Image CLOSED_DOOR_IMAGE = new Image("assets/close_door.png", DOOR_SIZE, DOOR_SIZE, true, false);
	private final static Image OPEN_DOOR_IMAGE = new Image("assets/open_door.png", DOOR_SIZE, DOOR_SIZE, true, false);

	private boolean open;

	public Door() {
		super(INITIAL_X, INITIAL_Y);
		this.loadImage(CLOSED_DOOR_IMAGE);
		this.open = false;
	}

	public void unlock(Player player){
		if (player.getKey() != null){
			this.open = true;
			this.loadImage(OPEN_DOOR_IMAGE);
			if (!GameStage.SINGLE_PLAYER_GAME) GameTimer.client.sendMessage("door;opened");
			//player.removeKey();
		}
	}

	public void checkCollision(Player player){
	    if (this.collidesWith(player) && !this.open){
	        this.unlock(player);
	        if (player.getKey() != null) {
	            player.getKey().use();
	        }
	    } else if (this.playerAtCenter(player) && this.open){
	        if (!GameStage.SINGLE_PLAYER_GAME && player.isVisible()) GameTimer.client.sendMessage("door;" + player.getUsername());
			player.vanish();
	    }
	}

	//checks if player is at the center of the door
	// **returns true if yes, otherwise false
	public boolean playerAtCenter(Player player){
		//center position of player
	    double playerCenterX = player.getXPos() + (Player.PLAYER_WIDTH / 2);
	    double playerCenterY = player.getYPos() + (Player.PLAYER_HEIGHT / 2);

	    //center position of door
	    double doorCenterX = this.getXPos() + (DOOR_SIZE / 2);
	    double doorCenterY = this.getYPos() + (DOOR_SIZE / 2);

	    double diffX = Math.abs(playerCenterX - doorCenterX);
	    double diffY = Math.abs(playerCenterY - doorCenterY);

	    return diffX <= Player.PLAYER_WIDTH / 2 && diffY <= Player.PLAYER_HEIGHT / 2;
	}

	//getter
	public boolean isOpen(){
		return this.open;
	}

	public void setOpen(){
		this.open = true;
		this.loadImage(OPEN_DOOR_IMAGE);
	}

}
