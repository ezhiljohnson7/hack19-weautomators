package com.conviva.automation.imageprocessing;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.xml.sax.SAXException;

import com.conviva.automation.imageprocessing.Constants.AppiumConstants;
import com.conviva.automation.imageprocessing.Constants.FolderNames;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.android.AndroidKeyCode;
import io.appium.java_client.remote.MobileCapabilityType;

public class AppUtils {

	private final static Logger LOG = Logger.getLogger(AppUtils.class);
	private String connectedAndroidDevice;
	private AndroidDriver<?> androidDriver = null;
	private Map<String, Integer> boundsMap = new LinkedHashMap<>();

	public Map<String, Integer> getBoundsMap() {
		return boundsMap;
	}

	public void setBoundsMap(Map<String, Integer> boundsMap) {
		this.boundsMap = boundsMap;
	}

	/**
	 * Installs and launches the android application specified on the
	 * Config.properties on any connected android device.
	 * 
	 * @return androidDriver
	 * @throws MalformedURLException
	 */
	public AndroidDriver<?> launchApplication() throws MalformedURLException {

		UtilsFactory.getAppiumUtils().startAppiumServer();

		this.connectedAndroidDevice = this.getConnectedDeviceName();
		DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
		desiredCapabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, AppiumConstants.AUTOMATION_NAME_UIAUTOMATOR2);
		desiredCapabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, AppiumConstants.AUTOMATION_PLATFORM_ANDROID);
		desiredCapabilities.setCapability(MobileCapabilityType.DEVICE_NAME, this.connectedAndroidDevice);
		desiredCapabilities.setCapability(MobileCapabilityType.APP, String.format("%s/%s", FolderNames.APKS, "BrightCoveOrangePlayer_2.145.2.apk"));
		desiredCapabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 1000);
		desiredCapabilities.setCapability(AppiumConstants.KEY_AUTO_GRANT_PERMISSION, Boolean.TRUE);
		desiredCapabilities.setCapability(AppiumConstants.KEY_SESSION_OVERRIDE, Boolean.TRUE);

		this.androidDriver = new AndroidDriver<AndroidElement>(new URL(UtilsFactory.getAppiumUtils().getAppiumServerUrl()), desiredCapabilities);
		return androidDriver;
	}

	/**
	 * Returns the connected deviceName by running the adb commands on the test
	 * machine.
	 * 
	 * @return deviceName, connected on test machine.
	 */
	private String getConnectedDeviceName() {

		String deviceName = null;
		boolean found = false;
		Scanner input = new Scanner(UtilsFactory.getJavaUtils().executeCommand(System.getenv("ANDROID_HOME") + "/platform-tools/adb devices"));
		input.nextLine();
		while (input.hasNextLine()) {
			found = true;
			deviceName = input.nextLine().replace("device", "").trim();
		}

		if (!found) {
			LOG.error("ERROR : NO ANDROID DEVICES CONNECTED");
		}
		input.close();

		return deviceName;
	}

	String orangePlayerButtonXpath = "//android.widget.Button[@text='Orange Player App']";
	String brightcoveVideoViewButtonXpath = "//android.widget.Button[@text='BrightcoveVideoView']";
	String idOfFirstProtocolFromList = "android:id/text1";
	String pauseButtonXpath = "//android.widget.Button[@resource-id='com.brightcove.brightcove:id/play']";
	String playerXpath = "//android.view.View";

	public void startVideo() {

		LOG.info("Navigating to the brightcove video player selection page");
		UtilsFactory.getJavaUtils().sleep(1000);
		this.androidDriver.findElementByXPath(orangePlayerButtonXpath).click();

		LOG.info("Navigating to the protocol listing page");
		UtilsFactory.getJavaUtils().sleep(1000);
		this.androidDriver.findElementByXPath(brightcoveVideoViewButtonXpath).click();

		LOG.info("Navigating to the player page");
		UtilsFactory.getJavaUtils().sleep(1000);
		this.androidDriver.findElementById(idOfFirstProtocolFromList).click();

		String xml = getPageSource();
		System.out.println(xml + "\n");
		String bounds = "";

		try {
			bounds = UtilsFactory.getJavaUtils().getAttributeFromXml(xml, "android.view.View", "bounds").replace("[", "").replaceFirst("]", ",").replace("]", "");
			boundsMap.clear();
			boundsMap.put("startX", Integer.valueOf(bounds.split(",")[0]));
			boundsMap.put("startY", Integer.valueOf(bounds.split(",")[1]));
			boundsMap.put("endX", Integer.valueOf(bounds.split(",")[2]));
			boundsMap.put("endY", Integer.valueOf(bounds.split(",")[3]));
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Pauses the video thats already playing in the appium-launched application.
	 */
	public void pauseVideo() {

	}

	/**
	 * Simulates back button on android devices.
	 */
	public void pressBackKey() {
		this.androidDriver.pressKeyCode(AndroidKeyCode.BACK);
	}

	/**
	 * Checks if an element is present in a page.
	 * 
	 * @param element
	 * @return {@link Boolean} status if the element is displayed.
	 */
	public boolean isDisplayed(AndroidElement element) {
		try {
			return element.isDisplayed();
		} catch (NoSuchElementException e) {
			return false;
		} catch (WebDriverException wde) {
			return false;
		}
	}

	/**
	 * Gives the Page source of the current page in the player.
	 * 
	 * @return the page source as string.
	 */
	public String getPageSource() {
		return androidDriver.getPageSource();
	}

	/**
	 * Checks if the video is playing or not.
	 * 
	 * @return true if video is played even for a second during the sample time and
	 *         false otherwise.
	 * @throws IOException
	 */
	public boolean isVideoPlaying() throws IOException {

		int totalSamples = 3;
		int totalSamplesWhereVideoPlayDetected = 0;

		String strImageA = "Scr1.png";
		String strImageB = "Scr2.png";

		for (int i = 0; i < totalSamples; i++) {
			// Capture a couple of screenshots from the device with 2 second interval.
			captureScreenshot(strImageA);
			UtilsFactory.getJavaUtils().sleep(2000);
			captureScreenshot(strImageB);

			// Process the imageA and crop only the video player based the the player
			// dimension read from the application xml
			BufferedImage imageA = ImageIO.read(new File(FolderNames.IMAGES, strImageA));
			BufferedImage croppedImageA = UtilsFactory.getImageUtils().cropImage(imageA, boundsMap.get("startX"), boundsMap.get("startY"), boundsMap.get("endX") - boundsMap.get("startX"),
					boundsMap.get("endY") - boundsMap.get("startY"));

			// Process the imageB and crop only the video player based the the player
			// dimension read from the application xml
			BufferedImage imageB = ImageIO.read(new File(FolderNames.IMAGES, strImageB));
			BufferedImage croppedImageB = UtilsFactory.getImageUtils().cropImage(imageB, boundsMap.get("startX"), boundsMap.get("startY"), boundsMap.get("endX") - boundsMap.get("startX"),
					boundsMap.get("endY") - boundsMap.get("startY"));

			// Creating a file copy of the images cropped for debugging purposes.
			UtilsFactory.getImageUtils().createImage(croppedImageA);
			UtilsFactory.getImageUtils().createImage(croppedImageB);

			// Comparing the cropped images to see if they're distinc
			if (!(UtilsFactory.getImageUtils().compareImage(croppedImageA, croppedImageB))) {
				totalSamplesWhereVideoPlayDetected++;
			}
		}

		return totalSamplesWhereVideoPlayDetected >= 1 ? true : false;
	}

	public void captureScreenshot(String fileNameToSave) {

		File file = ((TakesScreenshot) androidDriver).getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(file, new File(FolderNames.IMAGES, fileNameToSave));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
