package beat;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import detector.PerfectFailureDetector;

public class HeartBeatServer extends Thread{
	DatagramSocket socket;
	InetAddress group;
	String name;
	PerfectFailureDetector detector;
	public HeartBeatServer(String name,PerfectFailureDetector detector)throws IOException{
		socket=new DatagramSocket();
		group=InetAddress.getByName(Config.address);
		this.name=name;
		this.detector=detector;
	}
	public void run(){
		//while(!interrupted()){
			try{
				ByteArrayOutputStream outArray=new ByteArrayOutputStream(5000);
				ObjectOutputStream out=new ObjectOutputStream(new BufferedOutputStream(outArray));
				//we pass name name- the second should be an IP address- We are using name because it is localhost
				HeartBeat beat=new HeartBeat(name,name,detector.isLeader());
				beat.setTimeSent(System.currentTimeMillis());
				out.writeObject(beat);
				out.flush();
				byte [] data=outArray.toByteArray();
				DatagramPacket packet=new DatagramPacket(data, data.length,group,Config.port);
				socket.send(packet);
				//System.out.println("Data sent-> "+beat);
				Thread.sleep(3000);
			}catch(IOException ex){
				ex.printStackTrace();
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		//}
	}
}
