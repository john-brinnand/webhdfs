package spongecell.webhdfs.test;

import java.util.Iterator;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@Slf4j
@Getter @Setter
public class AgentC implements Agent {
	private AgentConfiguration config;
	
	public AgentC() {};
	
	private AgentC(Builder builder) {
		config = builder.config;
		log.info("AgentC configuration: {} ", config.getName());
	}
	
	@EnableConfigurationProperties({ AgentConfiguration.class })
	public static class Builder extends GenericBuilder<AgentC> {
		private ApplicationContext ctx;
		private String repoId;
		private AgentConfiguration config;
		private Iterator<String> iter;

		@Override
		public GenericBuilder<AgentC> context(ApplicationContext ctx) {
			this.ctx = ctx;
			return this;
		}

		@Override
		public GenericBuilder<AgentC> repoId(String repoId) {
			this.repoId = repoId;
			return this;
		}
		
		@Override
		public GenericBuilder<AgentC> agentIterator(Iterator<String> iter) {
			this.iter = iter;
			return this;
		}

		@Override
		public AgentC build() {
			config = (AgentConfiguration) ctx.getBean(repoId);
			return new AgentC(this);
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
	@Bean(name="agentC")
	public Agent buildAgent(ConfigurationRepository repo) {
		return new Builder()
			.context(repo.getCtx())
			.repoId(repo.getIterator().next())
			.agentIterator(repo.getIterator())
			.build();
	}
}
