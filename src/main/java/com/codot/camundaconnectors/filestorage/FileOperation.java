package com.codot.camundaconnectors.filestorage;


import java.io.*;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.feel.syntaxtree.In;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FileOperation {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileOperation.class);

	public static void upload(String url, String filePath, String fileName, Response executionResponse, DelegateExecution delegateExecution) {
		File file = new File(new File(filePath), fileName);
		if (file.exists()) {
			Connection.Response response;
			try {
				FileInputStream fileInputStream = new FileInputStream(file);
				response =
						Jsoup.connect(url)
								.method(Connection.Method.POST)
								.ignoreContentType(true)
								.header("Content-Type", "multipart/form-data")
								.data(
										"file",
										file.getName(),
										fileInputStream,
										"multipart/form-data") // Attach the file to the request
								.execute();
				fileInputStream.close();

				executionResponse.setStatusCode(Integer.toString(response.statusCode()));
				executionResponse.setStatusMsg(response.statusMessage());
				executionResponse.setResponse(response.body());

				if (response.statusCode() == 200)
					file.delete();
				else LOGGER.error(Utility.printLog("File not uploaded", delegateExecution));
			} catch (IOException e) {
				LOGGER.error(Utility.printLog(e.getClass().getSimpleName() + ": " + e, delegateExecution));
			}
		}
		LOGGER.error(Utility.printLog("File not created", delegateExecution));
	}

	public static void get(String url, String guid, String savePath, Response executionResponse, DelegateExecution delegateExecution) {
		Connection.Response response;
		try {
			response =
					Jsoup.connect(url + guid).method(Connection.Method.GET).ignoreContentType(true).execute();
			executionResponse.setStatusCode(Integer.toString(response.statusCode()));
			executionResponse.setStatusMsg(response.statusMessage());
			if (response.statusCode() == 200) {
				byte[] fileContent = response.bodyAsBytes();
				File file = new File(new File(savePath), guid);
				try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
					for (byte item : fileContent) {
						outputStream.write(item);
					}
				} catch (IOException e) {
					LOGGER.error(Utility.printLog(e.getClass().getSimpleName() + ": " + e, delegateExecution));
				}
				executionResponse.setResponse(file.getAbsolutePath());
			}
		} catch (IOException e) {
			LOGGER.error(Utility.printLog(e.getClass().getSimpleName() + ": " + e, delegateExecution));
		}
	}

	public static void delete(String url, String guid, Response executionResponse, DelegateExecution delegateExecution) {
		Connection.Response response;
		try {
			response =
					Jsoup.connect(url + guid)
							.method(Connection.Method.DELETE)
							.ignoreContentType(true)
							.execute();
			executionResponse.setStatusCode(Integer.toString(response.statusCode()));
			executionResponse.setStatusMsg(response.statusMessage());
			executionResponse.setResponse(response.body());
		} catch (IOException e) {
			LOGGER.error(Utility.printLog(e.getClass().getSimpleName() + ": " + e, delegateExecution));
		}
	}
}