package com.socool.soft.util;

import com.socool.soft.exception.ErrorCode;
import com.socool.soft.exception.SystemException;
import org.apache.commons.lang.StringUtils;

/**
 * 参数校验工具类
 */
public class CheckUtil {

    /**
     * 校验参数长度, 不符合则抛出异常
     * @param param 需要校验的参数
     * @param maxLength 最大长度
     * @param paramStr 提示字段
     * @throws SystemException
     */
    public static void tooLong(String param, int maxLength, String paramStr) throws SystemException {
        if (StringUtils.isNotBlank(param) && param.length() > maxLength) {
            throw new SystemException(ErrorCode.PARAMETER_ERROR, paramStr + " was too long.");
        }
    }

    /**
     * 校验参数是否为空, 是则抛出异常
     * @param param 需要校验的参数
     * @param paramStr 提示字段
     * @throws SystemException
     */
    public static void isBlank(String param, String paramStr) throws SystemException {
        if(StringUtils.isBlank(param)) {
            throw new SystemException(ErrorCode.PARAMETER_ERROR, paramStr + " can't be empty.");
        }
    }

}
