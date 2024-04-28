package gamestage;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GameStage {
	private Group root;
	private Scene gameScene;
	private Scene splashScene;
	private Stage stage;
	private Canvas canvas;
	private GameTimer gameTimer;

	public final static int WINDOW_SIZE = 800;
	public final static int TILE_SIZE = 16;
	public final static int MAX_ROW = WINDOW_SIZE/TILE_SIZE;


	public final static Color BACKGROUND_COLOR = Color.WHITE;
	public final static Color TILE_COLOR = Color.ORANGE;

	public final static Image BACKGROUND_IMAGE = new Image("assets/white.png", TILE_SIZE, TILE_SIZE, false, false);
	public final static Image TILE_IMAGE = new Image("assets/orange.png", TILE_SIZE, TILE_SIZE, false, false);

	public GameStage(){
		this.root = new Group();
		this.gameScene = new Scene(root);
		this.canvas = new Canvas(WINDOW_SIZE, WINDOW_SIZE);
		this.root.getChildren().add(canvas);
	}

	public void setStage(Stage stage){
		this.stage = stage;
		stage.setTitle("Isko Park");

		this.initSplash();

		stage.setScene(this.splashScene);
		this.prepareActionHandlers(this.splashScene);
//		stage.setResizable(false);
		stage.show();
	}

	private void initSplash() {
		StackPane root = new StackPane();

        root.getChildren().addAll(this.drawBackground("splash"));
        this.splashScene = new Scene(root, GameStage.WINDOW_SIZE, GameStage.WINDOW_SIZE);
	}

	private ImageView drawBackground(String name) {
        // load the GIF image.
        Image gifImage = new Image("assets/" + name + ".gif", GameStage.WINDOW_SIZE, GameStage.WINDOW_SIZE, false, false);

        // create an ImageView to display the GIF image.
        ImageView imageView = new ImageView(gifImage);

        return imageView;
	}


	private void prepareActionHandlers(Scene scene) {
	    scene.setOnKeyPressed(new EventHandler<KeyEvent>(){
	    	public void handle(KeyEvent e){
	    		KeyCode code = e.getCode();
	    		if(code.equals(KeyCode.ENTER))setGame(stage);
	    	}
	    });
	}

	private void setGame(Stage stage) {
        stage.setScene(this.gameScene);
        GraphicsContext gc = this.canvas.getGraphicsContext2D();
        this.gameTimer = new GameTimer(this.gameScene, gc);	// create a gametimer class.

        gameTimer.start(); // invoke the start method of the animation timer.
	}


//	public void setGameOverScene(Scene gameOverScene){
//        stage.setScene(gameOverScene);
//    }

}
