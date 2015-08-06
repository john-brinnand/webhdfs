package datavalidator.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

@Slf4j
@ContextConfiguration(classes = { WebHdfsTest.class, WebHdfs.class})
@EnableConfigurationProperties ({ WebHdfsConfiguration.class })
public class WebHdfsTest extends AbstractTestNGSpringContextTests{
	@Autowired WebHdfsConfiguration webHdfsConfig;
	@Autowired WebHdfs webHdfs;
	private URI uri;

	@BeforeTest
	public void beforeTest() { }
	@PostConstruct
	public void postConstruct() throws URISyntaxException {
		uri = new URIBuilder()
			.setScheme(webHdfsConfig.getScheme())
			.setHost(webHdfsConfig.getHost())
			.setPort(webHdfsConfig.getPort())
			.setPath(webHdfsConfig.getWEBHDFS_PREFIX()
				+ webHdfsConfig.getPath() + "/" 
				+ webHdfsConfig.getFileName())
			.setParameter("overwrite", "true")
			.setParameter("op", "CREATE")
			.setParameter("user", "spongecell")
			.build();
	}

	@Test
	public void validateCloseableHttpClient() throws URISyntaxException, UnsupportedEncodingException {
		//*************************************************
		StringEntity entity = new StringEntity("Greetings earthling!\n");
		HttpPut put = new HttpPut(uri);
		log.info (put.getURI().toString());
		put.setEntity(entity);
		log.info (put.getEntity().toString());

		CloseableHttpClient httpClient = HttpClients.createDefault(); 

		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(put);
			Assert.assertNotNull(response);
			log.info("Response status code {} ", response.getStatusLine().getStatusCode());
			Assert.assertEquals(307, response.getStatusLine().getStatusCode());
		} catch (IOException e) {
			throw new WebHdfsException("ERROR - failure to get redirect URL: " + uri.toString(), e);
		}
		//*************************************************
		Header[] header = response.getHeaders("Location");
		Assert.assertNotNull(header);
		log.info(header[0].toString());
		String redirectUrl = header[0].toString().substring("Location:0".length());

		URI uri = new URIBuilder(redirectUrl)
			.setParameter("user", "spongecell")
			.build();

		HttpPut httpPut = new HttpPut(uri);
		httpPut.setEntity(entity);

		try {
			response = httpClient.execute(httpPut);
			httpClient.close();
			response.close();
		} catch (IOException e) {
			throw new WebHdfsException("ERROR - failure to write data to " + uri.toString() + " Exception is: ", e);
		}
		log.info("Response status code {} ", response.getStatusLine().getStatusCode());
	}
}
