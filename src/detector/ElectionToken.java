package detector;

import java.io.Serializable;

import beat.HeartBeat;

public class ElectionToken implements Serializable{
	/**
	 * Serial version for this class
	 */
	private static final long serialVersionUID = 6745332451L;
	private final HeartBeat vote;
	private final String type;
	public ElectionToken(HeartBeat vote,String type){
		this.vote=vote;
		this.type=type;
	}
	public String getType() {
		return type;
	}
	HeartBeat getVote() {
		return vote;
	}
	@Override
	public String toString(){
		return "{type="+type+vote.toString()+"}";
	}
}
