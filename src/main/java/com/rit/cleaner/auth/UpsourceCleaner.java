package com.rit.cleaner.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rit.cleaner.ParcerChangesModel.ChangesRoot;
import com.rit.cleaner.ParcerReviewModel.Review;
import com.rit.cleaner.ParcerReviewModel.ReviewId;
import com.rit.cleaner.ParcerReviewModel.ReviewRoot;
import com.rit.cleaner.enums.RequestURL;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UpsourceCleaner {

	private static final ObjectMapper REVIEW_MAPPER = new ObjectMapper();
	private static final ObjectMapper REVISION_MAPPER = new ObjectMapper();

	private static final List<String> LIST_OF_REVIEWS_ID = new ArrayList<>();
	private static final List<String> REVIEWS_WITH_NO_REVISIONS = new ArrayList<>();

	public static void main(String[] args) {

		try {

			long a = System.currentTimeMillis();

			createReviewIdList(getReviewList());
			getReviewsWithNoRevisions();

			long b = System.currentTimeMillis();
			System.out.println(b - a);

		} catch (IOException protocolException) {
			protocolException.printStackTrace();
		}

		Collections.sort(REVIEWS_WITH_NO_REVISIONS);
		System.out.println(REVIEWS_WITH_NO_REVISIONS);

	}

	/**
	 * Обработчик запросов
	 */
	private static String doPostRequestAndReceiveResponse(HttpURLConnection con, String jsonRequest) {

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

	/**
	 *
	 * @return - настраивает и возвращает коннекцию
	 */
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

	/**
	 * Извлекает айдишники всех ревью из ответа и добавляет их в LIST_OF_REVIEWS_ID
	 *
	 * @param response - json-ответ на наш запрос
	 */
	private static void createReviewIdList(String response) throws com.fasterxml.jackson.core.JsonProcessingException {
		ReviewRoot reviewRootObj = REVIEW_MAPPER.readValue(response, ReviewRoot.class);

		reviewRootObj
				.getResult()
				.getReviews()
				.stream()
				.map(Review::getReviewId)
				.map(ReviewId::getReviewId)
				.forEach(LIST_OF_REVIEWS_ID::add);
	}

	private static void getReviewsWithNoRevisions() {
		LIST_OF_REVIEWS_ID.forEach(str -> makeSummaryChangesRequest(setConnectionForSummaryChangesInReview(), str));
	}

	/**
	 * @param con - HttpURLConnection для данного запроса
	 * @param reviewId - номер ревью
	 *
	 * Добавляет в лист REVIEWS_WITH_NO_REVISIONS ревью без ревизий
	 */
	private static void makeSummaryChangesRequest(HttpURLConnection con, String reviewId) {
		try {

			String jsonRequest = "{\"reviewId\": {\"projectId\": \"elk\", \"reviewId\":" + "\"" + reviewId + "\"}}";
			String response = doPostRequestAndReceiveResponse(con, jsonRequest);

			ChangesRoot revisionRootObj = REVISION_MAPPER.readValue(response, ChangesRoot.class);

			if (revisionRootObj.getResult().getAnnotation() != null) {
				REVIEWS_WITH_NO_REVISIONS.add(reviewId);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return возвращает все ревью в проекте
	 */
	private static String getReviewList() throws IOException {

		HttpURLConnection con = getConnectionForReviewList();
		String jsonRequest = "{\"limit\": 100000}";

		return doPostRequestAndReceiveResponse(con, jsonRequest);
	}

	/**
	 * @return возвращает коннекцию для получения всех ревизий в ревью
	 */
	private static HttpURLConnection setConnectionForSummaryChangesInReview() {
		URL getReviewRequestUrl = null;
		try {
			getReviewRequestUrl = new URL(RequestURL.GET_SUM_CHANGES.toString());

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
			getReviewRequestUrl = new URL(RequestURL.GET_REVIEWS.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return configureConnection(getReviewRequestUrl);
	}
}