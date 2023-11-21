package Controller;

import java.io.IOException;

import DAO.MVCBoardDAO;
import VO.MVCBoardVO;
import fileupload.FileUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import utils.JSFunction;

@WebServlet("/Controller/edit.do")
@MultipartConfig(
        maxFileSize = 1024 * 1024 * 1,
        maxRequestSize = 1024 * 1024 * 10
)
public class EditController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String idx = req.getParameter("idx");
        MVCBoardDAO dao = new MVCBoardDAO();
        MVCBoardVO vo = dao.selectView(idx);
        req.setAttribute("vo", vo);
        req.getRequestDispatcher("/View/Edit.jsp").forward(req, resp);
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
            JSFunction.alertBack(resp, "파일 업로드 오류입니다.");
            return;
        }

        // 2. 파일 업로드 외 처리 =============================
        // 수정 내용을 매개변수에서 얻어옴
        String idx = req.getParameter("idx");
        String prevOfile = req.getParameter("prevOfile");
        String prevSfile = req.getParameter("prevSfile");

        String name = req.getParameter("name");
        String title = req.getParameter("title");
        String content = req.getParameter("content");

        // 비밀번호는 session에서 가져옴
        HttpSession session = req.getSession();
        String pass = (String)session.getAttribute("pass");

        // DTO에 저장
        MVCBoardVO vo = new MVCBoardVO();
        vo.setIdx(idx);
        vo.setName(name);
        vo.setTitle(title);
        vo.setContent(content);
        vo.setPass(pass);

        // 원본 파일명과 저장된 파일 이름 설정
        if (originalFileName != "") {
            String savedFileName = FileUtil.renameFile(saveDirectory, originalFileName);

            vo.setOfile(originalFileName);  // 원래 파일 이름
            vo.setSfile(savedFileName);  // 서버에 저장된 파일 이름

            // 기존 파일 삭제
            FileUtil.deleteFile(req, "/Uploads", prevSfile);
        }
        else {
            // 첨부 파일이 없으면 기존 이름 유지
            vo.setOfile(prevOfile);
            vo.setSfile(prevSfile);
        }

        // DB에 수정 내용 반영
        MVCBoardDAO dao = new MVCBoardDAO();
        int result = dao.updatePost(vo);
        dao.close();

        // 성공 or 실패?
        if (result == 1) {  // 수정 성공
            session.removeAttribute("pass");
            resp.sendRedirect("../Controller/view.do?idx=" + idx);
        }
        else {  // 수정 실패
            JSFunction.alertLocation(resp, "비밀번호 검증을 다시 진행해주세요.",
                    "../Controller/view.do?idx=" + idx);
        }
    }
}