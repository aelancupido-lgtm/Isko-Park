package obstacle;

import gamestage.Config;
import gamestage.GameStage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.util.Duration;
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
            // TODO: add player death for collision + respawn
            // temp for player death
            //player.vanish();

            player.die();

			// vanish player after 2 seconds
			Timeline vanishTL = new Timeline(
				new KeyFrame(
					Duration.seconds(1),
					event -> {
						player.vanish();    
					}
            	)
			);
            vanishTL.setCycleCount(1);
            vanishTL.play();

			// respawn player after 5 seconds
			// set player visibility to true 5 seconds after respawning (player flickers otherwise)
			Timeline respawnTL = new Timeline(
				new KeyFrame(
					Duration.seconds(3),
					event -> {
						player.respawn();
						Timeline appearTL = new Timeline(
							new KeyFrame(
								Duration.seconds(3),
								e -> player.appear()
							)
						);
						appearTL.setCycleCount(1);
						appearTL.play();
					}
				)
			);
			respawnTL.setCycleCount(1);
			respawnTL.play();
		}
	}
}
