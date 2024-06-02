package gamestage;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.util.Duration;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import chat.ChatClientUDP;
import chat.ChatServerUDP;

enum MenuType {
	MODE,
	PLAYER,
}

public class GameStage {
	private Group root;
	private Scene gameScene;
	private Scene splashScene;
	private Scene modeScene;
	private Scene playerScene;
	private Scene stageScene;
	private Scene waitingScene;
	private Scene gameOverScene;
	private Stage stage;
	private Canvas canvas;
	private GameTimer gameTimer;

	private TextInputDialog playerUsernameInputDialog;
	private TextInputDialog serverAddressInputDialog;
	private static String IDENTIFIER;
	private static int STAGE_LEVEL = 1;

	// if one member of team fails to exit door in time
	private static boolean TEAM_FAILED = false;

	private static ChatClientUDP CLIENT;
	
	// for lobby
	private static int NUM_USERS_WAITING; //number of users waiting
	private static ArrayList<String> PLAYERS;
	
	private static String SERVER_ADDRESS;

	// private static ChatServerUDP SERVER;
	private static boolean GAME_CREATED;
	private static boolean SINGLE_PLAYER_GAME;
	private static boolean CREATE_GAME_MODE;
	public static boolean teamFail;

	public Font font_pico;
	public Font font_minecraft;

	private MediaPlayer mediaPlayer;

	public GameStage(){
		URL urlPico = getClass().getResource("/assets/font/pico.TTF");
		this.font_pico = Font.loadFont(urlPico.toString(), 12);

		URL urlMC = getClass().getResource("/assets/font/minecraft.ttf");
		this.font_minecraft = Font.loadFont(urlMC.toString(), 12);

		GameStage.STAGE_LEVEL = 1;
		GameStage.GAME_CREATED = false;

		this.initBackground();
		this.initSplash();
		this.initMode();
		this.initPlayer();

		this.playerUsernameInputDialog = new TextInputDialog("Isko 12");
		this.playerUsernameInputDialog.setHeaderText("Enter your username");
	}

	public void setStage(Stage stage){
		this.stage = stage;
		this.prepareActionHandlers(this.splashScene);
		stage.setScene(this.splashScene);
		stage.setResizable(false);
		stage.show();
	}

	private void prepareActionHandlers(Scene scene) {
	    scene.setOnKeyPressed(new EventHandler<KeyEvent>(){
	    	public void handle(KeyEvent e){
	    		KeyCode code = e.getCode();
	    		if(code.equals(KeyCode.ENTER)) {
					playerUsernameInputDialog.showAndWait();
					if (!(playerUsernameInputDialog.getEditor().getText().equals(""))){
						IDENTIFIER = playerUsernameInputDialog.getEditor().getText();
	    			} else {
	    				IDENTIFIER = "temp";
	    			}
					
					showModeScene();
				}
	    	}
	    });
	}

	private String getIPaddress(){
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface iface = interfaces.nextElement();
				if (
					iface.isLoopback() || 
				!iface.isUp() || 
				iface.isVirtual() || 
				iface.isPointToPoint())
					continue;

				Enumeration<InetAddress> addresses = iface.getInetAddresses();
				while(addresses.hasMoreElements()) {
					InetAddress addr = addresses.nextElement();

					final String ip = addr.getHostAddress();
					if(Inet4Address.class == addr.getClass()) {
						System.out.println(ip);
						return ip;
					};
				}
			}
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	private void initServer() throws UnknownHostException{
		GameStage.SERVER_ADDRESS = this.getIPaddress();

		ChatServerUDP server = new ChatServerUDP();
		Task<ChatServerUDP> task = new Task<ChatServerUDP>(){
			protected ChatServerUDP call() throws Exception {
				server.run();
				return server;
			}
		};

		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();

		System.out.println("Server ip address: " + GameStage.SERVER_ADDRESS);
	}

	private void initClient(){
		GameStage.PLAYERS = new ArrayList<>();
		GameStage.CLIENT = new ChatClientUDP();
	    try {
			GameStage.CLIENT.createClientThread(GameStage.getIdentifier());
		} catch (IOException e) {
			e.printStackTrace();
		}
	} 
	
