package beat;

import java.io.Serializable;

public class HeartBeat implements Serializable,Comparable<HeartBeat>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 345562345561L;
	private String name;
	private boolean suspect;
	private boolean leader;
	private String address;
	private long timeReceived;
	private long timeSent;
	private static final long MAX_DELAY=15;
	private long timeOut;
	private long delayAverage;
	public HeartBeat(String name,String addr,boolean isLeader){
		this.name=name;
		this.address=addr;
		suspect=false;
		timeOut=0;
		leader=isLeader;
		delayAverage=MAX_DELAY;
	}
	public boolean isLeader(){
		return leader;
	}
	public void setLeader(boolean status){
		leader=status;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getTimeReceived() {
		return timeReceived;
	}
	public void setTimeReceived(long timeReceived) {
		this.timeReceived = timeReceived;
		delayAverage=(long)(timeSent-timeReceived)/2;
		timeOut=MAX_DELAY+Math.abs(delayAverage);
	}
	public long getTimeSent() {
		return timeSent;
	}
	public void setTimeSent(long timeSent) {
		this.timeSent = timeSent;
	}
	public long getTimeOut() {
		return timeOut;
	}
	@Override
	public String toString(){
		return "["+getName()+",timeout="+getTimeOut()+", suspect="+suspect+",leader="+isLeader()+"]";
	}
	@Override
	public int compareTo(HeartBeat o) {
		return o.getAddress().compareTo(getAddress());
	}
	public boolean isSuspect() {
		return suspect;
	}
	public void setSuspect(boolean suspect) {
		this.suspect = suspect;
	}
	public String getAddress() {
		return address;
	}
}
