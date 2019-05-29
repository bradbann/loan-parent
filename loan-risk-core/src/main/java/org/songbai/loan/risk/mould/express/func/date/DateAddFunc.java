package org.songbai.loan.risk.mould.express.func.date;

import com.googlecode.aviator.exception.ExpressionRuntimeException;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorRuntimeJavaType;
import org.songbai.loan.risk.mould.express.func.FuncUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class DateAddFunc extends AbstractFunction {
    @Override
    public String getName() {
        return "date.add";
    }




    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3) {


        Date date = (Date) arg1.getValue(env);


        Integer int2 = FuncUtils.getInt(arg2, env);
        Integer int3 = FuncUtils.getInt(arg3, env);


        assert date != null;
        assert int2 != null;
        assert int3 != null;

        Date result = addDate(date, int2, int3);

        return new AviatorRuntimeJavaType(result);
    }

    private Date addDate(Date date, Integer int2, Integer int3) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);

        switch (int2) {
            case 1:
                calendar.add(Calendar.YEAR, int3);
                return calendar.getTime();
            case 2:
                calendar.add(Calendar.MONTH, int3);
                return calendar.getTime();
            case 3:
                calendar.add(Calendar.DAY_OF_MONTH, int3);
                return calendar.getTime();

            case 4:
                calendar.add(Calendar.HOUR_OF_DAY, int3);
                return calendar.getTime();

            case 5:
                calendar.add(Calendar.MINUTE, int3);
                return calendar.getTime();

            case 6:
                calendar.add(Calendar.SECOND, int3);
                return calendar.getTime();
            default:
                throw new ExpressionRuntimeException("add date type is error , and values in(1,2,3,4,5,6)");
        }
    }
}
