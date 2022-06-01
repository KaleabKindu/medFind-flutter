// package com.gis.medfind.jwt;

// import java.util.ArrayList;

// import com.gis.medfind.repository.UserRepository;
// import com.gis.medfind.security.CustomUserDetails;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.userdetails.User;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.stereotype.Service;

// @Service
// public class JwtUserDetailsService implements UserDetailsService {

// 	@Autowired
//     private UserRepository userRepository;
     
//     @Override
//     public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//         User user = userRepository.findByEmail(email);
//         if (user == null) {
//             throw new UsernameNotFoundException("User with the email" + email + "is not found");

//         }
//         return new CustomUserDetails(user);
//     }
// }