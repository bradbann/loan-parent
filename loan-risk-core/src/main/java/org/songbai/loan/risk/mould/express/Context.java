package org.songbai.loan.risk.mould.express;

import org.songbai.loan.risk.vo.RiskUserVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Context extends HashMap<String, Object> {

    public final static String CONTEXT_MAP_RISK_USER = "riskuser";
    public final static String CONTEXT_MAP_RISK_CONTACT = "riskcontact";

    public final static String CONTEXT_MAP_SOURCES = "risksources";


    public static Context of(String sources) {
        Context context = new Context();
        context.put(CONTEXT_MAP_SOURCES, sources);

        return context;
    }

    public Context putUser(RiskUserVO riskUserVO) {
        put(CONTEXT_MAP_RISK_USER, riskUserVO);
        return this;
    }

    public Context putContact(List<String> list) {
        put(CONTEXT_MAP_RISK_CONTACT, list);
        return this;
    }

    public Context putSources(String sources) {
        put(CONTEXT_MAP_SOURCES, sources);

        return this;
    }

    public Context putParam(Map<String, Object> param) {
        putAll(param);

        return this;
    }
}
