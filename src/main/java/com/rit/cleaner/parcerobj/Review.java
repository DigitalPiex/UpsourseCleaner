package com.rit.cleaner.parcerobj;

import java.util.List;

// import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString), Root.class); */

public class Review {
    public ReviewId reviewId;
    public String title;
    public List<Participant> participants;
    public int state;
    public boolean isUnread;
    public boolean isReadyToClose;
    public boolean isRemoved;
    public long createdAt;
    public String createdBy;
    public long updatedAt;
    public CompletionRate completionRate;
    public DiscussionCounter discussionCounter;
    public boolean isMuted;
}

