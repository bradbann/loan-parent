package org.songbai.loan.risk.mould.express.func.collection;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import org.apache.commons.beanutils.BeanUtils;
import org.songbai.loan.risk.mould.express.Convert;
import org.songbai.loan.risk.mould.express.func.FuncUtils;

import java.util.*;

public class JsonArrayValueFunc extends AbstractFunction {
    @Override
    public String getName() {
        return "jsonArray.getValue";
    }


    /**
     * @param env  变量对象
     * @param arg1 从哪里获取jsonArray
     * @param arg2 key 预期值
     * @return
     */
    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {

        return call(env, arg1, new AviatorString("key"), arg2, new AviatorString("value"));

    }


    /**
     * @param env  变量对象
     * @param arg1 从哪里获取jsonArray
     * @param arg2 key 的字段名
     * @param arg3 key 预期值
     * @return
     */
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3) {

        return call(env, arg1, arg2, arg3, new AviatorString("value"));
    }

    /**
     * @param env  变量对象
     * @param arg1 从哪里获取jsonArray
     * @param arg2 key 的字段名
     * @param arg3 key 预期值
     * @param arg4 value 字段名
     * @return
     */
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3, AviatorObject arg4) {


        Object obj1 = arg1.getValue(env);
        String str2 = FuncUtils.getString(arg2,env);
        String str3 = FuncUtils.getString(arg3,env);
        String str4 = FuncUtils.getString(arg4,env);


        Class<?> clazz = obj1.getClass();


        List<Object> array = new ArrayList<>();

        if (Collection.class.isAssignableFrom(clazz)) {
            array.addAll((Collection) obj1);
        } else if (clazz.isArray()) {
            array.addAll(Arrays.asList((Object[]) obj1));
        } else {
            throw new IllegalArgumentException(arg1.desc(env) + " is not a array");
        }


        for (Object o : array) {

            try {
                String value1 = BeanUtils.getNestedProperty(o, str2);
                String value2 = BeanUtils.getNestedProperty(o, str4);
                if (Convert.equal(value1, str3)) {
                    return new AviatorString(value2);
                }
            } catch (Exception e) {
                //Ignore
            }
        }


        return null;
    }


}
