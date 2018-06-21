package com.hanover.bordereaux;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * 
 * @author CTS
 *
 */
@SpringBootApplication
@EnableBatchProcessing
public class BordereauxUploadServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BordereauxUploadServiceApplication.class, args);
	}
}
