package org.songbai.loan.risk.mould.express.func;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SeqIfFunction extends AbstractFunction {


    @Override
    public String getName() {
        return "if";
    }


    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3, AviatorObject arg4) {
        List<AviatorEntry> entryList = new ArrayList<>();
        entryList.add(new AviatorEntry(arg2, arg3));
        return call(env, arg1, entryList, arg4);
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3, AviatorObject arg4, AviatorObject arg5, AviatorObject arg6) {
        List<AviatorEntry> entryList = new ArrayList<>();

        entryList.add(new AviatorEntry(arg2, arg3));
        entryList.add(new AviatorEntry(arg4, arg5));

        return call(env, arg1, entryList, arg6);
    }


    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3, AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7, AviatorObject arg8) {
        List<AviatorEntry> entryList = new ArrayList<>();

        entryList.add(new AviatorEntry(arg2, arg3));
        entryList.add(new AviatorEntry(arg4, arg5));
        entryList.add(new AviatorEntry(arg6, arg7));


        return call(env, arg1, entryList, arg8);
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3, AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7, AviatorObject arg8, AviatorObject arg9, AviatorObject arg10) {
        List<AviatorEntry> entryList = new ArrayList<>();

        entryList.add(new AviatorEntry(arg2, arg3));
        entryList.add(new AviatorEntry(arg4, arg5));
        entryList.add(new AviatorEntry(arg6, arg7));
        entryList.add(new AviatorEntry(arg8, arg9));


        return call(env, arg1, entryList, arg10);
    }


    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3, AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7, AviatorObject arg8, AviatorObject arg9, AviatorObject arg10, AviatorObject arg11, AviatorObject arg12) {
        List<AviatorEntry> entryList = new ArrayList<>();

        entryList.add(new AviatorEntry(arg2, arg3));
        entryList.add(new AviatorEntry(arg4, arg5));
        entryList.add(new AviatorEntry(arg6, arg7));
        entryList.add(new AviatorEntry(arg8, arg9));
        entryList.add(new AviatorEntry(arg10, arg11));


        return call(env, arg1, entryList, arg12);
    }


    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3, AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7, AviatorObject arg8, AviatorObject arg9, AviatorObject arg10, AviatorObject arg11, AviatorObject arg12, AviatorObject arg13, AviatorObject arg14) {
        List<AviatorEntry> entryList = new ArrayList<>();

        entryList.add(new AviatorEntry(arg2, arg3));
        entryList.add(new AviatorEntry(arg4, arg5));
        entryList.add(new AviatorEntry(arg6, arg7));
        entryList.add(new AviatorEntry(arg8, arg9));
        entryList.add(new AviatorEntry(arg10, arg11));
        entryList.add(new AviatorEntry(arg12, arg13));


        return call(env, arg1, entryList, arg14);
    }


    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3, AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7, AviatorObject arg8, AviatorObject arg9, AviatorObject arg10, AviatorObject arg11, AviatorObject arg12, AviatorObject arg13, AviatorObject arg14, AviatorObject arg15, AviatorObject arg16) {
        List<AviatorEntry> entryList = new ArrayList<>();

        entryList.add(new AviatorEntry(arg2, arg3));
        entryList.add(new AviatorEntry(arg4, arg5));
        entryList.add(new AviatorEntry(arg6, arg7));
        entryList.add(new AviatorEntry(arg8, arg9));
        entryList.add(new AviatorEntry(arg10, arg11));
        entryList.add(new AviatorEntry(arg12, arg13));
        entryList.add(new AviatorEntry(arg14, arg15));


        return call(env, arg1, entryList, arg16);

    }


    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3, AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7, AviatorObject arg8, AviatorObject arg9, AviatorObject arg10, AviatorObject arg11, AviatorObject arg12, AviatorObject arg13, AviatorObject arg14, AviatorObject arg15, AviatorObject arg16, AviatorObject arg17, AviatorObject arg18) {
        List<AviatorEntry> entryList = new ArrayList<>();

        entryList.add(new AviatorEntry(arg2, arg3));
        entryList.add(new AviatorEntry(arg4, arg5));
        entryList.add(new AviatorEntry(arg6, arg7));
        entryList.add(new AviatorEntry(arg8, arg9));
        entryList.add(new AviatorEntry(arg10, arg11));
        entryList.add(new AviatorEntry(arg12, arg13));
        entryList.add(new AviatorEntry(arg14, arg15));
        entryList.add(new AviatorEntry(arg16, arg17));


        return call(env, arg1, entryList, arg18);
    }


    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3, AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7, AviatorObject arg8, AviatorObject arg9, AviatorObject arg10, AviatorObject arg11, AviatorObject arg12, AviatorObject arg13, AviatorObject arg14, AviatorObject arg15, AviatorObject arg16, AviatorObject arg17, AviatorObject arg18, AviatorObject arg19, AviatorObject arg20) {

        List<AviatorEntry> entryList = new ArrayList<>();

        entryList.add(new AviatorEntry(arg2, arg3));
        entryList.add(new AviatorEntry(arg4, arg5));
        entryList.add(new AviatorEntry(arg6, arg7));
        entryList.add(new AviatorEntry(arg8, arg9));
        entryList.add(new AviatorEntry(arg10, arg11));
        entryList.add(new AviatorEntry(arg12, arg13));
        entryList.add(new AviatorEntry(arg14, arg15));
        entryList.add(new AviatorEntry(arg16, arg17));
        entryList.add(new AviatorEntry(arg18, arg19));


        return call(env, arg1, entryList, arg20);
    }


    private AviatorObject call(Map<String, Object> env, AviatorObject arg1, List<AviatorEntry> entryList, AviatorObject elseObj) {
        for (AviatorEntry entry : entryList) {
            if (entry.whenObj.compare(arg1, env) == 0) {
                return entry.thenObj;
            }
        }

        return elseObj;
    }


    class AviatorEntry {
        AviatorObject whenObj;
        AviatorObject thenObj;

        AviatorEntry(AviatorObject whenObj, AviatorObject thenObj) {
            this.whenObj = whenObj;
            this.thenObj = thenObj;
        }
    }
}
