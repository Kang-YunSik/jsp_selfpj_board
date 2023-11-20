package Controller;

import DAO.MVCBoardDAO;
import VO.MVCBoardVO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebServlet("/Controller/view.do")
public class ViewController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // 게시물 불러오기
        MVCBoardDAO dao = new MVCBoardDAO();
        String idx = req.getParameter("idx");
        dao.updateVisitCount(idx);  // 조회수 1 증가
        MVCBoardVO vo = dao.selectView(idx);
        dao.close();

        // 줄바꿈 처리
        vo.setContent(vo.getContent().replaceAll("\r\n", "<br/>"));

        //첨부파일 확장자 추출 및 이미지 타입 확인
        String ext = null, fileName = vo.getSfile();
        if(fileName!=null) {
            ext = fileName.substring(fileName.lastIndexOf(".")+1);
        }
        String[] mimeStr = {"png","jpg","gif"};
        List<String> mimeList = Arrays.asList(mimeStr);
        boolean isImage = false;
        if(mimeList.contains(ext)) {
            isImage = true;
        }


        // 게시물(dto) 저장 후 뷰로 포워드
        req.setAttribute("vo", vo);
        req.setAttribute("isImage", isImage);
        req.getRequestDispatcher("/View/View.jsp").forward(req, resp);
    }
}
