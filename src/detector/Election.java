package detector;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import beat.HeartBeat;

public interface Election {
	public String doElection();
	public HeartBeat getCurrentLeader();
	public void anounceLeader(HeartBeat leader);
	public String getWinner(ConcurrentMap<String,HeartBeat> votes);
	
}
