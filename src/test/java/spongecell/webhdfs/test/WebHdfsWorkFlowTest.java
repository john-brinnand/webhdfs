package spongecell.webhdfs.test;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;

import lombok.extern.slf4j.Slf4j;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import spongecell.webhdfs.FilePath;
import spongecell.webhdfs.WebHdfsOps;
import spongecell.webhdfs.WebHdfsWorkFlow;

@Slf4j
@ContextConfiguration(classes = { WebHdfsWorkFlowTest.class, WebHdfsWorkFlow.Builder.class})
@EnableConfigurationProperties ({ 
	ConfigurationRepository.class 
})
public class WebHdfsWorkFlowTest extends AbstractTestNGSpringContextTests{
	@Autowired WebHdfsWorkFlow.Builder webHdfsWorkFlowBuilder;
	private @Autowired ApplicationContext ctx;

	@Test
	public void validateWorkFlowOpsArgsConfiguration() throws NoSuchMethodException, 
		SecurityException, UnsupportedEncodingException, URISyntaxException {
		Assert.assertNotNull(webHdfsWorkFlowBuilder);
		
		StringEntity entity = new StringEntity("Greetings earthling!\n");
		
		DateTimeFormatter customDTF = new DateTimeFormatterBuilder()
	        .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
	        .appendValue(MONTH_OF_YEAR, 2)
	        .appendValue(DAY_OF_MONTH, 2)
	        .toFormatter();	
		
		FilePath path = new FilePath.Builder()
			.addPathSegment("data")
			.addPathSegment(customDTF.format(LocalDate.now()))
			.build();
		
		String fileName = path.getFile().getPath() + File.separator + "testfile.txt";
		
		WebHdfsWorkFlow workFlow = webHdfsWorkFlowBuilder
			.path(path.getFile().getPath())
			.context(ctx)
			.repoId("webhdfs")
			.addEntry("CreateBaseDir", 
				WebHdfsOps.MKDIRS, 
				HttpStatus.OK, 
				path.getFile().getPath())
			.addEntry("SetOwnerBaseDir",
				WebHdfsOps.SETOWNER, 
				HttpStatus.OK,
				path.getFile().getPath())
			.addEntry("CreateAndWriteToFile", 
				WebHdfsOps.CREATE, 
				HttpStatus.CREATED, 
				entity)
			.addEntry("SetOwnerFile", 
				WebHdfsOps.SETOWNER, 
				HttpStatus.OK, 
				fileName)
			.build();
		CloseableHttpResponse response = workFlow.execute(); 
		Assert.assertNotNull(response);
		Assert.assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.OK.value());
	}		
}
