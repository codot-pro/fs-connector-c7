package com.codot.camundaconnectors.filestorage;


import java.io.*;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileOperation {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileOperation.class);

	public static String upload(String url, String filePath, String fileName) {
		LOGGER.info(
				"Upload function has been called with the following values:"
						+ "\nFilePath: "
						+ filePath
						+ "\nFileName: "
						+ fileName);
		File file = new File(new File(filePath), fileName);
		if (file.exists()) {
			LOGGER.debug("File exists");
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
				if (response.statusCode() == 200) {
					LOGGER.info("Response statusCode = 200. File uploaded");
					JSONObject body = new JSONObject(response.body());
					if (file.delete()) LOGGER.debug("File sent and deleted");
					else LOGGER.error("File sent but not deleted");
					return body.getString("file_guid");
				}
				JSONObject body = new JSONObject(response.body());
				LOGGER.error(
						"HTTP error "
								+ response.statusCode()
								+ " :"
								+ response.statusMessage()
								+ " - "
								+ (body.has("message") ? body.getString("message") : ""));
			} catch (IOException e) {
				LOGGER.error(e.toString());
				throw new RuntimeException(e);
			}
		}
		LOGGER.error("File not created");
		return null;
	}

	public static String get(String url, String guid, String savePath) {
		LOGGER.info(
				"Get function has been called with the following values:"
						+ "\nGUID: "
						+ guid
						+ "\nSavePath: "
						+ savePath);
		Connection.Response response;
		try {
			response =
					Jsoup.connect(url + guid).method(Connection.Method.GET).ignoreContentType(true).execute();
			if (response.statusCode() == 200) {
				LOGGER.info("Response statusCode = 200. File received");
				byte[] fileContent = response.bodyAsBytes();
				File filePath = new File(savePath);
				File file = new File(filePath, guid);
				try (BufferedOutputStream outputStream =
						     new BufferedOutputStream(new FileOutputStream(file))) {
					LOGGER.debug("file on path \"" + savePath + guid + "\" created");
					for (byte item : fileContent) {
						outputStream.write(item);
					}
					LOGGER.debug("Byte stream was written");
				} catch (IOException e) {
					LOGGER.error(e.toString());
					throw new RuntimeException(e);
				}
				return savePath + guid;
			}
			JSONObject body = new JSONObject(response.body());
			LOGGER.error(
					"HTTP error "
							+ response.statusCode()
							+ " :"
							+ response.statusMessage()
							+ " - "
							+ (body.has("message") ? body.getString("message") : ""));
			return null;
		} catch (IOException e) {
			LOGGER.error(e.toString());
			throw new RuntimeException(e);
		}
	}

	public static String delete(String url, String guid) {
		LOGGER.info("Delete function has been called with the following values:" + "\nGUID: " + guid);
		Connection.Response response;
		try {
			response =
					Jsoup.connect(url + guid)
							.method(Connection.Method.DELETE)
							.ignoreContentType(true)
							.execute();
			JSONObject body = new JSONObject(response.body());
			if (response.statusCode() != 200) {
				LOGGER.error(
						"HTTP error "
								+ response.statusCode()
								+ " :"
								+ response.statusMessage()
								+ " - "
								+ (body.has("message") ? body.getString("message") : ""));
			}
			LOGGER.info("Response statusCode = 200. File deleted");
			return body.getString("message");
		} catch (IOException e) {
			LOGGER.error(e.toString());
			throw new RuntimeException(e);
		}
	}
}