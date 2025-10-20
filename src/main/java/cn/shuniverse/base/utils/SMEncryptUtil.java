package cn.shuniverse.base.utils;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.BCUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import cn.hutool.json.JSONUtil;
import cn.shuniverse.base.constants.SystemConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.springframework.util.ObjectUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 蛮小满Sama at 2025-10-20 20:25
 *
 * @author 蛮小满Sama
 * @description SM2加解密
 */
@Slf4j
public class SMEncryptUtil {

    // 加密模式：1 - C1C3C2，0 - C1C2C3，默认为1
    private static final SM2Engine.Mode MODE = SM2Engine.Mode.C1C2C3;

    private SMEncryptUtil() {
    }

    /**
     * SM2加密
     *
     * @param publicKey 公钥
     * @param data      待加密数据
     * @return 加密数据
     */
    public static String enSm2(String publicKey, Object data) {
        String enData = "";
        if (ObjectUtils.isEmpty(publicKey) || ObjectUtils.isEmpty(data)) {
            return null;
        }
        String json;
        if (data instanceof String st) {
            json = st;
        } else {
            json = JSONUtil.toJsonStr(data);
        }
        try {
            return getSm2(publicKey, null).encryptHex(json, KeyType.PublicKey);
        } catch (Exception e) {
            log.error("SM2加密失败", e);
        }
        return enData;
    }

    /**
     * SM2解密
     *
     * @param privateKey 私钥
     * @param data       待解密数据
     * @return
     */
    public static String deSm2(String privateKey, String data) {
        String deData = "";
        if (ObjectUtils.isEmpty(privateKey) || ObjectUtils.isEmpty(data)) {
            return null;
        }
        try {
            return getSm2(null, privateKey).decryptStr(data, KeyType.PrivateKey, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("SM2解密失败", e);
        }
        return deData;
    }

    public static String enSm4(String publicKey, String data) {
        String enData = "";
        if (ObjectUtils.isEmpty(publicKey) || ObjectUtils.isEmpty(data)) {
            return null;
        }
        try {
            return SmUtil.sm4(publicKey.getBytes(StandardCharsets.UTF_8)).encryptHex(data);
        } catch (Exception e) {
            log.error("SM4加密失败", e);
        }
        return enData;
    }

    public static String deSm4(String privateKey, String data) {
        String deData = "";
        if (ObjectUtils.isEmpty(privateKey) || ObjectUtils.isEmpty(data)) {
            return null;
        }
        try {
            return SmUtil.sm4(privateKey.getBytes(StandardCharsets.UTF_8)).decryptStr(data);
        } catch (Exception e) {
            log.error("SM4解密失败", e);
        }
        return deData;
    }

    /**
     * 获取SM2对象
     *
     * @param publicKey  公钥
     * @param privateKey 私钥
     * @return
     */
    public static SM2 getSm2(String publicKey, String privateKey) {
        ECPrivateKeyParameters ecPrivateKeyParameters = null;
        ECPublicKeyParameters ecPublicKeyParameters = null;
        if (StringUtils.isNotBlank(privateKey)) {
            ecPrivateKeyParameters = BCUtil.toSm2Params(privateKey);
        }

        if (StringUtils.isNotBlank(publicKey)) {
            if (publicKey.length() == 130) {
                publicKey = publicKey.substring(2);
            }
            String xHex = publicKey.substring(0, 64);
            String yHex = publicKey.substring(64, 128);
            ecPublicKeyParameters = BCUtil.toSm2Params(xHex, yHex);
        }
        SM2 sm2 = new SM2(ecPrivateKeyParameters, ecPublicKeyParameters);
        sm2.usePlainEncoding();
        sm2.setMode(MODE);
        return sm2;
    }

    public static Map<String, String> getSm2Key() {
        Map<String, String> map = new HashMap<>();
        SM2 sm2 = SmUtil.sm2().setMode(MODE);
        byte[] privateKeyByte = BCUtil.encodeECPrivateKey(sm2.getPrivateKey());
        byte[] publicKeyByte = ((BCECPublicKey) sm2.getPublicKey()).getQ().getEncoded(false);
        String privateKey = HexUtil.encodeHexStr(privateKeyByte);
        String publicKey = HexUtil.encodeHexStr(publicKeyByte);
        map.put(SystemConstants.PRIVATE_KEY, privateKey);
        map.put(SystemConstants.PUBLIC_KEY, publicKey);
        return map;
    }
}
