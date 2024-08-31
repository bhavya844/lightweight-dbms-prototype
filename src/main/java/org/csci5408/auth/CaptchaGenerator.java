package org.csci5408.auth;

import java.util.Random;


public class CaptchaGenerator {
    /**
     *The method generated Captcha which the user has to enter during signup and login
     * Reference - https://www.geeksforgeeks.org/program-generate-captcha-verify-user/
     * @return
     */
    public static String captchaGenerator(){
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int length=6;
        Random random=new Random();
        StringBuilder sb=new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();

    }
}
