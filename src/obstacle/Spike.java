package obstacle;

import gamestage.Config;
import gamestage.GameStage;
import javafx.scene.image.Image;
import player.Player;
import sprite.Sprite;

public class Spike extends Sprite {
    private static final double INITIAL_X = Config.INITIAL_SPIKE_LOCATION[GameStage.getLevel()][0];
    private static final double INITIAL_Y = Config.INITIAL_SPIKE_LOCATION[GameStage.getLevel()][1];
    private static final double SPIKE_SIZE = 64;
    private static final Image SPIKES_IMAGE = new Image("assets/spike.gif", SPIKE_SIZE, SPIKE_SIZE, true, false);

    public Spike(){
        super(INITIAL_X, INITIAL_Y);
        this.loadImage(SPIKES_IMAGE);
    }

    	public void checkCollision(Player player){
		if (this.collidesWith(player)){
            player.die();

        }
	}
}
