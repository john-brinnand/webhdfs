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
public class AgentA implements Agent {
	private AgentConfiguration config;
	private AgentB agentB;
	
	public AgentA () {};
	
	private AgentA(Builder builder) {
		config = builder.config;
		log.info("AgentA configuration: {} ", config.getName());
		agentB = new AgentB.Builder()
			.repoId(builder.iter.next())
			.context(builder.ctx)
			.build();
	}
	
	@EnableConfigurationProperties({ AgentConfiguration.class })
	public static class Builder extends GenericBuilder<AgentA> {
		private ApplicationContext ctx;
		private String repoId;
		private AgentConfiguration config;
		private Iterator<String> iter;

		@Override
		public GenericBuilder<AgentA> context(ApplicationContext ctx) {
			this.ctx = ctx;
			return this;
		}

		@Override
		public GenericBuilder<AgentA> repoId(String repoId) {
			this.repoId = repoId;
			return this;
		}
		
		@Override
		public GenericBuilder<AgentA> agentIterator(Iterator<String> iter) {
			this.iter = iter;
			return this;
		}

		@Override
		public AgentA build() {
			config = (AgentConfiguration) ctx.getBean(repoId);
			return new AgentA(this);
		}
	}

	@Override
	public Object[] getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Bean(name="agentA")
	public Agent buildAgent(ConfigurationRepository repo) {
		return  new Builder()
			.context(repo.getCtx())
			.repoId(repo.getIterator().next())
			.agentIterator(repo.getIterator())
			.build();
	}

	@Override
	public <T> T getConfiguration() {
		return (T) config;
	}
}
