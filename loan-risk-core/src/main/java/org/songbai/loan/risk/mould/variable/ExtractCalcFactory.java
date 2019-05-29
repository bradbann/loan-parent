package org.songbai.loan.risk.mould.variable;

import lombok.extern.slf4j.Slf4j;
import org.songbai.loan.risk.vo.VariableExtractVO;
import org.songbai.loan.risk.vo.VariableMergeVO;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;


@Component
@Slf4j
public class ExtractCalcFactory implements ApplicationContextAware {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MergeCalc mergeCalc;

    private Map<String, ExtractCalc> calcMap = new HashMap<>();


    @PostConstruct
    public void init() {
        String[] names = applicationContext.getBeanNamesForType(ExtractCalc.class);

        for (String name : names) {
            ExtractCalc instance = applicationContext.getBean(name, ExtractCalc.class);
            calcMap.put(instance.source(), instance);
        }
    }

    public VariableMergeVO extract(VariableExtractVO calcVO) {
        ExtractCalc calc = calcMap.get(calcVO.getSources());

        if (calc == null) {
            throw new RuntimeException("不能找到变量抽取的方法" + calcVO);
        }

        log.info("handle msg: {} , and start extract variable used for {} ", calcVO, calc.getClass().toString());

        return calc.calc(calcVO);
    }


    public VariableMergeVO extractAndMerge(VariableExtractVO calcVO) {
        VariableMergeVO resultVO = extract(calcVO);

        // 多来源的变量合并
        mergeCalc.merge(resultVO);

        return resultVO;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


}
