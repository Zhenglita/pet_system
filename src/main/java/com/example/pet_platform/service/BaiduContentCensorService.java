package com.example.pet_platform.service;


import com.baidu.aip.contentcensor.AipContentCensor;
import com.baidu.aip.contentcensor.EImgType;

import com.example.pet_platform.controller.util.CensorResult;
import com.example.pet_platform.controller.util.ContentWithCensorStateEnum;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class BaiduContentCensorService {

    /**
     * 百度文本审核，识别审核结果的JSON KEY
     */
    final public static String CENSOR_CONCLUSION_TYPE_KEY = "conclusionType";

    @Resource(name = "commonTextCensorClient")
    AipContentCensor commonTextCensorClient;

    /**
     * 获取常规文本审核结果
     *
     * @param content 内容
     * @return 百度内容审核JSON
     */
    public CensorResult getCommonTextCensorResult(String content) {

        //如果内容为空，则直接返回
        if (content == null || content.isEmpty()) {
            return getCensorResult(null);
        }

        try {
            JSONObject response = commonTextCensorClient.textCensorUserDefined(content);
            return getCensorResult(response);
        } catch (Exception exception) {
            System.out.println(exception);
            return getCensorResult(null);
        }
    }

    /**
     * 获取照片审核结果
     *
     * @param imageUrl 图片Url
     * @return 百度图片审核JSON
     */
    public CensorResult getImageCensorResult(String imageUrl) {

        //如果内容为空，则直接返回
        if (imageUrl == null || imageUrl.isEmpty()) {
            return getCensorResult(null);
        }

        try {
            JSONObject response = commonTextCensorClient.imageCensorUserDefined(imageUrl, EImgType.URL, null);
            return getCensorResult(response);
        } catch (Exception exception) {
            System.out.println(exception);
            return getCensorResult(null);
        }
    }

    /**
     * 获取审核结果
     *
     * @param clientJsonObject 百度审核的JSON字段
     * @return 审核结果
     */
    private CensorResult getCensorResult(JSONObject clientJsonObject) {

        //获取代表审核结果的字段
        //审核结果类型，可取值1.合规，2.不合规，3.疑似，4.审核失败
        int conclusionType;

        //如果是null就直接判定为失败
        if (clientJsonObject == null) {
            conclusionType = 4;
        } else {
            conclusionType = clientJsonObject.getInt(CENSOR_CONCLUSION_TYPE_KEY);
        }

        try {
            ContentWithCensorStateEnum result;

            switch (conclusionType) {
                case 1:
                    //合规情况
                    result = ContentWithCensorStateEnum.ADD;
                    break;
                case 2:
                    //不合规情况
                    result = ContentWithCensorStateEnum.CENSOR_FAIL;
                    break;
                case 3:
                    //疑似不合规
                    result = ContentWithCensorStateEnum.CENSOR_SUSPECT;
                    break;
                default:
                    //审核失败和其他情况，都归结到censor_error上去
                    result = ContentWithCensorStateEnum.CENSOR_ERROR;
                    break;
            }

            //过审要求：只能是合规情况
            //解释：因为百度云控制台是可以调节不合规和疑似不合规的参数值的，因此这里只写合规情况就可以了
            boolean isPass = result == ContentWithCensorStateEnum.ADD;

            return new CensorResult(isPass, result, clientJsonObject != null ? clientJsonObject.toString() : null, null);

        } catch (Exception exception) {
            System.out.println(exception);
            //如果出错，就直接返回true
            return new CensorResult(true, null, null, null);
        }

    }

}

