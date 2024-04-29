package objects;

import gamestage.GameStage;
import javafx.scene.image.Image;
import player.Player;
import sprite.Sprite;

public class Key extends Sprite{
	private final static int INITIAL_X = 100;
	private final static int INITIAL_Y = 118;
	private final static int KEY_SIZE = 35;
	private final static Image KEY_IMAGE = new Image("assets/key.png", KEY_SIZE, KEY_SIZE, true, false);

	private Player player;

	public Key(){
		super(INITIAL_X, INITIAL_Y);
		this.loadImage(KEY_IMAGE);
	}

	public void checkCollision(Player player){
		if(this.collidesWith(player) && this.player == null && this.visible){
			this.player = player;
			this.player.acquireKey(this);
			this.move();
			}
		}

	public void move(){
		if(this.player != null && this.visible){
			String direction = this.player.getDirection();
			switch (direction){
			case "left":
				//To avoid cases where key is out of screen
				if (this.player.getXPos() + (KEY_SIZE * 2) >= 0 && this.player.getXPos() + (KEY_SIZE * 2) <= GameStage.WINDOW_SIZE){
					this.xPos = this.player.getXPos() + KEY_SIZE / 1.5;
				} else{
					this.xPos = this.player.getXPos() + KEY_SIZE / 3.5;
				}
				break;
			case "right":
				//To avoid cases where key is out of screen
				if (this.player.getXPos() - (KEY_SIZE * 2) >= 0 && this.player.getXPos() - (KEY_SIZE * 2) <= GameStage.WINDOW_SIZE) {
					this.xPos = this.player.getXPos() - KEY_SIZE / 1.5;
				} else {
					this.xPos = this.player.getXPos() - KEY_SIZE / 3.5;
				}
				break;

			}

			if (this.player.getYPos() - (KEY_SIZE * 2) >= 0 && this.player.getYPos() - (KEY_SIZE * 2) <= GameStage.WINDOW_SIZE){
				this.yPos = this.player.getYPos() - KEY_SIZE;
			} else {
				this.yPos = this.player.getYPos() - KEY_SIZE / 3.5;
			}
		}

	}

	//For unlocking door
	public void use(){
		this.player = null;
		this.vanish();
	}


}
