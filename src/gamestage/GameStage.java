package gamestage;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Paths;

import chat.ChatServerUDP;
import javafx.util.Duration;
import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
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
	private Stage stage;
	private Canvas canvas;
	private GameTimer gameTimer;

	private TextInputDialog playerUsernameInputDialog;
	public static String identifier;
	public static int stageLevel = 1;

	public static String serverAddress;

	public static ChatServerUDP server;

	public static boolean SINGLE_PLAYER_GAME;
	public static boolean CREATE_GAME_MODE;

	public static Font font;

	public GameStage(){
		// URL fontUrl = getClass().getResource("/assets/font/PICOBL__.ttf");
    	// GameStage.font = Font.loadFont(fontUrl.toExternalForm(), 12);

		String url = System.getProperty("user.dir") + "/src/assets/font/PICOBL__.TTF";
    	GameStage.font = Font.loadFont(Paths.get(url).toUri().toString(), 12);
		
		GameStage.stageLevel = 1;

		this.initSplash();
		this.initMode();
		this.initPlayer();
		this.initStage();
		
		this.playerUsernameInputDialog = new TextInputDialog("Isko 12");
		this.playerUsernameInputDialog.setHeaderText("Enter your username");
	}

	// Create a scene for the splash screen.
	private void initSplash() {
		StackPane root = new StackPane();

        root.getChildren().addAll(this.playMedia("splash", ".mp4"));
        this.splashScene = new Scene(root, Config.WINDOW_SIZE, Config.WINDOW_SIZE);
	}

	// Create a scene for the selection of mode.
	private void initMode() {
		StackPane root = new StackPane();

		root.getChildren().addAll(this.playMedia("background", ".mp4"), this.createWrapper(this.createMainButton("create a game", "join a game", MenuType.MODE), this.createBackButton(this.splashScene)));
		this.modeScene = new Scene(root, Config.WINDOW_SIZE, Config.WINDOW_SIZE);
	}

	// Create a scene for the selection of player.
	private void initPlayer() {
		StackPane root = new StackPane();
		
		root.getChildren().addAll(this.playMedia("background", ".mp4"), this.createWrapper(this.createMainButton("single player", "multiplayer", MenuType.PLAYER), this.createBackButton(this.modeScene)));
		this.playerScene = new Scene(root, Config.WINDOW_SIZE, Config.WINDOW_SIZE);
	}

	// Create a scene for the stage level.
	private void initStage() {
		StackPane root = new StackPane();
		
		root.getChildren().add(this.playImage("stage_" + GameStage.stageLevel, ".gif"));
		this.stageScene = new Scene(root, Config.WINDOW_SIZE, Config.WINDOW_SIZE);
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

		this.root.getChildren().add(this.canvas);
		this.gameScene = new Scene(root);
	}

	private void initServer() throws UnknownHostException{
		GameStage.serverAddress = InetAddress.getLocalHost().getHostAddress();
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

		System.out.println("Server ip address: " + InetAddress.getLocalHost().getHostAddress());

	}

	public void setStage(Stage stage){
		this.stage = stage;
		stage.setTitle("Isko Park");
		
		stage.setScene(this.splashScene);
		this.prepareActionHandlers(this.splashScene);
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
						identifier = playerUsernameInputDialog.getEditor().getText();
	    			} else {
	    				identifier = "temp";
	    			}
					
					showModeScene(stage);}
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
					showPlayerScene(stage);
				}
				if(type == MenuType.PLAYER) {
					GameStage.SINGLE_PLAYER_GAME = true;
					initGame();
					showStageScene(stage);

					PauseTransition pause = new PauseTransition(Duration.seconds(2.0)); // Create a pause of 2 seconds.
					pause.setOnFinished(event -> setGameScene(stage)); // Set the gameScene after the pause.
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

					showStageScene(stage);

					PauseTransition pause = new PauseTransition(Duration.seconds(2.0)); // Create a pause of 2 seconds.
					pause.setOnFinished(event -> setGameScene(stage)); // Set the gameScene after the pause.
					pause.play(); // Start the pause.
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

	private MediaView playMedia(String name, String type) {
		// getting the video url file.
		URL url = getClass().getResource("/assets/" + name + type);

		// String url = System.getProperty("user.dir") + "/src/assets/" + name + type;

        // instantiating MediaPlayer class   
		MediaPlayer mediaPlayer = new MediaPlayer(new Media(url.toString()));

		// MediaPlayer mediaPlayer = new MediaPlayer(new Media(Paths.get(url).toUri().toString()));  

		mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // loop the video.
		mediaPlayer.setAutoPlay(true); // auto play the video.

        // instantiating MediaView class   
        MediaView mediaView = new MediaView(mediaPlayer); 
		mediaView.setFitHeight(Config.WINDOW_SIZE); // set the height of the video to the height of the window.
		mediaView.setFitWidth(Config.WINDOW_SIZE); // set the width of the video to the width of the window.
		mediaView.setPreserveRatio(false); // do not preserve the ratio of the video.
		mediaPlayer.setOnError(() -> System.out.println("Media error occurred: " + mediaPlayer.getError()));

		return mediaView;
	}

	private ImageView playImage(String name, String type) {
		// String url = System.getProperty("user.dir") + "/src/assets/" + name + type;
		// ImageView imageView = new ImageView(new Image(Paths.get(url).toUri().toString()));

		// getting the image url file.
		URL url = getClass().getResource("/assets/" + name + type);

		// instantiating ImageView class
		ImageView imageView = new ImageView(new Image(url.toString()));

		imageView.setFitHeight(Config.WINDOW_SIZE); // set the height of the image to the height of the window.
		imageView.setFitWidth(Config.WINDOW_SIZE); // set the width of the image to the width of the window.
		imageView.setPreserveRatio(false); // do not preserve the ratio of the image.

		return imageView;
	}

	private void setGameScene(Stage stage) {
        stage.setScene(this.gameScene);
        GraphicsContext gc = this.canvas.getGraphicsContext2D();

        this.gameTimer = new GameTimer(this.gameScene, gc, this, root, identifier);	// create a gametimer class.

        gameTimer.start(); // invoke the start method of the animation timer.
	}

	public void setGameOverScene(Scene gameOverScene){
        stage.setScene(gameOverScene);
    }

	private void showModeScene(Stage stage) {
		stage.setScene(this.modeScene);
	}

	private void showPlayerScene(Stage stage) {
		stage.setScene(this.playerScene);
	}

	private void showStageScene(Stage stage) {
		stage.setScene(this.stageScene);
	}
}
