package player;

import javafx.scene.image.Image;
import sprite.Sprite;

public class Player extends Sprite{

	public final static Image PLAYER_IMAGE = new Image("assets/green_character_sheet1.png", 30, 35, true, false);

	private String name; //player username
	private boolean hasKey;
	private Image playerCharacter;
	private double xPos, yPos;

	//Player constructor
	public Player(String name){
		super(0, 0, PLAYER_IMAGE);
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
