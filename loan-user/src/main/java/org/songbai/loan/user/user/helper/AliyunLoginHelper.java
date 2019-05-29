/*package org.songbai.loan.user.user.helper;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.afs.model.v20180112.AuthenticateSigRequest;
import com.aliyuncs.afs.model.v20180112.AuthenticateSigResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.utils.http.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;


@Component
public class AliyunLoginHelper {

    private Logger logger = LoggerFactory.getLogger(AliyunLoginHelper.class);


    @Autowired
    private SpringProperties springProperties;


    @Value("${config.aliyun.afs.appKey}")
    private String appKey;


    private DefaultAcsClient acsClient;


    @PostConstruct
    public void init() {
        try {
            String accessKeyId = "LTAIBdJsgPxlixAZ"; //springProperties.getProperty("config.aliyun.accessKeyId");
            String accessKeySecret = "MFpfDZ30eiY5XMAq347qdjTenjbycq"; // springProperties.getProperty("config.aliyun.accessKeySecret");
            String regionId = springProperties.getProperty("config.aliyun.afs.regionId");
            String endpointName = springProperties.getProperty("config.aliyun.afs.endpointName");
            String domain = springProperties.getProperty("config.aliyun.afs.domain");

            IClientProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);

            DefaultProfile.addEndpoint(endpointName, regionId, "afs", domain);

            acsClient = new DefaultAcsClient(profile);
        } catch (Exception e) {
            logger.error("aliyun login validate init fail ", e);
        }

    }


    public boolean validate(HttpServletRequest request, boolean force) {

        if (acsClient == null) {
            return true;
        }

        String recognition = request.getParameter("recognition");

        if (StringUtils.isEmpty(recognition)) {
            return !force;
        }

        JSONObject jsonObject = JSONObject.parseObject(recognition);

        AuthenticateSigRequest signRequest = new AuthenticateSigRequest();
        signRequest.setSessionId(jsonObject.getString("sessionId"));// 必填参数，从前端获取，不可更改，android和ios只变更这个参数即可，下面参数不变保留xxx
        signRequest.setSig(jsonObject.getString("sign"));// 必填参数，从前端获取，不可更改
        signRequest.setToken(jsonObject.getString("token"));// 必填参数，从前端获取，不可更改
        signRequest.setScene(jsonObject.getString("scene"));// 必填参数，从前端获取，不可更改
        signRequest.setAppKey(appKey);// 必填参数，后端填写
        signRequest.setRemoteIp(IpUtil.getIp(request));// 必填参数，后端填写

        try {
            AuthenticateSigResponse response = acsClient.getAcsResponse(signRequest);
            if (response.getCode() != 100) {
                logger.warn("validate user[{}] fail : {} , {}", recognition, response.getCode(), response.getDetail());
            }
            return response.getCode() == 100;
        } catch (Exception e) {
            //Ignore
        }

        return !force;
    }


    public boolean validate(String sessionId, String sign, String token, String scene, String ip) {
        AuthenticateSigRequest signRequest = new AuthenticateSigRequest();
        signRequest.setSessionId(sessionId);// 必填参数，从前端获取，不可更改，android和ios只变更这个参数即可，下面参数不变保留xxx
        signRequest.setSig(sign);// 必填参数，从前端获取，不可更改
        signRequest.setToken(token);// 必填参数，从前端获取，不可更改
        signRequest.setScene(scene);// 必填参数，从前端获取，不可更改
        signRequest.setAppKey(appKey);// 必填参数，后端填写
        signRequest.setRemoteIp(ip);// 必填参数，后端填写

        try {
            AuthenticateSigResponse response = acsClient.getAcsResponse(signRequest);
            System.out.println(JSONObject.toJSON(response));
            return response.getCode() == 100;
        } catch (Exception e) {
            e.printStackTrace();
            //Ignore
        }

        return false;
    }
}
*/