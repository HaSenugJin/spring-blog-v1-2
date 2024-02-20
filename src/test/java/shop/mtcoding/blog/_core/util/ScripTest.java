package shop.mtcoding.blog._core.util;


import org.junit.jupiter.api.Test;

public class ScripTest {

    @Test
    public void back_test() {
        String s = Script.back("권한이 없어요");
        System.out.println(s);
    }
}
