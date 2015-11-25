package spongecell.webhdfs.test;

import java.util.Iterator;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import spongecell.webhdfs.WebHdfsConfiguration;
import spongecell.webhdfs.test.AgentA.Builder;

@Slf4j
@Getter @Setter
public class AgentB implements Agent {
	private AgentConfiguration config;
	
	public AgentB() {};
	
	private AgentB(Builder builder) {
		config = builder.config;
		log.info("AgentB configuration: {} ", config.getName());
	}
	
	@EnableConfigurationProperties({ WebHdfsConfiguration.class })
	public static class Builder extends GenericBuilder<AgentB> {
		private ApplicationContext ctx;
		private String repoId;
		private AgentConfiguration config;
		private Iterator<String> iter;

		@Override
		public GenericBuilder<AgentB> context(ApplicationContext ctx) {
			this.ctx = ctx;
			return this;
		}

		@Override
		public GenericBuilder<AgentB> repoId(String repoId) {
			this.repoId = repoId;
			return this;
		}

		@Override
		public AgentB build() {
			config = (AgentConfiguration) ctx.getBean(repoId);
			return new AgentB(this);
		}

		@Override
		public GenericBuilder<AgentB> agentIterator(Iterator<String> iter) {
			this.iter = iter;
			return this;
		}
	}
	
	@Override
	public Object[] getStatus() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public <T> T getConfiguration() {
		return (T) config;
	}
	
	@Override
	@Bean(name="agentB")
	public Agent buildAgent(ConfigurationRepository repo) {
		return new Builder()
			.context(repo.getCtx())
			.repoId(repo.getIterator().next())
			.agentIterator(repo.getIterator())
			.build();
	}
}
