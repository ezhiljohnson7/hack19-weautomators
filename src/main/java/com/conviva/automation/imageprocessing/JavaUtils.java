package com.conviva.automation.imageprocessing;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class JavaUtils {
	
	private final static Logger LOG = Logger.getLogger(JavaUtils.class);
	
	/**
	 * Executes the command on terminal.
	 * 
	 * @param command
	 * 
	 * @return output for the executed command.
	 * 
	 */
	public String executeCommand(String command) {

		String line = "";
		String output = "";
		try {
			Process process = Runtime.getRuntime().exec(command);
			process.waitFor();
			BufferedReader buffer = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((line = buffer.readLine()) != null) {
				if (output.isEmpty()) {
					output = line;
				} else {
					output = output + "\n" + line;
				}
			}
			buffer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		LOG.debug(String.format("Output on executing the command %s is \n%s", command, output));
		return output;
	}

	/**
	 * Checks if a port is in use on a certain host.
	 * 
	 * @param hostName
	 * @param portNumber
	 * @return status whether the port is in use.
	 */
	public boolean isPortInUse(String hostName, int portNumber) {
		boolean result;
		try {
			Socket s = new Socket(hostName, portNumber);
			s.close();
			LOG.debug("Port is already used by another application");
			result = true;
		} catch (Exception e) {
			result = false;
		}
		return result;
	}
	
	/**
	 * Sleeps the thread for specified time.
	 * 
	 * @param timeInMillis
	 */
	public void sleep(long timeInMillis) {

		try {
			Thread.sleep(timeInMillis);
		} catch (InterruptedException e) {
			e.printStackTrace();
			LOG.error("ERROR : Could not execute sleep on this thread");
		}
	}
	
	public String getAttributeFromXml(String xml, String tagToCheck, String attributeKeyToGet) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		InputStream inStream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
		Document doc = docBuilder.parse(inStream);

		NodeList textViewNodes = doc.getElementsByTagName(tagToCheck);
		
		if(textViewNodes.getLength() > 0) {
			Node nNode = textViewNodes.item(0);
			Element eElement = (Element) nNode;
			return eElement.getAttribute(attributeKeyToGet);
		} else {
			return "";
		}		
	}

}
