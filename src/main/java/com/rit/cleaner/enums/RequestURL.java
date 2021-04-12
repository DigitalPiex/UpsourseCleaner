package com.rit.cleaner.enums;

public enum RequestURL {
	GET_SUM_CHANGES("https://codereview.ritperm.rt.ru/~rpc/getReviewSummaryChanges"),
	GET_REVIEWS("https://codereview.ritperm.rt.ru/~rpc/getReviews"),
	CLOSE_REVIEW("https://codereview.ritperm.rt.ru/~rpc/closeReview");

	private final String requestURL;

	RequestURL(String requestURL) {
		this.requestURL = requestURL;
	}

	@Override
	public String toString() {
		return requestURL;
	}
}
