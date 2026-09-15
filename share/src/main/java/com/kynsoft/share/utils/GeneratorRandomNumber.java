package com.kynsoft.share.utils;

import java.math.BigInteger;
import java.security.SecureRandom;

public class GeneratorRandomNumber {

    public static String generateRandomSecurity() {
        SecureRandom secureRandom = new SecureRandom();
        BigInteger bigInteger = new BigInteger(128, secureRandom).abs(); // Genera un número aleatorio de 128 bits
        return bigInteger.toString().substring(0, 12);
    }
}
