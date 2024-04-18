package objects;

import javafx.scene.image.Image;
import player.Player;
import sprite.Sprite;

public class Key extends Sprite{
	private final static int INITIAL_X = 50;
	private final static int INITIAL_Y = 100;
	private final static int KEY_SIZE = 35;
	private final static Image KEY_IMAGE = new Image("assets/key.png", KEY_SIZE, KEY_SIZE, true, false);
	private final static int KEY_SPACE = 5;

	private Player player;

	public Key(){
		super(INITIAL_X, INITIAL_Y);
		this.loadImage(KEY_IMAGE);
	}

	public void checkCollision(Player player){
		if(this.collidesWith(player)){
			this.player = player;

			// space between the player and the key
			this.xPos = this.player.getXPos() - KEY_SIZE + KEY_SPACE;
			this.yPos = this.player.getYPos() - KEY_SIZE + KEY_SPACE;

		}

		// methods not available currently
//		this.player.getKey();
//		String direction = this.player.getDirection();


			}


}
