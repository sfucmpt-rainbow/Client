package rainbow.client.worker;

import rainbowpc.node.NodeProtocol;
import rainbowpc.Message;
import rainbowpc.RainbowException;
import rainbow.client.events.Event;
import java.util.TreeMap;
import java.io.IOException;

public class Worker extends Thread {
	public static final String TEST_ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	
	public TreeMap<String, Event> eventMapping;
	private int id;
	private NodeProtocol agent;

	public Worker(int id, NodeProtocol agent) {
		this.id = id;
		this.agent = agent;
		log("Worker spawned!");
	}

	public void run() {
		log("Entering main execution loop...");
		while (true) {
			try {
				Message message = agent.getMessage();
				Event event = eventMapping.get(message.getMethod());
				if (event != null) {
					event.run(message);
				} else {
					warn(message.getMethod() + " event not recognized");
				}
				
			} catch (InterruptedException e) {
				break;
			}
		}
	}

	public void sendMessage(Message msg) {
		try {
			agent.sendMessage(msg);
		} catch (IOException e) {
			agent.shutdown();
			warn("Message sending failed, aborting!");
		}
	}

	public void setEventMapping(TreeMap<String, Event> eventMapping) {
		this.eventMapping = eventMapping;
	}

	public void log(String msg) {
		System.out.println("[====== Worker{" + 
							id +
							"} ======]: " +
							msg
		);
	}

	public void warn(String msg) {
		System.out.println("[****** Worker{" + 
							id +
							"} ******]: " +
							msg
		);
	}
}
