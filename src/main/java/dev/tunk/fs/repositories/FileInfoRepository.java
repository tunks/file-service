package dev.tunk.fs.repositories;

import org.springframework.data.repository.CrudRepository;

import dev.tunks.fs.model.FileInfo;

/**
 * FileInfo Redis Repository interface 
 */
public interface FileInfoRepository extends CrudRepository<FileInfo, String>{

}
