package beat;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.Map;

import detector.PerfectFailureDetector;

public class HeartBeatClient extends Thread {
	MulticastSocket socket;
	InetAddress group;
	PerfectFailureDetector detector;
	public HeartBeatClient(PerfectFailureDetector detector) throws IOException {
		group = InetAddress.getByName(Config.address);
		socket = new MulticastSocket(Config.port);
		this.detector=detector;
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
				HeartBeat beat = (HeartBeat) in.readObject();
				synchronized (this) {
					beat.setTimeReceived(System.currentTimeMillis());
					detector.onReceive(beat);
				}
				//System.out.println("Received -> " + beat);
			}
			socket.leaveGroup(group);
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
