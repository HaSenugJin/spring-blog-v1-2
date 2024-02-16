package shop.mtcoding.blog.board;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import shop.mtcoding.blog.reply.ReplyRepository;
import shop.mtcoding.blog.user.User;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class BoardController {

    private final BoardRepository boardRepository;
    private final HttpSession session;
    private final ReplyRepository replyRepository;

    // @RequestBody BoardRequest.UpdateDTO : json데이터를 받을 수 있음
    // @RequestBody String : 평문
    @PostMapping("/board/{id}/update")
    public String update(@PathVariable int id, BoardRequest.UpdateDTO requestDTO) {
        // 1. 인증 체크
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/loginForm";
        }

        // 모델 위임
        // 2. 권한 체크
        Board board = boardRepository.findById(id);

        if (board.getUserId() != sessionUser.getId()) {
            return "error/403";
        }

        // 3. 핵심 로직
        // update board_tb set title = ?, content = ? where id = ?;
        boardRepository.update(requestDTO, id);

        return "redirect:/board/"+id;
    }

    @GetMapping("/board/{id}/updateForm")
    public String updateForm(@PathVariable int id, HttpServletRequest request) {
        // 인증 체크
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/loginForm";
        }

        // 모델 위임 (id로 board를 조회)
        // 권한 체크
        Board board = boardRepository.findById(id);

        if (board.getUserId() != sessionUser.getId()) {
            return "error/403";
        }

        // 가방에 담기
        request.setAttribute("board", board);

        return "board/updateForm";
    }

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

    // localhost:8080?page=1 -> page값이 1
    // localhost:8080 -> page값이 0
    @GetMapping("/")
    public String index(HttpServletRequest request,
                        @RequestParam(defaultValue = "0") Integer page,
                        @RequestParam(defaultValue = "") String keyword) {

        // isEmpty -> null, 공백
        // isBlank -> null, 공백, 스페이스

        if (keyword.isBlank()) {
            List<Board> boardList = boardRepository.findAll(page);
            int count = boardRepository.count().intValue();

            int namerge = count % 3 == 0 ? 0 : 1;
            int allPageCount = count / 3 + namerge;

            request.setAttribute("boardList", boardList);
            request.setAttribute("first", page == 0);
            request.setAttribute("last", allPageCount == page + 1);
            request.setAttribute("prev", page - 1);
            request.setAttribute("next", page + 1);
            request.setAttribute("keyword", "");
        } else {
            List<Board> boardList = boardRepository.findAll(page, keyword);

            int count = boardRepository.count(keyword).intValue();

            int namerge = count % 3 == 0 ? 0 : 1;
            int allPageCount = count / 3 + namerge;

            request.setAttribute("boardList", boardList);
            request.setAttribute("first", page == 0);
            request.setAttribute("last", allPageCount == page + 1);
            request.setAttribute("prev", page - 1);
            request.setAttribute("next", page + 1);
            request.setAttribute("keyword", keyword);
        }

        // 전체 페이지 개수


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
        User sessionUser = (User) session.getAttribute("sessionUser");
        BoardResponse.DetailDTO boardDTO = boardRepository.findByIdWithUser(id);
        boardDTO.isBoardOwner(sessionUser);

        List<BoardResponse.ReplyDTO> replyDTOList = replyRepository.findByBoardId(id, sessionUser);

        request.setAttribute("replyList", replyDTOList);
        request.setAttribute("board", boardDTO);

        return "board/detail";
    }
}