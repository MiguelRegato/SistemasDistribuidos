package app.security;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    RepositoryUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10, new SecureRandom());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Public pages
        http.authorizeRequests().antMatchers("/").permitAll();
        http.authorizeRequests().antMatchers("/login").permitAll();
        http.authorizeRequests().antMatchers("/loginerror").permitAll();
        http.authorizeRequests().antMatchers("/logout").permitAll();
        http.authorizeRequests().antMatchers("/register").permitAll();
        http.authorizeRequests().antMatchers("/takenUserName").permitAll();
        http.authorizeRequests().antMatchers("/filmUnregistered/*").permitAll();
        http.authorizeRequests().antMatchers("/searchFilms").permitAll();

        // Private pages
        http.authorizeRequests().antMatchers("/menuRegistered").hasAnyRole("USER");
        http.authorizeRequests().antMatchers("/profile/*").hasAnyRole("USER");
        http.authorizeRequests().antMatchers("/editProfile/*").hasAnyRole("USER");
        http.authorizeRequests().antMatchers("/editPassword/*").hasAnyRole("USER");
        http.authorizeRequests().antMatchers("/errorOldPassword/*").hasAnyRole("USER");
        http.authorizeRequests().antMatchers("/followers/*").hasAnyRole("USER");
        http.authorizeRequests().antMatchers("/following/*").hasAnyRole("USER");
        http.authorizeRequests().antMatchers("/watchProfile/*").hasAnyRole("USER");
        http.authorizeRequests().antMatchers("/followUnfollow/*").hasAnyRole("USER");
        http.authorizeRequests().antMatchers("/filmRegistered/*").hasAnyRole("USER");
        http.authorizeRequests().antMatchers("/addComment/*").hasAnyRole("USER");
        http.authorizeRequests().antMatchers("/editComment/*").hasAnyRole("USER");
        http.authorizeRequests().antMatchers("/removeComment/*").hasAnyRole("USER");
        http.authorizeRequests().antMatchers("/menuAdmin").hasAnyRole("ADMIN");
        http.authorizeRequests().antMatchers("/filmAdmin/*").hasAnyRole("ADMIN");
        http.authorizeRequests().antMatchers("/addFilm").hasAnyRole("ADMIN");
        http.authorizeRequests().antMatchers("/removeFilm/*").hasAnyRole("ADMIN");
        http.authorizeRequests().antMatchers("/editFilm/*").hasAnyRole("ADMIN");

        // Login form
        http.formLogin().loginPage("/login");
        http.formLogin().usernameParameter("username");
        http.formLogin().passwordParameter("password");
        http.formLogin().defaultSuccessUrl("/menuRegistered");
        http.formLogin().failureUrl("/loginerror");

        // Logout
        http.logout().logoutUrl("/logout");
        http.logout().logoutSuccessUrl("/");
    }
}