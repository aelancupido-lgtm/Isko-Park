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

	private static ArrayList<Integer> users = new ArrayList<>();
    private static ArrayList<String> identifiers = new ArrayList<>();
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
    private static void initPlayersArray(int userPort, InetAddress userHost){
        System.out.println("initializing players...");
        String players = "player;";
        for (String identifier : identifiers ){
            players = players + identifier + ";";
        }

        forwardMessageToClient(userPort, userHost, players.getBytes(), players);
    }

    private static void forwardMessageToClient(int userPort, InetAddress userHost, byte[] byteMessage, String message){
        DatagramPacket forward = new DatagramPacket(byteMessage, byteMessage.length, userHost, userPort);
        try {
            socket.send(forward);
            System.out.println("Sent message: " + message + " to " + userHost.getHostAddress() + ":" + userPort);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void forwardMessageToAll(int userPort, InetAddress userHost, byte[] byteMessage, String message){
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

	@Override
	public void run(){
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
                // new user chat message
                byte[] newUserMessage = new String("[Server] Player " + username + " has joined the chat!").getBytes();
                
                // add user to users hashmap message
                byte[] addUserMessage = new String("player;" + username).getBytes();
                
                byte[] sendNumberOfPlayers = new String("userNum;" + users.size()).getBytes();

                int userPort = packet.getPort();
                InetAddress userHost = packet.getAddress();

                // System.out.println("identifiers in list: " + identifiers.size());
                // send all players to new port
                if (identifiers.size() != 0) initPlayersArray(userPort, userHost);

                identifiers.add(username);

                try {
                    forwardMessage(userPort, userHost, newUserMessage, "[Server] Player " + username + " has joined the chat!");
                    forwardMessage(userPort, userHost, addUserMessage, "player;" + username);
                    forwardMessageToAll(userPort, userHost, sendNumberOfPlayers, "userNum;" + users.size());

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

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
                

            } else {
                int userPort = packet.getPort();  // get port from the packet
                InetAddress userHost = packet.getAddress();
                byte[] byteMessage = message.getBytes(); // convert the string to bytes

                try {
                    forwardMessage(userPort, userHost, byteMessage, message);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }

			}

	}

//	public static void main(String[] args){
//		System.out.println("Server started on port " + PORT);
//
//		while (true){
//
//			DatagramPacket packet = new DatagramPacket(incoming, incoming.length); // prepare packet
//            try {
//                socket.receive(packet); // receive packet
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//
//            String message = new String(packet.getData(), 0, packet.getLength()); // create a string
//            System.out.println("Server received: " + message);
//
//
//            if (message.contains("init;")) {
//                users.add(packet.getPort());
//                String username = message.replace("init;", "");
//                byte[] newUserMessage = new String("[Server] Player " + username + " has joined the chat!").getBytes();
//                int userPort = packet.getPort();
//
//                forwardMessage(userPort, newUserMessage, "[Server] Player " + username + " has joined the chat!");
//                }
//
//            // forward
//            else {
//                int userPort = packet.getPort();  // get port from the packet
//                byte[] byteMessage = message.getBytes(); // convert the string to bytes
//
//                forwardMessage(userPort, byteMessage, message);
//            }
//
//			}
//		}
	}
