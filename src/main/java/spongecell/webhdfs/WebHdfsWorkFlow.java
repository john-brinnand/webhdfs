package spongecell.webhdfs;

import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

import spongecell.webhdfs.builder.GenericBuilder;



@Slf4j
@Getter
@EnableConfigurationProperties({ 
	GenericBuilder.class, 
	WebHdfs.Builder.class 
})
public class WebHdfsWorkFlow {
	private Map<String, WebHdfsOpsArgs> workFlow;
	private @Autowired WebHdfs webHdfs;
	
	private WebHdfsWorkFlow(Builder builder) {
		this.workFlow = builder.workFlow;
		webHdfs = builder.webHdfsBuilder
			.config(builder.config)
			.path(builder.path)
			.fileName(builder.fileName)
			.user(builder.user)
			.overwrite(builder.overwrite)
			.build();
	}

	@EnableConfigurationProperties ({ WebHdfs.Builder.class, WebHdfsConfiguration.class })
	public static class Builder extends GenericBuilder<WebHdfsWorkFlow> {
		@Autowired WebHdfs.Builder webHdfsBuilder;
		private String fileName;
		private String user;
		private String overwrite;
		private String path; 
		private Map<String, WebHdfsOpsArgs> workFlow;
		private WebHdfsConfiguration config;
		private Iterator<String> iter;
		private ApplicationContext ctx;
		private String elementId; 

		public Builder() {
			workFlow = new LinkedHashMap<String, WebHdfsOpsArgs>();
		}
		
		public Builder clear() {
			this.workFlow.clear();
			return this;
		} 
		
		public Builder addEntry(String step, WebHdfsOps ops, 
				HttpStatus httpStatus, Object...args) {
			WebHdfsOpsArgs opsArgs  = new WebHdfsOpsArgs(ops, httpStatus, args);
			this.workFlow.put(step, opsArgs);
			return this;
		}
		
		public Builder fileName(String fileName) {
			this.fileName = fileName;
			return this;
		}	
		
		public Builder user(String user) {
			this.user = user;
			return this;
		}	
		
		public Builder overwrite(String overwrite) {
			this.overwrite = overwrite;
			return this;
		}				
		
		public Builder path(String path) {
			this.path = path;
			return this;
		}	
		
		public Builder config(WebHdfsConfiguration config) {
			this.config = config;
			return this;
		}	
		
		
		@Override
		public Builder context(ApplicationContext ctx) {
			this.ctx = ctx;
			return this;
		}

		@Override
		public Builder repoId(String elementId) {
			this.elementId = elementId;
			return this;
		}

		public Builder agentIterator(Iterator<String> iter) {
			this.iter = iter;
			return this;
		}	
		
		public WebHdfsWorkFlow build() {
			if (this.config == null) {
				this.config = (WebHdfsConfiguration) ctx.getBean(elementId);
			}
			log.info("WebHdfsConfiguration is: {} ", this.config.toString());
			return new WebHdfsWorkFlow(this);
		}
	}
	
	public WebHdfsConfiguration getConfig() {
		return webHdfs.getWebHdfsConfig();
	}
	
	public CloseableHttpResponse execute() throws URISyntaxException {
		CloseableHttpResponse response = null;
		WebHdfsOpsArgs opsArgs =  null;
		Set<Entry<String, WebHdfsOpsArgs>>entries = workFlow.entrySet();
		
		for (Entry<String, WebHdfsOpsArgs>entry : entries) {
			if (opsArgs != null) {
				Assert.isTrue(response.getStatusLine().getStatusCode() == 
					opsArgs.getHttpStatus().value(), 
					"Response code indicates a failed operation: " + 
					response.getStatusLine().getStatusCode());	
			}
			opsArgs = entry.getValue();
			log.info("Executing step : {} ", entry.getKey());
			if (opsArgs.getWebHdfsOp().equals(WebHdfsOps.LISTSTATUS)) {
				response = webHdfs.listStatus((String)opsArgs.getArgs()[0]);
				continue;
			}
			if (opsArgs.getWebHdfsOp().equals(WebHdfsOps.GETFILESTATUS)) {
				response = webHdfs.getFileStatus((String)opsArgs.getArgs()[0]);
				continue;
			}					
			if (opsArgs.getWebHdfsOp().equals(WebHdfsOps.CREATE)) {
				response = webHdfs.create((AbstractHttpEntity)opsArgs.getArgs()[0]);
				continue;
			}
			if (opsArgs.getWebHdfsOp().equals(WebHdfsOps.APPEND)) {
				response = webHdfs.append((StringEntity)opsArgs.getArgs()[0]);
				continue;
			}	
			if (opsArgs.getWebHdfsOp().equals(WebHdfsOps.MKDIRS)) {
				response = webHdfs.mkdirs((String)opsArgs.getArgs()[0]);
				continue;
			}		
			if (opsArgs.getWebHdfsOp().equals(WebHdfsOps.SETOWNER)) {
				response = webHdfs.setOwner((String)opsArgs.getArgs()[0],
						getConfig().getOwner(), getConfig().getGroup());
				continue;
			}					
			if (opsArgs.getWebHdfsOp().equals(WebHdfsOps.OPENANDREAD)) {
				response = webHdfs.openRead((String)opsArgs.getArgs()[0]);
				continue;
			}		
		}
		return response;
	}	
}
