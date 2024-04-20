package obstacle;

import javafx.scene.image.Image;
import player.Player;
import sprite.Sprite;

public class Button extends Sprite{
	private static final double BUTTON_SIZE = 32;
	private static final double INITIAL_X = 450;
	private static final double INITIAL_Y = 643;
	private static final Image BUTTON_UNCLICKED = new Image("assets/button_unclicked.png", BUTTON_SIZE, BUTTON_SIZE, true, false);
	private static final Image BUTTON_CLICKED = new Image("assets/button_clicked.png", BUTTON_SIZE, BUTTON_SIZE, true, false);

	private boolean clicked;

	public Button(){
		super(INITIAL_X, INITIAL_Y);
		this.loadImage(BUTTON_UNCLICKED);
		this.clicked = false;
	}

	public void checkCollision(Player player){
		if (this.collidesWith(player)){
			this.clicked = true;
			this.loadImage(BUTTON_CLICKED);
		}
	}



}
