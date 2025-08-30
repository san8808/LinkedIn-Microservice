package com.codecomet.linkedInProject.connectionService.auth;


public class AuthContextHolder {

    private static final ThreadLocal<Long> currentUserId = new ThreadLocal<>();

    public static Long getCurrentUserId(){
        return currentUserId.get();
    }

    static void setCurrentUserId(Long userId){
        currentUserId.set(userId);
    }

    //removing once used to avoid data leaks
    static void clear(){
        currentUserId.remove();
    }
}
