package com.iamjunhyeok.petSitterAndWalker.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class UtilService {

    public String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.length() == 0 || ipAddress.equalsIgnoreCase("unknown")) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || ipAddress.equalsIgnoreCase("unknown")) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || ipAddress.equalsIgnoreCase("unknown")) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.length() == 0 || ipAddress.equalsIgnoreCase("unknown")) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED");
        }
        if (ipAddress == null || ipAddress.length() == 0 || ipAddress.equalsIgnoreCase("unknown")) {
            ipAddress = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || ipAddress.equalsIgnoreCase("unknown")) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || ipAddress.equalsIgnoreCase("unknown")) {
            ipAddress = request.getHeader("HTTP_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.length() == 0 || ipAddress.equalsIgnoreCase("unknown")) {
            ipAddress = request.getHeader("HTTP_FORWARDED");
        }
        if (ipAddress == null || ipAddress.length() == 0 || ipAddress.equalsIgnoreCase("unknown")) {
            ipAddress = request.getHeader("HTTP_VIA");
        }
        if (ipAddress == null || ipAddress.length() == 0 || ipAddress.equalsIgnoreCase("unknown")) {
            ipAddress = request.getHeader("REMOTE_ADDR");
        }
        if (ipAddress == null || ipAddress.length() == 0 || ipAddress.equalsIgnoreCase("unknown")) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    public String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
}
