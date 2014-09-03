/*
 * Copyright (c) 2013 Rising Oak LLC.
 *
 * Distributed under the MIT license: http://opensource.org/licenses/MIT
 */

package com.offbytwo.jenkins.model;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpResponseException;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.offbytwo.jenkins.utils.URLUtils;

public class JobWithDetails extends Job {
    String displayName;
    boolean buildable;
    List<Build> builds;
    Build lastBuild;
    Build lastCompletedBuild;
    Build lastFailedBuild;
    Build lastStableBuild;
    Build lastSuccessfulBuild;
    Build lastUnstableBuild;
    Build lastUnsuccessfulBuild;
    int nextBuildNumber;
    List<Job> downstreamProjects;
    List<Job> upstreamProjects;
    List<Job> jobs;

    public String getDisplayName() {
	return displayName;
    }

    public boolean isBuildable() {
	return buildable;
    }

    public List<Build> getBuilds() {
	return Lists.transform(builds, new Function<Build, Build>() {
	    @Override
	    public Build apply(Build from) {
		return buildWithClient(from);
	    }
	});
    }

    private Build buildWithClient(Build from) {
	Build ret = null;
	if (from != null) {
	    ret = new Build(from);
	    ret.setClient(client);
	}
	return ret;
    }

    public Build getLastBuild() {
	return buildWithClient(lastBuild);
    }

    public Build getLastCompletedBuild() {
	return buildWithClient(lastCompletedBuild);
    }

    public Build getLastFailedBuild() {
	return buildWithClient(lastFailedBuild);
    }

    public Build getLastStableBuild() {
	return buildWithClient(lastStableBuild);
    }

    public Build getLastSuccessfulBuild() {
	return buildWithClient(lastSuccessfulBuild);
    }

    public Build getLastUnstableBuild() {
	return buildWithClient(lastUnstableBuild);
    }

    public Build getLastUnsuccessfulBuild() {
	return buildWithClient(lastUnsuccessfulBuild);
    }

    public int getNextBuildNumber() {
	return nextBuildNumber;
    }

    public List<Job> getDownstreamProjects() {
	return Lists.transform(downstreamProjects, new JobWithClient());
    }

    public List<Job> getUpstreamProjects() {
	return Lists.transform(upstreamProjects, new JobWithClient());
    }

    public Map<String, Job> getJobs() {
	List<Job> transformedJobs = Lists.transform(jobs, new JobWithClient());
	return Maps.uniqueIndex(transformedJobs, new Function<Job, String>() {
	    @Override
	    public String apply(Job job) {
		job.setClient(client);
		return job.getName().toLowerCase();
	    }
	});
    }

    public JobWithDetails getJob(String jobName) throws IOException {
	try {
	    String url = getUrl();
	    url = StringUtils.substring(url, StringUtils.indexOf(url, '/'));
	    JobWithDetails job = client.get(
		    url + "/job/" + URLUtils.encode(jobName),
		    JobWithDetails.class);
	    job.setClient(client);

	    return job;
	} catch (HttpResponseException e) {
	    if (e.getStatusCode() == 404) {
		return null;
	    }
	    throw e;
	}
    }

    private class JobWithClient implements Function<Job, Job> {
	@Override
	public Job apply(Job job) {
	    job.setClient(client);
	    return job;
	}
    }
}
