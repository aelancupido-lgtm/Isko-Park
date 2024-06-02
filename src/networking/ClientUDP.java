package networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import gamestage.Config;
import gamestage.GameStage;
import javafx.concurrent.Task;

public class ClientUDP {
	private ClientThreadUDP clientThread;


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
			address = InetAddress.getByName(GameStage.getServerAddress());
		} catch (UnknownHostException e){
			throw new RuntimeException(e);
		}
	}

	public void sendMessage(String msg){
		try {
			byte[] message = msg.getBytes();
			DatagramPacket packet = new DatagramPacket(message, message.length, address, Config.SERVER_PORT_NUMBER);
			socket.send(packet);
		} catch (IOException e){
			throw new RuntimeException(e);
		}
	}

	public void createClientThread(String identifier) throws IOException{
		this.clientThread = new ClientThreadUDP(socket);
		Task<ClientThreadUDP> task = new Task<ClientThreadUDP>(){

			protected ClientThreadUDP call() throws Exception {

				clientThread.start();
				return clientThread;
			}

		};

		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();

        byte[] uuid = ("init;" + identifier).getBytes();
        DatagramPacket initialize = new DatagramPacket(uuid, uuid.length, address, Config.SERVER_PORT_NUMBER);
        socket.send(initialize);
	}

	public ClientThreadUDP getClientThread(){
		return this.clientThread;
	}

	public void close(){
		sendMessage("kill;" + GameStage.getIdentifier());
	}
}
