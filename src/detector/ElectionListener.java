package detector;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import beat.Config;
import beat.HeartBeat;

public class ElectionListener extends Thread {
	MulticastSocket socket;
	InetAddress group;
	PerfectFailureDetector detector;

	public ElectionListener(PerfectFailureDetector detector) throws IOException {
		group = InetAddress.getByName(Config.address);
		socket = new MulticastSocket(Config.election_port);
		this.detector = detector;
	}

	public void run() {
		try {
			socket.joinGroup(group);
			while (!interrupted()) {
				byte[] data = new byte[5000];
				DatagramPacket packet = new DatagramPacket(data, data.length);
				socket.receive(packet);
				ByteArrayInputStream inArray = new ByteArrayInputStream(data);
				ObjectInputStream in = new ObjectInputStream(
						new BufferedInputStream(inArray));
				ElectionToken type = (ElectionToken) in.readObject();
				if (type.getType().equalsIgnoreCase("election")) {
					synchronized (this) {
						detector.addVote(type.getVote());
					}
					System.out.println("Received (E)-> " + type);
				} else if (type.getType().equalsIgnoreCase("leader")) {
					// Set the new leader
					detector.onElection(type.getVote().getAddress());
				} else {
					System.out.println("Unknown option");
				}
			}
			socket.leaveGroup(group);
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
