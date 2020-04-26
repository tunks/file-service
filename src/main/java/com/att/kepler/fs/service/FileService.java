package com.att.kepler.fs.service;

import java.util.List;
import java.util.Optional;

import com.att.kepler.fs.dto.FileDto;
import com.att.kepler.fs.exceptions.FileException;
import com.att.kepler.fs.model.FileInfo;
import com.att.kepler.fs.support.FileResource;

/***
 * FileService base interface
 *  
 *  @author ebrimatunkara
 */
public interface FileService<T> {
   /***
    * 
    *  Find list of file entities by query
    *  
    *  @param query
    *  @return List<entity>
    */
   public List<FileInfo> findAll(FileDto fileDto);
   /***
    * 
    *  Get file resource
    *  
    *  @return Resource
    */
   public Optional<FileResource> getFile(FileDto fileDto) throws FileException;
   /***
    * 
    *  Save file resource
    *  
    *  @param object
    */
   public void save(FileDto fileDto) throws FileException;
}
