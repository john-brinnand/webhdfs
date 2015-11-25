package spongecell.webhdfs.test;

import org.springframework.context.annotation.Bean;


public interface Agent {
	/**
	 * Get the status of  managed object
	 * or component in an Infrastructure.
	 */
	public abstract Object[] getStatus();
	
	public abstract <T> T getConfiguration();
	
	/**
	 * This method must contain a name for the 
	 * bean. Without it, the agent will not be 
	 * built, loaded or run dynamically. 
	 * 
	 * @return
	 */
	@Bean(name="")
	Agent buildAgent(ConfigurationRepository repo); 
}
