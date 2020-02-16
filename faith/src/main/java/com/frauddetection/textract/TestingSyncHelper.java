package com.frauddetection.textract;

import com.amazonaws.services.textract.model.Block;
import com.frauddetection.textract.objects.Result;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.max;

public class TestingSyncHelper {

	public static boolean isNumeric(String strNum) {
		return strNum.matches("-?\\d+(\\.\\d+)?");
	}

	public static boolean validDate(String strDate) {
		if (strDate.trim().equals("")) {
			return false;
		} else {
			SimpleDateFormat sdfrmt = new SimpleDateFormat("dd/MM/yyyy");
			sdfrmt.setLenient(true);
			try {
				sdfrmt.parse(strDate);
			} catch (ParseException e) {
				return false;
			}
			return true;
		}
	}

	public static String nextNumber(List<Block> blocks, int i) {
		String total = "";
		for (int j = i; j < blocks.size(); j++) {
			if (isNumeric(blocks.get(j).getText())) {
				total = blocks.get(j).getText();
				break;
			}
		}
		return total;
	}

	public static String nextDate(List<Block> blocks, int i) {
		String date = "";
		for (int j = i; j < blocks.size(); j++) {
			if (validDate(blocks.get(j).getText())) {
				date = blocks.get(j).getText();
				break;
			}
		}
		return date;
	}

	public static String getnextDate(List<Block> blocks, int i) {
		String date = "";
		for (int j = i; j < blocks.size(); j++) {
			for (int k = 0; k < blocks.get(j).getText().length(); k++) {
				if (validDate(blocks.get(j).getText().substring(0, k))) {
					date = blocks.get(j).getText();
					return date.substring(0, k);
				}
				if (validDate(blocks.get(j).getText().substring(k))) {
					date = blocks.get(j).getText();
					return date.substring(k);
				}
			}
		}
		return date;
	}

	public static Result netAmount(List<Block> blocks) throws IOException {
		Result result = new Result();
		String total1 = "", total2 = "";
		Double maxi_amount = 0d;
		boolean invoiceDateFlag = true, depDateFlag = true;
		for (int i = 1; i < blocks.size(); i++) {
			String temp = blocks.get(i).getText();

			// Get net amount
			if (temp.toLowerCase().contains("Amount".toLowerCase())) {
				total1 = nextNumber(blocks, i);
				if (total1!= "" && Double.parseDouble(total1) > maxi_amount) {
					maxi_amount = Double.parseDouble(total1);
				}
			} else if (temp.equalsIgnoreCase("Total") || temp.equalsIgnoreCase("Net")) {
				total2 = nextNumber(blocks, i);
				if (total2!= "" && Double.parseDouble(total2) > maxi_amount) {
					maxi_amount = Double.parseDouble(total2);
				}
			}

			result.setTotal(maxi_amount);
			
			String temp_invoice_date="";
			String temp_dep_date="";

			// Get invoice date
			if ((temp.toLowerCase().contains("Invoice Date".toLowerCase())
					|| (temp.toLowerCase().contains("Invoice".toLowerCase())
							&& blocks.get(i + 1).getText().toLowerCase().contains("Date".toLowerCase())))
					&& invoiceDateFlag == true) {
				temp_invoice_date = getnextDate(blocks, i);
				result.setInvoiceDate(temp_invoice_date);
				invoiceDateFlag = false;
			}

			// Get Departure date
			if ((temp.toLowerCase().contains("Departure Date".toLowerCase())
					|| temp.toLowerCase().contains("Dep".toLowerCase())) && depDateFlag == true) {
				temp_dep_date = getnextDate(blocks, i);
				result.setDepDate(temp_dep_date);
				depDateFlag = false;
			}
			long diff = 12l;
			if(result.getInvoiceDate()!="" && result.getDepDate()!="") {
				diff=getDiffDates(result.getInvoiceDate(), result.getDepDate());
				if(diff < 7l) {
					result.setFlight(true);
				}
			}
			

			// Alcohol Test
			if (basicFilter(temp)) {
				continue;
			} else {
				if (alcoholTest(temp.toLowerCase())) {
					result.setAlcohol(true);
				}
				if (fuelTest(temp.toLowerCase())) {
					result.setFuel(true);
				}
			}
		}

		return result;
	}
	
	private static long getDiffDates(String temp_invoice_date, String temp_dep_date) {
		SimpleDateFormat myFormat = new SimpleDateFormat("dd/MM/yyyy");
		long diff=0l;
		try {
			Date date1 = myFormat.parse(temp_invoice_date);
			Date date2 = myFormat.parse(temp_dep_date);
			diff = date1.getTime() - date2.getTime();
			return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
	}

	private static boolean fuelTest(String theWord) throws IOException {
		String fileString = new String(
				Files.readAllBytes(
						Paths.get(System.getProperty("user.dir")+ "/src/main/resources/fuel.txt")),
				StandardCharsets.UTF_8);
		if (fileString.contains(theWord)) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean basicFilter(String temp) {
		return ((temp != null) && (!temp.equals("")) && (temp.matches("^[a-zA-Z]{0,3}$")));
	}

	private static boolean alcoholTest(String theWord) throws IOException {
		String fileString = new String(
				Files.readAllBytes(
						Paths.get(System.getProperty("user.dir")+ "/src/main/resources/drinks2.txt")),
				StandardCharsets.UTF_8);
		if (fileString.contains(theWord)) {
			return true;
		} else {
			return false;
		}
	}

	public static String nextDateTest(List<String> blocks, int i) {
		String date = "";
		for (int j = i; j < blocks.size(); j++) {
			for (int k = 0; k < blocks.get(j).length(); k++) {
				if (validDate(blocks.get(j).substring(k))) {
					date = blocks.get(j);
					break;
				}
				if (validDate(blocks.get(j).substring(0, k))) {
					date = blocks.get(j);
					break;
				}
			}
		}
		return date;
	}

//	public static void main(String[] args) throws IOException {
//		List<String> str = new ArrayList<>();
//		str.add("Total");
//		str.add("Dep:17/11/2013");
//		str.add("234");
//		TestingSyncHelper testingSyncHelper = new TestingSyncHelper();
//		System.out.println(testingSyncHelper.nextDateTest(str, 0));
//		System.out.println(alcoholTest("jack daniels"));
//		System.out.println(basicFilter("babaa"));
//		System.out.println(System.getProperty("user.dir"));
//	}
}