package gamestage;

import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class GameOver {
    private Scene scene;
    private GraphicsContext gc;
    private Stage stage;

    public GameOver(Scene scene, GraphicsContext gc) {
        this.scene = scene;
        this.gc = gc;
    }

    public void showGameOver(){
        gc.setFill(Color.ORANGE);
        gc.fillRect(0, 0, GameStage.WINDOW_WIDTH, GameStage.WINDOW_WIDTH);

        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Verdana", FontWeight.BOLD, 60));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("Game Over", GameStage.WINDOW_WIDTH / 2, GameStage.WINDOW_WIDTH / 2);
    }

	public Scene getScene(){
		return this.scene;
	}

}
