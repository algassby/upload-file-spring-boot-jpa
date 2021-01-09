package com.barry.uploadFile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.barry.uploadFile.model.FileDB;

@Repository
public interface FileDBRepository extends JpaRepository<FileDB, String> {

}
