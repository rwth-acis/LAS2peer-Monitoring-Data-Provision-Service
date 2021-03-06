package i5.las2peer.services.monitoring.provision;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import i5.las2peer.api.p2p.ServiceNameVersion;
import i5.las2peer.connectors.webConnector.WebConnector;
import i5.las2peer.connectors.webConnector.client.ClientResponse;
import i5.las2peer.connectors.webConnector.client.MiniClient;
import i5.las2peer.p2p.PastryNodeImpl;
import i5.las2peer.security.ServiceAgentImpl;
import i5.las2peer.security.UserAgentImpl;
import i5.las2peer.services.mobsos.successModeling.MonitoringDataProvisionService;
import i5.las2peer.testing.MockAgentFactory;
import i5.las2peer.testing.TestSuite;

/**
 *
 * Tests for the Monitoring Data Provision Service. Mostly just prints out results of method invocations, since it is
 * not predictable which data is stored at the time these tests are run.
 *
 * @author Peter de Lange
 *
 */
public class MonitoringDataProvisionServiceTest {

	private static final int HTTP_PORT = WebConnector.DEFAULT_HTTP_PORT;

	private PastryNodeImpl node;
	private static WebConnector connector;
	private static MiniClient c1;
	private static UserAgentImpl user1;
	private ByteArrayOutputStream logStream;

	private static final String adamsPass = "adamspass";
	private static final ServiceNameVersion testServiceClass = new ServiceNameVersion(
		MonitoringDataProvisionService.class.getCanonicalName(), "0.8.0");

	@Before
	public void startServer() throws Exception {
		// start Node
		node = TestSuite.launchNetwork(1).get(0);
		user1 = MockAgentFactory.getAdam();
		user1.unlock(adamsPass);
		node.storeAgent(user1);

		ServiceAgentImpl testService = ServiceAgentImpl.createServiceAgent(testServiceClass, "a pass");
		testService.unlock("a pass");

		node.registerReceiver(testService);

		// start connector
		logStream = new ByteArrayOutputStream();
		connector = new WebConnector(true, HTTP_PORT, false, 1000);
		connector.setLogStream(new PrintStream(logStream));
		connector.start(node);
		Thread.sleep(1000); // wait a second for the connector to become ready

		c1 = new MiniClient();
		c1.setConnectorEndpoint(connector.getHttpEndpoint());
		c1.setLogin(user1.getIdentifier(), "adamspass");
	}

	@After
	public void shutDownServer() throws Exception {
		if (connector != null) {
			connector.stop();
		}
		if (node != null) {
			node.shutDown();
		}

		connector = null;
		node = null;

		System.out.println("Connector-Log:");
		System.out.println("--------------");

		if (logStream != null) {
			System.out.println(logStream.toString());
		}
	}

	@Test
	public void testGetNames() {

		try {
			// Login
			ClientResponse result = c1.sendRequest("GET",
				"mobsos-success-modeling/measures?catalog=myGroupID/measure_catalog-mysql.xml&update=true",
				"", "*/*", "application/json", new HashMap<>());

			Assert.assertEquals(200, result.getHttpCode());
			JSONParser parser = new JSONParser();
			JSONArray resultObject = (JSONArray) parser.parse(result.getResponse());
			for (Object measureName : resultObject)
				System.out.println("Result of asking for all measures: " + (String) measureName);
			ClientResponse result2 = c1.sendRequest("GET", "mobsos-success-modeling/nodes", "", "*/*",
				"application/json", new HashMap<>());
			Assert.assertEquals(200, result2.getHttpCode());
			JSONObject resultObject2 = (JSONObject) parser.parse(result2.getResponse());

			for (Object node : resultObject2.keySet())
				System.out.println("Result of asking for all nodes: " + (String) node);

			ClientResponse result3 = c1.sendRequest("GET", "mobsos-success-modeling/services", "", "*/*",
				"application/json", new HashMap<String, String>());
			Assert.assertTrue(result3.getHttpCode() == 200);
			JSONObject resultObject3 = (JSONObject) parser.parse(result3.getResponse());
			for (Object service : resultObject3.keySet())
				System.out.println("Result of asking for all monitored service names: " + (String) service);

			if (resultObject3.size() != 0) {
				String serviceName = (String) resultObject3.get(0);
				System.out.println("Calling getModels with service: " + serviceName);
				String path = "mobsos-success-modeling/models?service=" + serviceName
					+ "&update=true&catalog=myGroupID/measure_catalog-mysql.xml";
				ClientResponse result4 = c1.sendRequest("GET",
					path,
					"", "*/*", "application/json", new HashMap<>());

				Assert.assertEquals(200, result4.getHttpCode());
				JSONArray resultObject4 = (JSONArray) parser.parse(result4.getResponse());
				for (Object service : resultObject4)
					System.out.println("Result of asking for all models: " + (String) service);
			} else
				System.out.println("Result of asking for all monitored service names: none!");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception: " + e);
		}
	}

	@Test
	public void testGetMeasuresAndModels() {
		try {
			ClientResponse result = c1.sendRequest("GET", "mobsos-success-modeling/nodes", "", "*/*",
				"application/json", new HashMap<String, String>());
			Assert.assertTrue(result.getHttpCode() == 200);
			JSONParser parser = new JSONParser();
			JSONObject resultObject = (JSONObject) parser.parse(result.getResponse());
			if (resultObject.size() != 0) {
				String knownNode = (String) resultObject.keySet().toArray()[0];
				// String location = (String) resultObject.get(knownNode);

				System.out.println("Calling Node Success Model with node " + knownNode);
				String params = "{\"nodeName\":\"" + knownNode + "\"," + "\"updateMeasures\":\"true\","
					+ "\"updateModels\":\"true\"," + "\"catalog\":\"myGroupID/measure_catalog-mysql.xml\"}";
				ClientResponse result2 = c1.sendRequest("POST", "mobsos-success-modeling/visualize/nodeSuccessModel",
					params, "application/json", "text/html", new HashMap<String, String>());
				Assert.assertTrue(result2.getHttpCode() == 200);
				System.out.println("Visualizing Node Success Model Result:\n" + result2.getResponse());
			} else
				System.out.println("No monitored nodes, no node success model visualization possible!");

			String params = "{\"modelName\":\"Chat Service Success Model\"," + "\"updateMeasures\":\"true\","
				+ "\"updateModels\":\"true\"," + "\"catalog\":\"measure_catalog-mysql.xml\"}";
			ClientResponse result3 = c1.sendRequest("POST", "mobsos-success-modeling/visualize/serviceSuccessModel",
				params, "application/json", "text/html", new HashMap<String, String>());
			Assert.assertTrue(result3.getHttpCode() == 200);
			System.out.println("Visualizing Chat Service Success Model Result:\n" + result3.getResponse());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception: " + e);
		}
	}

}
