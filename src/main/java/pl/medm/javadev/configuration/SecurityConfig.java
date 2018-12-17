package pl.medm.javadev.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    @Autowired
    private MySavedRequestAwareAuthenticationSuccessHandler mySavedRequestAwareAuthenticationSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return passwordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .and()
                .authorizeRequests()
                .antMatchers("/login").permitAll()
                .antMatchers("/logout").permitAll()
                //WYŚWIETLANIE LISTY UŻYTKOWNIKÓW
                .antMatchers(HttpMethod.GET, "/user").hasAnyRole( "ADMIN")
                //DODAWANIE UŻYTKOWNIKÓW
                .antMatchers(HttpMethod.POST, "/user").hasAnyRole( "ADMIN")
                //EDYTOWANIE DANYCH UŻYTKOWNIKA
                .antMatchers(HttpMethod.PUT, "/user/{id}").hasAnyRole( "USER", "ADMIN")
                //USUWANIE UŻYTKOWNIKA
                .antMatchers(HttpMethod.DELETE, "/user/{id}").hasAnyRole( "ADMIN")
                //ZMIANA HASŁA UŻYTKOWNIKA
                .antMatchers(HttpMethod.PUT, "/user/{id}/password").hasAnyRole("USER", "ADMIN")
                //WYŚWIETLANIE LISTY WYKŁADÓW
                .antMatchers(HttpMethod.GET, "/lectures").hasAnyRole("USER", "ADMIN")
                //DODAWANIE WYKŁADU
                .antMatchers(HttpMethod.POST,"/lectures").hasAnyRole("ADMIN")
                //WYŚWIETLANIE SZCZEGÓŁÓW WYKŁADU
                .antMatchers(HttpMethod.GET,"/lectures/{id}").hasAnyRole("USER", "ADMIN")
                //EDYCJA WYKŁADU
                .antMatchers(HttpMethod.PUT,"/lectures/{id}").hasAnyRole("ADMIN")
                //USUWANIE WYKŁADU
                .antMatchers(HttpMethod.DELETE,"/lectures/{id}").hasAnyRole("ADMIN")
                //WPISYWANIE SIĘ NA LISTĘ OBECNOŚCI
                .antMatchers(HttpMethod.POST,"/lectures/{id}/users").hasAnyRole("USER")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                //.successHandler(mySavedRequestAwareAuthenticationSuccessHandler)
                .and()
                .headers().frameOptions().disable()
                .and()
                .logout();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user")
                .password("{noop}user")
                .roles("USER")
                .and()
                .withUser("admin")
                .password("{noop}admin")
                .roles("ADMIN");
    }
}
