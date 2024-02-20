package shop.mtcoding.blog.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import shop.mtcoding.blog._core.util.Script;


@RequiredArgsConstructor // final이 붙은 애들에 대한 생성자를 만들어줌
@Controller
public class UserController {

    // 자바는 final 변수는 반드시 초기화가 되어야함.
    private final UserRepository userRepository;
    private final HttpSession session;

    @PostMapping("/user/update")
    public String update(UserRequest.LoginDTO requestDTO, UserRequest.UpdateDTO updateDTO) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        userRepository.update(updateDTO, sessionUser.getId());
//        User user = userRepository.findByUsernameAndPassword(requestDTO);
//        session.setAttribute("sessionUser", user);

        return "redirect:/";
    }

    @GetMapping("/user/updateForm")
    public String updateForm(HttpServletRequest request) {
        User sessionUser = (User) session.getAttribute("sessionUser");

        if (sessionUser == null) {
            return "redirect:/loginForm";
        }

        request.setAttribute("username", sessionUser.getUsername());
        request.setAttribute("email", sessionUser.getEmail());

        return "user/updateForm";
    }

    // 왜 조회인데 post임? 민간함 정보는 body로 보낸다.
    // 로그인만 예외로 select인데 post 사용
    // select * from user_tb where username=? and password=?
    @PostMapping("/login")
    public String login(UserRequest.LoginDTO requestDTO){
        System.out.println(requestDTO); // toString -> @Data

        if(requestDTO.getUsername().length() < 3){
            return "error/400"; // ViewResolver 설정이 되어 있음. (앞 경로, 뒤 경로)
        }

        User user = userRepository.findByUsernameAndPassword(requestDTO);
        session.setAttribute("sessionUser", user); // 락카에 담음 (StateFul)

        return "redirect:/"; // 컨트롤러가 존재하면 무조건 redirect 외우기
    }

    @PostMapping("/join")
    public String join(UserRequest.JoinDTO requestDTO){
        System.out.println(requestDTO);

        try {
            userRepository.save(requestDTO);
        } catch (Exception e) {
            throw new RuntimeException("ID가 중복 되었습니다.");
        }

        return "redirect:/loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "user/joinForm";
    }

    @GetMapping("/loginForm")
    public String loginForm() {
        return "user/loginForm";
    }


    @GetMapping("/logout")
    public String logout() {
        session.invalidate();
        return "redirect:/";
    }
}
