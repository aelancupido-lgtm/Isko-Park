package application;


import chat.ChatServerUDP;
import gamestage.GameStage;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.stage.Stage;

public class Main extends Application {

	static ChatServerUDP server;
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
