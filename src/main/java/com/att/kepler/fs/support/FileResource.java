package com.att.kepler.fs.support;

import org.springframework.core.io.Resource;

import com.att.kepler.fs.model.FileInfo;

public class FileResource extends FileInfo {
	private Resource resource;

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}
}
