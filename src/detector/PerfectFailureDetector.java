package detector;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import beat.HeartBeat;
import beat.HeartBeatClient;
import beat.HeartBeatServer;

public class PerfectFailureDetector extends ElectionPerfectFailureDetector {
	static final long HEART_BEAT_RATE = 5;
	String CURRENT_LEADER;
	ScheduledExecutorService executor;
	HeartBeatServer heartBeatServer;
	ConcurrentMap<String, HeartBeat> servers;
	Set<HeartBeat> listSuspects;
	HeartBeatClient client;
	ElectionListener listener;
	final String MY_NAME;
	// final ScheduledExecutorService executor=new ScheduledExecutorService();
	final ConcurrentMap<String, Boolean> suspects;
	final ConcurrentMap<String, Future<?>> sheduledSuspicion;
	final ConcurrentMap<String, HeartBeat> votes;

	public PerfectFailureDetector(ConcurrentMap<String, HeartBeat> map,
			String name) throws IOException {
		servers = map;
		MY_NAME = name;
		heartBeatServer = new HeartBeatServer(MY_NAME,this);
		client = new HeartBeatClient(this);
		client.start();
		listener = new ElectionListener(this);
		listener.start();
		executor = Executors.newScheduledThreadPool(50);
		listSuspects = new CopyOnWriteArraySet<HeartBeat>();
		sheduledSuspicion = new ConcurrentHashMap<String, Future<?>>();
		suspects = new ConcurrentHashMap<String, Boolean>();
		votes = new ConcurrentHashMap<String, HeartBeat>();
		executor.scheduleWithFixedDelay(heartBeatServer, 0, HEART_BEAT_RATE,
				TimeUnit.SECONDS);
		// Wait for some time and see if you find the server, if not start
		// election
		FutureTask<?> checkLeader = new FutureTask<Object>(
				new Callable<Object>() {
					@Override
					public Object call() throws Exception {
						startElection();
						return null;
					}

				});
		// Wait for 20 seconds before checking the leader
		executor.schedule(checkLeader, 20, TimeUnit.SECONDS);
	}

	/**
	 * Receives other servers including its own heart beat. The server will
	 * therefore never suspect itself
	 * 
	 * @param beat
	 */
	public void onReceive(HeartBeat beat) {
		// Check if its is a known server
		if (servers.containsKey(beat.getAddress())) {
			HeartBeat current = servers.get(beat.getAddress());
			current.setTimeSent(beat.getTimeSent());
			current.setTimeReceived(beat.getTimeReceived());
			if (current.isSuspect()) {
				current.setSuspect(false);
				removeFromSuspects(current);
			}
			// System.out.println(beat.getAddress() + " scheduled "
			// + current.getTimeOut());
			scheduleSuspicion(current);
			if(current.isLeader()){
				CURRENT_LEADER=current.getAddress();
			}
		} else {
			// Add unknown server here
			servers.put(beat.getAddress(), beat);
			scheduleSuspicion(beat);
			if(beat.isLeader()){
				CURRENT_LEADER=beat.getAddress();
			}
		}
	}

	/**
	 * Timeout suspects If timed out, add the server to suspects Remove previous
	 * suspect task
	 * 
	 * @param beat
	 */
	protected void scheduleSuspicion(HeartBeat beat) {
		FutureTask<?> task = new FutureTask<Object>(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				addSuspect(beat);

				// System.out.println(beat.getAddress() + " suspected ");
				return null;
			}
		});
		synchronized (executor) {
			executor.schedule(task, beat.getTimeOut(), TimeUnit.SECONDS);
			Future<?> previousSchedule = sheduledSuspicion.put(
					beat.getAddress(), task);
			if (previousSchedule != null) {
				previousSchedule.cancel(true);
			}
		}
	}

	/**
	 * Add a new suspect to the list os suspects and start election if the
	 * suspect is the current leader.
	 * 
	 * @param beat
	 */
	protected synchronized void addSuspect(HeartBeat beat) {
		if (suspects.putIfAbsent(beat.getAddress(), true) == null) {
			// Notify suspect listener
			beat.setSuspect(true);
			// Check if the suspect is the leader
			if (checkLeader(beat)) {
				servers.get(beat.getAddress()).setLeader(false);
				votes.clear();
				startElection();
			}
		}
	}

	/**
	 * Checks for existing leader and if non exists, it starts the election
	 * process
	 */
	protected void startElection() {
		for (String key : servers.keySet()) {
			if (servers.get(key).isLeader() && !suspects.containsKey(key)) {
				CURRENT_LEADER = key;
				return; // There is leader already
			}
		}
		//Mark all as not leaders
		
		String leader = doElection(); // Select with the highest IP (Replaces
			for (String key : servers.keySet()) {
			servers.get(key).setLeader(false);
		}						// ID)
		//Start election process
		HeartBeat newLeader = servers.get(leader);
		this.sendVote(newLeader);
		FutureTask<?> electionComplete = new FutureTask<Object>(
				new Callable<Object>() {
					@Override
					public Object call() throws Exception {
						String leader = getWinner(votes);
						if (leader.equalsIgnoreCase(MY_NAME)) {
							anounceLeader(servers.get(leader));
							System.out.println("LEADER: "+leader);
						}
						return null;
					}

				});
		// Wait for 10 seconds and evaluate the winner
		executor.schedule(electionComplete, 10, TimeUnit.SECONDS);
	}

	/**
	 * Notify when a new leader is elected
	 * 
	 * @param leader
	 */
	public void onElection(String leader) {
		if (!servers.get(leader).isLeader() && !servers.get(leader).isSuspect()) {
			for (String key : servers.keySet()) {
				servers.get(key).setLeader(false);
			}
			servers.get(leader).setLeader(true);
			CURRENT_LEADER = leader;
		}
	}

	/**
	 * Check whether the timed out server was the leader.
	 * 
	 * @param beat
	 * @return
	 */
	protected boolean checkLeader(HeartBeat beat) {
		if (suspects.get(beat.getAddress())) {
			if (servers.get(beat.getAddress()).isLeader()) {
				return true;
			}
		}
		return false;
	}

	private void removeFromSuspects(HeartBeat beat) {
		if (suspects.remove(beat.getAddress(), true)) {
			// Notify suspect listener
			beat.setSuspect(false);
		}
	}

	/**
	 * Returns the maximum-ranked member of the leaders
	 * 
	 * @return
	 */
	public String doElection() {
		String leader = "";
		for (String key : servers.keySet()) {
			if (!suspects.containsKey(key)) {
				if (leader.isEmpty()) {
					leader = key;
				} else {
					if (leader.compareTo(key) > 0) {
						leader = key;
					}
				}
			}
		}
		return leader;
	}

	/**
	 * Used to add votes into the count
	 * 
	 * @param vote
	 *            Server that was voted as the leader
	 */
	public void addVote(HeartBeat vote) {
		if (!votes.containsKey(vote.getAddress())) {
			votes.put(vote.getAddress(), vote);
		}
	}

	/**
	 * Used to check whether the leader is suspected
	 * 
	 * @param beat
	 * @return
	 */
	public boolean isSuspect(HeartBeat beat) {
		return suspects.containsKey(beat.getAddress());
	}

	@Override
	public HeartBeat getCurrentLeader() {
		if (servers.containsKey(CURRENT_LEADER))
			return servers.get(CURRENT_LEADER);
		return null;
	}
	public boolean isLeader(){
		return MY_NAME.equalsIgnoreCase(CURRENT_LEADER);
	}
}
