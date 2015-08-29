package com.spark.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OAuth2AccessTokenResp {

		@JsonProperty("access_token")
	    private String accessToken;
	    private String tokenType;
	    private Long expiresIn;
	    private String scope;

	    public void setAccessToken(String accessToken) {
	        this.accessToken = accessToken;
	    }

	    public void setTokenType(String tokenType) {
	        this.tokenType = tokenType;
	    }

	    public void setExpiresIn(Long expiresIn) {
	        this.expiresIn = expiresIn;
	    }

	    public void setScope(String scope) {
	        this.scope = scope;
	    }

	    public String getAccessToken(){
	        return accessToken;
	    }

	    public String getTokenType(){
	        return tokenType;
	    }

	    public Long getExpiresIn(){
	        return expiresIn;
	    }

	    public String getScope(){
	        return scope;
	    }

}
