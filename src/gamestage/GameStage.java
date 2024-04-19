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

	public final static int WINDOW_WIDTH = 800;

	public GameStage(){
		this.canvas = new Canvas(WINDOW_WIDTH, WINDOW_WIDTH);
		this.root = new Group();
		this.root.getChildren().add(canvas);
		this.gameScene = new Scene(root);
	}

	public void setStage(Stage stage){
		this.stage = stage;
		stage.setTitle("Isko Park");
		stage.setScene(this.gameScene);
//		stage.setResizable(false);
		stage.show();

		GraphicsContext graphics = this.canvas.getGraphicsContext2D();
		GameTimer gameTimer = new GameTimer(gameScene, graphics);
		gameTimer.start();
	}



}
