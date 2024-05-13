package application;


import gamestage.GameStage;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

	Stage window;
	String address;
	int port;

	@Override
	public void start(Stage primaryStage) throws Exception{
		GameStage game = new GameStage();
		game.setStage(primaryStage);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
