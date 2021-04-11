package com.rit.cleaner.ParcerReviewModel;

import java.util.List;

public class ReviewResult {
    public List<Review> reviews;
    public boolean hasMore;
    public int totalCount;

    public List<Review> getReviews() {
        return reviews;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public int getTotalCount() {
        return totalCount;
    }
}