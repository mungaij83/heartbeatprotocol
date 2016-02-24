package beat;


import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.AbstractListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.ListModel;

public class ClientMain {
	public static void main(String []args){
		JFrame frame=new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());
		JList<String> list=new JList<>();
		frame.getContentPane().add(list);
	try{
		ScheduledExecutorService service=Executors.newScheduledThreadPool(5);
		Map<String,HeartBeat> servers=new HashMap<String, HeartBeat>();
		List<String> s=new ArrayList<>();
		ListModel<String> model=new AbstractListModel<String>() {

			@Override
			public String getElementAt(int index) {
				// TODO Auto-generated method stub
				return s.get(index);
			}

			@Override
			public int getSize() {
				// TODO Auto-generated method stub
				return s.size();
			}
			
		};
		list.setModel(model);
		
		Thread t=new Thread(new Runnable(){
			@Override
			public void run() {
				s.clear();
				for(String key:servers.keySet()){
					s.add(servers.get(key).getName());
				}
			}
			
		});
		service.scheduleWithFixedDelay(t, 5,5, TimeUnit.SECONDS);
		frame.setVisible(true);
		System.out.println("Client started");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
