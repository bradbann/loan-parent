package org.songbai.loan.risk.mould.express;

import com.alibaba.fastjson.JSONObject;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Options;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ExpressTest {

    public static void main(String[] args) throws Exception {

        try {
            String a = FileUtils.readFileToString(new File("/Users/navy/test/yhj_mobile.json"));
            JSONObject data = JSONObject.parseObject(a);

            data.put("riskcontact", Arrays.asList("18072960313", "18268141836", "15088609896", "13023632106", "18145041957"));

            AviatorEvaluator.setOption(Options.TRACE_EVAL, true);


            System.out.println(Express.calc("xpath(\"$.active_degree[app_point='no_call_day'].item.item_1m\")",data));
            System.out.println(Express.calc("xpath(\"$.active_degree[app_point='no_call_day'].item.item_3m\")",data));
            System.out.println(Express.calc("xpath(\"$.active_degree[app_point='no_call_day'].item.item_6m\")",data));

            System.out.println(Express.calc("xpath(\"$.active_degree[app_point='power_off_day'].item.item_1m\")",data));
            System.out.println(Express.calc("xpath(\"$.active_degree[app_point='power_off_day'].item.item_3m\")",data));
            System.out.println(Express.calc("xpath(\"$.active_degree[app_point='power_off_day'].item.item_6m\")",data));


            System.out.println(Express.calc("xpath(\"$.active_degree[app_point='continue_power_off_cnt'].item.item_1m\")",data));
            System.out.println(Express.calc("xpath(\"$.active_degree[app_point='continue_power_off_cnt'].item.item_3m\")",data));
            System.out.println(Express.calc("xpath(\"$.active_degree[app_point='continue_power_off_cnt'].item.item_6m\")",data));

//            System.out.println(Express.calc("trim(xpath(\"$.behavior_check[check_point='phone_silent'].result\"),'')=~/[\\d]+天内有([\\d]+)天无通话记录/ ? $1 : 0",data));
//            System.out.println(Express.calc("$1",data));
//            System.out.println(Express.calc("double(wealth_info.totalssets.huai_bei_can_use_limit) + double(wealth_info.totalssets.taobao_jiebie_available_amount)", data));
//            System.out.println(Express.calc("double(wealth_info.totalssets.huai_bei_limit)-double(wealth_info.totalssets.huai_bei_can_use_limit)+double(wealth_info.totalssets.taobao_jiebei_amount)-double(wealth_info.totalssets.taobao_jiebie_available_amount)", data));


//            System.out.println(Express.calc("xpath(\"$.new_analysis.trade_info.tb_userinfo_length_day_firstorder\")",data));

//            System.out.println(Express.calc("jsonArray.getValue(cell_phone,'carrier_name') == '齐幕伟' ? 1 : 0 ",data));
//            System.out.println(Express.calc("xpath(\"$.cell_phone[key='carrier_name'].value\")",data));
//            System.out.println(Express.calc("match(xpath(\"$.cell_phone[key='carrier_name'].value\"),riskuser.userinfo.name)",data));


//            System.out.println(Express.calc("xpath(\"$.cell_phone[key='carrier_idcard'].value\")",data));
//            System.out.println(Express.calc("xpath(\"$.user_basic[key='id_card'].value\")",data));
//
//            System.out.println(Express.calc("ifs(xpath(\"$.cell_phone[key='carrier_idcard'].value\"),\"运营商未提供身份证\",xpath(\"$.user_basic[key='id_card'].value\"))",data));
//
//            System.out.println(Express.calc("match(ifs(xpath(\"$.cell_phone[key='carrier_idcard'].value\"),\"运营商未提供身份证\",xpath(\"$.user_basic[key='id_card'].value\")),\"32032119940215369X\",17)",data));
//            System.out.println(Express.calc("jsonArray.getValue(basic_check_items,'check_item','arrearage_risk_3m','result')", data));
//            System.out.println(Express.calc("ifs(xpath(\"$.basic_check_items[check_item='arrearage_risk_3m'].result\"),\"近3月无实际消费\",0)", data));
//            System.out.println(Express.calc("xpath("$.basic_check_items[check_item='arrearage_risk_3m'].result")", data));
//            System.out.println(Express.calc("xpath(\"$.cell_behavior['behavior'].size()\")", data));
//            System.out.println(Express.calc("xpath(\"$.cell_phone[key='in_time'].value\")", data));
//            System.out.println(Express.calc("if(xpath(\"$.cell_phone[key='in_time'].value\"),\"运营商未提供入网时间\" ,xpath(\"$.cell_behavior['behavior'].size()\"),0)", data));
//            System.out.println(Express.calc("address_analysis.fundamental_point_analysis.self_city_change", data));
//            System.out.println(Express.calc("if(address_analysis.fundamental_point_analysis.self_city_change,'稳定',1,'较频繁',2,'变化频繁',3,0)", data));
//            System.out.println(Express.calc("if(address_analysis.fundamental_point_analysis.self_address_change,\"稳定\",1,\"较频繁\",2,\"变化频繁\",3,0)", data));
//            System.out.println(Express.calc("jsonArray.sort(call_contact_detail,'peer_num','call_cnt_6m',20)",data));
//            System.out.println(Express.calc("coll.size(coll.inter(jsonArray.sort(call_contact_detail,'peer_num','call_time_6m',20),riskcontact))/double(20)", data));
//            System.out.println(Express.calc("jsonArray.sort(call_contact_detail,'peer_num','call_cnt_6m',20)", data));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}