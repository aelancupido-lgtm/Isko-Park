package gamestage;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import objects.Door;
import objects.Key;
import player.Player;

public class GameTimer extends AnimationTimer{
	private Scene scene;
	private GraphicsContext gc;

	private Player player;
	private Key key;
	private Door door;

	GameTimer(Scene scene, GraphicsContext gc) {
	    this.gc = gc;
	    this.scene = scene;

	    this.player = new Player("player", this.scene);
	    this.key = new Key();
	    this.door = new Door();
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
	}

	//For rendering sprites
	private void renderSprites(){
		this.door.render(gc, this.door.getXPos(), this.door.getYPos());

		if (this.key.isVisible()){
			this.key.render(gc, this.key.getXPos(), this.key.getYPos());
		}

		this.player.render(gc, this.player.getXPos(), this.player.getYPos());
	}

	//For sprite movement
	private void moveSprites(){
		this.key.move();
		this.player.movePlayer();
	}

}

