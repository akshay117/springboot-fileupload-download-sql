package com.fileoperation.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fileoperation.demo.model.FileDB;

@Repository
public interface FileDBRepository extends JpaRepository<FileDB, String> {

}
