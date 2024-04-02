package gamestage;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GameTimer extends AnimationTimer{
	private Scene scene;
	private GraphicsContext gc;
	//direction input booleans
	private static boolean left;
	private static boolean right;
	private static boolean up;
	private static boolean down;

	GameTimer(Scene scene, GraphicsContext gc) {
	    this.gc = gc;
	    this.scene = scene;

	    this.prepareActionHandlers();
	}


	@Override
	public void handle(long arg0) {
		// TODO Auto-generated method stub

	}

	//For movement of player character
	private void prepareActionHandlers() {
	    this.scene.setOnKeyPressed(new EventHandler<KeyEvent>(){
	    	public void handle(KeyEvent e){
	    		String code = e.getCode().toString();
	            if(code.equals("A")) {
	                GameTimer.left = true;
	            }else if(code.equals("D")) {
	                GameTimer.right = true;
	            }else if(code.equals("W")){
	                GameTimer.up = true;
	            }else if(code.equals("S")){
	                GameTimer.down = true;
	            }
	    	}
	    });

	    this.scene.setOnKeyReleased(new EventHandler<KeyEvent>(){
	    	public void handle(KeyEvent e){
	    		String code = e.getCode().toString();
	    		if(code.equals("A")) {
	                GameTimer.left = false;
	    		}else if(code.equals("D")) {
	                GameTimer.right = false;
	    		}else if(code.equals("W")){
	                GameTimer.up = false;
	    		}else if(code.equals("S")){
	                GameTimer.down = false;
	    		}
	    	}
	    });
	}
}

