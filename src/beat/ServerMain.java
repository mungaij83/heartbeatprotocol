package beat;

import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerMain {
	public static void main(String[] args) {
		Properties prop=System.getProperties();
		for(Object key:prop.keySet()){	
			System.out.println(key+" ->: "+prop.getProperty(key.toString()));
		}
		/**
		try {
			int DELAY = 2;
			int START = 2;
			HeartBeatServer server1 = new HeartBeatServer("Server1",false);
			HeartBeatServer server2 = new HeartBeatServer("Server2",false);
			HeartBeatServer server3 = new HeartBeatServer("Server3",false);
			HeartBeatServer server4 = new HeartBeatServer("Server4",false);
			ScheduledExecutorService pool = Executors.newScheduledThreadPool(6);
			pool.scheduleWithFixedDelay(server1, DELAY, START, TimeUnit.SECONDS);
			pool.scheduleWithFixedDelay(server2, START, START, TimeUnit.SECONDS);
			pool.scheduleWithFixedDelay(server3, START, START, TimeUnit.SECONDS);
			pool.scheduleWithFixedDelay(server4, START, START, TimeUnit.SECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
	}
}
