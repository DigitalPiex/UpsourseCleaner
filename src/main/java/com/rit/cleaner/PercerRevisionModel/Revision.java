package com.rit.cleaner.PercerRevisionModel;

import java.util.List;

public class Revision {
	public String projectId;
	public String revisionId;
	public Object revisionDate;
	public Object effectiveRevisionDate;
	public String revisionCommitMessage;
	public int state;
	public String vcsRevisionId;
	public String shortRevisionId;
	public String authorId;
	public int reachability;
	public List<String> parentRevisionIds;
	public List<String> branchHeadLabel;
}
