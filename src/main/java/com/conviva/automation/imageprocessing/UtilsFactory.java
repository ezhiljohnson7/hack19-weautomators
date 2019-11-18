package com.conviva.automation.imageprocessing;

/**
 * Utility factory class for easy object creation of all the available utility classes.
 * 
 * @author ezjohnson
 *
 */
public class UtilsFactory {
	
	private static ImageUtils imageUtils;
	private static AppUtils appUtils;
	private static JavaUtils javaUtils;
	private static AppiumUtils appiumUtils;

	/**
	 * @return {@link ImageUtils} instance to call any api utility available in it.
	 */
	public static ImageUtils getImageUtils() {

		if (imageUtils == null) {
			imageUtils = new ImageUtils();
		}
		return imageUtils;
	}
	
	/**
	 * @return {@link AppUtils} instance to call any api utility available in it.
	 */
	public static AppUtils getWebPlayerUtils() {

		if (appUtils == null) {
			appUtils = new AppUtils();
		}
		return appUtils;
	}
	
	/**
	 * @return {@link JavaUtils} instance to call any api utility available in it.
	 */
	public static JavaUtils getJavaUtils() {

		if (javaUtils == null) {
			javaUtils = new JavaUtils();
		}
		return javaUtils;
	}
	
	/**
	 * @return {@link AppUtils} instance to call any api utility available in it.
	 */
	public static AppUtils getAppUtils() {

		if (appUtils == null) {
			appUtils = new AppUtils();
		}
		return appUtils;
	}
	
	/**
	 * @return {@link AppiumUtils} instance to call any api utility available in it.
	 */
	public static AppiumUtils getAppiumUtils() {

		if (appiumUtils == null) {
			appiumUtils = new AppiumUtils();
		}
		return appiumUtils;
	}
	
}
