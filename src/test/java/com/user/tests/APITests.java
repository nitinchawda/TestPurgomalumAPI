package com.user.tests;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.read.ReadResponse;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class APITests {
	ExtentReports report = new ExtentReports(
			"./automationreport.html", true);
	ExtentTest test;
	private static String SERVICE_URL = "https://www.purgomalum.com/service/";
	private URL url;
	public HttpURLConnection ConnectionDetails(String newurl) throws FileNotFoundException, IOException {
		url = new URL(newurl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		return conn;
	}

	@DataProvider(name = "DifferentData")
	public static Object[][] responseText() {
		return new Object[][] { { "This is xml text", "xml" },
			{ "This is plain text", "plain" },
			{ "This is json text", "json"} };
	}

	@DataProvider(name = "ProfanityList")
	public static Object[][] profanityText() {
		return new Object[][] { { "This is plain text" },
			{ "This is Shit text"},
			{ "Ass Goddamn Goddamnit"}, //words from profanity list
			{ "Welcome to Dog! Lol"}};
	}

	@DataProvider(name = "AddProfanityList")
	public static Object[][] profanityList() {
		return new Object[][] { { "hello", "chu,chi,chn,chj,chk,chm,chb,chf,chg,cht" }, //add 10 words to profanity lists
			{ "New", "chu,chi,chn,chj,chk,chm,chb,chf,chg,cht,chr,chs,che,chd,cha,chz"}}; //add more than 10 words to profanity lists
	}

	@DataProvider(name = "FillChars")
	public static Object[][] fill_charText() {
		return new Object[][] { { "This is xml text", "xml", "xml", "*" },
			{ "This is plain text", "plain", "plain", "_" },
			{ "This is json text", "json", "json", "~"},
			{ "This is plain text", "plain", "plain", "^"}, //fill invalid characters
			{ "This is json text", "json", "is, json", "*"},
			{ "This is profanity text", "plain", "profanity", "smartass" }}; //fill with profanity word
	}

	@DataProvider(name = "FillWords")
	public static Object[][] fill_StringText() {
		return new Object[][] { { "This is xml text", "xml", "xml", "Op1" },
			{ "This is plain text", "plain", "plain", "Op2" },
			{ "This is json text", "json", "json", "Op3"}};
	}

	@DataProvider(name = "ErrorHandling")
	public static Object[][] ErrorHandling_text() {
		return new Object[][] { { "This is plain text", "plain", "Optional plain text with large content and check for errors" }								};
	}

	@Test(dataProvider = "DifferentData")
	public void testgetresponse_for_simpleplaintext_withdifferent_texttypes(String giventxt, String type){
		test = report.startTest("Test Multiple Text with different types");
		String request_text = giventxt.replace(" ", "%20");
		try {
			HttpURLConnection connection = ConnectionDetails(SERVICE_URL + type + "?text=" + request_text);
			connection.setRequestMethod("GET");
			ReadResponse r = new ReadResponse();
			String output = r.getResponse_Message(connection, connection.getContentType());
			Assert.assertTrue(output.contains(giventxt));
			test.log(LogStatus.PASS, "Test Multiple Text with different types Passed '" + giventxt + "' of type '" + type + "'");
		}catch(Exception e)
		{
			test.log(LogStatus.FATAL, e.getMessage());	
			test.log(LogStatus.FAIL, "Test Profanity Failed" );
		} 
	}

	@Test(dataProvider = "ProfanityList")
	public void testprofanity_for_plaintext(String giventxt){
		test = report.startTest("Test Profanity status");
		String request_text = giventxt.replace(" ", "%20");
		try {
			HttpURLConnection connection = ConnectionDetails(SERVICE_URL + "containsprofanity?text=" + request_text);
			connection.setRequestMethod("GET");
			ReadResponse r = new ReadResponse();
			String output = r.getResponse_Message(connection, connection.getContentType());
			if(output.equals("false"))
				test.log(LogStatus.PASS, "Test Profanity status Passed '" + giventxt + "' has no text from Profanity List");
			if(output.equals("true"))
				test.log(LogStatus.WARNING, "Test Profanity status Passed '" + giventxt + "' has text from Profanity List");
		}catch(Exception e)
		{
			test.log(LogStatus.FATAL, e.getMessage());	
			test.log(LogStatus.FAIL, "Test Profanity Failed" );
		}
	}

	@Test(dataProvider = "FillChars")
	public void test_fill_char_different_types(String giventxt, String type, String replacefrom, String replaceto){
		test = report.startTest("Test Fill Characters");
		String request_text = giventxt.replace(" ", "%20");
		try {
			HttpURLConnection connection = ConnectionDetails(SERVICE_URL + type + "?text=" + request_text + "&add=" + replacefrom + "&fill_char=" + replaceto );
			connection.setRequestMethod("GET");
			ReadResponse r = new ReadResponse();
			String output = r.getResponse_Message(connection, connection.getContentType());
			Assert.assertTrue(output.contains(replaceto));
			test.log(LogStatus.PASS, "Test Fill Characters '" + giventxt + "' has text from '[" + replaceto + "]'");
		}catch(Exception e)
		{
			test.log(LogStatus.FATAL, e.getMessage());	
			test.log(LogStatus.FAIL, "Test Fill Characters Failed '"+ giventxt + "' does not has text from '[" + replaceto + "]'" );
		}
	}

	@Test(dataProvider = "FillWords")
	public void test_fill_word_different_types(String giventxt, String type, String replacefrom, String replaceto){
		test = report.startTest("Test Fill Words");
		String request_text = giventxt.replace(" ", "%20");
		try {
			HttpURLConnection connection = ConnectionDetails(SERVICE_URL + type + "?text=" + request_text + "&add=" + replacefrom + "&fill_text=" + replaceto );
			connection.setRequestMethod("GET");
			ReadResponse r = new ReadResponse();
			String output = r.getResponse_Message(connection, connection.getContentType());
			Assert.assertTrue(output.contains(replaceto));
			test.log(LogStatus.PASS, "Test Fill Characters '" + giventxt + "' has text from '[" + replaceto + "]'");
		}catch(Exception e)
		{
			test.log(LogStatus.FATAL, e.getMessage());	
			test.log(LogStatus.FAIL, "Test Fill Characters Failed '"+ giventxt + "' does not has text from '[" + replaceto + "]'" );
		}
	}

	@Test(dataProvider = "ErrorHandling")
	public void test_error_handling(String giventxt, String type, String replaceto){
		test = report.startTest("Test Error Handling");
		String request_text = giventxt.replace(" ", "%20");
		try {
			HttpURLConnection connection = ConnectionDetails(SERVICE_URL + type + "?text=" + request_text + "&fill_text=" + replaceto );
			connection.setRequestMethod("GET");
			ReadResponse r = new ReadResponse();
			String output = r.getResponse_Message(connection, connection.getContentType());
			Assert.assertTrue(output.contains("User Replacement Text Exceeds Limit of 20 Characters."));
			test.log(LogStatus.PASS, "Test Fill Characters '" + giventxt + "' has text from '[" + replaceto + "]'");
		}catch(Exception e)
		{
			test.log(LogStatus.FATAL, e.getMessage());	
			test.log(LogStatus.FAIL, "Test Fill Characters Failed '"+ giventxt + "' does not has text from '[" + replaceto + "]'" );
		}
	}

	@Test(dataProvider = "AddProfanityList")
	public void testadd_profanity_list(String giventxt, String profanitylist){
		test = report.startTest("Test Add Profanity status");
		try {
			HttpURLConnection connection = ConnectionDetails(SERVICE_URL + "plain?text=" + giventxt + "&add=" + profanitylist);
			connection.setRequestMethod("GET");
			ReadResponse r = new ReadResponse();
			String output = r.getResponse_Message(connection, connection.getContentType());
			if(output.equals(giventxt))
				test.log(LogStatus.PASS, "Test Add Profanity status Passed '" + profanitylist + "' has been added");
			else
				test.log(LogStatus.WARNING, "Test Add Profanity status Passed '" + profanitylist + "' has not been added");
		}catch(Exception e)
		{
			test.log(LogStatus.FATAL, e.getMessage());	
			test.log(LogStatus.FAIL, "Test Profanity Failed" );
		}
	}

	
	
	@AfterTest
	public void teardown() {
		report.endTest(test);
		report.flush();
	}
}
