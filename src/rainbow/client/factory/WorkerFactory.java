package rainbow.client.factory;

import rainbow.client.events.Event;
import rainbow.client.worker.Worker;
import rainbow.client.tasks.*;
import rainbowpc.node.NodeProtocol;
import rainbowpc.node.messages.*;
import rainbowpc.Message;
import java.util.TreeMap;

public class WorkerFactory {
	public static Worker getDefaultWorker(int id, NodeProtocol agent) {
		final Worker worker = new Worker(id, agent);

		TreeMap<String, Event> eventMapping = new TreeMap<String, Event>();
		eventMapping.put(WorkMessage.LABEL, new Event() {
			public void action(Message msg) {
				WorkMessage details = (WorkMessage)msg;
				details.markUnfound();
				worker.log("Brute forcing...");
				worker.sendMessage(Md5Task.md5BruteForce(Worker.TEST_ALPHABET, details));
			}
		});

		worker.setEventMapping(eventMapping);		
		return worker;
	}
}
