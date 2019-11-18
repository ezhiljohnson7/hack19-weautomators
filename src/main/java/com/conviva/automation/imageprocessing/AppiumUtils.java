package com.conviva.automation.imageprocessing;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.testng.Assert;

public class AppiumUtils {

	private final static Logger LOG = Logger.getLogger(AppiumUtils.class);

	private int[] ports = new int[] { 4721, 4722, 4723, 4724, 4725, 4726, 4727, 4728, 4729, 4730 };
	private String hostName = "127.0.0.1";
	private String nodePath = System.getenv("NODE");
	private String appiumMainJS = System.getenv("APPIUM_PATH");
	private Process process;
	private String appiumServerPort;
	private String appiumServerUrl;

	public AppiumUtils() {
	}

	/**
	 * Starts the appium server.
	 * 
	 * @return {@link String}, socket at which appium server is running.
	 * @throws Exception
	 */
	public void startAppiumServer() {

		LOG.info("Starting Appium Server");
		String command = "";
		int availablePort = 0;

		if (this.appiumMainJS == null || this.nodePath == null) {
			try {
				throw new Exception("Error : Environment varaiable not found for appium_path or node");
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail("Error : Please set the environment variables for appium_path or node or verify if these environmental variables are accessible via eclipse");
			}
		}

		for (int i = 0; i < this.ports.length; i++) {
			if (!UtilsFactory.getJavaUtils().isPortInUse(this.hostName, this.ports[i])) {
				availablePort = this.ports[i];
				this.appiumServerPort = String.valueOf(availablePort);
				LOG.debug("Available port : " + availablePort);
				break;
			}
		}
		if (availablePort == 0) {
			try {
				throw new Exception("Error : Ports are not available");
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail("Error : Kill any port between mentioned in the ports integer array");
			}
		} else {

			command = String.format("%s %s -a %s -p %d --no-reset --log %s/appium.log --log-level debug --session-override", this.nodePath, this.appiumMainJS, this.hostName, availablePort,
					System.getProperty("user.dir"));
			LOG.debug("Command to start appium server \n" + command);
		}

		try {
			this.process = Runtime.getRuntime().exec(command);
		} catch (IOException e) {

			e.printStackTrace();
		}

		LOG.debug("Wait for appium server to start - ideally it should not take more than 10 seconds");
		while (!UtilsFactory.getJavaUtils().isPortInUse(hostName, availablePort)) {
			LOG.debug("Appium is not started. waiting 1 second.");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		this.appiumServerUrl = String.format("http://%s:%s/wd/hub", hostName, availablePort);
		LOG.info("Appium server started on : " + this.appiumServerUrl);
	}

	/**
	 * Returns the appium server url.
	 * 
	 * @return appiumServerUrl.
	 */
	public String getAppiumServerUrl() {

		if (this.appiumServerUrl != null) {
			return this.appiumServerUrl;
		} else {
			LOG.error("Error : appium server is not started");
			throw new NullPointerException("appiumServerUrl is Null, as appium server is not started.");
		}
	}

}
