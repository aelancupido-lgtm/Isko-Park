package gamestage;

import java.util.ArrayList;
import java.util.HashMap;

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
import obstacle.Portal;
import obstacle.Spike;
import player.Player;
import tile.TileManager;

// TODO: fix static variables

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
	private Group root;
	private HashMap<String, Player> players;

	private static ArrayList<Box> boxes = new ArrayList<>();
	private ArrayList<Portal> portals = new ArrayList<>();
	private ArrayList<Button> buttons = new ArrayList<>();
	private ArrayList<Spike> spikesList = new ArrayList<>();
	

	GameTimer(Scene scene, GraphicsContext gc, GameStage gameStage, Group root) {
	    this.gc = gc;
	    GameTimer.scene = scene;
	    this.root = root;
		this.gameStage = gameStage;
	    this.gameOver = new GameOver(scene, gc);
	    this.key = new Key();
	    this.door = new Door();

	    GameTimer.tm = new TileManager();
	    GameTimer.tm.loadMap(Config.INITIAL_MAPS[GameStage.getLevel()]);
		this.initObstacles();
		this.player = new Player(GameStage.getIdentifier());
		

		if (!GameStage.isSinglePlayer()) {	
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

		GameStage.client.getClientThread().startGame(this, messageArea);

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
					String message = "[" + GameStage.getIdentifier() + "] " + inputBox.getText();
					messageArea.setText(messageArea.getText() + message + "\n");
					inputBox.setText("");
					GameStage.client.sendMessage(message);
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

		this.checkObstacleCollision();

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

	private void checkObstacleCollision(){
		for (Spike spike: this.spikesList){
			spike.checkCollision(this.player);
		}

		for (Button btn: this.buttons){
			btn.checkCollision(this.player);
		}

		for (Portal portal: this.portals){
			portal.checkCollision(this.player);
		}

		for (Box box: GameTimer.boxes){
			for (Button btn: this.buttons){
				box.checkCollision(btn);
			}
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

		this.renderObstacles();

		if (this.key.isVisible()){
			this.key.render(this.gc, this.key.getXPos(), this.key.getYPos());
		}

		if(this.player.isVisible()) {
			this.player.render(this.gc, this.player.getXPos(), this.player.getYPos());
		}

		//Render other players
		if (!GameStage.isSinglePlayer()){
			this.players.forEach((key, user) -> {
				if (user.isVisible()) user.render(gc, user.getXPos(), user.getYPos());
			});
		}
		
	}

	private void initObstacles(){
		this.portals.clear();
		GameTimer.boxes.clear();
		this.buttons.clear();
		this.spikesList.clear();

		Button.resetButtonCount();
		Spike.resetSpikeCount();
		Box.resetBoxCount();
		Portal.resetPortalCount();

		int portalNumber = Config.obstacleCount("PORTAL");
		int boxNumber = Config.obstacleCount("BOX");
		int spikeNumber = Config.obstacleCount("SPIKE");
		int buttonNumber = Config.obstacleCount("BUTTON");

		for (int i = 0; i < portalNumber; i++){
			this.portals.add(new Portal());
		}

		this.setUpPortalWarps();

		for (int i = 0; i < boxNumber; i++){
			GameTimer.boxes.add(new Box());
		}

		for (int i =0 ; i < spikeNumber; i++){
			this.spikesList.add(new Spike());
		}

		for (int i = 0; i <buttonNumber; i++){
			this.buttons.add(new Button());
		}
	}

	private void setUpPortalWarps(){
		for (int i = 0; i< this.portals.size(); i++){
			this.portals.get(i).setWarpPortal(this.portals.get(Config.getWarpPortalIndex(i)));
		}
	}

	private void renderObstacles(){
		for (Spike spike: this.spikesList){
			spike.render(gc, spike.getXPos(), spike.getYPos());
		}

		for (Button btn: this.buttons){
			if (btn.isVisible()) btn.render(gc, btn.getXPos(), btn.getYPos());
		}

		for (Portal portal: this.portals){
			portal.render(gc, portal.getXPos(), portal.getYPos());
		}

		for (Box box: GameTimer.boxes){
			box.render(gc, box.getXPos(), box.getYPos());
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

	public static ArrayList<Box> getBoxes(){
		return GameTimer.boxes;
	}

	public ArrayList<Button> getButtons(){
		return this.buttons;
	}

}

