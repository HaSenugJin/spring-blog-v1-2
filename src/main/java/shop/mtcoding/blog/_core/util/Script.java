package shop.mtcoding.blog._core.util;

public class Script {

    // 메시지를 띄우고 전 주소로 이동
    public static String back(String msg) {
        StringBuilder sb = new StringBuilder();
        sb.append("<script>");
        sb.append("alert('"+msg+"');");
        sb.append("history.back();");
        sb.append("</script>");
        return sb.toString();
    }

    // 지정된 하이퍼 링크로 이동
    public static String href(String path) {
        StringBuilder sb = new StringBuilder();
        sb.append("<script>");
        sb.append("location.href='"+path+"';");
        sb.append("</script>");
        return sb.toString();
    }

    // 메시지를 띄우고 지정되 하이퍼 링크로 이동
    public static String href(String path, String msg) {
        StringBuilder sb = new StringBuilder();
        sb.append("<script>");
        sb.append("alert('"+msg+"');");
        sb.append("location.href='"+path+"';");
        sb.append("</script>");
        return sb.toString();
    }
}