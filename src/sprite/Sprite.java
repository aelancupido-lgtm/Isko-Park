package sprite;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Sprite {
	protected Image img;
	protected double xPos, yPos, dx, dy;
	protected boolean visible;
	protected double width;
	protected double height;

	public Sprite(double xPos, double yPos){
		this.xPos = xPos;
		this.yPos = yPos;
		this.visible = true;
	}

	public Sprite(double xPos, double yPos, Image img){
		this.xPos = xPos;
		this.yPos = yPos;
		this.visible = true;
		this.loadImage(img);
	}

	public Rectangle2D getBounds(){
		return new Rectangle2D(this.xPos, this.yPos, this.width, this.height);
	}

	private void setSize(){
		this.width = this.img.getWidth();
        this.height = this.img.getHeight();
	}

	//Collision checker
	protected boolean collidesWith(Sprite rect2)	{
		Rectangle2D rectangle1 = this.getBounds();
		Rectangle2D rectangle2 = rect2.getBounds();

		return rectangle1.intersects(rectangle2);
	}

	protected void loadImage(Image image){
		try{
			this.img = image;
	        this.setSize();
		} catch(Exception e)	{
			e.printStackTrace();
		}
	}


	//For player movement
	public void render(GraphicsContext gc, double xPos, double yPos){
		gc.drawImage(this.img, xPos, yPos);
	}

	//Getter methods
	public Image getImage(){
		return this.img;
	}

	public double getXPos(){
		return this.xPos;
	}

	public double getYPos(){
		return this.yPos;
	}

	public boolean isVisible(){
		return visible;
	}

	public double getDx(){
		return this.dx;
	}

	public double getDy(){
		return this.dy;
	}

	//Setter methods
	public void setDX(double val){
		this.dx = val;
	}

	public void setDY(double val){
		this.dy = val;
	}

	public void vanish(){
		this.visible = false;
	}

}
