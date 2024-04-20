package gamestage;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import objects.Door;
import objects.Key;
import obstacle.Button;
import player.Player;

public class GameTimer extends AnimationTimer{
	private Scene scene;
	private GraphicsContext gc;
	private GameStage gameStage;
	private GameOver gameOver;

	private Player player;
	private Key key;
	private Door door;
	private Button button;

	GameTimer(Scene scene, GraphicsContext gc, GameStage gameStage) {
	    this.gc = gc;
	    this.scene = scene;
		this.gameStage = gameStage;
	    this.gameOver = new GameOver(scene, gc);

	    this.player = new Player("player", this.scene);
	    this.key = new Key();
	    this.door = new Door();
	    this.button = new Button();
	}


	@Override
	public void handle(long arg0) {
		this.gc.clearRect(0, 0, GameStage.WINDOW_WIDTH, GameStage.WINDOW_WIDTH);

		this.checkStatus();
		this.renderSprites();
		this.moveSprites();
	}

	//For checking collision between objects and the player
	private void checkStatus(){
		this.key.checkCollision(this.player);
		this.door.checkCollision(this.player);
		this.button.checkCollision(this.player);

		//shows GameOver scene when player enter the door
		// **need to edit when game level is implemented
        if (this.door.isOpen() && this.door.playerAtCenter(this.player)){
            PauseTransition transition = new PauseTransition(Duration.seconds(1));
            transition.setOnFinished(event -> {
            	this.stop();
                this.gameOver.showGameOver();
                gameStage.setGameOverScene(gameOver.getScene());
            });
            transition.play();
        }
	}

	//For rendering sprites
	private void renderSprites(){
		this.gc.setFill(Color.ORANGE);
		this.gc.fillRect(0, Player.INITIAL_Y + Player.PLAYER_HEIGHT, GameStage.WINDOW_WIDTH, GameStage.WINDOW_WIDTH - Player.INITIAL_Y);

		this.door.render(this.gc, this.door.getXPos(), this.door.getYPos());
		this.button.render(this.gc, this.button.getXPos(), this.button.getYPos());

		if (this.key.isVisible()){
			this.key.render(this.gc, this.key.getXPos(), this.key.getYPos());
		}

		if(this.player.isVisible()) {
			this.player.render(this.gc, this.player.getXPos(), this.player.getYPos());
		}
	}

	//For sprite movement
	private void moveSprites(){
		this.key.move();
		this.player.move();
	}
}

