package org.heat.login.users;

import com.typesafe.config.Config;
import lombok.SneakyThrows;
import org.fungsi.concurrent.Future;
import org.fungsi.concurrent.Futures;
import org.heat.User;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

public final class RsaCredentialsStrategy implements UserCredentialsStrategy {
    private final byte[] ankamaPubkey;

    private final Cipher cipher;
    private final KeyPair keys;

    @Inject
    @SneakyThrows
    public RsaCredentialsStrategy(Config config) {
        this.ankamaPubkey = Files.readAllBytes(Paths.get(config.getString("heat.login.frontend.ankama-pub-key-path")));

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        this.keys = generator.generateKeyPair();
        this.cipher = Cipher.getInstance("RSA");
        this.cipher.init(Cipher.DECRYPT_MODE, this.keys.getPrivate());
    }

    @Override
    public byte[] getPublicKey() {
        return ankamaPubkey;
    }

    @Override
    public String getNewSalt() {
        return Base64.getEncoder().encodeToString(keys.getPublic().getEncoded());
    }

    @Override
    public Future<String> convert(User user, byte[] credentials) {
        try {
            byte[] decrypted = cipher.doFinal(credentials);
            return Futures.success(new String(decrypted, StandardCharsets.UTF_8));
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            // since this is an application failure, we directly throw an exception
            // instead of returning a future, which in this case describes an authentication failure
            throw new IllegalArgumentException("cannot decipher credentials of " + user, e);
        }
    }
}
