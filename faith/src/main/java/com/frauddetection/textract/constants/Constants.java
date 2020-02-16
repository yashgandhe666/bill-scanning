package com.frauddetection.textract.constants;

import com.amazonaws.regions.Regions;

public enum Constants {
    BUCKET("expense-faithplusone");

    public static final String BUCKET_NAME = "expense-faithplusone";
    public static final Regions DEFAULT_REG = Regions.US_EAST_2;


    private final String name;
    private Constants(String name) {
        this.name = name;
    }
}