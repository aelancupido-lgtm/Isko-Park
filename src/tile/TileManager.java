package tile;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import javafx.scene.canvas.GraphicsContext;
import gamestage.GameStage;

public class TileManager {
	private Tile tile[];
	private int tileMap[][];

	public TileManager(){
		this.tile = new Tile[2];
		this.tileMap = new int [GameStage.MAX_ROW][GameStage.MAX_ROW];
		this.populateTileList();
	}

	//For tile list
	private void populateTileList(){
		this.tile[0] = new Tile();
		this.tile[0].tileColor = GameStage.BACKGROUND_COLOR;

		this.tile[1] = new Tile();
		this.tile[1].tileColor = GameStage.TILE_COLOR;
		this.tile[1].collision = true;
	}

	//For rendering map
	public void draw (GraphicsContext gc){
		int col = 0;
		int row = 0;
		int x = 0;
		int y = 0;

		while(col < GameStage.MAX_ROW && row < GameStage.MAX_ROW ){

			int tileNum = tileMap[col][row];

			gc.setFill(tile[tileNum].tileColor);
			gc.fillRect(x, y, GameStage.TILE_SIZE, GameStage.TILE_SIZE);
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

		} catch (Exception e) {
			e.printStackTrace();
		}
	}



}
