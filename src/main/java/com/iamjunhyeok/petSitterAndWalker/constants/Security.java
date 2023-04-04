package com.iamjunhyeok.petSitterAndWalker.constants;

public class Security {

    private Security() {
    }

    public static final int TOKEN_EXPIRATION = 7200000;
    public static final String BEARER = "Bearer ";
    public static final String AUTHORIZATION = "Authorization";
    public static final String REGISTER_PATH = "/join";
    public static final String SEND_PATH = "/verification/send";
    public static final String VERIFY_PATH = "/verification/verify";

}
