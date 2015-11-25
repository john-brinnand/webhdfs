package spongecell.webhdfs.test;

import java.util.Iterator;

import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import spongecell.webhdfs.WebHdfsConfiguration;

@Getter @Setter
@Scope("prototype")
public class ConfigurationRepository {
	private final String[] elements = {"step1", "step2", "step3", "step4"};
	private Iterator<String> iterator;
	private int index = 0;
	private @Autowired ApplicationContext ctx;
	
	public ConfigurationRepository() {
		if (iterator == null) {
			iterator = new Iterator<String>() {

				@Override
				public boolean hasNext() {
					if (elements.length <= index) {
						return false;
					}
					return true;	
				}

				@Override
				public String next() {
					String agentId = elements[index];
					index++;
					return agentId;
				}
			};
		}
	}
	
	/**
	 * Get an individual element.
	 * 
	 * @param id
	 * @return
	 */
	public String getElement (String id) {
		String elementId = null;
		for (String element : elements) {
			if (element.equals(id)) {
				elementId = id;
				break;
			}
		}
		return elementId;
	}
	
	@Bean(name="step1")
	@ConfigurationProperties(prefix="step1.agent")
	public AgentConfiguration buildAgentConfigurationStep1() {
		return new AgentConfiguration();
	}	

	@Bean(name="step2")
	@ConfigurationProperties(prefix="step2.agent")
	public AgentConfiguration buildAgentConfigurationStep2() {
		return new AgentConfiguration();
	}
	
	@Bean(name="step3")
	@ConfigurationProperties(prefix="step3.agent")
	public AgentConfiguration buildAgentConfigurationStep3() {
		return new AgentConfiguration();
	}
	
	@Bean(name="step4")
	@ConfigurationProperties(prefix="step4.agent")
	public WebHdfsConfiguration buildWebHdfsConfiguration() {
		return new WebHdfsConfiguration();
	}	
}