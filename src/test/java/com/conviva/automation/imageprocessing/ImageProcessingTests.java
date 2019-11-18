package com.conviva.automation.imageprocessing;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;


public class ImageProcessingTests {
	
	private final static Logger LOG = Logger.getLogger(ImageProcessingTests.class);
	
	@Test
	public void testRunner() throws IOException {
		
		LOG.info("Launching the player application");
		UtilsFactory.getAppUtils().launchApplication();
		
		LOG.info("Starting the video player");
		UtilsFactory.getAppUtils().startVideo();
		
		LOG.info("Checking if the video is playing in the player");
		boolean status = UtilsFactory.getAppUtils().isVideoPlaying();
		Assert.assertTrue(status, "The video is playing as expected.");
		
		LOG.info("Pausing the video");
		UtilsFactory.getAppUtils().pauseVideo();
		
		LOG.info("Checking if the video is playing in the player");
		status = UtilsFactory.getAppUtils().isVideoPlaying();
		Assert.assertFalse(status, "The video is paused as expected.");
	}

}
