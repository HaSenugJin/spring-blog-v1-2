package shop.mtcoding.blog.board;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import shop.mtcoding.blog.user.User;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class BoardController {

    private final BoardRepository boardRepository;
    private final HttpSession session;

    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable int id, HttpServletRequest request) {
        User sessionUser = (User) session.getAttribute("sessionUser");

        // 1. 인증 안되면 막기
        if (sessionUser == null) { // 401
            return "redirect:/loginForm";
        }

        // 2. 권환 없으면 막기
        Board board = boardRepository.findById(id);
        // 작성자 id != 로그인한 사람의 id
        if (board.getUserId() != sessionUser.getId()) {
            request.setAttribute("status", 403);
            request.setAttribute("msg", "게시글을 삭제할 권한이 없습니다.");
            return "error/40x"; // 리다이렉트 하면 리퀘스트에 적은 내용이 사라진다.(하면 안된다)
        }

        boardRepository.deleteById(id);

        return "redirect:/";
    }

    @PostMapping("/board/save")
    public String save(BoardRequest.SaveDTO requestDTO, HttpServletRequest request) {
        // 1. 인증 체크
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/loginForm";
        }

        // 2. 바디 데이터 확인 및 유효성 검사
        System.out.println(requestDTO);
        if (requestDTO.getTitle().length() > 30) {
            request.setAttribute("status", 400);
            request.setAttribute("msg", "title의 길이가 30자를 초과해선 안됩니다.");
            return "error/40x"; // BodRequest
        }

        // 3. 모델에게 위임
        // insert into board_tb(title, content, user_id, create_at) values(?,?,? now())
        boardRepository.save(requestDTO, sessionUser.getId());

        return "redirect:/";
    }

    @GetMapping({ "/", "/board" })
    public String index(HttpServletRequest request) {
        request.getSession();
        List<Board> boardList = boardRepository.findAll();
        request.setAttribute("boardList", boardList);

        return "index";
    }

    @GetMapping("/board/saveForm")
    public String saveForm() {

        // 글쓰기 페이지 인증체크
        User sessionUser = (User) session.getAttribute("sessionUser");
        if(sessionUser == null) {
            return "redirect:/loginForm";
        }

        return "board/saveForm";
    }

    @GetMapping("/board/{id}")
    public String detail(@PathVariable int id, HttpServletRequest request) {

        // 1. 모델 진입 : 상세보기 데이터 가져오기
        // 바디 데이터가 없으면 유효성 검사가 필요없지 ㅎ
        BoardResponse.DetailDTO responseDTO = boardRepository.findByIdWithUser(id);

        // 2. 페이지 주인 여부 체크 (board의 userId와 sessionUser의 id를 비교)
        boolean pageOwner = false;
        User sessionUser = (User) session.getAttribute("sessionUser");

        // sessionUser을 비교해서 로그인 한 상태인지 확인 (로그인 안햇으면 null값일것)
        // 이렇게 해야 상세보기 할 때 null들어가서 오류 나는거 막음
        if (sessionUser != null) {
            // sessionUser의 값이 null이 아닐 때 만 if문 동작시킴
            // 비교하고자 하는 데이터의 값이 같은지 확인 하여야 한다.
            if(sessionUser.getId() == responseDTO.getUserId()) {
                pageOwner = true;
            }
        }

        request.setAttribute("board", responseDTO);
        request.setAttribute("pageOwner", pageOwner);
        return "board/detail";
    }
}