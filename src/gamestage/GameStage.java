package gamestage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class GameStage {
	private Stage stage;
	private Scene gameScene;
	private Canvas canvas;
	private Group root;

	public final static int WINDOW_SIZE = 800;
	public final static int TILE_SIZE = 16;
	public final static int MAX_ROW = WINDOW_SIZE/TILE_SIZE;


	public final static Color BACKGROUND_COLOR = Color.WHITE;
	public final static Color TILE_COLOR = Color.ORANGE;

	public final static Image BACKGROUND_IMAGE = new Image("assets/white.png", TILE_SIZE, TILE_SIZE, false, false);
	public final static Image TILE_IMAGE = new Image("assets/orange.png", TILE_SIZE, TILE_SIZE, false, false);

	public GameStage(){
		this.root = new Group();
		this.root.getChildren().add(canvas);
		this.gameScene = new Scene(root);
		this.canvas = new Canvas(WINDOW_SIZE, WINDOW_SIZE);
	}

	public void setStage(Stage stage){
		this.stage = stage;
		stage.setTitle("Isko Park");
		stage.setScene(this.gameScene);
//		stage.setResizable(false);
		stage.show();

		GraphicsContext graphics = this.canvas.getGraphicsContext2D();
		GameTimer gameTimer = new GameTimer(gameScene, graphics, this);
		gameTimer.start();
	}

	public void setGameOverScene(Scene gameOverScene){
        stage.setScene(gameOverScene);
    }

}
