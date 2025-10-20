//package cn.shuniverse.base.core;
//
//import cn.hutool.crypto.SmUtil;
//import cn.hutool.crypto.asymmetric.KeyType;
//import cn.shuniverse.base.core.annotation.DecryptRequest;
//import org.springframework.core.MethodParameter;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpInputMessage;
//import org.springframework.http.converter.HttpMessageConverter;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;
//
//import java.io.BufferedReader;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.lang.reflect.Type;
//
///**
// * Created by 蛮小满Sama at 2025-10-19 13:49
// *
// * @author 蛮小满Sama
// * @description
// */
//@ControllerAdvice
//public class DecryptRequestBodyAdvice extends RequestBodyAdviceAdapter {
//    /**
//     * Invoked first to determine if this interceptor applies.
//     *
//     * @param methodParameter the method parameter
//     * @param targetType      the target type, not necessarily the same as the method
//     *                        parameter type, e.g. for {@code HttpEntity<String>}.
//     * @param converterType   the selected converter type
//     * @return whether this interceptor should be invoked or not
//     */
//    @Override
//    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
//        // 判断是否需要解密，比如通过自定义注解
//        return methodParameter.hasMethodAnnotation(DecryptRequest.class);
//    }
//
//    @Override
//    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
//        String body = new BufferedReader(new InputStreamReader(inputMessage.getBody())).lines().reduce("", (a, b) -> a + b);
//        // 解密
//        String plainText = SmUtil.sm2("", "").decryptStr(body, KeyType.PublicKey);
//        InputStream newStream = new ByteArrayInputStream(plainText.getBytes());
//        return new HttpInputMessage() {
//            @Override
//            public InputStream getBody() {
//                return newStream;
//            }
//
//            @Override
//            public HttpHeaders getHeaders() {
//                return inputMessage.getHeaders();
//            }
//        };
//    }
//}
