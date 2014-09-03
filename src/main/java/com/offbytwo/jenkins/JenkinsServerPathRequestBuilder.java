package com.offbytwo.jenkins;

import org.apache.commons.lang.StringUtils;

import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.offbytwo.jenkins.utils.URLUtils;

public class JenkinsServerPathRequestBuilder extends AbstractJenkinsServer {
    
    private final String jobPath;
    
    JenkinsServerPathRequestBuilder(JenkinsHttpClient client, String... jobNames) {
	this(client, StringUtils.EMPTY, jobNames);
    }

    private JenkinsServerPathRequestBuilder(JenkinsServerPathRequestBuilder builder, String... jobNames) {
	this(builder.getClient(), builder.jobPath, jobNames);
    }
    
    private JenkinsServerPathRequestBuilder(JenkinsHttpClient client, String parentPath, String... jobNames) {
	super(client);
	StringBuilder jobPathBuilder = new StringBuilder(parentPath);
	for (String jobName : jobNames) {
	    if (StringUtils.isNotBlank(jobName)) {
		jobPathBuilder.append(JOB_PATH_SEPARATOR).append(URLUtils.encode(jobName));
	    }
	}
	jobPath = jobPathBuilder.toString();
    }

    public JenkinsServerPathRequestBuilder folder(String... jobNames) {
	return new JenkinsServerPathRequestBuilder(this, jobNames);
    }
    
    @Override
    protected String getRootJobPath() {
	return jobPath;
    }

}
