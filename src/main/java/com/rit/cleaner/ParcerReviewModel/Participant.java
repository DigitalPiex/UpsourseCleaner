package com.rit.cleaner.ParcerReviewModel;

// import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString), Root.class); */

public class Participant {
    public String userId;
    public int role;
    public int state;

    public String getUserId() {
        return userId;
    }

    public int getRole() {
        return role;
    }

    public int getState() {
        return state;
    }
}

