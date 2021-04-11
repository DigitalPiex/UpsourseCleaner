package com.rit.cleaner.PercerRevisionModel;

public class RevisionResult {
	public AllRevisions allRevisions;
	public NewRevisions newRevisions;
	public boolean hasMissingRevisions;
	public boolean canSquash;
	public String branchHint;

	public AllRevisions getAllRevisions() {
		return allRevisions;
	}

	public NewRevisions getNewRevisions() {
		return newRevisions;
	}

	public boolean isHasMissingRevisions() {
		return hasMissingRevisions;
	}

	public boolean isCanSquash() {
		return canSquash;
	}

	public String getBranchHint() {
		return branchHint;
	}
}