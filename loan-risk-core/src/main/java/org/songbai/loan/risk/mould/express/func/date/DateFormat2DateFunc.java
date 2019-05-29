package org.songbai.loan.risk.mould.express.func.date;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.system.String2DateFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Map;

public class DateFormat2DateFunc extends AbstractFunction {


    @Override
    public String getName() {
        return "date.f2date";
    }


    private String2DateFunction string2DateFunction = new String2DateFunction();

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        return string2DateFunction.call(env, arg1, arg2);
    }


}
