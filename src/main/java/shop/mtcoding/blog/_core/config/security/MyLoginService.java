package shop.mtcoding.blog._core.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import shop.mtcoding.blog.user.User;
import shop.mtcoding.blog.user.UserRepository;

// Post, /login, x폼, 키값이 username, password
@RequiredArgsConstructor
@Service
public class MyLoginService implements UserDetailsService {

    private final UserRepository userRepository;
    private final HttpSession session;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("loadUserByUsername : " + username);

        User user = userRepository.findByUsername(username);

        if (user == null) {
            System.out.println("user = null");
            return null;
        } else {
            // 세션에 넣기전에 getPassword 를 때려서 자기가 직접 비교해서 인증함 맞으면 세션이 만들어짐
            System.out.println("user를 찾았어요");
            session.setAttribute("sessionUser", user); // 머스테치에서만 사용해야함
            return new MyLoginUser(user); // SecurityContextHolder 저장
        }
    }
}
