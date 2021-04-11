package com.rit.cleaner.ParcerReviewModel;

import java.util.List;

public class Review {
    public ReviewId reviewId;
    public String title;
    public List<Participant> participants;
    public int state;
    public boolean isUnread;
    public boolean isReadyToClose;
    public boolean isRemoved;
    public Object createdAt;
    public String createdBy;
    public Object updatedAt;
    public CompletionRate completionRate;
    public DiscussionCounter discussionCounter;
    public boolean isMuted;
    public List<String> branch;

    public ReviewId getReviewId() {
        return reviewId;
    }

    public String getTitle() {
        return title;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public int getState() {
        return state;
    }

    public boolean isUnread() {
        return isUnread;
    }

    public boolean isReadyToClose() {
        return isReadyToClose;
    }

    public boolean isRemoved() {
        return isRemoved;
    }

    public Object getCreatedAt() {
        return createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Object getUpdatedAt() {
        return updatedAt;
    }

    public CompletionRate getCompletionRate() {
        return completionRate;
    }

    public DiscussionCounter getDiscussionCounter() {
        return discussionCounter;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public List<String> getBranch() {
        return branch;
    }
}

