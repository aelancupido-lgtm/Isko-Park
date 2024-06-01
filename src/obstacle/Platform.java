package obstacle;

import javafx.scene.image.Image;
import sprite.Sprite;

public class Platform extends Sprite {
    private static final double PLATFORM_SIZE = 32;
    private static final Image PLATFORM_IMAGE = new Image("assets/box.png", PLATFORM_SIZE, PLATFORM_SIZE, true, false);
    private static final int LIMIT = 50;
    private static int PLATFORM_NUMBER = 0;

    private double leftBound;
    private double rightBound;
    private double speed;
    private boolean movingRight;

    public Platform(double x, double y) {
        super(x, y);
        this.leftBound = x - Platform.LIMIT;
        this.rightBound =  x + Platform.LIMIT;;
        this.speed = 1;
        this.movingRight = true;
        this.loadImage(PLATFORM_IMAGE);
        Platform.PLATFORM_NUMBER++;
    }

    public void move() {
        if (movingRight){
            setXPos(getXPos() + speed);
            if (getXPos() >= rightBound){
                movingRight = false;
            }
        } else {
            setXPos(getXPos() - speed);
            if (getXPos() <= leftBound){
                movingRight = true;
            }
        }
    }

    public static void resetPlatformCount(){
        Platform.PLATFORM_NUMBER = 0;
    }

    public static int getPlatformNumber(){
        return Platform.PLATFORM_NUMBER;
    }
}
