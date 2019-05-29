package org.songbai.loan.risk.mould.express.func.date;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorRuntimeJavaType;

import java.util.Date;
import java.util.Map;

public class DateNowFunc extends AbstractFunction {


    @Override
    public String getName() {
        return "date.now";
    }


    @Override
    public AviatorObject call(Map<String, Object> env) {
        return new AviatorRuntimeJavaType(new Date());
    }

}