	// Create a scene for the splash screen.
	private void initSplash() {
		StackPane root = new StackPane();

        root.getChildren().addAll(this.playMedia(), this.playImage("enter", ".gif"));
        this.splashScene = new Scene(root, Config.WINDOW_SIZE, Config.WINDOW_SIZE);
	}

	// Create a scene for the selection of mode.
	private void initMode() {
		StackPane root = new StackPane();

		root.getChildren().addAll(this.playMedia(), this.createWrapper(this.createMainButton("create a game", "join a game", MenuType.MODE), this.createBackButton(this.splashScene)));
		this.modeScene = new Scene(root, Config.WINDOW_SIZE, Config.WINDOW_SIZE);
	}

	// Create a scene for the selection of player.
	private void initPlayer() {
		StackPane root = new StackPane();
		
		root.getChildren().addAll(this.playMedia(), this.createWrapper(this.createMainButton("single player", "multiplayer", MenuType.PLAYER), this.createBackButton(this.modeScene)));
		this.playerScene = new Scene(root, Config.WINDOW_SIZE, Config.WINDOW_SIZE);
	}

	// Create a scene for the stage level.
	void initStage() {
		StackPane root = new StackPane();
		
		root.getChildren().add(this.playImage("stage_" + GameStage.STAGE_LEVEL, ".gif"));
		this.stageScene = new Scene(root, Config.WINDOW_SIZE, Config.WINDOW_SIZE);
	}

	void initWaiting() {
		StackPane root = new StackPane();
		
		root.getChildren().addAll(this.playMedia(), this.playImage("waiting", ".gif"));
		this.waitingScene = new Scene(root, Config.WINDOW_SIZE, Config.WINDOW_SIZE);
	}

	private void initGameOver(String result) {
		StackPane root = new StackPane();
		
		root.getChildren().addAll(this.playImage("gameover-" + result, ".gif"), this.playImage("gameover", ".gif"));
		this.gameOverScene = new Scene(root, Config.WINDOW_SIZE, Config.WINDOW_SIZE);
	}

	void initGame() {
		this.root = new Group();

		//Adds space for chat if multiplayer game
		if (!GameStage.SINGLE_PLAYER_GAME){
			this.canvas = new Canvas(Config.SCREEN_SIZE, Config.WINDOW_SIZE);
		}  
		
		if (GameStage.SINGLE_PLAYER_GAME){
			this.canvas = new Canvas(Config.WINDOW_SIZE, Config.WINDOW_SIZE);
		}

		this.root.getChildren().add(this.canvas);
		this.gameScene = new Scene(root);
	}

	private void initBackground() {
		// getting the video url file.
		URL url = getClass().getResource("/assets/background.mp4");
		
        // instantiating MediaPlayer class   
		this.mediaPlayer = new MediaPlayer(new Media(url.toString()));  
		this.mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // loop the video.
		this.mediaPlayer.setAutoPlay(true); // auto play the video.
		this.mediaPlayer.setOnError(() -> System.out.println("Media error occurred: " + mediaPlayer.getError()));
	}

	private MediaView playMedia() {
		// instantiating MediaView class   
		MediaView mediaView = new MediaView(this.mediaPlayer); 
		mediaView.setFitHeight(Config.WINDOW_SIZE); // set the height of the video to the height of the window.
		mediaView.setFitWidth(Config.WINDOW_SIZE); // set the width of the video to the width of the window.
		mediaView.setPreserveRatio(false); // do not preserve the ratio of the video.

		return mediaView;
	}

	private ImageView playImage(String name, String type) {
		// getting the image url file.
		URL url = getClass().getResource("/assets/" + name + type);
		// instantiating ImageView class
		ImageView imageView = new ImageView(new Image(url.toString()));

		imageView.setFitHeight(Config.WINDOW_SIZE); // set the height of the image to the height of the window.
		imageView.setFitWidth(Config.WINDOW_SIZE); // set the width of the image to the width of the window.
		imageView.setPreserveRatio(false); // do not preserve the ratio of the image.

		return imageView;
	}

