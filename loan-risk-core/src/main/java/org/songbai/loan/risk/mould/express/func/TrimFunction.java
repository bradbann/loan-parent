package org.songbai.loan.risk.mould.express.func;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorNil;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Map;

public class TrimFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "trim";
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {

        Object object = arg1.getValue(env);

        return FuncUtils.convert(object);
    }


    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {


        Object object = arg1.getValue(env);

        AviatorObject obj = FuncUtils.convert(object);

        return obj == AviatorNil.NIL ? arg2 : obj;
    }
}
