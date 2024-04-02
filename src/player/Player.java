package player;

import javafx.scene.image.Image;
import sprite.Sprite;

public class Player extends Sprite{

	private String name; //player username
	private boolean hasKey;
	private Image playerCharacter;
	private double xPos, yPos;

	//Player constructor
	public Player(String name, Image playerCharacter, double xPos, double yPos){
		super(xPos, yPos, playerCharacter);
		this.name = name;
		this.hasKey = false;
	}

	//Getter methods
	private boolean checkKey(){
		return this.hasKey;
	}


	//Setter methods
	private void getKey(){
		this.hasKey = true;
	}

}
