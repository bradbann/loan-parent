package org.songbai.loan.risk.mould.express.func.date;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.system.Date2StringFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Map;

public class DateFormat2StrFunc extends AbstractFunction {


    @Override
    public String getName() {
        return "date.f2str";
    }


    private Date2StringFunction date2StringFunction = new Date2StringFunction();

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        return date2StringFunction.call(env, arg1, arg2);
    }


}
