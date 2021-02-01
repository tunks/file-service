package dev.tunks.fs.support;

import org.springframework.core.io.Resource;

import dev.tunks.fs.model.FileInfo;

public class FileResource extends FileInfo {
	private Resource resource;

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}
}