	void waitAllPlayers(Boolean isServer) {
		Parent root = this.stage.getScene().getRoot();
		if(root instanceof StackPane) {
			StackPane stackPane = (StackPane) root;
			Label node = createLabel("(0/" + Config.MAX_PLAYERS + ")", this.font_pico, 50, 800);
			stackPane.getChildren().addAll(node);

			if (isServer){
				Label code = createLabel("Game Code: " + GameStage.SERVER_ADDRESS, this.font_minecraft, 20, 300);
				stackPane.getChildren().addAll(code);
			}

			// Run the loop on a separate thread.
			AtomicBoolean running = new AtomicBoolean(true);
			new Thread(() -> {
				while(running.get()) {
					// Use Platform.runLater to update the UI from another thread.
					Platform.runLater(() -> {
						node.setText("(" + String.valueOf(GameStage.NUM_USERS_WAITING) + "/" + Config.MAX_PLAYERS + ")");

						if(GameStage.NUM_USERS_WAITING == Config.MAX_PLAYERS) {
							// Stop the while loop.
							running.set(false);

							// Add a delay before changing the scene.
							PauseTransition pause = new PauseTransition(Duration.seconds(2.0)); // Create a pause of 2 seconds.
							pause.setOnFinished(event -> {
								// Start the game here.
								initGame();
								initStage();

								// Change the scene after the pause.
								showStageScene();
								PauseTransition pauseGameScene = new PauseTransition(Duration.seconds(2.0)); // Create a pause of 2 seconds.
								pauseGameScene.setOnFinished(e -> setGameScene()); // Set the gameScene after the pause.
								pauseGameScene.play();
							});
							pause.play();
						} else if (TEAM_FAILED){
							running.set(false);
							setGameOver("lose");
						}
					});

					// Add 1 sec delay.
					if(running.get()) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
		}
	}

	private VBox createWrapper(VBox mainButton, HBox backButton) {
		VBox vBox = new VBox();

		mainButton.setPadding(new Insets(250, 0, 0, 0));
		backButton.setPadding(new Insets(30, 30, 30, 30));
		
		vBox.getChildren().addAll(backButton, mainButton);

		return vBox;
	}

	private void designButton(Button b, double w, double h, double s) {
		// Set the font of the buttons.
		b.setFont(Font.font(this.font_pico.getName(), s));

		// Set the size of the buttons.
		b.setMinSize(w, h); 
		b.setMaxSize(w, h); 
		b.setPrefSize(w, h);

		// Set the color of the buttons.
		b.setStyle("-fx-text-fill: #FF874E; -fx-background-color: transparent;");

		// Add a hover animation.
		b.setOnMouseEntered(e -> b.setStyle("-fx-text-fill: #FF874E; -fx-background-color: transparent; -fx-scale-x: 1.1; -fx-scale-y: 1.1;"));
		b.setOnMouseExited(e -> b.setStyle("-fx-text-fill: #FF874E; -fx-background-color: transparent; -fx-scale-x: 1; -fx-scale-y: 1;"));
	}

	private VBox createMainButton(String text1, String text2, MenuType type) {
    	VBox vBox = new VBox();
    	vBox.setAlignment(Pos.CENTER);

        Button b1 = new Button(text1);
        Button b2 = new Button(text2);
		
		designButton(b1, 500, 100, 60);
		designButton(b2, 500, 100, 60);
		vBox.getChildren().addAll(b1, b2);

        b1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                System.out.println(text1);; // should call the init server and flash the ip address and also allow the user to select number of players.


				if(type == MenuType.MODE) {	// Create a game.
					GameStage.CREATE_GAME_MODE = true;
					showPlayerScene();
				}
				if(type == MenuType.PLAYER) { // Single player game.
					GameStage.SINGLE_PLAYER_GAME = true;
					initGame();
					initStage();
					showStageScene();

					PauseTransition pause = new PauseTransition(Duration.seconds(2.0)); // Create a pause of 2 seconds.
					pause.setOnFinished(event -> setGameScene()); // Set the gameScene after the pause.
					pause.play(); // Start the pause.
				}
            }
        });

        b2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                System.out.println(text2);; // should allow the client input the server ip address.

				if(type == MenuType.MODE) { // Join a game.
					GameStage.CREATE_GAME_MODE = false;
					GameStage.SINGLE_PLAYER_GAME = false;
					serverAddressInputDialog = new TextInputDialog();
					serverAddressInputDialog.setHeaderText("Enter the server address");
					serverAddressInputDialog.showAndWait();

					SERVER_ADDRESS = serverAddressInputDialog.getEditor().getText();
					initClient(); // initializes the client thread
					initWaiting();
					showWaitingScene();
					waitAllPlayers(false);

				}

				if (type == MenuType.PLAYER){ // Multiplayer game.
					GameStage.SINGLE_PLAYER_GAME = false;
					if (GameStage.CREATE_GAME_MODE){
						try {
							initServer();
						} catch (UnknownHostException e1) {
							e1.printStackTrace();
						}
					}
					initClient(); // initializes the client thread
					initWaiting();
					showWaitingScene();
					waitAllPlayers(true);
				}
            }
        });

        return vBox;
    }

	private HBox createBackButton(Scene scene) {
    	HBox hbox = new HBox();
    	hbox.setAlignment(Pos.TOP_LEFT);
		
        Button b1 = new Button("back");

		designButton(b1, 80, 40, 20);
        hbox.getChildren().addAll(b1);

        b1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                stage.setScene(scene);
            }
        });

        return hbox;
	}

	private Label createLabel(String text, Font font, double size, double padding) {
		Label label = new Label(text);
		label.setFont(Font.font(font.getName(), size));
		label.setTextFill(Color.web("#FF874E"));

		// Set the size of the label.
        label.setPrefSize(Config.WINDOW_SIZE, Config.WINDOW_SIZE);
		label.setAlignment(Pos.CENTER);
		
		// Set the padding of the label.
		label.setPadding(new Insets(padding, 0, 0, 0)); // top, right, bottom, left

		return label;
	}

	void setGameScene() {
		GameStage.GAME_CREATED = true;
        this.stage.setScene(this.gameScene);
        GraphicsContext gc = this.canvas.getGraphicsContext2D();

        this.gameTimer = new GameTimer(this.gameScene, gc, this, root);	// create a gametimer class.

        gameTimer.start(); // invoke the start method of the animation timer.
	}

	private void setGameOverScene(){
        this.stage.setScene(this.gameOverScene);
    }

	private void showModeScene() {
		this.stage.setScene(this.modeScene);
	}

	private void showPlayerScene() {
		this.stage.setScene(this.playerScene);
	}

	void showStageScene() {
		this.stage.setScene(this.stageScene);
	}

	void showWaitingScene() {
		this.stage.setScene(this.waitingScene);
	}

	// for getting int value for array
	static int getLevel(){
		return GameStage.STAGE_LEVEL - 1;
	}

	static int getStageLevel(){
		return GameStage.STAGE_LEVEL;
	}

	public static String getIdentifier(){
		return GameStage.IDENTIFIER;
	}

	public static String getServerAddress(){
		return GameStage.SERVER_ADDRESS;
	}

	public static boolean isSinglePlayer(){
		return GameStage.SINGLE_PLAYER_GAME;
	}

	public static boolean isCreateGame(){
		return GameStage.CREATE_GAME_MODE;
	}

	// used for setting number of users waiting
	public static void setNumWaiting(int usersWaiting){
		GameStage.NUM_USERS_WAITING = usersWaiting;
	}

	public static void addPlayers(String playerName){
		GameStage.PLAYERS.add(playerName);
	}

	static ArrayList<String> getPlayers(){
		return GameStage.PLAYERS;
	}

	static void nextLevel(){
		GameStage.STAGE_LEVEL++;
	}

	public static boolean isCreated(){
		return GameStage.GAME_CREATED;
	}

	public static ChatClientUDP getClient(){
		return GameStage.CLIENT;
	}

	void setGameOver(String result) {
		this.initGameOver(result);
		this.setGameOverScene();
	}

	public static void teamFailed(){
		GameStage.TEAM_FAILED = true;
	}

	public static boolean checkTeamFail(){
		return GameStage.TEAM_FAILED;
	}
}
