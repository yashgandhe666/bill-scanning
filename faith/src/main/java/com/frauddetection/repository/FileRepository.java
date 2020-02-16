package com.frauddetection.repository;

import java.util.List;
import java.util.Optional;

import com.frauddetection.entities.ExpenseEntity;
import org.springframework.data.repository.CrudRepository;


public interface FileRepository extends CrudRepository<ExpenseEntity, String>{

	Optional<List<ExpenseEntity>> findAllByEmployeenumber(String userId);
	
	Optional<List<ExpenseEntity>> findAllByFilenameAndEmployeenumber(String key_name, String userId);
	
	void deleteAllByFilenameAndEmployeenumber(String filename,String userId);

	//Optional<List<ExpenseEntity>> findAllByUser(UserEntity userEntity);

	
}
