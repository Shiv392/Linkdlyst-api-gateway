package com.Linkdlyst.api_gateway.Utils.Dtos;

public class JwtContext {
    private Long userId;
    private String userEmail;
    private String tokenType;

    public JwtContext(){}

    public JwtContext(Long _userId, String _userEmail, String _tokenType){
        userId = _userId;
        userEmail = _userEmail;
        tokenType = _tokenType;
    }

    public long getUserId(){
        return userId;
    }
    public String getUserEmail(){
        return userEmail;
    }
    public String getTokenType(){
        return tokenType;
    }
}
