package com.att.kepler.fs.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.stereotype.Service;
import com.att.kepler.fs.support.FileInfo;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;

@Service("fileService")
public class FileServiceImpl implements FileService<InputStream> {
	private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

	@Autowired
	@Qualifier("gridFsTemplate")
	private GridFsOperations gridFsOperations;

	@Override
	public List<FileInfo> findAll(FileInfo info) {
		Query query = new Query();
		query.addCriteria(Criteria.where("metadata.fileName").is(info.getFileName()));
		GridFSFindIterable iterable = this.gridFsOperations.find(query);
		List<FileInfo> files = new ArrayList<FileInfo>();
		MongoCursor<GridFSFile> cursor = iterable.iterator();
		Document metadata;
		FileInfo file;
		while (cursor.hasNext()) {
			metadata = cursor.next().getMetadata();
			file = new FileInfo();
			file.setFileType(metadata.getString("fileType"));
			file.setFileNamespace(metadata.getString("namespace"));
			file.setFileVersion(metadata.getInteger("fileVersion"));
			file.setCreatedBy(metadata.getString("createdBy"));
			file.setFileName(metadata.getString("fileName"));
			files.add(file);
		}
		return files;
	}

	@Override
	public InputStream getResource(FileInfo info) {
		Query query = new Query();
		query.addCriteria(Criteria.where("metadata.fileName").is(info.getFileName())
				                  .and("metadata.namespace").is(info.getFileNamespace()));
		query.with(new Sort(Sort.Direction.DESC, "uploadDate"));

		try {
			GridFSFile file = this.gridFsOperations.findOne(query);
			if (file != null) {
				Document metadata = file.getMetadata();
				info.setFileType(metadata.getString("fileType"));
				return this.gridFsOperations.getResource(file).getInputStream();
			}
		} catch (IllegalStateException | IOException ex) {
			logger.error(ex.getMessage());
		}
		return null;
	}

	@Override
	public void save(FileInfo info, InputStream object) {
		Query query = new Query();
		query.addCriteria(Criteria.where("metadata.fileName").is(info.getFileName())
				                  .and("metadata.namespace").is(info.getFileNamespace()));
		query.with(new Sort(Sort.Direction.DESC, "uploadDate"));
		GridFSFile file = gridFsOperations.findOne(query);
		if(file != null) {
		   info.setFileVersion(file.getMetadata().getInteger("fileVersion", FileInfo.DEFAULT_VERSION) + 1);
		}
		gridFsOperations.store(object, info.getFileName(), info.dbObject());
	}
}
