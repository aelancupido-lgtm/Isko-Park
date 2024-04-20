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
import javafx.scene.text.Text;
import objects.Door;
import objects.Key;
import obstacle.Button;
import player.Player;

public class GameTimer extends AnimationTimer{
	private Scene scene;
	private GraphicsContext gc;

	private Player player;
	private Key key;
	private Door door;
	private Button button;
	private static final double FONT_SIZE = 15;

	//username position differences
	private static final double USERNAME_HEIGHT = 10;
	private static final double USERNAME_WIDTH = 5;

	GameTimer(Scene scene, GraphicsContext gc) {
	    this.gc = gc;
	    this.scene = scene;

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

		this.drawPlayerUsername();
		this.player.render(this.gc, this.player.getXPos(), this.player.getYPos());
	}

	//For sprite movement
	private void moveSprites(){
		this.key.move();
		this.player.movePlayer();
	}

	//For player username
	private void drawPlayerUsername(){
	    Text text = new Text(this.player.getUsername());
	    text.setFont(Font.font("Verdana", FontWeight.BOLD, FONT_SIZE));

	    double centerXPos = this.player.getXPos() + (Player.PLAYER_WIDTH / 2);		//get the horizontal center point of the player
	    double textWidth = text.getLayoutBounds().getWidth();				//get width of text
	    double textXPos = centerXPos - (textWidth / 2);					//x-coordinate for positioning the text horizontally
	    double textYPos = this.player.getYPos() - USERNAME_HEIGHT;				//y-coordinate for positioning the text vertically

	    this.gc.setFont(Font.font("Verdana", FontWeight.BOLD, FONT_SIZE));
	    this.gc.setFill(Color.BLACK);
	    this.gc.fillText(this.player.getUsername(), textXPos, textYPos);
	}



}

