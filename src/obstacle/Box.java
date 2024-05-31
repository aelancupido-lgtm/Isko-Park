package obstacle;

import gamestage.Config;
import gamestage.GameStage;
import gamestage.GameTimer;
import javafx.scene.image.Image;
import player.Player;
import sprite.Sprite;

public class Box extends Sprite{
    private static final double INITIAL_X = Config.INITIAL_BOX_LOCATION[GameStage.getLevel()][0];
    private static final double INITIAL_Y = Config.INITIAL_BOX_LOCATION[GameStage.getLevel()][1];
    private static final double BOX_SIZE = 32;
    private static final Image BOX_IMAGE = new Image("assets/box.png", BOX_SIZE, BOX_SIZE, true, false);

    private static final double PUSH_FORCE = 1;

    public Box(){
        super(INITIAL_X, INITIAL_Y);
        this.loadImage(BOX_IMAGE);
    }

	public void move(Player player){
        if (player.getDirection().equals("left")){
            // this.xPos = this.xPos - PUSH_FORCE;
            this.dx = -PUSH_FORCE; 
        } else if (player.getDirection().equals("right")) {
            // this.xPos = this.xPos + PUSH_FORCE;
            this.dx = PUSH_FORCE;
        }

        if (this.xPos + this.dx >= 0 && this.xPos + this.dx + this.width <= Config.WINDOW_SIZE){
			this.xPos += dx;
		}

        if (!GameStage.isSinglePlayer()) GameTimer.client.sendMessage("box;" + this.getXPos());
	}

    private boolean buttonAtCenter(Button button){
		//center position of button
	    double boxCenterX = this.getXPos() + (BOX_SIZE / 2);
        double boxCenterY = this.getYPos() + (BOX_SIZE / 2);
	    //center x position of key
	    double btnCenterX = button.getXPos() + (Button.BUTTON_SIZE / 2);

	    double diffX = Math.abs(boxCenterX - btnCenterX);
	    double diffY = Math.abs(boxCenterY - button.getYPos());

	    return diffX <= BOX_SIZE / 2 && diffY <= BOX_SIZE / 2;
	}

    public void checkCollision(Button button){
        if (buttonAtCenter(button)){
            button.setClicked();
        }
    }

    // public boolean playerAtBorder(Player player){
    //     //center position of button
    //     double playerBorderX = player.getXPos() + player.getWidth();
    //     // double playerBorderY = player.getYPos() + player.getHeight();
    //     //center x position of key
    //     double boxBorderX = this.getXPos() + BOX_SIZE;
    

    //     double diffX = Math.abs(playerBorderX - boxBorderX);
    //     double diffY = Math.abs(boxCenterY - this.yPos);
    
    //     return diffX <= player.getWidth() / 2 && diffY <= player.getHeight() / 2;
    // }
}
