package org.songbai.loan.risk.mould.express.func.collection;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorLong;
import com.googlecode.aviator.runtime.type.AviatorObject;
import org.songbai.loan.risk.mould.express.func.FuncUtils;

import java.util.List;
import java.util.Map;

public class SizeFunction extends AbstractFunction {

    @Override
    public String getName() {
        return "coll.size";
    }


    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {

        List<Object> array1 = FuncUtils.getCollectionValue(env, arg1);

        return new AviatorLong(array1.size());
    }



}
