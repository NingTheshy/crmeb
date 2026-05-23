package com.zbkj.front.config;

import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Captcha配置 - 提供CaptchaService的简单实现
 * 原captcha库使用javax.servlet，与Spring Boot 3.x不兼容
 */
@Configuration
public class CaptchaConfig {

    @Bean
    public CaptchaService captchaService() {
        return new CaptchaService() {
            @Override
            public void init(Properties properties) {
            }

            @Override
            public ResponseModel get(CaptchaVO captchaVO) {
                return new ResponseModel();
            }

            @Override
            public ResponseModel check(CaptchaVO captchaVO) {
                return new ResponseModel();
            }

            @Override
            public ResponseModel verification(CaptchaVO captchaVO) {
                return new ResponseModel();
            }

            @Override
            public String captchaType() {
                return "default";
            }

            @Override
            public void destroy(Properties properties) {
            }
        };
    }
}
