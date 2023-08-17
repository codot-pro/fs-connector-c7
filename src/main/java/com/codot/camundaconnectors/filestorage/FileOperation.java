package com.codot.camundaconnectors.filestorage;


import java.io.*;
import java.nio.file.Files;

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

	public static void upload(String url, String fileName, Response executionResponse, DelegateExecution delegateExecution) {
		File file = new File(System.getProperty("java.io.tmpdir"), fileName);
		System.out.println(file.getAbsolutePath());
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
				return;
			} catch (IOException e) {
				LOGGER.error(Utility.printLog(e.getClass().getSimpleName() + ": " + e, delegateExecution));
				return;
			}
		}
		LOGGER.error(Utility.printLog("File not found", delegateExecution));
	}

	public static void get(String url, String guid, Response executionResponse, DelegateExecution delegateExecution) {
		Connection.Response response;
		try {
			response =
					Jsoup.connect(url + guid).method(Connection.Method.GET).ignoreContentType(true).execute();
			executionResponse.setStatusCode(Integer.toString(response.statusCode()));
			executionResponse.setStatusMsg(response.statusMessage());
			if (response.statusCode() == 200) {
				byte[] fileContent = response.bodyAsBytes();
				File file = new File(System.getProperty("java.io.tmpdir"), guid);
				try (BufferedOutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(file.toPath()))) {
					for (byte item : fileContent) {
						outputStream.write(item);
					}
				} catch (IOException e) {
					LOGGER.error(Utility.printLog(e.getClass().getSimpleName() + ": " + e, delegateExecution));
				}
				executionResponse.setResponse(file.getName());
			}
		} catch (IOException e) {
			LOGGER.error(Utility.printLog(e.getClass().getSimpleName() + ": " + e.getMessage(), delegateExecution));
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
			LOGGER.error(Utility.printLog(e.getClass().getSimpleName() + ": " + e.getMessage(), delegateExecution));
		}
	}
}