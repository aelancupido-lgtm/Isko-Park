package tile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;
import sprite.Sprite;
import gamestage.GameStage;

public class TileManager {
	private Tile tile[];
	private int tileMap[][];
	private ArrayList<Sprite> platforms;

	public TileManager(){
		this.tile = new Tile[2];
		this.platforms = new ArrayList<Sprite>();
		this.tileMap = new int [GameStage.MAX_ROW][GameStage.MAX_ROW];
		this.populateTileList();
	}

	//For tile list
	private void populateTileList(){
		this.tile[0] = new Tile();
		this.tile[0].tileImage = GameStage.BACKGROUND_IMAGE;

		this.tile[1] = new Tile();
		this.tile[1].tileImage = GameStage.TILE_IMAGE;
		this.tile[1].collision = true;
	}

	//Populates the platforms arraylist
	private void populatePlatforms(){
		int col = 0;
		int row = 0;
		int x = 0;
		int y = 0;

		while(col < GameStage.MAX_ROW && row < GameStage.MAX_ROW ){

			int tileNum = tileMap[col][row];
			this.platforms.add(new Sprite(x, y, tile[tileNum].tileImage));

			col++;

			x+= GameStage.TILE_SIZE;

			if(col == GameStage.MAX_ROW){
				col = 0;
				x = 0;
				row++;
				y += GameStage.TILE_SIZE;
			}

		}

	}

	//For rendering map
	public void draw (GraphicsContext gc){
		for (Sprite platform: this.platforms){
			platform.render(gc, platform.getXPos(), platform.getYPos());
		}
	}

	//For reading matrix file
	// *matrix is 50x50
	// *tile size is 16x16
	public void loadMap(String fileName){
		try {
			InputStream is = getClass().getResourceAsStream("/map/" + fileName + ".txt");
			BufferedReader in = new BufferedReader(new InputStreamReader(is));

			int col = 0;
			int row = 0;

			while(col < GameStage.MAX_ROW && row < GameStage.MAX_ROW){
				String line = in.readLine();

				while(col < GameStage.MAX_ROW){
					String tiles[] = line.split(" ");
					int num = Integer.parseInt(tiles[col]);
					tileMap[col][row] = num;
					col++;
				}

				if(col == GameStage.MAX_ROW){
					col = 0;
					row++;
				}

			}

			in.close();
			this.populatePlatforms();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//Returns the platforms arraylist
	public ArrayList<Sprite> getPlatforms(){
		return this.platforms;
	}



}
