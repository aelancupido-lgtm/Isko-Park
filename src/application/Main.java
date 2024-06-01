package application;

import gamestage.GameStage;
import gamestage.GameTimer;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

	Stage window;
	String address;
	int port;

	@Override
	public void start(Stage primaryStage) throws Exception{
		GameStage game = new GameStage();

		primaryStage.setOnCloseRequest(event -> {
			if (GameStage.isCreated()){
				if (!GameStage.isSinglePlayer()) GameTimer.client.close();
			}
			System.exit(0);
		});

		primaryStage.setTitle("Isko Park");
		primaryStage.getIcons().add( new Image("/assets/logo.png"));
		game.setStage(primaryStage);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
