package org.songbai.loan.risk.mould.express;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Options;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.songbai.loan.risk.model.mould.RiskVariableSourceModel;
import org.songbai.loan.risk.mould.express.func.*;
import org.songbai.loan.risk.mould.express.func.collection.IntersectionFunc;
import org.songbai.loan.risk.mould.express.func.collection.JsonArraySortFunc;
import org.songbai.loan.risk.mould.express.func.collection.JsonArrayValueFunc;
import org.songbai.loan.risk.mould.express.func.collection.SizeFunction;
import org.songbai.loan.risk.mould.express.func.date.DateAddFunc;
import org.songbai.loan.risk.mould.express.func.date.DateFormat2DateFunc;
import org.songbai.loan.risk.mould.express.func.date.DateFormat2StrFunc;
import org.songbai.loan.risk.mould.express.func.date.DateNowFunc;

import java.util.Map;
import java.util.regex.Pattern;

import static org.apache.commons.beanutils.ConvertUtils.convert;

/**
 * @author navy
 */
public class Express {

    private static final Pattern PATTERN_NUMBER = Pattern.compile("[\\w\\.]+");

    private static PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();

    static {
        AviatorEvaluator.setOption(Options.OPTIMIZE_LEVEL, AviatorEvaluator.EVAL);


        /*
         * 常用的操作func
         */
        AviatorEvaluator.addFunction(new SeqIfFunction());
        AviatorEvaluator.addFunction(new SeqIfsFunction());
        AviatorEvaluator.addFunction(new XpathFunction());
        AviatorEvaluator.addFunction(new TrimFunction());
        AviatorEvaluator.addFunction(new MatchFunction());

        /*
         * 时间的操作 func
         */
        AviatorEvaluator.addFunction(new DateAddFunc());
        AviatorEvaluator.addFunction(new DateFormat2DateFunc());
        AviatorEvaluator.addFunction(new DateFormat2StrFunc());
        AviatorEvaluator.addFunction(new DateNowFunc());

        /*
         *   集合的操作 func
         */
        AviatorEvaluator.addFunction(new IntersectionFunc());
        AviatorEvaluator.addFunction(new SizeFunction());
        AviatorEvaluator.addFunction(new JsonArrayValueFunc());
        AviatorEvaluator.addFunction(new JsonArraySortFunc());
    }

    public static Object calc(String expression, Map<String, Object> env) {

        if (PATTERN_NUMBER.matcher(expression).matches()) {
            try {
                return propertyUtilsBean.getProperty(env, expression);
            } catch (Exception e) {
                e.printStackTrace();
                //Ignore
            }
        }

        try {
            return AviatorEvaluator.execute(expression, env, true);
        } catch (Exception e) {
            throw new RuntimeException("expression execute fail : " + expression, e);
            //Ignore
        }
    }


    public static String calcStr(RiskVariableSourceModel sourceModel, Map<String, Object> env) {

        Object object = null;

        try {
            if (sourceModel.getVariableType() == 1) {
                object = propertyUtilsBean.getProperty(env, sourceModel.getVariable());
            } else if (sourceModel.getVariableType() == 2) {
                object = AviatorEvaluator.execute(sourceModel.getVariable(), env, true);
            } else {
                object = calc(sourceModel.getVariable(), env);
            }
        } catch (Exception e) {
            //Ignore
        }

        return object == null ? null : object.toString();
    }


    public static <T> T calc(String expression, Map<String, Object> env, Class<T> clazz) {


        Object obj = calc(expression, env);

        if (obj == null) {
            return null;
        }


        return (T) (clazz.isInstance(obj) ? obj : convert(obj, clazz));
    }

}
