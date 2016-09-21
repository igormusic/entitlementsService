package com.rbc.bdc;

import javax.validation.constraints.NotNull;

public class EntitlementSet {
	@NotNull
	private String userId;
	@NotNull
	private Object entitlements;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Object getEntitlements() {
		return entitlements;
	}
	public void setEntitlements(Object entitlements) {
		this.entitlements = entitlements;
	}

	
	
	
}
