package com.conviva.automation.imageprocessing;

public class Constants {
	
	public static final class GenericConstants {

		public static final String ROOT;
		public static final String HOME;

		static {
			ROOT = System.getProperty("user.dir");
			HOME = System.getProperty("user.home");
		}

		private GenericConstants() {
		}
		
	}
	
	public static final class FolderNames {

		private FolderNames() {
		}

		public static final String BASE_DIR = GenericConstants.ROOT + "/src/test/resources/";

		public static final String CONFIGS = BASE_DIR + "configs";
		public static final String APKS = BASE_DIR + "apks";
		public static final String IMAGES = BASE_DIR + "images";
		public static final String APPIUM = BASE_DIR + "appium";
	}
	
	public static final class AppiumConstants {

		private AppiumConstants() {
		}

		public static final String AUTOMATION_NAME_UIAUTOMATOR2 = "uiautomator2";
		public static final String AUTOMATION_PLATFORM_ANDROID = "android";
		public static final String KEY_AUTO_GRANT_PERMISSION = "autoGrantPermissions";
		public static final String KEY_SESSION_OVERRIDE = "session-override";
		public static final String KEY_LOG = "log";
		public static final String KEY_LOG_LEVEL = "log-level";
	}

}
