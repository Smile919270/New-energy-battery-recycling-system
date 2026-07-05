package cn.zyc.basics.security;

import cn.zyc.basics.redis.RedisTemplateHelper;
import cn.zyc.basics.security.jwt.*;
import cn.zyc.basics.utils.SecurityUtil;
import cn.zyc.basics.parameter.ZwzLoginProperties;
import cn.zyc.basics.security.validate.ImageValidateFilter;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

 
@ApiOperation(value = "SpringSecurity配置类")
@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    private ZwzLoginProperties zwzLoginProperties;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private AuthenticationFailHandler authenticationFailHandler;

    @Autowired
    private ZwzAccessDeniedHandler zwzAccessDeniedHandler;

    @Autowired
    private ZwzAuthenticationEntryPoint zwzAuthenticationEntryPoint;

    @Autowired
    private ImageValidateFilter imageValidateFilter;

    @Autowired
    private RedisTemplateHelper redisTemplate;

    @Autowired
    private SecurityUtil securityUtil;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests().requestMatchers("/zyc/dictData/getByType/**","/zyc/file/view/**","/zyc/user/regist","/zyc/user/sendSmsCode","/zyc/user/smsLogin","/zyc/user/social/**","/zyc/common/**","/*/*.js","/*/*.css","/*/*.png","/*/*.ico","/*/*.jpg","/default-avatar.jpg", "/swagger-ui.html").permitAll()
                .and().formLogin().loginPage("/zyc/common/needLogin").loginProcessingUrl("/zyc/login").permitAll()
                .successHandler(authenticationSuccessHandler).failureHandler(authenticationFailHandler).and()
                .headers().frameOptions().disable().and()
                .logout()
                .permitAll()
                .and()
                .authorizeHttpRequests()
                .anyRequest()
                .authenticated()
                .and()
                .cors().and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(zwzAuthenticationEntryPoint)
                .accessDeniedHandler(zwzAccessDeniedHandler)
                .and()
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(imageValidateFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userDetailsService.loadUserByUsername(username);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public JwtTokenOncePerRequestFilter authenticationJwtTokenFilter() throws Exception {
        return new JwtTokenOncePerRequestFilter(redisTemplate, securityUtil, zwzLoginProperties);
    }
}
