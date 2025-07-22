package com.project.demo.config;


import com.project.demo.common.filter.JWTAuthenticationFilter;
import com.project.demo.common.secuity.JwtAccessDeniedHandler;
import com.project.demo.common.secuity.JwtAuthenticationEntryPoint;
import com.project.demo.common.secuity.UserDetailServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class WebSecurityConfig {

    // 这个类主要是获取库中的用户信息，交给security
    @Resource
    private UserDetailServiceImpl userDetailsService;

    // 这个的类是认证失败处理（我在这里主要是把错误消息以json方式返回）
    @Resource
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    // 鉴权失败的时候的处理类
    @Resource
    private JwtAccessDeniedHandler jwtAccessDeniedHandler;

    public static String[] permitAllUrl = {
            "/authority/login",
            "/authority/register",
            "/favicon.ico",
            "/error",
            "/doc.html",
            "/webjars/**",
            "/v3/api-docs/**",
            "/paly/**",
            "/futures/**",
            "/404",
            "/snake",
            "/Love",
            "/yanhua",
            "/css/**", "/images/**", "/js/**", "/fonts/**","/favicon.ico"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 这个配置是关闭csrf
                .csrf(AbstractHttpConfigurer::disable)
                // 禁用HTTP响应标头
                .headers((headersCustomizer) -> headersCustomizer.cacheControl(HeadersConfigurer.CacheControlConfig::disable).frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                // 处理请求
                .authorizeHttpRequests(authorize -> {
                    // 放开哪些接口
                    authorize
                            .requestMatchers(permitAllUrl)
                            .permitAll();
                    // 其他的都需要认证
                    authorize.anyRequest().authenticated();
                })// 错误处理
                .exceptionHandling(m -> {
                    // 认证失败处理
                    m.authenticationEntryPoint(unauthorizedHandler);
                    // 鉴权失败处理
                    m.accessDeniedHandler(jwtAccessDeniedHandler);
                })
                // 基于token，所以不需要session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 如果使用token这个配置是必须的
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
                // 这个不配置也会生效
                .authenticationProvider(authenticationProvider())
                // 下面这个两个不待测效果了
                .httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults());
        return http.build();
    }

    /**
     * 这个是如果使用token方式需要在这个类中先处理token（根据token判断是否去认证）
     */
    @Bean
    public JWTAuthenticationFilter authenticationJwtTokenFilter() {
        return new JWTAuthenticationFilter();
    }

    // 身份认证提供程序（加密方式，用户查询）
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // 这个主要是为了其他地方可以使用认证管理器
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

    // 加密方式
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}