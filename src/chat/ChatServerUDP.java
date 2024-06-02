package chat;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import java.io.IOException;

import gamestage.Config;
import gamestage.GameStage;

public class ChatServerUDP extends Thread{
	private static byte[] incoming = new byte[256];
	private static DatagramSocket socket;

	static {
		try {
			socket = new DatagramSocket(Config.SERVER_PORT_NUMBER, InetAddress.getByName(GameStage.getServerAddress()));
		} catch (SocketException | UnknownHostException e){
			throw new RuntimeException(e);
		}
	}

    // ports
	private static ArrayList<Integer> users = new ArrayList<>();
    
    // usernames - for adding other players to clients
    private static ArrayList<String> identifiers = new ArrayList<>();

    // ip addresses
    private static ArrayList<InetAddress> hosts = new ArrayList<>();

	private static void forwardMessage(int userPort, InetAddress userHost, byte[] byteMessage, String message) throws UnknownHostException{
        for (int i = 0; i < users.size(); i++){
            if (userPort != users.get(i) && userHost != hosts.get(i)){
                DatagramPacket forward = new DatagramPacket(byteMessage, byteMessage.length, hosts.get(i), users.get(i));
                forward.setPort(users.get(i));
                try {
                    forward.setAddress(hosts.get(i));
                    socket.send(forward);
                    System.out.println("Sent message: " + message + " to " + hosts.get(i).getHostAddress() + ":" + users.get(i));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

	}

    // send all players to new port
    private static void initPlayersArray(){
        System.out.println("initializing players...");

        String players = "player;";
        for (String identifier : identifiers ){
            players = players + identifier + ";";
        }

        forwardMessageToAll(players.getBytes(), players);
    }

    private static void forwardMessageToAll(byte[] byteMessage, String message){
        for (int i = 0; i < users.size(); i++){
            DatagramPacket forward = new DatagramPacket(byteMessage, byteMessage.length, hosts.get(i), users.get(i));
            forward.setPort(users.get(i));
            try {
                forward.setAddress(hosts.get(i));
                socket.send(forward);
                System.out.println("Sent message: " + message + " to " + hosts.get(i).getHostAddress() + ":" + users.get(i));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public int getNumberOfPlayers() {
        return users.size();
    }

	@Override
	public void run(){
        int playersWaiting = 0;
        boolean sentPlayerIdentifiers = false;
        System.out.println("Server started on port " + Config.SERVER_PORT_NUMBER);
		while (!socket.isClosed()){

			DatagramPacket packet = new DatagramPacket(incoming, incoming.length); // prepare packet
            try {
                socket.receive(packet); // receive packet
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String message = new String(packet.getData(), 0, packet.getLength()); // create a string
            System.out.println("Server received: " + message);

            if (message.startsWith("init;")) {
                users.add(packet.getPort());
                hosts.add(packet.getAddress());

                String username = message.replace("init;", "");
                identifiers.add(username);
                
                byte[] sendNumberOfPlayers = new String("userNum;" + users.size()).getBytes();

                forwardMessageToAll(sendNumberOfPlayers, "userNum;" + users.size());

            // forward
            } else if (message.startsWith("kill;")) {
                int userPort = packet.getPort();
                InetAddress userHost = packet.getAddress();
                users.remove(users.indexOf(userPort));
                hosts.remove(userHost);
                String username = message.replace("kill;", "");
                byte[] userLeftMessage = new String("[Server] Player " + username + " has left the game!").getBytes();
                
                identifiers.remove(username);
                try {
                    forwardMessage(userPort, userHost, userLeftMessage, "[Server] Player " + username + " has left the game!");
                    forwardMessage(userPort, userHost, message.getBytes(), message);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                
            // if player is at the next level
            } else if (message.contains("nextLvl;")) {
                playersWaiting++;
                byte[] nextLevelPlayers = new String("userNum;" + playersWaiting).getBytes();
                forwardMessageToAll(nextLevelPlayers, "userNum;" + playersWaiting);
            } 
            
            else {
                int userPort = packet.getPort();  // get port from the packet
                InetAddress userHost = packet.getAddress();
                byte[] byteMessage = message.getBytes(); // convert the string to bytes

                try {
                    forwardMessage(userPort, userHost, byteMessage, message);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }

            // send all players to port
            // assumes that all players have already joined the game
            if (identifiers.size() == Config.MAX_PLAYERS && !sentPlayerIdentifiers) {
                
                initPlayersArray();
                sentPlayerIdentifiers = true;
            }

		}

	}
	}
