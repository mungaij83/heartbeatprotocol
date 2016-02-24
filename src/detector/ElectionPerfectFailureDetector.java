package detector;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import beat.Config;
import beat.HeartBeat;

public abstract class ElectionPerfectFailureDetector implements Election{
	public void anounceLeader(HeartBeat leader){
		Thread t=new Thread(new Runnable(){
			@Override
			public void run() {
				try{
					InetAddress group=InetAddress.getByName(Config.address);
					DatagramSocket socket=new DatagramSocket();
					ByteArrayOutputStream outArray=new ByteArrayOutputStream(5000);
					ObjectOutputStream out=new ObjectOutputStream(new BufferedOutputStream(outArray));
					//we pass name name- the second should be an IP address- We are using name because it is localhost
					ElectionToken token=new ElectionToken(leader, "leader");
					out.writeObject(token);
					out.flush();
					byte [] dataLeader=outArray.toByteArray();
					DatagramPacket leader=new DatagramPacket(dataLeader, dataLeader.length,group,Config.election_port);
					socket.send(leader);
					System.out.println("Leader -> "+leader);
					Thread.sleep(3000);
					socket.close();
				}catch(IOException ex){
					ex.printStackTrace();
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		});
		t.start();
	}
	public void sendVote(HeartBeat leader){
		Thread t=new Thread(new Runnable(){
			@Override
			public void run() {
				try{
					InetAddress group=InetAddress.getByName(Config.address);
					DatagramSocket socket=new DatagramSocket();
					ByteArrayOutputStream outArray=new ByteArrayOutputStream(5000);
					ObjectOutputStream out=new ObjectOutputStream(new BufferedOutputStream(outArray));
					//we pass name name- the second should be an IP address- We are using name because it is localhost
					ElectionToken token=new ElectionToken(leader, "election");
					out.writeObject(token);
					out.flush();
					byte [] dataLeader=outArray.toByteArray();
					DatagramPacket leader=new DatagramPacket(dataLeader, dataLeader.length,group,Config.election_port);
					System.out.println("Election-> "+token);
					socket.send(leader);
					Thread.sleep(3000);
					socket.close();
				}catch(IOException ex){
					ex.printStackTrace();
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		});
		t.start();
	}
	public String getWinner(ConcurrentMap<String,HeartBeat> votes){
		if(votes.size()==0)
			return "";
		Map<String,Integer> m=new HashMap<String, Integer>();
		for(String s:votes.keySet()){
			if(m.containsKey(votes.get(s).getAddress())){
				m.put(votes.get(s).getAddress(),m.get(votes.get(s).getAddress())+1);
			}else{
				m.put(s, 1);
			}
		}
		String winner="";
		for(String key:m.keySet()){
			if(winner.isEmpty()){
				winner=key;
			}else if(m.get(key)>m.get(winner)){
				winner=key;
			}
		}
		return winner;
	}
}
