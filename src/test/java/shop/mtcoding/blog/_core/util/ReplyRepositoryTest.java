package shop.mtcoding.blog._core.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import shop.mtcoding.blog.board.BoardResponse;
import shop.mtcoding.blog.reply.ReplyRepository;
import shop.mtcoding.blog.user.User;

import java.util.List;

@Import(ReplyRepository.class) // 내가 만든 클래스는 import 해줘야 한다.
@DataJpaTest // DB 관련 객체들이 IoC에 뜬다.
public class ReplyRepositoryTest {

    @Autowired // Test에서 DI 하는 코드
    private ReplyRepository replyRepository;
    private User user = new User();

    @Test
    public void findByBoardId_test() {
        Integer id = 1;

        List<BoardResponse.ReplyDTO> replyDTOList = replyRepository.findByBoardId(id, user);

        System.out.println(replyDTOList);
    }
}
