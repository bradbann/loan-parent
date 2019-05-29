package org.songbai.loan.risk.moxie.carrier.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.loan.risk.moxie.api.MoxieClient;
import org.songbai.loan.risk.moxie.carrier.dto.report.V3.ReportBasicV3;
import org.songbai.loan.risk.moxie.carrier.dto.union.UnionDataV3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

@Component
public class CarrierClient extends MoxieClient {
	
	 private CarrierApi api;
	 
	 @Autowired
	public CarrierClient(@Value("${moxie.api.baseUrl}") String apiBaseUrl,
            @Value("${moxie.api.token}") String apiToken) {
		super(apiBaseUrl, apiToken);  
		api = retrofit.create(CarrierApi.class);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(CarrierClient.class);
	
//	public MobileBasic getMobileBasic(String mobile,String taskId) throws IOException {
//        Call<MobileBasic> call = api.getBasic(mobile,taskId);
//        Response<MobileBasic> response = call.execute();
//        if (response.code() == 200) {
//            return response.body();
//        } else {
//            LOGGER.info("getMobileBasic, status:{}, message:{}", response.code(), response.message());
//        }
//        return null;
//    }
	
	
//	public PackageUsageDetail getPackageUsage(String mobile,String fromMonth,String toMonth) throws IOException {
//        Call<PackageUsageDetail> call = api.getPackage(mobile,fromMonth,toMonth);
//        Response<PackageUsageDetail> response = call.execute();
//        if (response.code() == 200) {
//            return response.body();
//        } else {
//            LOGGER.info("getPackageUsage, status:{}, message:{}", response.code(), response.message());
//        }
//        return null;
//    }
	
	
//	public BillDetail getBill(String mobile,String fromMonth,String toMonth,String taskId) throws IOException {
//        Call<BillDetail> call = api.getBill(mobile,fromMonth,toMonth,taskId);
//        Response<BillDetail> response = call.execute();
//        if (response.code() == 200) {
//            return response.body();
//        } else {
//            LOGGER.info("getBill, status:{}, message:{}", response.code(), response.message());
//        }
//        return null;
//    }

	
//	public VoiceCallList getCall(String mobile,String month,String taskId) throws IOException {
//        Call<VoiceCallList> call = api.getCall(mobile,month,taskId);
//        Response<VoiceCallList> response = call.execute();
//        if (response.code() == 200) {
//            return response.body();
//        } else {
//            LOGGER.info("getCall, status:{}, message:{}", response.code(), response.message());
//        }
//        return null;
//    }
	
//	public ShortMessageList getSms(String mobile, String month, String taskId) throws IOException {
//        Call<ShortMessageList> call = api.getSms(mobile,month,taskId);
//        Response<ShortMessageList> response = call.execute();
//        if (response.code() == 200) {
//            return response.body();
//        } else {
//            LOGGER.info("getSms, status:{}, message:{}", response.code(), response.message());
//        }
//        return null;
//    }

    public UnionDataV3 getMxData(String mobile, String taskId) throws IOException {
        Call<UnionDataV3> call = api.getMxData(mobile,taskId);
        Response<UnionDataV3> response = call.execute();
        if (response.code() == 200) {
            return response.body();
        } else {
            LOGGER.info("getMxData, status:{}, message:{}", response.code(), response.message());
        }
        return null;
    }

    public ReportBasicV3 getReportBasic(String mobile, String name, String idCard, String taskId, String contact) throws IOException {
        Call<ReportBasicV3> reportBasic = api.getReportBasic(mobile, name, idCard, taskId, contact);
        Response<ReportBasicV3> response = reportBasic.execute();
        if (response.code() == 200) {
            return response.body();
        } else {
            LOGGER.info("getReportBasic, status:{}, message:{}", response.code(), response.message());
        }
        return null;
    }


//	public MobileRechargeDetail getRecharge(String mobile,String month) throws IOException {
//        Call<MobileRechargeDetail> call = api.getRecharge(mobile,month);
//        Response<MobileRechargeDetail> response = call.execute();
//        if (response.code() == 200) {
//            return response.body();
//        } else {
//            LOGGER.info("getRecharge, status:{}, message:{}", response.code(), response.message());
//        }
//        return null;
//    }
	
//	public FamilyNetDetail getFamily(String mobile) throws IOException {
//        Call<FamilyNetDetail> call = api.getFamily(mobile);
//        Response<FamilyNetDetail> response = call.execute();
//        if (response.code() == 200) {
//            return response.body();
//        } else {
//            LOGGER.info("getFamily, status:{}, message:{}", response.code(), response.message());
//        }
//        return null;
//    }
	
}
  
