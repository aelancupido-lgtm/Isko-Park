package gamestage;

import java.io.IOException;
import java.util.HashMap;

import chat.ChatClientUDP;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;
import objects.Door;
import objects.Key;
import obstacle.Box;
import obstacle.Button;
import obstacle.Spike;
import player.Player;
import tile.TileManager;

public class GameTimer extends AnimationTimer{
	private static Scene scene;
	private GraphicsContext gc;

	private Player player;
	private Key key;
	private Door door;
	private Button button;
	private static TileManager tm;
	private GameOver gameOver;
	private GameStage gameStage;

	private TextArea messageArea;
	private TextField inputBox;
	public static ChatClientUDP client;
	private Group root;
	private static String identifier;
	private HashMap<String, Player> players;
	private Spike spikes;
	private Box box;

	GameTimer(Scene scene, GraphicsContext gc, GameStage gameStage, Group root, String identifier) {
	    this.gc = gc;
	    GameTimer.scene = scene;
	    this.root = root;
		this.gameStage = gameStage;
	    this.gameOver = new GameOver(scene, gc);
	    GameTimer.identifier = identifier;
	    this.key = new Key();
	    this.door = new Door();
	    this.button = new Button();
		this.spikes = new Spike();
		this.box = new Box();
	    GameTimer.tm = new TileManager();
	    GameTimer.tm.loadMap(Config.INITIAL_MAPS[GameStage.getLevel()]);
		this.player = new Player(identifier);

		if (!GameStage.SINGLE_PLAYER_GAME) {	
			this.players = new HashMap<>();
			this.initChat();
		}
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
	    GameTimer.client = new ChatClientUDP();
	    try {
			GameTimer.client.createClientThread(this.messageArea, identifier, this);
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

		this.messageArea.textProperty().addListener(new ChangeListener<Object>() {

			@Override
			public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
				messageArea.setScrollTop(Double.MAX_VALUE);
			}
			
		});


		this.inputBox.setMaxWidth(Config.CHAT_SIZE);
		this.inputBox.setPromptText("Enter your message");
		this.inputBox.setLayoutX(Config.WINDOW_SIZE + 7.5);
		this.inputBox.setMinHeight(Config.CHAT_INPUT_SIZE);
		this.inputBox.setLayoutY(Config.WINDOW_SIZE - Config.CHAT_INPUT_SIZE);

		this.inputBox.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER){

				if (checkChatRestrictions(inputBox.getText())){
					String message = "[" + GameTimer.identifier + "] " + inputBox.getText();
					messageArea.setText(messageArea.getText() + message + "\n");
					inputBox.setText("");
					client.sendMessage(message);
				}
				
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
		this.spikes.checkCollision(this.player);

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

	//For chat restrictions on keywords
	private boolean checkChatRestrictions(String chatText){
		for (String keyword : Config.RESERVED_KEYWORDS) {
			if (chatText.startsWith(keyword)){
				return false;
			}
		}

		return true;
	}

	//For rendering sprites
	private void renderSprites(){

		GameTimer.tm.draw(gc);

		this.door.render(this.gc, this.door.getXPos(), this.door.getYPos());
		this.spikes.render(gc, this.spikes.getXPos(), this.spikes.getYPos());
		this.box.render(gc, this.box.getXPos(), this.box.getYPos());

		if (this.button.isVisible()){
			this.button.render(this.gc, this.button.getXPos(), this.button.getYPos());
		}

		if (this.key.isVisible()){
			this.key.render(this.gc, this.key.getXPos(), this.key.getYPos());
		}

		if(this.player.isVisible()) {
			this.player.render(this.gc, this.player.getXPos(), this.player.getYPos());
		}

		//Render other players
		if (!GameStage.SINGLE_PLAYER_GAME){
			this.players.forEach((key, user) -> {
				if (user.isVisible()) user.render(gc, user.getXPos(), user.getYPos());
			});
		}
		
	}

	//For sprite movement
	private void moveSprites(){
		if (this.player.isVisible()) {
			this.key.move();
			this.player.move();
		}
	}

	public static TileManager getTileManager(){
		return GameTimer.tm;
	}

	public static Scene getScene(){
		return GameTimer.scene;

	}

	public static String getPlayer(){
		return GameTimer.identifier;
	}

	public Door getDoor(){
		return this.door;
	}

	public Key getKey(){
		return this.key;
	}

	public Button getButton(){
		return this.button;
	}

	public HashMap<String, Player> getPlayers(){
		return this.players;
	}

}

