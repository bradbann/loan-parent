package org.songbai.loan.risk.mould.express.func;

import com.alibaba.fastjson.JSONPath;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Map;

public class XpathFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "xpath";
    }


    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {

        return getAviatorObject(env, arg1);
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {

        return getAviatorObject(env, arg1, arg2);
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3) {
        return getAviatorObject(env, arg1, arg2, arg3);
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3, AviatorObject arg4) {
        return getAviatorObject(env, arg1, arg2, arg3, arg4);
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3, AviatorObject arg4, AviatorObject arg5) {
        return getAviatorObject(env, arg1, arg2, arg3, arg4, arg5);
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3, AviatorObject arg4, AviatorObject arg5, AviatorObject arg6) {
        return getAviatorObject(env, arg1, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3, AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7) {
        return getAviatorObject(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3, AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7, AviatorObject arg8) {
        return getAviatorObject(env, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
    }

    private AviatorObject getAviatorObject(Map<String, Object> env, AviatorObject arg1, AviatorObject... params) {
        String xpath = FuncUtils.getString(arg1, env);


        if (params != null && params.length > 0) {
            Object[] paramList = Arrays.stream(params).map(v -> {

                Object obj = v.getValue(env);

                if (obj instanceof String) {
                    return "'" + obj + "'";
                } else {
                    return obj;
                }
            }).toArray();


            xpath = MessageFormat.format(xpath, paramList);
        }

        return getXpathValue(env, xpath);
    }


    private AviatorObject getXpathValue(Map<String, Object> env, String xpath) {
        Object object = JSONPath.eval(env, xpath);

        return FuncUtils.convert(object);
    }
}
