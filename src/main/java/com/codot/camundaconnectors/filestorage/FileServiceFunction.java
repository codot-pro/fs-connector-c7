package com.codot.camundaconnectors.filestorage;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class FileServiceFunction implements JavaDelegate {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceFunction.class);

	@Override
	public void execute(DelegateExecution delegateExecution) throws Exception {
		// using config.properties
		// Properties properties = getProperties();
		// String url = validateURL(properties.getProperty("url"));

		// using template

		String url = validateURL((String) delegateExecution.getVariable("url"));

		String operation = (String) delegateExecution.getVariable("operation");
		switch (operation) {
			case "upload" -> {
				String guid = FileOperation.upload(
						url,
						(String) delegateExecution.getVariable("filePath"),
						(String) delegateExecution.getVariable("fileName"));
				delegateExecution.setVariable("fs_result", guid); // guid
			}
			case "get" -> {
				String path = FileOperation.get(
						url,
						(String) delegateExecution.getVariable("fileId"),
						(String) delegateExecution.getVariable("filePath"));
				delegateExecution.setVariable("fs_result", path); // path
			}
			case "delete" -> {
				String answer = FileOperation.delete(
						url,
						(String) delegateExecution.getVariable("fileId"));
				delegateExecution.setVariable("fs_result", answer); // deleteResult
			}
			default -> throw new RuntimeException("Operation not found");
		}
	}

	// using config.properties
	private static Properties getProperties() throws IOException {
		FileInputStream fis;
		Properties property = new Properties();

		fis = new FileInputStream("src/main/resources/config.properties");
		property.load(fis);
		fis.close();

		return property;
	}

	private static String validateURL(String URL){
		return URL.endsWith("/") ? URL : URL + "/";
	}
}
