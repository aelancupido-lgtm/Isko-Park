package tile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;
import sprite.Sprite;
import gamestage.Config;

public class TileManager {
	private Tile tile[];
	private int tileMap[][];
	private ArrayList<Sprite> blocks;

	public TileManager(){
		this.tile = new Tile[2];
		this.blocks = new ArrayList<Sprite>();
		this.tileMap = new int [Config.MAX_ROW][Config.MAX_ROW];
		this.populateTileList();
	}

	//For tile list
	private void populateTileList(){
		this.tile[0] = new Tile();
		this.tile[0].tileImage = Config.BACKGROUND_IMAGE;

		this.tile[1] = new Tile();
		this.tile[1].tileImage = Config.TILE_IMAGE;
		this.tile[1].collision = true;
	}

	//Populates the blocks arraylist
	private void populateBlocks(){
		int col = 0;
		int row = 0;
		int x = 0;
		int y = 0;

		while(col < Config.MAX_ROW && row < Config.MAX_ROW ){

			int tileNum = tileMap[col][row];

			if (tileNum == 1){
				this.blocks.add(new Sprite(x, y, tile[tileNum].tileImage));
			}

			col++;

			x+= Config.TILE_SIZE;

			if(col == Config.MAX_ROW){
				col = 0;
				x = 0;
				row++;
				y += Config.TILE_SIZE;
			}

		}

	}

	//For rendering map
	public void draw (GraphicsContext gc){
		for (Sprite block: this.blocks){
			block.render(gc, block.getXPos(), block.getYPos());
		}
	}

	public void clear(GraphicsContext gc){
		this.platforms.clear();
		gc.clearRect(0, 0, Config.WINDOW_SIZE, Config.WINDOW_SIZE);
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

			while(col < Config.MAX_ROW && row < Config.MAX_ROW){
				String line = in.readLine();

				while(col < Config.MAX_ROW){
					String tiles[] = line.split(" ");
					int num = Integer.parseInt(tiles[col]);
					tileMap[col][row] = num;
					col++;
				}

				if(col == Config.MAX_ROW){
					col = 0;
					row++;
				}

			}

			in.close();
			this.populateBlocks();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//Returns the blocks arraylist
	public ArrayList<Sprite> getBlocks(){
		return this.blocks;
	}

}
