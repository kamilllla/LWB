package com.example.lwb;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerificationAndValidation {
    public static StringBuilder getPassword(String password){
        StringBuilder hash= new StringBuilder();
        MessageDigest messageDigest= null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] bytes=messageDigest.digest(password.getBytes());
        for (byte b: bytes){
            hash.append(String.format("%02X",b));
        }
        return hash;
    }
    public static boolean checkNumberIncorrect(String number){
        String regex = "^((\\+7)|8)\\(?9[\\d]{2}\\)?[\\d]{3}\\-?[\\d]{2}\\-?[\\d]{2}\\-?$";
        number=number.replaceAll("\\s+", "");

        if(Pattern.matches(regex,number))
            return false;
        else
            return true;
    }

    public static boolean checkEmailIncorrect(String email){
        String regex =  "^([a-zA-Z0-9]{1,})+([a-zA-Z0-9]{0,})+([_.-]{0,1})([a-zA-Z0-9]{1,})+@([a-z0-9]{1,})+[\\.]{1}[a-z]+$";
        email=email.trim();

        if(Pattern.matches(regex,email))
            return false;
        else
            return true;
    }
    public static boolean checkFIOIncorrect(String fio){
        String regex =  "^([a-zA-ZА-Яа-я]{2,})+$";
        fio=fio.trim();

        if(Pattern.matches(regex,fio))
            return false;
        else
            return true;
    }


}
