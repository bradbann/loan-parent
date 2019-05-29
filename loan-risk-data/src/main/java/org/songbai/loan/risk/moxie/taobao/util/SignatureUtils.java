package org.songbai.loan.risk.moxie.taobao.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class SignatureUtils {

    public static String base64Hmac256(String secret, String message) {
        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256Hmac.init(secretKey);
            return Base64.encodeBase64String(sha256Hmac.doFinal(message.getBytes()));
        } catch (Exception ignored) {
            return "";
        }
    }
    
//    public static void main(String[] args) {
//    	System.out.println(base64Hmac256("27c7e4bc518c48d095d9caf544771876",
//                "{\"mobile\":\"18668122978\",\"name\":\"袁冬\",\"idcard\":\"371428198709024034\",\"timestamp\":1483066862001,\"result\":true,\"message\":\"aX92rOy5T05K%2BfKzoM6ZokYjXQ%2BgwMhda6vzI89N%2BEiIlu4v9Q9qNrpvJpyB6YDVMEFp7MYO8S1uZtbmy3fKpC%2Bq5hkHlpNFzfYPHlwsMHgp34zISneDxSyue1lBT2Qo5vYBWVWdm4euj9SpMZNdQchB%2BpHDN7Fe\",\"task_id\":\"13aea0f0-ce3c-11e6-92dc-00163e004a23\",\"user_id\":\"1378877432\"}"));
//	}
}