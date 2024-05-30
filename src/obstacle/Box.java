package obstacle;

import gamestage.Config;
import gamestage.GameStage;
import javafx.scene.image.Image;
import player.Player;
import sprite.Sprite;

public class Box extends Sprite{
    private static final double INITIAL_X = Config.INITIAL_BOX_LOCATION[GameStage.getLevel()][0];
    private static final double INITIAL_Y = Config.INITIAL_BOX_LOCATION[GameStage.getLevel()][1];
    private static final double BOX_SIZE = 32;
    private static final Image BOX_IMAGE = new Image("assets/box.png", BOX_SIZE, BOX_SIZE, true, false);

    public Box(){
        super(INITIAL_X, INITIAL_Y);
        this.loadImage(BOX_IMAGE);
    }

	public void checkCollision(Player player){
		if(this.collidesWith(player)){
            // TODO: add push animation to player
            // TODO: add box moving with player push
		    }
		}
    }
