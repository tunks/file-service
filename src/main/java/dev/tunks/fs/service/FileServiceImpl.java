package dev.tunks.fs.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;

import dev.tunk.fs.dto.FileDto;
import dev.tunks.fs.exceptions.FileException;
import dev.tunks.fs.model.FileInfo;
import dev.tunks.fs.support.FileResource;

@Service("fileService")
public class FileServiceImpl implements FileService<InputStream> {
	private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

	@Autowired
	@Qualifier("gridFsTemplate")
	private GridFsOperations gridFsOperations;

	@Override
	public List<FileInfo> findAll(FileDto fileDto) {
		FileInfo fileInfo;
		List<FileInfo> files = new ArrayList<FileInfo>();
		Optional<Query> query = getFileQuery(fileDto);
		try {
			GridFSFindIterable iterable = this.gridFsOperations.find(query.get());
			MongoCursor<GridFSFile> cursor = iterable.iterator();
			Document metadata;
			GridFSFile gridFile;
			while (cursor.hasNext()) {
				gridFile = cursor.next();
				metadata = gridFile.getMetadata();
				fileInfo = new FileInfo();
				fileInfo.setId(gridFile.getObjectId().toString());
				fileInfo.setFileType(metadata.getString("fileType"));
				fileInfo.setFileVersion(metadata.getInteger("fileVersion"));
				fileInfo.setCreatedBy(metadata.getString("createdBy"));
				fileInfo.setFileName(gridFile.getFilename());
				fileInfo.setNamespace(metadata.getString("namespace"));
				fileInfo.setResourceId(metadata.getString("resourceId"));
				fileInfo.setResourceType(metadata.getString("resourceType"));
				files.add(fileInfo);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return files;
	}

	@Override
	public Optional<FileResource> getFile(FileDto fileDto) throws FileException {
		try {
			Optional<Query> query = getFileQuery(fileDto);
			Optional<GridFSFile> gridFile = (query.isPresent()) ? Optional.ofNullable(gridFsOperations.findOne(query.get()))
					                         : Optional.empty();
			if (gridFile.isPresent()) {
				GridFSFile file = gridFile.get();
				FileResource fileResource = new FileResource();
				fileResource.setId(file.getObjectId().toString());
				fileResource.setFileName(file.getFilename());
				Document metadata = file.getMetadata();
				fileResource.setFileType(metadata.getString("fileType"));
				Resource resource = this.gridFsOperations.getResource(file);
				fileResource.setResource(resource);
				return Optional.ofNullable(fileResource);
			}
			return Optional.empty();
		} catch (IllegalStateException ex) {
			logger.error(ex.getMessage());
			throw new FileException(ex.getMessage());
		}
	}

	@Override
	public void save(FileDto fileDto) throws FileException {
		try {
			FileInfo info = new FileInfo.FileInfoBuilder(fileDto).build();
			gridFsOperations.store(fileDto.getUploadedFile().getInputStream(), info.getFileName(), info.dbObject());
		} catch (IOException ex) {
			logger.error(ex.getMessage());
		}
	}
	
	private Optional<Query> getFileQuery(FileDto fileDto){
		Optional<String> fileId = Optional.ofNullable(fileDto.getId());
		Criteria criteria = new Criteria();
		if (fileId.isPresent() && ObjectId.isValid(fileId.get())) {
			ObjectId objectId = new ObjectId(fileId.get());
			criteria = Criteria.where("_id").is(objectId);
		}
		else {	
			List<Criteria> conditions = new ArrayList<Criteria>(); 
			Optional<String> fileName = Optional.ofNullable(fileDto.getFileName());
			Optional<String> resourceId = Optional.ofNullable(fileDto.getResourceId());
			Optional<String> resourceType = Optional.ofNullable(fileDto.getResourceType());
			Optional<String> namespace = Optional.ofNullable(fileDto.getNamespace());
			if (fileName.isPresent()) {
			    conditions.add(Criteria.where("filename").is(fileName.get()));
			}
			
			if (resourceId.isPresent()) {
				conditions.add(Criteria.where("metadata.resourceId").is(resourceId.get()));
			}
			
			if (resourceType.isPresent()) {
				conditions.add(Criteria.where("metadata.resourceType").is(resourceType.get()));
			}
			
			if (namespace.isPresent()) {
				conditions.add(Criteria.where("metadata.namespace").is(namespace.get()));
			}
			if(conditions.isEmpty()) {
			   return Optional.empty();
			}

			criteria.andOperator(conditions.toArray(new Criteria[0]));
		}
		Query query = new Query(criteria);
		System.out.println(query.toString());
		return Optional.of(query);
	}
}
