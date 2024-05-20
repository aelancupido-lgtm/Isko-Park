package gamestage;

import java.io.IOException;

import chat.ChatClientUDP;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;
import objects.Door;
import objects.Key;
import obstacle.Button;

import player.Player;
import tile.TileManager;

public class GameTimer extends AnimationTimer{
	private Scene scene;
	private GraphicsContext gc;

	private Player player;
	private Key key;
	private Door door;
	private Button button;
	private TileManager tm;
	private GameOver gameOver;
	private GameStage gameStage;

	private TextArea messageArea;
	private TextField inputBox;
	private ChatClientUDP client;
	private Group root;
	private String identifier;

	GameTimer(Scene scene, GraphicsContext gc, GameStage gameStage, Group root, String identifier) {
	    this.gc = gc;
	    this.scene = scene;
	    this.root = root;
		this.gameStage = gameStage;
	    this.gameOver = new GameOver(scene, gc);
	    this.identifier = identifier;
	    this.key = new Key();
	    this.door = new Door();
	    this.button = new Button();
	    this.tm = new TileManager();
	    this.tm.loadMap("matrix_1");
		this.player = new Player(identifier, this.scene, this.tm);

		if (!GameStage.SINGLE_PLAYER_GAME) this.initChat();
	}


	@Override
	public void handle(long arg0) {
		this.gc.clearRect(0, 0, Config.WINDOW_SIZE, Config.WINDOW_SIZE);

		this.checkStatus();
		this.renderSprites();
		this.moveSprites();
	}

	private void initChat(){
		this.messageArea = new TextArea();
		this.inputBox = new TextField();

	    this.client = new ChatClientUDP();
	    try {
			this.client.createClientThread(this.messageArea, identifier);
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.messageArea.setMaxWidth(Config.CHAT_SIZE);
		this.messageArea.setLayoutX(Config.WINDOW_SIZE);
		this.messageArea.setMinHeight(Config.WINDOW_SIZE - 2.5);
		this.messageArea.setEditable(false);
		this.messageArea.setWrapText(true);
		this.messageArea.setMouseTransparent(true);
		this.messageArea.setFocusTraversable(false);

		this.inputBox.setMaxWidth(Config.CHAT_SIZE);
		this.inputBox.setPromptText("Enter your message");
		this.inputBox.setLayoutX(Config.WINDOW_SIZE + 7.5);
		this.inputBox.setMinHeight(Config.CHAT_INPUT_SIZE);
		this.inputBox.setLayoutY(Config.WINDOW_SIZE - Config.CHAT_INPUT_SIZE);

		this.inputBox.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER){
				String message = "[" + this.identifier + "] " + inputBox.getText();
				messageArea.setText(messageArea.getText() + message + "\n");
				inputBox.setText("");
				client.sendMessage(message);
			} else if (e.getCode() == KeyCode.ESCAPE){
				inputBox.setFocusTraversable(false);
				root.requestFocus();
			}
		});

		this.inputBox.setFocusTraversable(false);
		this.root.getChildren().addAll(messageArea, inputBox);
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

		this.tm.draw(gc);

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

