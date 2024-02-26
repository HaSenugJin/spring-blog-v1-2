package shop.mtcoding.blog.board;

import lombok.Data;
import shop.mtcoding.blog.user.User;

public class BoardResponse {

    @Data
    public static class DetailDTO {
        private Integer id;
        private String title;
        private String content;
        private Integer userId; // 게시글 작성자 id
        private String username;
        private Boolean boardOwner;

        public void isBoardOwner(User sessionUser) {
            if (sessionUser == null) {
                boardOwner = false;
            } else {
                boardOwner = sessionUser.getId() == userId;
            }
        }
    }

    @Data
    public static class ReplyDTO {
        private Integer id; // 프라이머리 키
        private Integer userId; // 댓글의 주인여부 확인 해야함
        private String username;
        private String comment;
        private Boolean replyOwner; // 게시글 주인 여부 (세션값과 비교)

        public ReplyDTO(Object[] ob, User sessionUser) {
            this.id = (Integer) ob[0];
            this.userId = (Integer) ob[1];
            this.comment = (String) ob[2];
            this.username = (String) ob[3];
            if(sessionUser == null) replyOwner = false;
            else replyOwner = sessionUser.getId() == userId;
        }
    }
}
