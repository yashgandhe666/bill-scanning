package com.frauddetection.controller;

import java.util.List;
import java.util.Optional;

import com.frauddetection.entities.ExpenseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.frauddetection.aws.FileStorage;
import com.frauddetection.textract.TestingSync;
import com.frauddetection.textract.objects.Result;
import com.frauddetection.repository.FileRepository;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
public class UiController {

	@Autowired
	FileRepository fileRepository;

	@Autowired
	FileStorage fileStorage;

//	@PostMapping(value="/save/{userName}/{userId}")
//	public void postUser(@PathVariable(value = "userName", required = false) String userName,
//			@PathVariable(value = "userId", required = false) String userId){
//		System.out.println(userId+"------>");
//		ExpenseEntity user=ExpenseEntity.builder().employeename(userName).employeenumber(userId).build();
//				fileRepository.save(user);
//		
//	}
//	@PostMapping(value = "/userpost")
//	public UserEntity postUser(@RequestBody UserEntity user) {
//		System.out.println("posting user");
//		userRepository.save(user);
//		return user;
//	}

	@PostMapping(value = "/filepost/{userId}")
	public ResponseEntity<String> postBills(@PathVariable(value = "userId", required = false) String userId,
			@RequestParam(value = "file", required = false) MultipartFile multiPart) throws Exception {
		// Optional<ExpenseEntity> user = fileRepository.findByEmployeenumber(userId);

//			fileRepository
//					.save(ExpenseEntity.builder().status(true).reason("bkla").employeenumber(userId).filename(multiPart.getOriginalFilename()).totalAmount("45").build());
		fileStorage.uploadFile(multiPart);
		TestingSync testSync = new TestingSync();
		Result total = testSync.driver(multiPart.getOriginalFilename(), userId);
		ExpenseEntity ent = ExpenseEntity.builder().employeenumber(userId).filename(multiPart.getOriginalFilename())
				.totalAmount(total.getTotal().toString()).build();
		if (total.isFlight()) {
			ent.setStatus(false);
			ent.setReason("Flight bill booked less than 7 days prior.");
		} else if (total.isAlcohol()) {
			ent.setStatus(false);
			ent.setReason("Alcohol bill found");
		} else if (total.isFuel()) {
			ent.setStatus(false);
			ent.setReason("Fuel Bill Found");
		} else {
			ent.setStatus(true);
		}

		fileRepository.save(ent);
		return new ResponseEntity<String>(multiPart.getOriginalFilename(), HttpStatus.OK);

	}

//
	@GetMapping(value = "/files/{userId}")
	public List<ExpenseEntity> getBills(@PathVariable(value = "userId", required = false) String userId) {

		Optional<List<ExpenseEntity>> expenseList = fileRepository.findAllByEmployeenumber(userId);

		return expenseList.isPresent() ? expenseList.get() : null;

	}

}
