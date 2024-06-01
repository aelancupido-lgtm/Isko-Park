package obstacle;

import gamestage.Config;
import javafx.scene.image.Image;
import player.Player;
import sprite.Sprite;

public class Spike extends Sprite {
    private static final double SPIKE_SIZE = 64;
    private static final Image SPIKES_IMAGE = new Image("assets/spike.gif", SPIKE_SIZE, SPIKE_SIZE, true, false);
    private static int SPIKE_NUMBER = 0;

    public Spike(){
        super(Config.obstacleLocation("SPIKE", SPIKE_NUMBER, true), Config.obstacleLocation("SPIKE", SPIKE_NUMBER, false));
        this.loadImage(SPIKES_IMAGE);
        Spike.SPIKE_NUMBER++;
    }

    public void checkCollision(Player player){
		if (this.collidesWith(player)){
            player.die();

        }
	}

    public static void resetSpikeCount(){
		Spike.SPIKE_NUMBER = 0;
	}
}
