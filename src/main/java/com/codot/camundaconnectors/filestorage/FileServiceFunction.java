package com.codot.camundaconnectors.filestorage;

import org.camunda.bpm.engine.ExternalTaskService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

@Component
public class FileServiceFunction implements JavaDelegate {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceFunction.class);
	private static final String result = null;

	@Override
	public void execute(DelegateExecution delegateExecution) throws Exception {
		Properties properties = getProperties();
		String url = properties.getProperty("url");

		String operation = (String) delegateExecution.getVariable("operation");


		
	}

	private static Properties getProperties() throws IOException {
		FileInputStream fis;
		Properties property = new Properties();

		fis = new FileInputStream("src/main/resources/config.properties");
		property.load(fis);

		return property;

	}
}
