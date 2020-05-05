package com.cybr406.account.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import javax.sql.DataSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${spring.h2.console.enabled}")
            boolean h2ConsoleEnabled;

    @Autowired
    DataSource dataSource;



    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // Configure authentication to use the database.

        auth
                .jdbcAuthentication()
                .dataSource(dataSource);
    }
    @Bean
    public UserDetailsManager userDetailsManager(){

        return new JdbcUserDetailsManager(dataSource);

    }
    @Bean
    public PasswordEncoder passwordEncoder(){

            return PasswordEncoderFactories.createDelegatingPasswordEncoder();
}
    @Bean
    public User.UserBuilder userBuilder(){
    PasswordEncoder passwordEncoder = passwordEncoder();
    User.UserBuilder users = User.builder();
    users.passwordEncoder(passwordEncoder :: encode);
    return users;


}



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        configureH2ConsoleSecurity(http);
        http.authorizeRequests()
                .mvcMatchers(HttpMethod.GET, "/**").permitAll()
                .mvcMatchers(HttpMethod.POST, "/signup").permitAll()
                .anyRequest().authenticated()
                .and()
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .httpBasic();

    }
    private void configureH2ConsoleSecurity(HttpSecurity http) throws Exception{
        if (h2ConsoleEnabled){
            http
                    .authorizeRequests()
                    .antMatchers("/h2-console/**").permitAll();
            http.headers().frameOptions().sameOrigin();
        }
    }


}
