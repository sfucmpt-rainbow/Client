package rainbow.client.application;

import rainbow.client.worker.Worker;
import rainbow.client.factory.WorkerFactory;
import rainbowpc.RainbowException;
import rainbowpc.node.*;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.Executor;

public class Node {
	private static boolean hasValidArguments(String[] args) {
		return args.length == 1;
	}
	
	private static NodeProtocol connectToController(String host) {
		try {
			return new NodeProtocol(host);
		}
		catch (IOException e) {
		}
		catch (RainbowException e) {
		}
		return null;
	}

	public static void main(String[] args) {
		if (!hasValidArguments(args)) {
			System.out.println("Client takes the controller host as its only argument");
			System.exit(1);
		}
		NodeProtocol protocol = connectToController(args[0]);
		if (protocol == null) {
			System.err.println("Failed to connect to host " + args[0]);
			System.exit(1);
		}
		Executor protocolExecutor = Executors.newSingleThreadExecutor();
		protocolExecutor.execute(protocol);
		protocol.setInterruptThread(Thread.currentThread());
	
		int cores = Runtime.getRuntime().availableProcessors();
		Thread[] workers = new Thread[cores];
		for (int i = 0; i < cores; i++) {
			Worker worker = WorkerFactory.getDefaultWorker(i, protocol);
			workers[i] = new Thread(worker);
			workers[i].start();
		}

		try {
			for (int i = 0; i < cores; i++) {
				workers[i].join();
			}
		} catch (InterruptedException e) {
			try {
				protocol.shutdown();
			} catch (Exception dont_care) {}
		}

		System.exit(0);
	}
}
