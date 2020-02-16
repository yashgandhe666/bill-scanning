package com.frauddetection.textract;

import static com.frauddetection.textract.TestingSyncHelper.netAmount;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.frauddetection.textract.constants.Constants;
import com.frauddetection.textract.objects.Result;
import com.frauddetection.entities.ExpenseEntity;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.textract.model.Block;
import com.amazonaws.services.textract.model.DetectDocumentTextResult;
import com.frauddetection.repository.FileRepository;


public class TestingSync {

	@Autowired
	FileRepository fileRepository;

	private static final Logger LOGGER = Logger.getLogger(TestingSync.class.getName());

	public S3ObjectInputStream downloadFile(String key_name) {
		String bucket_name = Constants.BUCKET_NAME;
		System.out.format("Downloading %s from S3 bucket %s...\n", key_name, bucket_name);
		final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Constants.DEFAULT_REG).build();
		S3ObjectInputStream s3is = null;
		try {
			S3Object o = s3.getObject(bucket_name, key_name);
			s3is = o.getObjectContent();
			FileOutputStream fos = new FileOutputStream(System.getProperty("user.dir") + "/src/main/resources/" + key_name);
			byte[] read_buf = new byte[1024];
			int read_len = 0;
			while ((read_len = s3is.read(read_buf)) > 0) {
				fos.write(read_buf, 0, read_len);
			}
			s3is.close();
			fos.close();
		} catch (AmazonServiceException e) {
			System.err.println(e.getErrorMessage());
			System.exit(1);
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		return s3is;
	}

	public List<String> convertPdfToImage(String key_name) {
		List<String> files = new ArrayList<>();
		try (final PDDocument document = PDDocument
				.load(new File(System.getProperty("user.dir") + "/src/main/resources/" + key_name))) {
			PDFRenderer pdfRenderer = new PDFRenderer(document);
			for (int page = 0; page < document.getNumberOfPages(); ++page) {
				BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
				String fileName = System.getProperty("user.dir") + "/src/main/resources/" + key_name.substring(0, key_name.length()-4) + "-"
						+ page + ".png";
				ImageIOUtil.writeImage(bim, fileName, 300);
				files.add(fileName);
			}
			document.close();
		} catch (IOException e) {
			System.err.println("Exception while trying to create pdf document - " + e);
		}
		return files;
	}

	public Result driver(String key_name, String userId) {

		TestingSync testingSync = new TestingSync();
		Testing testing = new Testing();

		S3ObjectInputStream s3is = null;
		List<String> files = new ArrayList<>();
		Result total = new Result();

		try {
			s3is = testingSync.downloadFile(key_name);
			if (s3is != null) {
				LOGGER.info("File downloaded successfully.");
			}

			if (key_name.endsWith(".pdf")) {
				files = testingSync.convertPdfToImage(key_name);
				for (int i = 0; i < files.size(); i++) {
					key_name = key_name.replaceAll("pdf", "png");
					int index = files.get(i).lastIndexOf('/');
					files.set(i, files.get(i).substring(index));
				}
			} else {
				files.add(key_name);
			}
			// for each png file in the list of files
			
			for (String file : files) {
				LOGGER.info(file);
				List<Block> blocks = new ArrayList<>();
				DetectDocumentTextResult result = testing.test(file);
				blocks = result.getBlocks();
				total = netAmount(blocks);
				
				LOGGER.info(result.toString());
			}
			
//			saveToFilesRepo(total, userId, key_name);
//			LOGGER.info("Total: " + total.toString());
//            words.forEach(word -> LOGGER.info(word + " "));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return total;

	}
	
	private  void saveToFilesRepo(Result total, String userId, String key_name) {
		System.out.println("user"+userId+"----key"+key_name);
		System.out.println(total.toString());
		fileRepository.deleteAllByFilenameAndEmployeenumber(key_name, userId);
			ExpenseEntity ent =ExpenseEntity.builder().filename(key_name).employeenumber(userId).totalAmount("0").status(true).build();
		
			
			if(total.isFlight()) {
				ent.setStatus(false);
				ent.setReason("Flight bill booked less than 7 days prior.");
			}
			else if(total.isAlcohol()) {
				ent.setStatus(false);
				ent.setReason("Alcohol bill found");
			} 
			else if(total.isFuel()) {
				ent.setStatus(false);
				ent.setReason("Fuel Bill Found");
			} else {
				ent.setStatus(true);
			}
			
			fileRepository.save(ent);
		}
		
	}

//	private  void saveToFilesRepo(Result total, String userId, String key_name) {
//		System.out.println("user"+userId+"----key"+key_name);
//		Optional<List<ExpenseEntity>>expEntity=fileRepository.findAllByFilenameAndEmployeenumber(key_name, userId);
//		if(expEntity.isPresent()) {
//		for(ExpenseEntity ent:expEntity.get()) {
//		
////			total.isAlcohol()?(expEntity.get().setStatus(false)):(expEntity.get().setStatus(true)));
////			total.isFuel()
//			
//			ent.setTotalAmount(total.getTotal());
//			ent.setStatus(true);
//			
//			if(total.isFlight()) {
//				ent.setStatus(false);
//				ent.setReason("Flight bill booked less than 7 days prior.");
//			}
//			else if(total.isAlcohol()) {
//				ent.setStatus(false);
//				ent.setReason("Alcohol bill found");
//			} 
//			else if(total.isFuel()) {
//				ent.setStatus(false);
//				ent.setReason("Fuel Bill Found");
//			} else {
//				ent.setStatus(true);
//			}
//			
//			fileRepository.save(ent.get());
//		}
//		}
//	}
