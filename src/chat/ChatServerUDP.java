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
			socket = new DatagramSocket(Config.SERVER_PORT_NUMBER);
		} catch (SocketException e){
			throw new RuntimeException(e);
		}
	}

	private static ArrayList<Integer> users = new ArrayList<>();
    private static ArrayList<String> identifiers = new ArrayList<>();
	private static InetAddress address = null;

	static {
		try {
			address = InetAddress.getByName(GameStage.getServerAddress());
		} catch (UnknownHostException e){
			throw new RuntimeException(e);
		}
	}

	public static void forwardMessage(int userPort, byte[] byteMessage, String message){
        for (int forward_port : users) {
            if (forward_port != userPort) {
                DatagramPacket forward = new DatagramPacket(byteMessage, byteMessage.length, address, forward_port);
                try {
                    socket.send(forward);
                    System.out.println("Sent message: " + message + " to " + forward_port);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
	}

    // send all players to new port
    public static void initPlayersArray(int userPort){
        System.out.println("initializing players...");
        String players = "player;";
        for (String identifier : identifiers ){
            players = players + identifier + ";";
        }

        System.out.println(players);
        DatagramPacket forward = new DatagramPacket(players.getBytes(), players.getBytes().length, address, userPort);
        try {
            socket.send(forward);
            System.out.println("Sent message: " + players + " to " + userPort);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

	@Override
	public void run(){
        System.out.println("Server started on port " + Config.SERVER_PORT_NUMBER);
		while (true){

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
                String username = message.replace("init;", "");
                byte[] newUserMessage = new String("[Server] Player " + username + " has joined the chat!").getBytes();
                byte[] addUserMessage = new String("player;" + username).getBytes();
                int userPort = packet.getPort();

                System.out.println("identifiers in list: " + identifiers.size());
                // send all players to new port
                if (identifiers.size() != 0) initPlayersArray(userPort);

                identifiers.add(username);
                forwardMessage(userPort, newUserMessage, "[Server] Player " + username + " has joined the chat!");
                forwardMessage(userPort, addUserMessage, "player;" + username);

            // forward
            } else {
                int userPort = packet.getPort();  // get port from the packet
                byte[] byteMessage = message.getBytes(); // convert the string to bytes

                forwardMessage(userPort, byteMessage, message);
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
