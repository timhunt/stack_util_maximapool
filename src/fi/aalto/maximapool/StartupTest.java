package fi.aalto.maximapool;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;


public class StartupTest {

	public static void main(String[] args) {
		Properties properties = new Properties();
		try {
			properties.load(new FileReader(new File("maximapool.conf")));
		} catch (IOException e) {
			e.printStackTrace();
		}

		MaximaProcessConfig processConfig = new MaximaProcessConfig();
		MaximaPoolConfig poolConfig = new MaximaPoolConfig();
		processConfig.loadProperties(properties);
		poolConfig.loadProperties(properties);

		MaximaPool pool = new MaximaPool(poolConfig, processConfig);

		MaximaProcess mp = null;

		for (int i = 0; i < 60; ++i) {
			Map<String, String> status = pool.getStatus();
			System.out.format("%4d) ", i);
			System.out.println("Starting: " + status.get("Processes starting up") +
					", Ready: " + status.get("Ready processes in the pool") +
					", In use: " + status.get("Processes in use") +
					", Total: " + status.get("Total number of processes started"));

			if (i == 10) {
				mp = pool.getProcess();
			}

			if (i == 20) {
				mp.doAndDie("1+1;", 3000);
				System.out.println(mp.getOutput());
				pool.notifyProcessFinishedWith(mp);
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}

		pool.destroy();
		
	}
}
