package objects;

import javafx.scene.image.Image;
import player.Player;
import sprite.Sprite;

public class Door extends Sprite{
	private final static double INITIAL_X = 650;
	private final static double INITIAL_Y = 582;
	private final static double DOOR_SIZE = 70;
	private final static Image CLOSED_DOOR_IMAGE = new Image("assets/closed_door.png", DOOR_SIZE, DOOR_SIZE, true, false);
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
			player.removeKey();
		}
	}

	public void checkCollision(Player player){
		if (this.collidesWith(player)){
			if(this.open) {
				player.getKey().use();
				player.vanish(); // needs delay here.
			} else {
				this.unlock(player);
			}
		}
	}

}
