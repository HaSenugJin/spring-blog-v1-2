package shop.mtcoding.blog.board;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import shop.mtcoding.blog._core.config.security.MyLoginUser;
import shop.mtcoding.blog.user.User;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class BoardController {

    private final BoardRepository boardRepository;
    private final HttpSession session;


    // @RequestBody BoardRequest.UpdateDTO : json데이터를 받을 수 있음
    // @RequestBody String : 평문
    @PostMapping("/board/{id}/update")
    public String update(@PathVariable int id, BoardRequest.UpdateDTO requestDTO, @AuthenticationPrincipal MyLoginUser myLoginUser) {
        // 모델 위임
        // 2. 권한 체크
        Board board = boardRepository.findById(id);

        if (board.getUserId() != myLoginUser.getUser().getId()) {
            return "error/403";
        }

        // 3. 핵심 로직
        // update board_tb set title = ?, content = ? where id = ?;
        boardRepository.update(requestDTO, id);

        return "redirect:/board/"+id;
    }

    @GetMapping("/board/{id}/updateForm")
    public String updateForm(@PathVariable int id, HttpServletRequest request, @AuthenticationPrincipal MyLoginUser myLoginUser) {

        // 모델 위임 (id로 board를 조회)
        // 권한 체크
        Board board = boardRepository.findById(id);

        if (board.getUserId() != myLoginUser.getUser().getId()) {
            return "error/403";
        }

        // 가방에 담기
        request.setAttribute("board", board);

        return "board/updateForm";
    }

    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable int id, HttpServletRequest request, @AuthenticationPrincipal MyLoginUser myLoginUser) {

        // 2. 권환 없으면 막기
        Board board = boardRepository.findById(id);
        // 작성자 id != 로그인한 사람의 id
        if (board.getUserId() != myLoginUser.getUser().getId()) {
            request.setAttribute("status", 403);
            request.setAttribute("msg", "게시글을 삭제할 권한이 없습니다.");
            return "error/40x"; // 리다이렉트 하면 리퀘스트에 적은 내용이 사라진다.(하면 안된다)
        }

        boardRepository.deleteById(id);

        return "redirect:/";
    }

    @PostMapping("/board/save")
    public String save(BoardRequest.SaveDTO requestDTO, HttpServletRequest request, @AuthenticationPrincipal MyLoginUser myLoginUser) {

        // 2. 바디 데이터 확인 및 유효성 검사
        if (requestDTO.getTitle().length() > 30) {
            request.setAttribute("status", 400);
            request.setAttribute("msg", "title의 길이가 30자를 초과해선 안됩니다.");
            return "error/40x"; // BodRequest
        }

        // 3. 모델에게 위임
        // insert into board_tb(title, content, user_id, create_at) values(?,?,? now())
        boardRepository.save(requestDTO, myLoginUser.getUser().getId());

        return "redirect:/";
    }

    @GetMapping({"/"})
    public String index(HttpServletRequest request, @AuthenticationPrincipal MyLoginUser myLoginUser) {
        System.out.println("로그인 되었나? : " + myLoginUser.getUsername());
        request.getSession();
        List<Board> boardList = boardRepository.findAll();
        request.setAttribute("boardList", boardList);

        return "index";
    }

    @GetMapping("/board/saveForm")
    public String saveForm() {
        return "board/saveForm";
    }

    @GetMapping("/board/{id}")
    public String detail(@PathVariable int id, HttpServletRequest request, @AuthenticationPrincipal MyLoginUser myLoginUser) {
        // 1. 모델 진입 : 상세보기 데이터 가져오기
        // 바디 데이터가 없으면 유효성 검사가 필요없지 ㅎ
        BoardResponse.DetailDTO responseDTO = boardRepository.findByIdWithUser(id);

        boolean pageOwner;
        if (myLoginUser == null) {
            pageOwner = false;
        } else {
            int 게시글작성자번호 = responseDTO.getUserId();
            int 로그인한사람의번호 = myLoginUser.getUser().getId();
            pageOwner = 게시글작성자번호 == 로그인한사람의번호;
        }

        request.setAttribute("board", responseDTO);
        request.setAttribute("pageOwner", pageOwner);
        return "board/detail";
    }
}