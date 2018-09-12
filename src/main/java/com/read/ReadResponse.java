package com.read;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.util.Optional;
import java.util.Scanner;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.json.JSONObject;

public class ReadResponse {
	JSONObject jsonobj;

	public String getResponse_Message(HttpURLConnection conn, String responsetype)
			throws Exception {

		String entireResponse = new String();
		if (conn.getResponseCode() != 200) {
			throw new RuntimeException(" HTTP error code : " + conn.getResponseCode());
		}
		if(responsetype.equals("text/plain")) {
			Scanner scan = new Scanner(conn.getURL().openStream());
			while (scan.hasNext())
				entireResponse += scan.nextLine();
		}
		if(responsetype.equals("application/json")) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder result = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null)
				result.append(line);
			JSONObject obj = new JSONObject(result.toString());
			entireResponse = obj.getString("result");
		}
		if(responsetype.equals("application/xml")) {
			Document doc = parseXML(conn.getInputStream());
			NodeList descNodes = doc.getElementsByTagName("result");
			for(int i=0; i<descNodes.getLength();i++)
			{
				entireResponse = descNodes.item(i).getTextContent();
			}
		}	
		return entireResponse;
	}

	private Document parseXML(InputStream stream)
			throws Exception
	{
		DocumentBuilderFactory objDocumentBuilderFactory = null;
		DocumentBuilder objDocumentBuilder = null;
		Document doc = null;
		try
		{
			objDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
			objDocumentBuilder = objDocumentBuilderFactory.newDocumentBuilder();

			doc = objDocumentBuilder.parse(stream);
		}
		catch(Exception ex)
		{
			throw ex;
		}       

		return doc;
	}
}