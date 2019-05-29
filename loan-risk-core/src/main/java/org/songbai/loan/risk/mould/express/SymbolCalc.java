package org.songbai.loan.risk.mould.express;

import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.constant.risk.RiskConst;

import java.util.HashMap;

import static org.songbai.loan.constant.risk.RiskConst.CalcSymbol.*;


public class SymbolCalc {

    private static final HashMap<RiskConst.CalcSymbol, Symbol> SYMBOL_MAP = new HashMap<>();

    private static final HashMap<String, Symbol> SYMBOL_CODE_MAP = new HashMap<>();


    @FunctionalInterface
    public interface Symbol {

        boolean calc(String param, String left, String right);

    }

    static {
        initSymbol();


        SYMBOL_MAP.forEach((k, v) -> {
            SYMBOL_CODE_MAP.put(k.code, v);
        });
    }


    public static boolean calc(String symbol, String param, String left, String right) {

        Symbol symbolCalc = SYMBOL_CODE_MAP.get(symbol);

        if (symbolCalc == null) {
            throw new RuntimeException("符号错误:"+symbol+"{param:"+param+",left:"+left+",right:"+right+" }");
        }

        if(StringUtil.isEmpty(param)){
            return false;
        }

        return symbolCalc.calc(param, left, right);
    }


    private static void initSymbol() {
        SYMBOL_MAP.put(EQ, (param, left, right) -> {
            if (Convert.isNumber(param, right)) {
                return Convert.decimal(param).compareTo(Convert.decimal(right)) == 0;
            } else {
                return param.compareTo(right) == 0;
            }
        });


        SYMBOL_MAP.put(GT, (param, left, right) -> {
            if (Convert.isNumber(param, right)) {
                return Convert.decimal(param).compareTo(Convert.decimal(right)) > 0;
            } else {
                return param.compareTo(right) > 0;
            }
        });

        SYMBOL_MAP.put(LT, (param, left, right) -> {
            if (Convert.isNumber(param, right)) {
                return Convert.decimal(param).compareTo(Convert.decimal(right)) < 0;
            } else {
                return param.compareTo(right) < 0;
            }
        });


        SYMBOL_MAP.put(GTE, (param, left, right) -> {
            if (Convert.isNumber(param, right)) {
                return Convert.decimal(param).compareTo(Convert.decimal(right)) >= 0;
            } else {
                return param.compareTo(right) >= 0;
            }
        });


        SYMBOL_MAP.put(LTE, (param, left, right) -> {
            if (Convert.isNumber(param, right)) {
                return Convert.decimal(param).compareTo(Convert.decimal(right)) <= 0;
            } else {
                return param.compareTo(right) <= 0;
            }
        });


        SYMBOL_MAP.put(SECTION, (param, left, right) -> {
            return SYMBOL_MAP.get(GTE).calc(param, null, left)
                    && SYMBOL_MAP.get(LTE).calc(param, null, right);
        });


        SYMBOL_MAP.put(CONTAIN, (param, left, right) -> {

            String[] arrays = StringUtil.tokenizeToStringArray(right, "|");

            for (String s : arrays) {
                if (param.contains(s)) {
                    return true;
                }
            }
            return false;
        });

        SYMBOL_MAP.put(MATCHS, (param, left, right) -> {

            String[] arrays = StringUtil.tokenizeToStringArray(right, "|");

            for (String s : arrays) {
                if (param.startsWith(s)) {
                    return true;
                }
            }

            return false;
        });
        SYMBOL_MAP.put(MATCHE, (param, left, right) -> {
            String[] arrays = StringUtil.tokenizeToStringArray(right, "|");

            for (String s : arrays) {
                if (param.endsWith(s)) {
                    return true;
                }
            }

            return false;
        });

    }


}
