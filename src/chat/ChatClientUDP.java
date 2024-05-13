package chat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import gamestage.GameStage;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;

public class ChatClientUDP {

	private static DatagramSocket socket;
	static {
		try {
			socket = new DatagramSocket();
		} catch (SocketException e){
			throw new RuntimeException(e);
		}
	}

	private static InetAddress address;
	static {
		try {
			address = InetAddress.getByName(GameStage.SERVER_ADDRESS);
		} catch (UnknownHostException e){
			throw new RuntimeException(e);
		}
	}

	public void sendMessage(String msg){
		try {
			byte[] message = msg.getBytes();
			DatagramPacket packet = new DatagramPacket(message, message.length, address, GameStage.SERVER_PORT_NUMBER);
			socket.send(packet);
		} catch (IOException e){
			throw new RuntimeException(e);
		}
	}

	public void createClientThread(TextArea messageArea, String identifier) throws IOException{
		ChatClientThreadUDP clientThread = new ChatClientThreadUDP(socket, messageArea);
		Task<ChatClientThreadUDP> task = new Task<ChatClientThreadUDP>(){

			protected ChatClientThreadUDP call() throws Exception {

				clientThread.start();
				return clientThread;
			}

		};

		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();

        byte[] uuid = ("init;" + identifier).getBytes();
        DatagramPacket initialize = new DatagramPacket(uuid, uuid.length, address, GameStage.SERVER_PORT_NUMBER);
        socket.send(initialize);
	}





}
