package com.rit.cleaner.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rit.cleaner.ParcerChangesModel.ChangesRoot;
import com.rit.cleaner.ParcerReviewModel.Review;
import com.rit.cleaner.ParcerReviewModel.ReviewId;
import com.rit.cleaner.ParcerReviewModel.ReviewRoot;
import com.rit.cleaner.enums.RequestURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class UpsourceCleaner {

	private static final Logger logger = LoggerFactory.getLogger(UpsourceCleaner.class);

	private static final ObjectMapper REVIEW_MAPPER = new ObjectMapper();
	private static final ObjectMapper REVISION_MAPPER = new ObjectMapper();

	private static final List<String> LIST_OF_REVIEWS_ID = new ArrayList<>();
	private static final List<String> REVIEWS_WITH_NO_REVISIONS = new ArrayList<>();

	public static void main(String[] args) {

		try {
			createReviewIdList(getReviewList());
			getReviewsWithNoRevisions();
			closeEmptyReviews();
		} catch (IOException protocolException) {
			protocolException.printStackTrace();
		}

		System.out.println(REVIEWS_WITH_NO_REVISIONS);

	}

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
		LIST_OF_REVIEWS_ID
				.forEach(str -> makeSummaryChangesRequest(getConnection(RequestURL.GET_SUM_CHANGES), str));
	}

	private static void closeEmptyReviews() {
		REVIEWS_WITH_NO_REVISIONS
				.forEach(reviewId -> makeCloseReviewRequest(getConnection(RequestURL.CLOSE_REVIEW), reviewId));
	}

	/**
	 * Добавляет в лист REVIEWS_WITH_NO_REVISIONS ревью без ревизий
	 */
	private static void makeSummaryChangesRequest(HttpURLConnection con, String reviewId) {
		try {

			String jsonRequest = "{\"reviewId\": {\"projectId\": \"elk\", \"reviewId\":" + "\"" + reviewId + "\"}}";
			String response = doPostRequestAndReceiveResponse(con, jsonRequest);

			ChangesRoot revisionRootObj = REVISION_MAPPER.readValue(response, ChangesRoot.class);
			//у пустых ревью есть аннотация "Review does not contain any revisions."
			if (revisionRootObj.getResult().getAnnotation() != null) {
				REVIEWS_WITH_NO_REVISIONS.add(reviewId);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void makeCloseReviewRequest(HttpURLConnection con, String reviewId) {
		String jsonRequest = "{\"reviewId\": {\"projectId\": \"elk\", \"reviewId\":" + "\"" + reviewId + "\"" + "}, \"isFlagged\":" + true + "}";
		doPostRequestAndReceiveResponse(con, jsonRequest);
		logger.info("Ревью " + reviewId + " закрыто");
	}

	/**
	 * @return возвращает первые limit пустых/закрытых ревью в проекте, в порядке убывания
	 */
	private static String getReviewList() {

		HttpURLConnection con = getConnection(RequestURL.GET_REVIEWS);
		String jsonRequest = "{\"limit\": 100, \"sortBy\": \"id,desc\"}";

		return doPostRequestAndReceiveResponse(con, jsonRequest);
	}

	private static HttpURLConnection getConnection(RequestURL requestURL) {
		URL url = null;
		try {
			url = new URL(requestURL.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return configureConnection(url);
	}

	private static HttpURLConnection configureConnection(URL url) {
		HttpURLConnection con = null;
		try {

			BufferedReader br = new BufferedReader(new FileReader("lgpw.txt"));
			String authData = br.readLine();
			br.close();

			String basicAuth = "Basic " + /*тут вставить закодированные лог-пасс и убрать 3 строки выше*/new String(Base64.getEncoder().encode(authData.getBytes()));

			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setRequestProperty("Accept", "application/json");
			con.setRequestProperty("Authorization", basicAuth);
			con.setRequestProperty("Content-Type", "application/json");

		} catch (IOException e) {
			e.printStackTrace();
		}
		return con;
	}

}