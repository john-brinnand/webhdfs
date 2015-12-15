package spongecell.webhdfs.builder;

import org.springframework.context.ApplicationContext;

public abstract class GenericBuilder <T>{
	
	public abstract GenericBuilder<T> context (ApplicationContext ctx); 
	
	public abstract GenericBuilder<T> repoId (String repoId); 
	
	public abstract T build();
}
