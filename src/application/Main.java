package application;


import gamestage.GameStage;
import gamestage.GameTimer;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

	Stage window;
	String address;
	int port;

	@Override
	public void start(Stage primaryStage) throws Exception{
		GameStage game = new GameStage();

		primaryStage.setOnCloseRequest(event -> {
			if (GameStage.getServerAddress() != null){
				if (!GameStage.SINGLE_PLAYER_GAME) GameTimer.client.close();
			}
			System.exit(0);
		});

		game.setStage(primaryStage);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
