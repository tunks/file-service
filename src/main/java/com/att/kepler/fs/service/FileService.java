package com.att.kepler.fs.service;

import java.util.List;

import com.att.kepler.fs.support.FileInfo;

/***
 * FileService base interface
 *  
 */
public interface FileService<T> {
   /***
    * 
    *  Find list of file entities by query
    *  
    *  @param query
    *  @return List<entity>
    */
   public List<FileInfo> findAll(FileInfo info);
   /***
    * 
    *  Get file resource
    *  
    *  @param query
    *  @return Resource
    */
   public T getResource(FileInfo info);
   /***
    * 
    *  Save file resource
    *  
    *  @param object
    */
   public void save(FileInfo info, T object);
}
