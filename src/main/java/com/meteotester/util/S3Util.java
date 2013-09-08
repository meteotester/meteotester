/*
 * Copyright 2010-2013 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.meteotester.util;

import java.io.File;


import org.apache.log4j.Logger;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.meteotester.config.Config;


public class S3Util {
	
	private static Logger log = Logger.getLogger(S3Util.class);
	
	public static void saveFileToS3(File file) {
		try {
		AWSCredentials myCredentials = new BasicAWSCredentials(Config.AWS_ACCESS_KEY, Config.AWS_SECRET_KEY);
		AmazonS3 s3client = new AmazonS3Client(myCredentials); 
		
		s3client.setRegion(Region.getRegion(Regions.EU_WEST_1));
		String filename = file.getName();
		String path = file.getPath();
		
		PutObjectRequest req = new PutObjectRequest(Config.S3_BUCKETNAME, path, file);
    	ObjectMetadata metadata = new ObjectMetadata();
    	metadata.setContentLength(file.length());
    	String contentType = (filename.contains("json"))?"application/json":"text/csv";
    	metadata.setContentType(contentType);
    	req.setMetadata(metadata);
       
        s3client.putObject(req);
		
		log.info(filename + " stored in S3");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
