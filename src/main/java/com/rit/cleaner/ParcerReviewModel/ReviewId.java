package com.rit.cleaner.ParcerReviewModel;

public class ReviewId {
	public String projectId;
	public String reviewId;

	public String getProjectId() {
		return projectId;
	}

	public String getReviewId() {
		return reviewId;
	}

	@Override
	public String toString() {
		return "reviewId='" + reviewId + '\'';
	}
}
