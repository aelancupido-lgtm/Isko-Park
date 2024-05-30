package chat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;

import gamestage.GameTimer;
import javafx.scene.control.TextArea;
import objects.Key;
import player.Player;

public class ChatClientThreadUDP extends Thread{

	private DatagramSocket socket;
	private byte[] incoming = new byte[256];

	private TextArea textArea;
    private HashMap<String, Player> players;
    private GameTimer gameTimer;

	public ChatClientThreadUDP(DatagramSocket socket, TextArea textArea, GameTimer gameTimer){
		this.socket = socket;
		this.textArea = textArea;
        this.gameTimer = gameTimer;
        this.players = this.gameTimer.getPlayers();
	}

	@Override
	public void run(){
		System.out.println("Starting thread");
        while (!this.socket.isClosed()) {
            DatagramPacket packet = new DatagramPacket(incoming, incoming.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            
            String message = new String(packet.getData(), 0, packet.getLength());

            // for player coordinates
            if (message.startsWith("coor;")){
                String[] playerCoor = message.split(";");
                
                String name = playerCoor[1];
                double xPos = Double.parseDouble(playerCoor[2]);
                double yPos = Double.parseDouble(playerCoor[3]);
                String action = playerCoor[4];
                String direction = playerCoor[5];
                boolean left = Boolean.parseBoolean(playerCoor[6]);
                boolean right = Boolean.parseBoolean(playerCoor[7]);

                Player player = this.players.get(name);
                player.setXPos(xPos);
                player.setYPos(yPos);
                player.setDirection(direction);
                player.setAction(action);
                player.setLeftRight(left, right);

                if (playerCoor.length > 8) {
                    int spriteNum = Integer.parseInt(playerCoor[8]);
                    player.setSpriteNum(spriteNum);
                }


            // for adding players to gametimer hashmap
            } else if (message.startsWith("player;")){
                String[] players = message.split(";");
                for(int i = 1; i<players.length; i++){
                    this.players.put(players[i], new Player(players[i]));
                    // String current = textArea.getText();
                    // textArea.setText(current + players[i] + "\n");
                    // textArea.appendText("");
                }

            // opens door for all players
            } else if(message.equals("door;opened")){
                this.gameTimer.getDoor().setOpen();
                this.gameTimer.getKey().use();
            
            // for other players entering opened door vanishing
            } else if(message.startsWith("door;")){
                String playerName = message.split(";")[1];
                this.players.get(playerName).vanish();

            // for other players clicking button
            } else if(message.equals("button;clicked")){
                this.gameTimer.getButton().setClicked();

            // for other players obtaining key
            } else if(message.startsWith("key;")){
                String[] playerKey = message.split(";");
                Player player = this.players.get(playerKey[1]);
                Key key = this.gameTimer.getKey();
                key.setPlayer(player);
                player.acquireKey(key);
            
            // for other players exiting the game
            } else if (message.startsWith("kill;")){
                String playerName = message.split(";")[1];
                Player player = this.players.get(playerName);
                
                if (player.getKey() != null){
                    player.getKey().removePlayer();
                    player.removeKey();
                }

                player.vanish();
                this.players.remove(playerName);

            }

            //Regular chat
            else {
                message = message  + "\n";
                String current = textArea.getText();
                textArea.setText(current + message);
                textArea.appendText("");
            }

        }
    }
}