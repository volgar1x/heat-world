package org.heat.world.controllers.utils;

import org.heat.shared.Strings;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Md5 {

    private Md5() { }

    private static final ThreadLocal<MessageDigest> LOCAL_DIGEST = new ThreadLocal<MessageDigest>() {

        @Override
        protected MessageDigest initialValue() {
            try {
                return MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new Error(e);
            }
        }
    };

    public static String hash(int playerId, String secretAnswer) {
        String concat = Integer.toString(playerId, 10) + '~' + secretAnswer;
        byte[] digest = LOCAL_DIGEST.get().digest(concat.getBytes());
        return Strings.toHexBytes(digest);
    }
}
