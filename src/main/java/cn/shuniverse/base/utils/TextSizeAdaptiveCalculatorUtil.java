package cn.shuniverse.base.utils;

import cn.hutool.core.io.unit.DataSizeUtil;
import lombok.Getter;

import java.nio.charset.Charset;

/**
 * Created by 蛮小满Sama at 2025-07-09 11:02
 *
 * @author 蛮小满Sama
 * @description 文本大小适配计算工具类
 */
public class TextSizeAdaptiveCalculatorUtil {
    private static final String[] UNITS = new String[]{"Byte", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};

    public TextSizeAdaptiveCalculatorUtil() {
    }

    public static int byteSizeCalculator(String text, String charset) {
        if (text != null && charset != null) {
            try {
                return text.getBytes(Charset.forName(charset)).length;
            } catch (Exception e) {
                throw new IllegalArgumentException("字符编码不支持：" + charset, e);
            }
        } else {
            throw new IllegalArgumentException("文本和字符编码名称不能为空！");
        }
    }

    public static TextSizeDetail textSizeWithAdaptiveUnitCalculator(String text, String charset) {
        if (text != null && !text.isEmpty() && charset != null && !charset.isEmpty()) {
            return new TextSizeDetail(byteSizeCalculator(text, charset));
        } else {
            throw new IllegalArgumentException("文本内容和字符编码名称不能为空");
        }
    }

    public static TextSizeDetail textSizeWithAdaptiveUnitCalculator(String text) {
        return textSizeWithAdaptiveUnitCalculator(text, "UTF-8");
    }

    @Getter
    public static class TextSizeDetail {
        private final int byteSize;

        public TextSizeDetail(int byteSize) {
            this.byteSize = byteSize;
        }

        @Override
        public String toString() {
            return DataSizeUtil.format(this.byteSize);
        }

    }
}
