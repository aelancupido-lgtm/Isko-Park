package obstacle;

import gamestage.Config;
import gamestage.GameStage;
import javafx.scene.image.Image;
import player.Player;
import sprite.Sprite;

public class Portal extends Sprite{
    private final static double PORTAL_SIZE = 64;
    private final static Image PORTAL_IMAGE = new Image("assets/portal.gif", PORTAL_SIZE, PORTAL_SIZE, true, false);
    private final static double PLAYER_DISTANCE = Player.PLAYER_WIDTH / 4;

    private static int PORTAL_NUMBER = 0;

    private Portal warpPortal;
    private boolean warped;

    public Portal(){
        super(Config.obstacleLocation("PORTAL", PORTAL_NUMBER, true), Config.obstacleLocation("PORTAL", PORTAL_NUMBER, false));
        this.loadImage(PORTAL_IMAGE);
        this.warped = false;
        Portal.PORTAL_NUMBER++;
    }

    public void setWarpPortal(Portal portal){
        this.warpPortal = portal;
    }

    public void warpPlayer(Player player){
        double newXPos = warpPortal.getXPos();
        if (player.getDirection() == "left") {
            newXPos -= PLAYER_DISTANCE + (PORTAL_SIZE / 2); 
        } else if (player.getDirection() == "right") {
            newXPos += PLAYER_DISTANCE + (PORTAL_SIZE / 2);
        }

        player.setXPos(newXPos);
        player.setYPos(warpPortal.getYPos() - (PORTAL_SIZE / 8));
        if (!GameStage.isSinglePlayer()) player.sendPlayerCoordinates();
        this.setWarpMove();
        this.warpPortal.resetWarpMove();
    }

    public void checkCollision(Player player){
        if (this.collidesWith(player) && !this.warpPortal.getWarpMove()){
            this.warpPlayer(player);
        } 
        else {
            this.resetWarpMove();
        }
    }

    public void setWarpMove(){
        this.warped = true;
    }

    public void resetWarpMove(){
        this.warped = false;
    }

    public Portal getWarpPortal(){
        return this.warpPortal;
    }

    public boolean getWarpMove(){
        return this.warped;
    }

    public static void resetPortalCount(){
		Portal.PORTAL_NUMBER = 0;
	}
}
