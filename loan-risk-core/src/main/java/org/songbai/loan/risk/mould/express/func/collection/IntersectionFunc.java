package org.songbai.loan.risk.mould.express.func.collection;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorRuntimeJavaType;
import org.songbai.loan.risk.mould.express.func.FuncUtils;

import java.util.List;
import java.util.Map;

public class IntersectionFunc extends AbstractFunction {
    @Override
    public String getName() {
        return "coll.inter";
    }


    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {

        List<Object> array1 = FuncUtils.getCollectionValue(env, arg1);
        List<Object> array2 = FuncUtils.getCollectionValue(env, arg2);


        array1.retainAll(array2);

        return new AviatorRuntimeJavaType(array1);
    }
}
