package org.songbai.loan.risk.mould.express.func;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorObject;
import org.springframework.util.AntPathMatcher;

import java.util.Map;

public class MatchFunction extends AbstractFunction {

    private static AntPathMatcher matcher = new AntPathMatcher();

    @Override
    public String getName() {
        return "match";
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {

        String str1 = FuncUtils.getString(arg1, env);
        String str2 = FuncUtils.getString(arg2, env);


        return internalAnt(str1, str2);
    }


    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3) {

        String str1 = FuncUtils.getString(arg1, env);
        String str2 = FuncUtils.getString(arg2, env);

        Integer int3 = FuncUtils.getInt(arg3, env);

        if (int3 == null) {
            return internalAnt(str1, str2);
        }

        if (int3 < 0) {
            return internalEnd(str1, str2, Math.abs(int3));
        }
        if (int3 > 0) {
            return internalStart(str1, str2, Math.abs(int3));
        }

        return internalAnt(str1, str2);
    }

    private AviatorObject internalAnt(String str1, String str2) {

        if (str1.equals(str2)) {
            return AviatorBoolean.TRUE;
        }


        if (matcher.isPattern(str1) && matcher.match(str1, str2)) {
            return AviatorBoolean.TRUE;
        }

        if (matcher.isPattern(str2) && matcher.match(str2, str1)) {
            return AviatorBoolean.TRUE;
        }
        return AviatorBoolean.FALSE;
    }


    private AviatorObject internalStart(String str1, String str2, int size) {

        if (str1.equals(str2)) {
            return AviatorBoolean.TRUE;
        }

        if (str1.length() < size || str2.length() < size) {
            return AviatorBoolean.FALSE;
        }

        String t1 = str1.substring(0, size);
        String t2 = str2.substring(0, size);
        return t1.equals(t2) ? AviatorBoolean.TRUE : AviatorBoolean.FALSE;
    }


    private AviatorObject internalEnd(String str1, String str2, int size) {

        if (str1.equals(str2)) {
            return AviatorBoolean.TRUE;
        }

        if (str1.length() < size || str2.length() < size) {
            return AviatorBoolean.FALSE;
        }

        String t1 = str1.substring(size);
        String t2 = str2.substring(size);
        return t1.equals(t2) ? AviatorBoolean.TRUE : AviatorBoolean.FALSE;
    }


    public static void main(String[] args) {

        System.out.println(matcher.isPattern("齐*"));
        System.out.println(matcher.match("齐*伟", "齐*"));
        System.out.println(matcher.match("188*9220", "188***9220"));
    }
}
