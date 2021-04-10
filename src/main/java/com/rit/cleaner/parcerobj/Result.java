package com.rit.cleaner.parcerobj;

import java.util.List;

// import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString), Root.class); */

public class Result {
    public List<Review> reviews;
    public boolean hasMore;
    public int totalCount;
}