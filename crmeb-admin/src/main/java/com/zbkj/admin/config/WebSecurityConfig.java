package com.zbkj.admin.config;

import com.zbkj.admin.filter.JwtAuthenticationTokenFilter;
import com.zbkj.admin.manager.AuthenticationEntryPointImpl;
import com.zbkj.admin.manager.CustomAccessDeniedHandler;
import com.zbkj.admin.manager.CustomAuthenticationProvider;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.UploadConstants;
import com.zbkj.service.service.impl.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.CorsFilter;

/**
 * Security配置
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig {

    @Autowired
    private CorsFilter corsFilter;

    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
        return new JwtAuthenticationTokenFilter();
    }

    @Bean
    public AuthenticationEntryPointImpl unauthorizedHandler() {
        return new AuthenticationEntryPointImpl();
    }

    @Bean
    public CustomAccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider(new UserDetailServiceImpl());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> {})
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(unauthorizedHandler())
                .accessDeniedHandler(accessDeniedHandler()))
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/admin/login", "/api/admin/validate/code/get").permitAll()
                .requestMatchers("/api/admin/getLoginPic").permitAll()
                .requestMatchers("/api/admin/login/account/detection").permitAll()
                .requestMatchers("/api/admin/validate/code/getcaptchaconfig").permitAll()
                .requestMatchers("/" + UploadConstants.UPLOAD_FILE_KEYWORD + "/**").permitAll()
                .requestMatchers("/" + UploadConstants.DOWNLOAD_FILE_KEYWORD + "/**").permitAll()
                .requestMatchers("/" + UploadConstants.UPLOAD_AFTER_FILE_KEYWORD + "/**").permitAll()
                .requestMatchers("/api/admin/upload/image").permitAll()
                .requestMatchers("/api/admin/upload/file").permitAll()
                .requestMatchers(HttpMethod.GET,
                    "/*.html").permitAll()
                .requestMatchers(new AntPathRequestMatcher("/**/*.html", "GET")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/**/*.css", "GET")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/**/*.js", "GET")).permitAll()
                .requestMatchers("/profile/**").permitAll()
                .requestMatchers("/common/download**").permitAll()
                .requestMatchers("/common/download/resource**").permitAll()
                .requestMatchers("/doc.html").permitAll()
                .requestMatchers("/swagger-resources/**").permitAll()
                .requestMatchers("/webjars/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/swagger-ui.html/**").permitAll()
                .requestMatchers("/druid/**").permitAll()
                .requestMatchers("/captcha/get", "/captcha/check").permitAll()
                .requestMatchers("/api/admin/payment/callback/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .anyRequest().authenticated())
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
            .authenticationProvider(customAuthenticationProvider())
            .addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(corsFilter, JwtAuthenticationTokenFilter.class)
            .addFilterBefore(corsFilter, LogoutFilter.class);

        return http.build();
    }
}
