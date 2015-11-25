package spongecell.webhdfs.test;

import java.util.Iterator;

import org.springframework.context.ApplicationContext;

public abstract class GenericBuilder <T>{
	
	public abstract GenericBuilder<T> context (ApplicationContext ctx); 
	
	public abstract GenericBuilder<T> repoId (String repoId); 
	
	public abstract GenericBuilder<T> agentIterator (Iterator<String> iter); 
	
	public abstract T build();
}
