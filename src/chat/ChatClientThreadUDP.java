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

	public ChatClientThreadUDP(DatagramSocket socket, TextArea textArea, HashMap<String, Player> players){
		this.socket = socket;
		this.textArea = textArea;
        this.players = players;
	}

	@Override
	public void run(){
		System.out.println("Starting thread");
        while (true) {
            DatagramPacket packet = new DatagramPacket(incoming, incoming.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            
            String message = new String(packet.getData(), 0, packet.getLength());

            // for player coordinates
            if (message.contains("coor;")){
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
            } else if (message.contains("player;")){
                String[] players = message.split(";");
                if (players.length > 2){
                    for(int i = 1; i<players.length; i++){
                        this.players.put(players[i], new Player(players[i], GameTimer.getScene(), GameTimer.getTileManager()));
                        String current = textArea.getText();
                        textArea.setText(current + players[i] + "\n");
                        textArea.appendText("");
                    }
                } else {
                    this.players.put(players[1], new Player(players[1], GameTimer.getScene(), GameTimer.getTileManager()));   
                    String current = textArea.getText();
                    textArea.setText(current + players[1] + "\n");
                    textArea.appendText("");
                    
                }
            } else if(message.equals("door;opened")){
                GameTimer.getDoor().setOpen();
                GameTimer.getKey().use();
            } else if(message.contains("door;")){
                String playerName = message.split(";")[1];
                this.players.get(playerName).vanish();
            } else if(message.equals("button;clicked")){
                GameTimer.getButton().setClicked();
            } else if(message.contains("key;")){
                String[] playerKey = message.split(";");
                Player player = this.players.get(playerKey[1]);
                Key key = GameTimer.getKey();
                key.setPlayer(player);
                player.acquireKey(key);
                
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