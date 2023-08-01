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
		Properties properties = getProperties();
		String url = properties.getProperty("url");
		LOGGER.debug("url=" + url);

		String operation = (String) delegateExecution.getVariable("operation");
		LOGGER.debug("operation=" + operation);

		switch (operation) {
			case "upload" -> {
				String guid = FileOperation.upload(
						url,
						(String) delegateExecution.getVariable("filePath"),
						(String) delegateExecution.getVariable("fileName"));
				delegateExecution.setVariable("uploadResult", guid); // guid
			}
			case "get" -> {
				String path = FileOperation.get(
						url,
						(String) delegateExecution.getVariable("guid"),
						(String) delegateExecution.getVariable("filePath"));
				delegateExecution.setVariable("getResult", path); // path
			}
			case "delete" -> {
				String answer = FileOperation.delete(
						url,
						(String) delegateExecution.getVariable("guid"));
				delegateExecution.setVariable("deleteResult", answer); // deleteResult
			}
			default -> throw new RuntimeException("Operation not found");
		}
	}

	private static Properties getProperties() throws IOException {
		FileInputStream fis;
		Properties property = new Properties();

		fis = new FileInputStream("src/main/resources/config.properties");
		property.load(fis);
		fis.close();

		return property;
	}
}
