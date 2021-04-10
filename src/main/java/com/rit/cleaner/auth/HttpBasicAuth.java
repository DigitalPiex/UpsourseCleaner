package com.rit.cleaner.auth;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rit.cleaner.parcerobj.Root;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class HttpBasicAuth {

	public static void main(String[] args) {

		try {

			URL getReviewRequestUrl = new URL("https://codereview.ritperm.rt.ru/~rpc/getReviews");
			HttpURLConnection con = (HttpURLConnection) getReviewRequestUrl.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setRequestProperty("Accept", "application/json");
			con.setRequestProperty("Authorization", "Basic cGlyb3poa292LW5hOldpZGFlIUZhNg==");
			con.setRequestProperty("Content-Type", "application/json");

			String data = "{\"projectId\": \"elk\", \"sortBy\": \"id,desc\", \"limit\": 1}";

			try (OutputStream os = con.getOutputStream()) {
				byte[] input = data.getBytes(StandardCharsets.UTF_8);
				os.write(input, 0, input.length);
			}

			try (BufferedReader br = new BufferedReader(
					new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
				StringBuilder response = new StringBuilder();
				String responseLine;
				while ((responseLine = br.readLine()) != null) {
					response.append(responseLine.trim());
				}
				System.out.println(response);

				ObjectMapper mapper = new ObjectMapper();

				Root student = mapper.readValue(response.toString(), Root.class);

				System.out.println(student);

				String responseString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(student);

				System.out.println(responseString);
				
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println(con.getResponseCode() + " " + con.getResponseMessage());
			con.disconnect();

		} catch (IOException protocolException) {
			protocolException.printStackTrace();
		}
	}
}
