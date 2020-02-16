package com.frauddetection.textract;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

import com.amazonaws.services.textract.AmazonTextract;
import com.amazonaws.services.textract.AmazonTextractClientBuilder;
import com.amazonaws.services.textract.model.DetectDocumentTextRequest;
import com.amazonaws.services.textract.model.DetectDocumentTextResult;
import com.amazonaws.services.textract.model.Document;
import com.amazonaws.util.IOUtils;

public class Testing {

    static final Logger logger = Logger.getLogger(String.valueOf(Testing.class));

    ByteBuffer imageBytes;

    InputStream inputStream;
    public DetectDocumentTextResult test(String document) {

        try {
            inputStream = new FileInputStream(new File(System.getProperty("user.dir")+"/src/main/resources/" + document) );
            imageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
        AmazonTextract client = AmazonTextractClientBuilder.defaultClient();

        DetectDocumentTextRequest request = new DetectDocumentTextRequest()
                .withDocument(new Document()
                        .withBytes(imageBytes));


        DetectDocumentTextResult result = client.detectDocumentText(request);
        return result;
    }
}
