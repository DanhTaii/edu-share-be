package vn.edu.nlu.edushare.edu_share.api.mail.util;

import java.util.Random;

public class OtpGenerator {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String generateOtp() {

        Random random = new Random();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            otp.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }

        return otp.toString();
    }
}