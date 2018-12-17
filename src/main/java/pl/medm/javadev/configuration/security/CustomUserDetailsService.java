package pl.medm.javadev.configuration.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import pl.medm.javadev.model.Role;
import pl.medm.javadev.model.User;
import pl.medm.javadev.repository.UserRepository;

import java.util.HashSet;
import java.util.Set;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        org.springframework.security.core.userdetails.User userDetails;
        User user;

        user = userRepository.findByEmail(username);
        if (user != null) {
            userDetails = new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    convertAuthorities(user.getRoles())
            );
            return userDetails;
        }
        user = userRepository.findByIndexNumber(username);
        if (user != null) {
            userDetails = new org.springframework.security.core.userdetails.User(
                    user.getIndexNumber(),
                    user.getPassword(),
                    convertAuthorities(user.getRoles())
            );
            return userDetails;
        }

        throw new UsernameNotFoundException("User not found");
    }

    private Set<GrantedAuthority> convertAuthorities(Set<Role> userRoles) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        for(Role ur: userRoles) {
            authorities.add(new SimpleGrantedAuthority(ur.getRole()));
        }
        return authorities;
    }
}
