package com.offbytwo.jenkins;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.client.HttpResponseException;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.offbytwo.jenkins.model.LabelWithDetails;
import com.offbytwo.jenkins.model.MainView;
import com.offbytwo.jenkins.model.MavenJobWithDetails;
import com.offbytwo.jenkins.utils.URLUtils;

public abstract class AbstractJenkinsServer {
    
    protected static final String JOB_PATH_SEPARATOR = "/job/";

    private final JenkinsHttpClient client;

    AbstractJenkinsServer(JenkinsHttpClient client) {
	this.client = client;
    }
    
    protected JenkinsHttpClient getClient() {
	return client;
    }
    
    protected abstract String getRootJobPath();

    /**
     * Get a list of all the defined jobs on the server (at the summary level)
     *
     * @return list of defined jobs (summary level, for details @see Job#details
     * @throws IOException
     */
    public Map<String, Job> getJobs() throws IOException {
	try {
	    List<Job> jobs = client.get(getRootJobPath() + "/", MainView.class)
		    .getJobs();
	    return Maps.uniqueIndex(jobs, new Function<Job, String>() {
		@Override
		public String apply(Job job) {
		    job.setClient(client);
		    return job.getName().toLowerCase();
		}
	    });
	} catch (HttpResponseException e) {
	    if (e.getStatusCode() == 404) {
		return null;
	    }
	    throw e;
	}
    }

    /**
     * Get a single Job from the server.
     *
     * @return A single Job, null if not present
     * @throws IOException
     */
    public JobWithDetails getJob(String jobName) throws IOException {
        try {
            JobWithDetails job = client.get(getRootJobPath() + JOB_PATH_SEPARATOR + URLUtils.encode(jobName),JobWithDetails.class);
            job.setClient(client);
    
            return job;
        } catch (HttpResponseException e) {
            if(e.getStatusCode() == 404) {
                return null;
            }
            throw e;
        }
    
    }

    public MavenJobWithDetails getMavenJob(String jobName) throws IOException {
        try {
            MavenJobWithDetails job = client.get(getRootJobPath() + JOB_PATH_SEPARATOR + URLUtils.encode(jobName), MavenJobWithDetails.class);
            job.setClient(client);
    
            return job;
        } catch (HttpResponseException e) {
            if(e.getStatusCode() == 404) {
                return null;
            }
            throw e;
        }
    }

    /**
     * Create a job on the server using the provided xml
     *
     * @return the new job object
     * @throws IOException
     */
    public void createJob(String jobName, String jobXml) throws IOException {
        client.post_xml(getRootJobPath() + "/createItem?name=" + URLUtils.encode(jobName), jobXml);
    }

    /**
     * Get the xml description of an existing job
     *
     * @return the new job object
     * @throws IOException
     */
    public String getJobXml(String jobName) throws IOException {
        return client.get(getRootJobPath() + JOB_PATH_SEPARATOR + URLUtils.encode(jobName) + "/config.xml");
    }

    /**
     * Get the description of an existing Label
     *
     * @return label object
     * @throws IOException
     */
    public LabelWithDetails getLabel(String labelName) throws IOException {
        return client.get(getRootJobPath() + "/label/" + URLUtils.encode(labelName), LabelWithDetails.class);
    }

    /**
     * Update the xml description of an existing job
     *
     * @return the new job object
     * @throws IOException
     */
    public void updateJob(String jobName, String jobXml) throws IOException {
        client.post_xml(getRootJobPath() + JOB_PATH_SEPARATOR + URLUtils.encode(jobName) + "/config.xml", jobXml);
    }

}