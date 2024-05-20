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
        gc.fillRect(0, 0, Config.WINDOW_SIZE, Config.WINDOW_SIZE);

        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Verdana", FontWeight.BOLD, 60));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("Game Over", Config.WINDOW_SIZE / 2, Config.WINDOW_SIZE / 2);
    }

	public Scene getScene(){
		return this.scene;
	}

}
