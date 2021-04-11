package com.rit.cleaner.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rit.cleaner.ParcerReviewModel.Review;
import com.rit.cleaner.ParcerReviewModel.ReviewId;
import com.rit.cleaner.ParcerReviewModel.ReviewRoot;
import com.rit.cleaner.PercerRevisionModel.RevisionResult;
import com.rit.cleaner.PercerRevisionModel.RevisionRoot;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpsourceCleaner {

	private static final List<String> listOfReviewsId = new ArrayList<>();
	private static final List<String> listOfRevisions = new ArrayList<>();
	private static final List<String> reviewsWithEmptyRevisions = new ArrayList<>();

	public static void main(String[] args) {

		try {

			createReviewIdList(getReviewList());

/*			System.out.println(con.getResponseCode() + " " + con.getResponseMessage());
			con.disconnect();*/

		} catch (IOException protocolException) {
			protocolException.printStackTrace();
		}
		List<String> mm = new ArrayList<>();
		System.out.println(reviewsWithEmptyRevisions);

	}

	private static String doPostRequestAndReceiveResponse(HttpURLConnection con, String jsonRequest) throws IOException {

		StringBuilder response = new StringBuilder();
		try (OutputStream os = con.getOutputStream()) {
			byte[] input = jsonRequest.getBytes(StandardCharsets.UTF_8);
			os.write(input, 0, input.length);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {

			String responseLine;
			while ((responseLine = br.readLine()) != null) {
				response.append(responseLine.trim());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return response.toString();
	}

	private static HttpURLConnection configureConnection(URL url) {
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setRequestProperty("Accept", "application/json");
			con.setRequestProperty("Authorization", "Basic cGlyb3poa292LW5hOldpZGFlIUZhNg==");
			con.setRequestProperty("Content-Type", "application/json");

		} catch (IOException e) {
			e.printStackTrace();
		}
		return con;
	}

/*	private static void checkMissingRevisions(String response) throws com.fasterxml.jackson.core.JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		RevisionRoot rootObj = mapper.readValue(response, RevisionRoot.class);

		rootObj
				.getResult()
				.stream()
				.filter(RevisionResult::isHasMissingRevisions)
				.forEach();
	}*/


	private static void createReviewIdList(String response) throws com.fasterxml.jackson.core.JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		ReviewRoot reviewRootObj = mapper.readValue(response, ReviewRoot.class);

		reviewRootObj
				.getResult()
				.getReviews()
				.stream()
				.map(Review::getReviewId)
				.map(ReviewId::getReviewId)
				.forEach(listOfReviewsId::add);
	}


	private static void getRevisionsInReview() {
		listOfReviewsId.forEach(str -> makeRevisionRequest(setConnectionForRevisionsInReview(), str));
	}

	private static void makeRevisionRequest(HttpURLConnection con, String str) {
		try {

			String jsonRequest = "{\"projectId\": \"elk\", \"reviewId\":" + "\"" + str + "\"}";
			String response = doPostRequestAndReceiveResponse(con, jsonRequest);

			ObjectMapper revisionMapper = new ObjectMapper();
			RevisionRoot reviewRootObj = revisionMapper.readValue(response, RevisionRoot.class);

			if (reviewRootObj.getResult().isHasMissingRevisions()) {
				reviewsWithEmptyRevisions.add(str);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getReviewList() throws IOException {

		HttpURLConnection con = getConnectionForReviewList();
		String jsonRequest = "{\"limit\": 5}";

		return doPostRequestAndReceiveResponse(con, jsonRequest);
	}

	/**
	 * @return возвращает коннекцию для получения всех ревизий в ревью
	 */
	private static HttpURLConnection setConnectionForRevisionsInReview() {
		URL getReviewRequestUrl = null;
		try {
			getReviewRequestUrl = new URL("https://codereview.ritperm.rt.ru/~rpc/getRevisionsInReview");

		} catch (IOException e) {
			e.printStackTrace();
		}
		return configureConnection(getReviewRequestUrl);
	}

	/**
	 * @return возвращает коннекцию для получения всех ревью в проекте
	 */
	private static HttpURLConnection getConnectionForReviewList() {
		URL getReviewRequestUrl = null;
		try {
			getReviewRequestUrl = new URL("https://codereview.ritperm.rt.ru/~rpc/getReviews");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return configureConnection(getReviewRequestUrl);
	}
}
