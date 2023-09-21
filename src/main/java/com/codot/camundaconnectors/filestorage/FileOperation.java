package com.codot.camundaconnectors.filestorage;


import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FileOperation {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileOperation.class);

	public static void upload(String url, String jwt, String fileName, Response executionResponse, DelegateExecution delegateExecution) {
		File file = new File(System.getProperty("java.io.tmpdir"), fileName);

		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "multipart/form-data");
		if (jwt != null) headers.put("Authorization", "Bearer " + jwt);

		if (file.exists()) {
			Connection.Response response;
			try {
				FileInputStream fileInputStream = new FileInputStream(file);
				response =
						Jsoup.connect(url)
								.method(Connection.Method.POST)
								.ignoreContentType(true)
								.headers(headers)
								.data(
										"file",
										file.getName(),
										fileInputStream,
										"multipart/form-data")
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

	public static void get(String url, String jwt, String guid, Response executionResponse, DelegateExecution delegateExecution) {
		Map<String, String> headers = new HashMap<>();
		if (jwt != null) headers.put("Authorization", "Bearer " + jwt);

		Connection.Response response;
		try {
			response =
					Jsoup
							.connect(url + guid)
							.headers(headers)
							.method(Connection.Method.GET)
							.ignoreContentType(true)
							.execute();
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

	public static void delete(String url, String jwt, String guid, Response executionResponse, DelegateExecution delegateExecution) {
		Map<String, String> headers = new HashMap<>();
		if (jwt != null) headers.put("Authorization", "Bearer " + jwt);

		Connection.Response response;
		try {
			response =
					Jsoup
							.connect(url + guid)
							.headers(headers)
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