package com.fire.consumer;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author fire
 * @date 2021年06月14日10:11
 */
public class SmsUtil {
    @Value("${accessKeyId}")
    private String accessKeyId;
    @Value("${accessSecret}")
    private String accessSecret;

    public CommonResponse sendSms(String phone,String templateCode,String param){
        DefaultProfile profile = DefaultProfile.getProfile("cn-qingdao", accessKeyId, accessSecret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName","青城");
        request.putQueryParameter("TemplateCode",templateCode);
        request.putQueryParameter("TemplateParam",param);
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
            return  response;
        } catch (ServerException e) {
            e.printStackTrace();
            return null;
        } catch (ClientException e) {
            e.printStackTrace();
            return null;
        }
    }
}
