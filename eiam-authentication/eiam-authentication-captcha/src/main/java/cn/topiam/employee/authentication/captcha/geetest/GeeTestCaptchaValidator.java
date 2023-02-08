/*
 * eiam-authentication-captcha - Employee Identity and Access Management Program
 * Copyright © 2020-2023 TopIAM (support@topiam.cn)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cn.topiam.employee.authentication.captcha.geetest;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson2.JSONObject;

import cn.topiam.employee.authentication.captcha.CaptchaValidator;
import cn.topiam.employee.common.util.RequestUtils;

/**
 * 极速验证
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/14 19:11
 */
public class GeeTestCaptchaValidator implements CaptchaValidator {
    private static final String RESULT  = "result";
    private static final String SUCCESS = "success";

    private final Logger        logger  = LoggerFactory.getLogger(GeeTestCaptchaValidator.class);

    /**
     * 验证
     *
     * @param request {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     * @return {@link Boolean}
     */
    @Override
    public boolean validate(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> getParams = RequestUtils.getParams(request);
        // 1.初始化极验参数信息
        String captchaId = config.getCaptchaId();
        String captchaKey = config.getCaptchaKey();
        String domain = "https://gcaptcha4.geetest.com";

        // 2.获取用户验证后前端传过来的验证流水号等参数
        String lotNumber = (String) getParams.get("lot_number");
        String captchaOutput = (String) getParams.get("captcha_output");
        String passToken = (String) getParams.get("pass_token");
        String genTime = (String) getParams.get("gen_time");

        // 3.生成签名
        // 生成签名使用标准的hmac算法，使用用户当前完成验证的流水号lot_number作为原始消息message，使用客户验证私钥作为key
        // 采用sha256散列算法将message和key进行单向散列生成最终的签名
        String signToken = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, captchaKey)
            .hmacHex(lotNumber);

        // 4.上传校验参数到极验二次验证接口, 校验用户验证状态
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("lot_number", lotNumber);
        queryParams.add("captcha_output", captchaOutput);
        queryParams.add("pass_token", passToken);
        queryParams.add("gen_time", genTime);
        queryParams.add("sign_token", signToken);
        // captcha_id 参数建议放在 url 后面, 方便请求异常时可以在日志中根据id快速定位到异常请求
        String url = String.format(domain + "/validate" + "?captcha_id=%s", captchaId);
        HttpHeaders headers = new HttpHeaders();
        HttpMethod method = HttpMethod.POST;
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        JSONObject jsonObject;
        //注意处理接口异常情况，当请求极验二次验证接口异常时做出相应异常处理
        //保证不会因为接口请求超时或服务未响应而阻碍业务流程
        try {
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(queryParams,
                headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, method,
                requestEntity, String.class);
            String resBody = responseEntity.getBody();
            jsonObject = JSONObject.parseObject(resBody);
        } catch (Exception e) {
            logger.error("验证发生异常:  {}", e.getMessage());
            return false;
        }
        // 5.根据极验返回的用户验证状态, 网站主进行自己的业务逻辑
        if (SUCCESS.equals(jsonObject.getString(RESULT))) {
            logger.info("验证成功:  {}", jsonObject.toJSONString());
            return true;
        }
        logger.info("验证失败:  {}", jsonObject.toJSONString());
        return true;
    }

    private final GeeTestCaptchaProviderConfig config;
    private final RestTemplate                 restTemplate;

    public GeeTestCaptchaValidator(GeeTestCaptchaProviderConfig config, RestTemplate restTemplate) {
        this.config = config;
        this.restTemplate = restTemplate;
    }
}
