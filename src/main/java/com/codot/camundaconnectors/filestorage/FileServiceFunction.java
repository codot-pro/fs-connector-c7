package com.codot.camundaconnectors.filestorage;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class FileServiceFunction implements JavaDelegate {
	Response response = new Response();
	private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceFunction.class);

	@Override
	public void execute(DelegateExecution delegateExecution) throws Exception {

		String url = Utility.validateURL((String) delegateExecution.getVariable("url"));

		String operation = (String) delegateExecution.getVariable("operation");
		switch (operation) {
			case "upload":
				FileOperation.upload(
						url,
						(String) delegateExecution.getVariable("filePath"),
						(String) delegateExecution.getVariable("fileName"),
						response,
						delegateExecution);
				packRespond(delegateExecution);
				break;
			case "get":
				FileOperation.get(
						url,
						(String) delegateExecution.getVariable("fileId"),
						(String) delegateExecution.getVariable("filePath"),
						response,
						delegateExecution);
				packRespond(delegateExecution);
			 	break;
			case "delete":
				FileOperation.delete(
						url,
						(String) delegateExecution.getVariable("fileId"),
						response,
						delegateExecution);
				packRespond(delegateExecution);
				break;
			default:
				response.setStatusCode("400");
				response.setStatusMsg("Bad request. Invalid operation");
				packRespond(delegateExecution);
				break;
		}
	}

	private void packRespond(DelegateExecution delegateExecution){
		delegateExecution.setVariable("fs_result", response.getResponse());
		delegateExecution.setVariable("status_code", response.getStatusCode());
		delegateExecution.setVariable("status_msg", response.getStatusMsg());
	}
}
