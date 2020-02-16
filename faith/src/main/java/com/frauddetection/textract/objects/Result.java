package com.frauddetection.textract.objects;

public class Result {
	private Double total = 0d;
	private String invoiceDate = "";
	private String depDate = "";
	private boolean alcohol = false;
	private boolean fuel = false;
	private boolean flight = false;

	public boolean isFlight() {
		return flight;
	}

	public void setFlight(boolean flight) {
		this.flight = flight;
	}
	
	public boolean isFuel() {
		return fuel;
	}

	public void setFuel(boolean fuel) {
		this.fuel = fuel;
	}

	public boolean isAlcohol() {
		return alcohol;
	}

	public void setAlcohol(boolean alcohol) {
		this.alcohol = alcohol;
	}

	public String getInvoiceDate() {
		return invoiceDate;
	}

	@Override
	public String toString() {
		return "Result [total=" + total + ", invoiceDate=" + invoiceDate + ", depDate=" + depDate + ", alcohol="
				+ alcohol + ", fuel=" + fuel + ", flight=" + flight + "]";
	}

	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public String getDepDate() {
		return depDate;
	}

	public void setDepDate(String depDate) {
		this.depDate = depDate;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public String getDate() {
		return invoiceDate;
	}

	public void setDate(String date) {
		this.invoiceDate = date;
	}
}
