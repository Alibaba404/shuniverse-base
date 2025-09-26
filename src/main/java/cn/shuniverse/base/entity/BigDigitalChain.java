package cn.shuniverse.base.entity;


import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

/**
 * Created by 蛮小满Sama at 2025-07-07 20:05
 *
 * @author 蛮小满Sama
 * @description 大数字支持链式调用
 */
@Getter
public class BigDigitalChain {
    private static final Logger LOGGER = LoggerFactory.getLogger(BigDigitalChain.class);
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    private BigDecimal value;

    private BigDigitalChain() {
    }

    public BigDigitalChain(Object value) {
        this.value = bigDecimalConvertor(value, null);
    }

    /**
     * 开始计算的初始值
     *
     * @param value 初始值
     * @return BigDigitalChain
     */
    public static BigDigitalChain begin(Object value) {
        return new BigDigitalChain(value);
    }

    /**
     * 加
     *
     * @param addend 加数
     * @return BigDigitalChain
     */
    public BigDigitalChain add(Object addend) {
        return operator(BigDecimal::add, addend);
    }

    /**
     * 加
     *
     * @param addend             加数
     * @param beforeOperateScale 加之前先把 addend 四舍五入法保留 beforeOperateScale 位小数
     * @return BigDigitalChain
     */
    public BigDigitalChain add(Object addend, Integer beforeOperateScale) {
        return operator(BigDecimal::add, addend, beforeOperateScale);
    }

    /**
     * 减
     *
     * @param subtrahend 减数
     * @return BigDigitalChain
     */
    public BigDigitalChain subtract(Object subtrahend) {
        return operator(BigDecimal::subtract, subtrahend);
    }

    /**
     * 减
     *
     * @param subtrahend         减数
     * @param beforeOperateScale 减之前先把 other 四舍五入法保留 beforeOperateScale 位小数
     * @return BigDigitalChain
     */
    public BigDigitalChain subtract(Object subtrahend, Integer beforeOperateScale) {
        return operator(BigDecimal::subtract, subtrahend, beforeOperateScale);
    }

    /**
     * 乘
     *
     * @param multiplier 乘数
     * @return BigDigitalChain
     */
    public BigDigitalChain multiply(Object multiplier) {
        return operator(BigDecimal::multiply, multiplier);
    }

    /**
     * 乘法
     *
     * @param multiplier         乘数
     * @param beforeOperateScale 乘之前先把 other 四舍五入法保留 beforeOperateScale 位小数
     * @return BigDigitalChain
     */
    public BigDigitalChain multiply(Object multiplier, Integer beforeOperateScale) {
        return operator(BigDecimal::multiply, multiplier, beforeOperateScale);
    }

    /**
     * 除法
     *
     * @param dividend 被除数
     * @return BigDigitalChain
     */
    public BigDigitalChain divide(Object dividend) {
        return operator(BigDecimal::divide, dividend);
    }

    /**
     * 除法
     *
     * @param dividend           被除数
     * @param beforeOperateScale 除之前先把 dividend 四舍五入法保留 beforeOperateScale 位小数
     * @return BigDigitalChain
     */
    public BigDigitalChain divide(Object dividend, Integer beforeOperateScale) {
        return operator(BigDecimal::divide, dividend, beforeOperateScale);
    }

    /**
     * 除法
     *
     * @param dividend 被除数
     * @param scale    结果保留 scale 位小数
     * @return BigDigitalChain
     */
    public BigDigitalChain divideWithScale(Object dividend, Integer scale) {
        return baseOperator(dividendValue -> this.value.divide(dividendValue, scale, ROUNDING_MODE), dividend, null);
    }

    /**
     * 除法
     *
     * @param dividend           被除数
     * @param scale              结果保留 scale 位小数
     * @param beforeOperateScale 除以之前先把 dividend 四舍五入法保留 beforeOperateScale 位小数
     * @return BigDigitalChain
     */
    public BigDigitalChain divideWithScale(Object dividend, Integer scale, Integer beforeOperateScale) {
        return baseOperator(dividendValue -> this.value.divide(dividendValue, scale, ROUNDING_MODE), dividend, beforeOperateScale);
    }

    public BigDecimal getValue(int scale) {
        return value.setScale(scale, ROUNDING_MODE);
    }

    /**
     * 获取double值
     *
     * @return double
     */
    public double doubleValue() {
        return getValue().doubleValue();
    }

    /**
     * 获取double值
     *
     * @param scale 小数位数
     * @return double
     */
    public double doubleValue(int scale) {
        return getValue(scale).doubleValue();
    }

    /**
     * 获取long值
     *
     * @return long
     */
    public long longValue() {
        return getValue().longValue();
    }

    /**
     * 获取int值
     *
     * @return int
     */
    public int intValue() {
        return getValue().intValue();
    }

    private BigDigitalChain operator(BinaryOperator<BigDecimal> operator, Object dividend) {
        return operator(operator, dividend, null);
    }

    private BigDigitalChain operator(BinaryOperator<BigDecimal> operator, Object dividend, Integer beforeOperateScale) {
        return baseOperator(dividendValue -> operator.apply(this.value, dividendValue), dividend, beforeOperateScale);
    }

    private synchronized BigDigitalChain baseOperator(UnaryOperator<BigDecimal> operator, Object dividend, Integer beforeOperateScale) {
        if (dividend == null) {
            return this;
        }
        if (dividend instanceof BigDigitalChain dividendDigital) {
            this.value = operator.apply(dividendDigital.getValue());
            return this;
        }
        this.value = operator.apply(bigDecimalConvertor(dividend, beforeOperateScale));
        return this;
    }

    /**
     * 将数据转成BigDecimal
     *
     * @param value 待转数据
     * @param scale 小数位数
     * @return BigDecimal
     */
    private BigDecimal bigDecimalConvertor(Object value, Integer scale) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal res;
        if (value instanceof Number val) {
            res = BigDecimal.valueOf(val.doubleValue());
        } else {
            try {
                res = new BigDecimal(value.toString());
            } catch (NumberFormatException e) {
                return BigDecimal.ZERO;
            }
        }
        if (scale != null) {
            res = BigDecimal.valueOf(res.setScale(scale, ROUNDING_MODE).doubleValue());
        }
        return res;
    }


    public static void main(String[] args) {
        LOGGER.info("{}", BigDigitalChain.begin(0).doubleValue());
        // 0 + 2 - 3 * 2
        LOGGER.info("{}", BigDigitalChain.begin(0).add(2).subtract(BigDigitalChain.begin(3).multiply(2)).intValue());
        LOGGER.info("{}", BigDigitalChain.begin(0).add(2.1).subtract(1.23).doubleValue());
    }
}
