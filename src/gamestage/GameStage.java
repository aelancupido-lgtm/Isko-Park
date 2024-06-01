package gamestage;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.Enumeration;

import chat.ChatServerUDP;
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
	private Stage stage;
	private Canvas canvas;
	private GameTimer gameTimer;

	private TextInputDialog playerUsernameInputDialog;
	private TextInputDialog serverAddressInputDialog;
	private static String IDENTIFIER;
	private static int STAGE_LEVEL = 1;

	private static String SERVER_ADDRESS;

	private static ChatServerUDP server;
	private static boolean GAME_CREATED;
	private static boolean SINGLE_PLAYER_GAME;
	private static boolean CREATE_GAME_MODE;

	public static Font font;

	private MediaPlayer mediaPlayer;

	public GameStage(){
		String url = System.getProperty("user.dir") + "/src/assets/font/PICOBL__.TTF";
    	GameStage.font = Font.loadFont(Paths.get(url).toUri().toString(), 12);
		
		GameStage.STAGE_LEVEL = 1;

		this.initBackground();
		this.initSplash();
		this.initMode();
		this.initPlayer();
		// this.initStage();
		// this.initWaiting();
		GameStage.GAME_CREATED = false;
		
		this.playerUsernameInputDialog = new TextInputDialog("Isko 12");
		this.playerUsernameInputDialog.setHeaderText("Enter your username");
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
	private void initStage() {
		StackPane root = new StackPane();
		
		root.getChildren().add(this.playImage("stage_" + GameStage.STAGE_LEVEL, ".gif"));
		this.stageScene = new Scene(root, Config.WINDOW_SIZE, Config.WINDOW_SIZE);
	}

	private void initWaiting() {
		StackPane root = new StackPane();
		
		root.getChildren().add(this.playImage("waiting", ".gif"));
		this.waitingScene = new Scene(root, Config.WINDOW_SIZE, Config.WINDOW_SIZE);
	}

	private void initGame() {
		this.root = new Group();

		//Adds space for chat if multiplayer game
		if (!GameStage.SINGLE_PLAYER_GAME){
			this.canvas = new Canvas(Config.SCREEN_SIZE, Config.WINDOW_SIZE);
		}  
		
		if (GameStage.SINGLE_PLAYER_GAME){
			this.canvas = new Canvas(Config.WINDOW_SIZE, Config.WINDOW_SIZE);
		}
		GameStage.GAME_CREATED = true;

		this.root.getChildren().add(this.canvas);
		this.gameScene = new Scene(root);
	}

	private void initBackground() {
		// getting the video url file.
		String url = System.getProperty("user.dir") + "\\src\\assets\\background.mp4";

        // instantiating MediaPlayer class   
		this.mediaPlayer = new MediaPlayer(new Media(Paths.get(url).toUri().toString()));  
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
		String url = System.getProperty("user.dir") + "\\src\\assets\\" + name + type;

		// instantiating ImageView class
		ImageView imageView = new ImageView(new Image(Paths.get(url).toUri().toString()));

		imageView.setFitHeight(Config.WINDOW_SIZE); // set the height of the image to the height of the window.
		imageView.setFitWidth(Config.WINDOW_SIZE); // set the width of the image to the width of the window.
		imageView.setPreserveRatio(false); // do not preserve the ratio of the image.

		return imageView;
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


		server = new ChatServerUDP();
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

	private VBox createWrapper(VBox mainButton, HBox backButton) {
		VBox vBox = new VBox();

		mainButton.setPadding(new Insets(250, 0, 0, 0));
		backButton.setPadding(new Insets(30, 30, 30, 30));
		
		vBox.getChildren().addAll(backButton, mainButton);

		return vBox;
	}

	private VBox createMainButton(String text1, String text2, MenuType type) {
    	VBox vBox = new VBox();
    	vBox.setAlignment(Pos.CENTER);
    	// vBox.setPadding(new Insets(150, 0, 0, 0));

        Button b1 = new Button(text1);
        Button b2 = new Button(text2);

		// Set the font of the buttons.
		b1.setFont(Font.font(GameStage.font.getName(), 60));
		b2.setFont(Font.font(GameStage.font.getName(), 60)); 

		// Set the size of the buttons.
		b1.setMinSize(500, 100); 
		b1.setMaxSize(500, 100); 
		b2.setMinSize(500, 100);
		b2.setMaxSize(500, 100);

		b1.setPrefSize(500, 100);
		b2.setPrefSize(500, 100);

		// Set the color of the buttons.
		b1.setStyle("-fx-text-fill: #FF874E; -fx-background-color: transparent;");
		b2.setStyle("-fx-text-fill: #FF874E; -fx-background-color: transparent;");

		 // Add a hover animation.
		 b1.setOnMouseEntered(e -> b1.setStyle("-fx-text-fill: #FF874E; -fx-background-color: transparent; -fx-scale-x: 1.1; -fx-scale-y: 1.1;"));
		 b1.setOnMouseExited(e -> b1.setStyle("-fx-text-fill: #FF874E; -fx-background-color: transparent; -fx-scale-x: 1; -fx-scale-y: 1;"));
		 b2.setOnMouseEntered(e -> b2.setStyle("-fx-text-fill: #FF874E; -fx-background-color: transparent; -fx-scale-x: 1.1; -fx-scale-y: 1.1;"));
		 b2.setOnMouseExited(e -> b2.setStyle("-fx-text-fill: #FF874E; -fx-background-color: transparent; -fx-scale-x: 1; -fx-scale-y: 1;"));

		vBox.getChildren().addAll(b1, b2);

        b1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                System.out.println(text1);; // should call the init server and flash the ip address and also allow the user to select number of players.


				if(type == MenuType.MODE) {
					GameStage.CREATE_GAME_MODE = true;
					showPlayerScene();
				}
				if(type == MenuType.PLAYER) {
					GameStage.SINGLE_PLAYER_GAME = true;
					initGame();
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

				if(type == MenuType.MODE) {
					GameStage.CREATE_GAME_MODE = false;

					serverAddressInputDialog = new TextInputDialog();
					serverAddressInputDialog.setHeaderText("Enter the server address");
					serverAddressInputDialog.showAndWait();

					SERVER_ADDRESS = serverAddressInputDialog.getEditor().getText();

					initGame();
					showStageScene();

					PauseTransition pause = new PauseTransition(Duration.seconds(2.0)); // Create a pause of 2 seconds.
					pause.setOnFinished(event -> setGameScene()); // Set the gameScene after the pause.
					pause.play();
				}

				if (type == MenuType.PLAYER){
					GameStage.SINGLE_PLAYER_GAME = false;
					initGame();
					if (GameStage.CREATE_GAME_MODE){
						try {
							initServer();
						} catch (UnknownHostException e1) {
							e1.printStackTrace();
						}
					}
					initWaiting();
					showWaitingScene();

					Parent root = stage.getScene().getRoot();
					if(root instanceof StackPane) {
						StackPane stackPane = (StackPane) root;
						Label node = createLabel("0");
						stackPane.getChildren().add(node);

						// Run the loop on a separate thread.
						new Thread(() -> {
							while(true) {
								// Use Platform.runLater to update the UI from another thread.
								Platform.runLater(() -> {
									int numberOfPlayers = GameStage.server.getNumberOfPlayers();
									System.out.println(numberOfPlayers);

									node.setText(String.valueOf(numberOfPlayers));

									if(numberOfPlayers == Config.MAX_PLAYERS) {
										// Start the game here.
									}
								});

								// Use PauseTransition to introduce a delay.
								PauseTransition pause = new PauseTransition(Duration.seconds(1));
								pause.play();

								// Wait for the pause to finish.
								try {
									Thread.sleep(1000);
								} catch (InterruptedException ev) {
									ev.printStackTrace();
								}
							}
						}).start();
					}

					// temp 1 sec delay
					// PauseTransition pause1 = new PauseTransition(Duration.seconds(1.0)); // Create a pause of 4 seconds.
					// pause1.setOnFinished(event1 -> {
					// 	initStage();
					// 	showStageScene();

					// 	PauseTransition pause2 = new PauseTransition(Duration.seconds(10.0)); // Create a pause of 2 seconds.
					// 	pause2.setOnFinished(event2 -> setGameScene()); // Set the gameScene after the pause.
					// 	pause2.play();
					// }); 
					
					// Set the gameScene after the pause.

					// pause1.play(); // Start the pause.
					
					// showStageScene(stage);
					 // Start the pause.
				}
            }
        });

        return vBox;
    }

	private HBox createBackButton(Scene scene) {
    	HBox hbox = new HBox();
    	hbox.setAlignment(Pos.TOP_LEFT);
		
        Button b1 = new Button("back");

		// Set the font color to orange.
		b1.setFont(Font.font(GameStage.font.getName(), 20));

		// Set the font of the buttons.
		b1.setMinSize(80, 40); 
		b1.setMaxSize(80, 40); 

        b1.setPrefSize(80, 40);
		
		// Set the color of the buttons.
		b1.setStyle("-fx-text-fill: #FF874E; -fx-background-color: transparent;");

		// Add a hover animation.
		b1.setOnMouseEntered(e -> b1.setStyle("-fx-text-fill: #FF874E; -fx-background-color: transparent; -fx-scale-x: 1.1; -fx-scale-y: 1.1;"));
		b1.setOnMouseExited(e -> b1.setStyle("-fx-text-fill: #FF874E; -fx-background-color: transparent; -fx-scale-x: 1; -fx-scale-y: 1;"));
        hbox.getChildren().addAll(b1);

        b1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                stage.setScene(scene);
            }
        });

        return hbox;
	}

	private Label createLabel(String text) {
		Label label = new Label(String.valueOf(text));
		label.setFont(Font.font(GameStage.font.getName(), 20));
		label.setTextFill(Color.web("#FF874E"));
		return label;
	}

	private void setGameScene() {
        this.stage.setScene(this.gameScene);
        GraphicsContext gc = this.canvas.getGraphicsContext2D();

        this.gameTimer = new GameTimer(this.gameScene, gc, this, root);	// create a gametimer class.

        gameTimer.start(); // invoke the start method of the animation timer.
	}

	public void setGameOverScene(Scene gameOverScene){
        this.stage.setScene(gameOverScene);
    }

	private void showModeScene() {
		this.stage.setScene(this.modeScene);
	}

	private void showPlayerScene() {
		this.stage.setScene(this.playerScene);
	}

	private void showStageScene() {
		this.stage.setScene(this.stageScene);
	}

	private void showWaitingScene() {
		this.stage.setScene(this.waitingScene);
	}

	// for getting int value for array
	public static int getLevel(){
		return GameStage.STAGE_LEVEL - 1;
	}

	public static int getStageLevel(){
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

	public static void nextLevel(){
		if (GameStage.STAGE_LEVEL < Config.MAX_LEVEL){
			GameStage.STAGE_LEVEL++;
		}
	}

	public static boolean isCreated(){
		return GameStage.GAME_CREATED;
	}
}
