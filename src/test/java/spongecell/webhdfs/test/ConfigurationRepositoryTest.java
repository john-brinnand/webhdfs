package spongecell.webhdfs.test;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import spongecell.webhdfs.WebHdfsWorkFlow;


@Slf4j
@ContextConfiguration(classes = { 
	ConfigurationRepositoryTest.class,
})
@EnableConfigurationProperties({ 
	ConfigurationRepository.class, 
	AgentA.Builder.class, 
	AgentC.Builder.class,
	WebHdfsWorkFlow.Builder.class,
	AgentA.class,
	AgentC.class
})
public class ConfigurationRepositoryTest extends AbstractTestNGSpringContextTests {
	private @Autowired ApplicationContext ctx;
	private @Autowired AgentA.Builder builder;
	private @Autowired AgentC.Builder cbuilder;
	
	@Test
	public void validateAgentConfigurationRepository(){
		final String agentAStep1Name = "one";
		AgentConfiguration config = (AgentConfiguration) ctx.getBean("step1"); 
		log.info("Config A is: {}", config.getName());
		Assert.assertEquals(config.getName(), agentAStep1Name);
	} 

	@Test
	public void validateAgentAConfiguration() {
		final String agentAStep1Name = "one";
		final String agentBStep2Name = "two";
		final String agentCStep3Name = "three";
		ConfigurationRepository repo = ctx.getBean(ConfigurationRepository.class); 
		
		// Note: AgentA depends on AgentB, so
		// there are two steps within it.
		//************************************
		AgentA agentStep1 = builder
			.context(ctx)
			.repoId(repo.getIterator().next())
			.agentIterator(repo.getIterator())
			.build();
		Assert.assertEquals(agentStep1.getConfig().getName(), agentAStep1Name);
		Assert.assertEquals(agentStep1.getAgentB().getConfig().getName(),
				agentBStep2Name);
		
		AgentC agentCStep3 = cbuilder
			.context(ctx)
			.repoId(repo.getIterator().next())
			.agentIterator(repo.getIterator())
			.build();		
		Assert.assertEquals(agentCStep3.getConfig().getName(), agentCStep3Name);
	}		
	
	@Test
	public void validateWebHdfsConfiguration() {
		final String agentAStep1Name = "one";
		final String agentBStep2Name = "two";
		final String agentCStep3Name = "three";
		final String baseDir = "/data";
		ConfigurationRepository repo = ctx.getBean(ConfigurationRepository.class); 
		WebHdfsWorkFlow.Builder wbuilder = ctx.getBean(WebHdfsWorkFlow.Builder.class);
		
		// Note: AgentA depends on AgentB, so
		// there are two steps within it.
		//************************************
		AgentA agentStep1 = builder
			.context(ctx)
			.repoId(repo.getIterator().next())
			.agentIterator(repo.getIterator())
			.build();
		Assert.assertEquals(agentStep1.getConfig().getName(), agentAStep1Name);
		Assert.assertEquals(agentStep1.getAgentB().getConfig().getName(),
				agentBStep2Name);
		
		AgentC agentCStep3 = cbuilder
			.context(ctx)
			.repoId(repo.getIterator().next())
			.build();		
		Assert.assertEquals(agentCStep3.getConfig().getName(), agentCStep3Name);
		
		
		String repoId = repo.getIterator().next();
		WebHdfsWorkFlow webHdfsWorkFlow = wbuilder
			.context(ctx)
			.agentIterator(repo.getIterator())
			.repoId(repoId)
			.fileName("testfile.txt")
			.build();				
		Assert.assertEquals(webHdfsWorkFlow.getWebHdfs().getWebHdfsConfig()
				.getBaseDir(), baseDir);
	}	
	/**
	 * Note: in the test below. Step two is
	 * AgentB which is configured by AgentA.
	 */
	@Test
	public void validateAgentBuilders() {
		final String agentAStep1Name = "one";
		final String agentCStep1Name = "three";
		ConfigurationRepository repo = ctx.getBean(ConfigurationRepository.class); 
		final String[] agentIds = {"agentA", "agentC"};
		
		Agent agentStep1 = (Agent) ctx.getBean(agentIds[0], repo);
		Assert.assertEquals(
			(agentStep1.<AgentConfiguration> getConfiguration()).getName(),
			agentAStep1Name);
		
		Agent agentStep3 = (Agent) ctx.getBean(agentIds[1], repo);
		Assert.assertEquals(
			(agentStep3.<AgentConfiguration> getConfiguration()).getName(),
			agentCStep1Name);
	}	
}
