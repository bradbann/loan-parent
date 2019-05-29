package org.songbai.loan.risk.mould.express.func;

import com.googlecode.aviator.runtime.type.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class FuncUtils {

    public static String getString(AviatorObject object, Map<String, Object> env) {


        switch (object.getAviatorType()) {
            case String:
                return ((AviatorString) object).getLexeme();

            case Decimal:
            case Long:
            case BigInt:
            case Double:
                return ((AviatorNumber) object).toDecimal().toString();

            default:
                Object obj = object.getValue(env);
                return obj != null ? obj.toString() : "";

        }

    }


    public static Integer getInt(AviatorObject object, Map<String, Object> env) {


        switch (object.getAviatorType()) {
            case String:
                return Integer.parseInt(((AviatorString) object).getLexeme());
            case Decimal:
            case Long:
            case BigInt:
            case Double:
                return ((AviatorNumber) object).toBigInt().intValue();
            default:
                try {
                    Object obj = object.getValue(env);
                    return Integer.parseInt(obj.toString());
                } catch (NumberFormatException e) {
                    return null;
                }
        }

    }


    public static List<Object> getCollectionValue(Map<String, Object> env, AviatorObject arg1) {
        List<Object> array = new ArrayList<>();

        Object obj1 = arg1.getValue(env);

        if (obj1 == null) {
            return array;
        }

        Class clazz = obj1.getClass();

        if (Collection.class.isAssignableFrom(clazz)) {
            array.addAll((Collection) obj1);
        } else if (clazz.isArray()) {
            array.addAll(Arrays.asList((Object[]) obj1));
        } else {
            array.add(obj1);
        }

        return array;
    }


    public static AviatorObject getValue(Map<String, Object> env, AviatorObject arg1) {
        Object object = arg1.getValue(env);


        return convert(object);
    }


    public static AviatorObject convert(Object object) {
        if (object == null) {
            return AviatorNil.NIL;
        }

        object = checkSignleArray(object);

        if (object instanceof AviatorObject) {
            return (AviatorObject) object;
        } else if (object instanceof Long) {
            return new AviatorLong((Number) object);
        } else if (object instanceof Double) {
            return new AviatorDouble((Number) object);
        } else if (object instanceof String) {
            return new AviatorString((String) object);
        } else if (object instanceof Boolean) {
            return AviatorBoolean.valueOf((Boolean) object);
        } else if (object instanceof BigInteger) {
            return new AviatorBigInt((Number) object);
        } else if (object instanceof BigDecimal) {
            return new AviatorDecimal((Number) object);
        } else {
            return new AviatorRuntimeJavaType(object);
        }
    }

    private static Object checkSignleArray(Object object) {
        if (object instanceof List) {
            List array = (List) object;

            if (array.size() == 0) {
                return AviatorNil.NIL;
            } else if (array.size() == 1) {
                object = array.get(0);
            }
        }

        if (object.getClass().isArray()) {
            Object[] objects = (Object[]) object;

            if (objects.length == 0) {
                return AviatorNil.NIL;
            } else if (objects.length == 1) {
                object = objects[0];
            }
        }

        return object;
    }


}
