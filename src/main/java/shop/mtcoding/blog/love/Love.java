package shop.mtcoding.blog.love;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Table(name="love_tb", uniqueConstraints = {
        @UniqueConstraint(
                name="love_uk",
                columnNames={"board_id","user_id"}
        )})
@Data
@Entity // 테이블 생성하기 위해 필요한 어노테이션
public class Love {

    @Id // PK 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment 전략
    private Integer id;
    private Integer userId;
    private Integer boardId;
    private Timestamp createdAt;
}
