package application;

import gamestage.GameStage;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		GameStage game = new GameStage();
		game.setStage(primaryStage);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
