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
import player.Player;

public class GameTimer extends AnimationTimer{
	private Scene scene;
	private GraphicsContext gc;

	private Player player;

	GameTimer(Scene scene, GraphicsContext gc) {
	    this.gc = gc;
	    this.scene = scene;


	    this.player = new Player("player", this.scene);
	}


	@Override
	public void handle(long arg0) {
		// TODO Auto-generated method stub
		this.gc.clearRect(0, 0, GameStage.WINDOW_WIDTH, GameStage.WINDOW_WIDTH);
		this.player.render(gc, this.player.getXPos(), this.player.getYPos());
		this.player.movePlayer();
	}

}

