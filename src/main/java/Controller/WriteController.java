package Controller;

import java.io.IOException;

import DAO.MVCBoardDAO;
import VO.MVCBoardVO;
import fileupload.FileUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import utils.JSFunction;


@MultipartConfig(
        maxFileSize = 1024 * 1024 * 1,
        maxRequestSize = 1024 * 1024 * 10
)
public class WriteController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/View/Write.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {


        // 1. 파일 업로드 처리 =============================
        // 업로드 디렉터리의 물리적 경로 확인
        String saveDirectory = req.getServletContext().getRealPath("/Uploads");

        // 파일 업로드
        String originalFileName = "";
        try {
            originalFileName = FileUtil.uploadFile(req, saveDirectory);
        }
        catch (Exception e) {
            JSFunction.alertLocation(resp, "파일 업로드 오류입니다.",
                    "../Controller/write.do");
            return;
        }

        // 2. 파일 업로드 외 처리 =============================
        // 폼값을 DTO에 저장
        MVCBoardVO vo = new MVCBoardVO();
        vo.setName(req.getParameter("name"));
        vo.setTitle(req.getParameter("title"));
        vo.setContent(req.getParameter("content"));
        vo.setPass(req.getParameter("pass"));

        // 원본 파일명과 저장된 파일 이름 설정
        if (originalFileName != "") {
            // 파일명 변경
            String savedFileName = FileUtil.renameFile(saveDirectory, originalFileName);

            vo.setOfile(originalFileName);  // 원래 파일 이름
            vo.setSfile(savedFileName);  // 서버에 저장된 파일 이름
        }

        // DAO를 통해 DB에 게시 내용 저장
        MVCBoardDAO dao = new MVCBoardDAO();
        int result = dao.insertWrite(vo);
        dao.close();

        // 성공 or 실패?
        if (result == 1) {  // 글쓰기 성공
            resp.sendRedirect("../Controller/list.do");
        }
        else {  // 글쓰기 실패
            JSFunction.alertLocation(resp, "글쓰기에 실패했습니다.",
                    "../Controller/write.do");
        }
    }
}