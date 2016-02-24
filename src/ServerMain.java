import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import detector.PerfectFailureDetector;
import beat.HeartBeat;

public class ServerMain extends JFrame {
	/**
	 * Serial class ID
	 */
	private static final long serialVersionUID = 89966441L;
	JButton start;
	JButton stop;
	JButton exit;
	JList<HeartBeat> serverList;
	DefaultListModel<HeartBeat> model;
	ConcurrentMap<String, HeartBeat> currentServers;
	JPanel left;
	JPanel center;
	JLabel serverName;
	String name;
	PerfectFailureDetector detector;
	final ScheduledExecutorService executor;

	public ServerMain(String name) throws IOException {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(450, 500);
		getContentPane().setLayout(new BorderLayout());
		this.name = name;
		currentServers = new ConcurrentHashMap<String, HeartBeat>();
		detector = new PerfectFailureDetector(currentServers, this.name);
		model = new DefaultListModel<HeartBeat>();
		executor = Executors.newScheduledThreadPool(10);
		Thread updator = new Thread(new Runnable() {
			@Override
			public void run() {
				model.clear();
				for (String key : currentServers.keySet()) {
					model.addElement(currentServers.get(key));
					//System.out.println("SERVER: "+key);
				}
			}

		});
		executor.scheduleWithFixedDelay(updator, 0, 5, TimeUnit.SECONDS);
		initialize();
	}

	private void initialize() {
		left = new JPanel();
		left.setLayout(new GridLayout(0, 1, 2, 3));
		start = new JButton("Start server");
		stop = new JButton("Stop server");
		exit = new JButton("Quit");
		left.add(start);
		left.add(stop);
		left.add(exit);
		getContentPane().add(left, BorderLayout.WEST);
		center = new JPanel();
		center.setLayout(new GridLayout(3, 1, 5, 5));
		serverName = new JLabel(name);
		center.add(serverName);
		getContentPane().add(center, BorderLayout.CENTER);
		serverList = new JList<HeartBeat>();
		serverList.setModel(model);
		center.add(serverList);
	}

	public static void main(String[] args) {
		try {
			String value = JOptionPane.showInputDialog("Enter server name");
			new ServerMain(value).setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
