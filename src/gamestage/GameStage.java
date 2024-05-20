package gamestage;

import java.net.URL;

import chat.ChatServerUDP;
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
	private Stage stage;
	private Canvas canvas;
	private GameTimer gameTimer;

	private TextInputDialog playerUsernameInputDialog;
	public static String identifier;

	public final static int WINDOW_SIZE = 800;
	public final static int TILE_SIZE = 16;
	public final static int MAX_ROW = WINDOW_SIZE/TILE_SIZE;
	public final static int CHAT_SIZE = 200;
	public final static int CHAT_INPUT_SIZE = 35;
	public final static int SCREEN_SIZE = WINDOW_SIZE + CHAT_SIZE;

	public final static Color BACKGROUND_COLOR = Color.WHITE;
	public final static Color TILE_COLOR = Color.ORANGE;

	public final static Image BACKGROUND_IMAGE = new Image("assets/white.png", TILE_SIZE, TILE_SIZE, false, false);
	public final static Image TILE_IMAGE = new Image("assets/orange.png", TILE_SIZE, TILE_SIZE, false, false);

	public final static int SERVER_PORT_NUMBER = 8080;
	public final static String SERVER_ADDRESS = "localhost";

	public static ChatServerUDP server;

	public static Font font;

	public GameStage(){
		URL fontUrl = getClass().getResource("/assets/font/PICOBL__.ttf");
    	GameStage.font = Font.loadFont(fontUrl.toExternalForm(), 12);

		this.initSplash();
		this.initMode();
		this.initPlayer();
		this.initGame();
		
		this.playerUsernameInputDialog = new TextInputDialog("Isko 12");
		this.playerUsernameInputDialog.setHeaderText("Enter your username");
	}

	private void initSplash() {
		StackPane root = new StackPane();

        root.getChildren().addAll(this.playMedia("splash", ".mp4"));
        this.splashScene = new Scene(root, GameStage.WINDOW_SIZE, GameStage.WINDOW_SIZE);
	}

	private void initMode() {
		StackPane root = new StackPane();

		root.getChildren().addAll(this.playMedia("background", ".mp4"), this.createWrapper(this.createMainButton("Create a Game", "Join a Game", MenuType.MODE), this.createBackButton(this.splashScene)));
		this.modeScene = new Scene(root, GameStage.WINDOW_SIZE, GameStage.WINDOW_SIZE);
	}

	
	private void initPlayer() {
		StackPane root = new StackPane();
		
		root.getChildren().addAll(this.playMedia("background", ".mp4"), this.createWrapper(this.createMainButton("single player", "multiplayer", MenuType.PLAYER), this.createBackButton(this.modeScene)));
		this.playerScene = new Scene(root, GameStage.WINDOW_SIZE, GameStage.WINDOW_SIZE);
	}

	private void initGame() {
		this.root = new Group();

		this.canvas = new Canvas(GameStage.SCREEN_SIZE, GameStage.WINDOW_SIZE);
		this.root.getChildren().add(this.canvas);
		this.gameScene = new Scene(root);
	}

	public void initServer(){
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
	    		if(code.equals(KeyCode.ENTER)) showModeScene(stage);	
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
				if(type == MenuType.MODE) showPlayerScene(stage);
				if(type == MenuType.PLAYER) setGameScene(stage);
            }
        });

        b2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                System.out.println(text2);; // should allow the client input the server ip address.
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

        // instantiating MediaPlayer class   
        MediaPlayer mediaPlayer = new MediaPlayer(new Media(url.toString()));  
		mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // loop the video.
		mediaPlayer.setAutoPlay(true); // auto play the video.

        // instantiating MediaView class   
        MediaView mediaView = new MediaView(mediaPlayer); 
		mediaView.setFitHeight(GameStage.WINDOW_SIZE); // set the height of the video to the height of the window.
		mediaView.setFitWidth(GameStage.WINDOW_SIZE); // set the width of the video to the width of the window.
		mediaView.setPreserveRatio(false); // do not preserve the ratio of the video.

		return mediaView;
	}

	private void setGameScene(Stage stage) {
        stage.setScene(this.gameScene);
        GraphicsContext gc = this.canvas.getGraphicsContext2D();

        this.gameTimer = new GameTimer(this.gameScene, gc, this, root, identifier, 0, "CREATE");	// create a gametimer class.

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
}
