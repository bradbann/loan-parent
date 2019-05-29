package org.songbai.loan.risk.mould.express.func.collection;

import com.alibaba.fastjson.JSONObject;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorRuntimeJavaType;
import org.apache.commons.lang3.StringUtils;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.risk.mould.express.Convert;
import org.songbai.loan.risk.mould.express.func.FuncUtils;

import java.util.*;
import java.util.stream.Collectors;

public class JsonArraySortFunc extends AbstractFunction {
    @Override
    public String getName() {
        return "jsonArray.sort";
    }


    /**
     * @param env  变量对象
     * @param arg1 从哪里获取jsonArray
     * @param arg2 result 返回的结果字段
     * @return
     */
    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        return call(env, arg1, arg2, null, null);
    }


    /**
     * @param env  变量对象
     * @param arg1 从哪里获取jsonArray
     * @param arg2 result 返回的结果字段
     * @param arg3 排序字段。
     * @return
     */
    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3) {
        return call(env, arg1, arg2, arg3, null);
    }


    /**
     * @param env  变量对象
     * @param arg1 从哪里获取jsonArray
     * @param arg2 result 返回的结果字段
     * @param arg3 排序字段。
     * @param arg4 返回数据条数。
     * @return
     */
    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3, AviatorObject arg4) {


        String str2 = arg2 == null ? null : FuncUtils.getString(arg2, env);
        String str3 = arg3 == null ? null : FuncUtils.getString(arg3, env);
        Integer int4 = arg4 == null ? null : FuncUtils.getInt(arg4, env);


        List<Entry> objectList = getObjectEntryList(env, arg1, str3, str2);


        if (StringUtils.isNotEmpty(str3)) {
            objectList.sort((o1, o2) -> {

                try {
                    return Convert.compare(o2.sortValue, o1.sortValue);
                } catch (Exception e) {
                    //Ignore
                }

                return -1;
            });
        }

        List<Entry> subList = objectList;
        if (int4 != null && int4 > 0) {
            subList = objectList.subList(0, int4 >= objectList.size() ? objectList.size() : int4);
        }

        List<Object> resultList = subList.stream().map(v -> v.resultValue).collect(Collectors.toList());


        return new AviatorRuntimeJavaType(resultList);
    }


    private List<Entry> getObjectEntryList(Map<String, Object> env, AviatorObject arg1, String sortKey, String resultKey) {

        List<Object> array = FuncUtils.getCollectionValue(env, arg1);

        return array.parallelStream().map(v -> {
            JSONObject json = (JSONObject) JSONObject.toJSON(v);

            Entry entry = new Entry();
            entry.obj = v;
            entry.resultValue = v;
            if (StringUtil.isNotEmpty(sortKey)) {
                entry.sortValue = json.getString(sortKey);
            }

            if (StringUtil.isNotEmpty(resultKey)) {
                entry.resultValue = json.getString(resultKey);
            }

            return entry;
        }).collect(Collectors.toList());
    }


    private class Entry {
        Object obj;
        String sortValue;
        Object resultValue;
    }

}
