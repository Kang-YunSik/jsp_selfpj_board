package DAO;

import VO.MVCBoardVO;
import common.DBConnPool;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

public class MVCBoardDAO extends DBConnPool {
    // DB 연결 : 커넥션 풀
    public MVCBoardDAO() {
        super();
    }

    // 게시물 수를 세어주는 메소드
    public int selectCount(Map<String, Object> map) {
        int totalCount = 0;

        String query = "SELECT COUNT(*) FROM mvcboard";
        if (map.get("searchWord") != null) {
            query += " WHERE " + map.get("searchField") + " "
                    + " LIKE '%" + map.get("searchWord") + "%'";
        }

        try{
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            totalCount = rs.getInt(1);
        }catch (Exception e) {
            System.out.println("게시물 카운트 중 예외 발생");
            e.printStackTrace();
        }

        return totalCount;  // 게시물 개수를 서블릿으로 반환
    }

    // 게시물 목록을 반환해주는 메소드
    public List<MVCBoardVO> selectListPage(Map<String,Object> map) {
        List<MVCBoardVO> board = new Vector<>();

        String query = "SELECT * FROM (" +
                " SELECT Tb.*, ROW_NUMBER() OVER (ORDER BY idx DESC) AS rNUM" +
                " FROM (SELECT * FROM mvcboard";

        if (map.get("searchWord") != null) {
            query += " WHERE " + map.get("searchField") + " "
                    + " LIKE '%" + map.get("searchWord") + "%'";
        }

        query += ") AS Tb" +
                ") AS subquery " +
                "WHERE rNUM BETWEEN ? AND ?";

        try{
            psmt = con.prepareStatement(query);
            psmt.setString(1, map.get("start").toString());
            psmt.setString(2, map.get("end").toString());
            rs = psmt.executeQuery();

            while (rs.next()) {
                MVCBoardVO vo = new MVCBoardVO();

                vo.setIdx(rs.getString(1));
                vo.setName(rs.getString(2));
                vo.setTitle(rs.getString(3));
                vo.setContent(rs.getString(4));
                vo.setPostdate(rs.getDate(5));
                vo.setOfile(rs.getString(6));
                vo.setSfile(rs.getString(7));
                vo.setDowncount(rs.getInt(8));
                vo.setPass(rs.getString(9));
                vo.setVisitcount(rs.getInt(10));

                board.add(vo);
            }
        }catch (Exception e) {
            System.out.println("게시물 조회 중 예외 발생");
            e.printStackTrace();
        }
        return board;
    }

    // 게시글 데이터를 받아 DB에 추가합니다(파일 업로드 지원).
    public int insertWrite(MVCBoardVO vo) {
        int result = 0;
        try {
            String query = "INSERT INTO mvcboard ( "
                    + " name, title, content, ofile, sfile, pass) "
                    + " VALUES ( "
                    + " ?,?,?,?,?,?)";
            psmt = con.prepareStatement(query);
            psmt.setString(1, vo.getName());
            psmt.setString(2, vo.getTitle());
            psmt.setString(3, vo.getContent());
            psmt.setString(4, vo.getOfile());
            psmt.setString(5, vo.getSfile());
            psmt.setString(6, vo.getPass());
            result = psmt.executeUpdate();
        }
        catch (Exception e) {
            System.out.println("게시물 입력 중 예외 발생");
            e.printStackTrace();
        }
        return result;
    }

    // 주어진 일련번호에 해당하는 게시물을 DTO에 담아 반환합니다.
    public MVCBoardVO selectView(String idx) {
        MVCBoardVO vo = new MVCBoardVO();  // DTO 객체 생성
        String query = "SELECT * FROM mvcboard WHERE idx=?";  // 쿼리문 템플릿 준비
        try {
            psmt = con.prepareStatement(query);  // 쿼리문 준비
            psmt.setString(1, idx);  // 인파라미터 설정
            rs = psmt.executeQuery();  // 쿼리문 실행

            if (rs.next()) {  // 결과를 DTO 객체에 저장
                vo.setIdx(rs.getString(1));
                vo.setName(rs.getString(2));
                vo.setTitle(rs.getString(3));
                vo.setContent(rs.getString(4));
                vo.setPostdate(rs.getDate(5));
                vo.setOfile(rs.getString(6));
                vo.setSfile(rs.getString(7));
                vo.setDowncount(rs.getInt(8));
                vo.setPass(rs.getString(9));
                vo.setVisitcount(rs.getInt(10));
            }
        }
        catch (Exception e) {
            System.out.println("게시물 상세보기 중 예외 발생");
            e.printStackTrace();
        }
        return vo;  // 결과 반환
    }

    // 주어진 일련번호에 해당하는 게시물의 조회수를 1 증가시킵니다.
    public void updateVisitCount(String idx) {
        String query = "UPDATE mvcboard SET "
                + " visitcount=visitcount+1 "
                + " WHERE idx=?";
        try {
            psmt = con.prepareStatement(query);
            psmt.setString(1, idx);
            psmt.executeQuery();
        }
        catch (Exception e) {
            System.out.println("게시물 조회수 증가 중 예외 발생");
            e.printStackTrace();
        }
    }

    // 다운로드 횟수를 1 증가시킵니다.
    public void downCountPlus(String idx) {
        String sql = "UPDATE mvcboard SET "
                + " downcount=downcount+1 "
                + " WHERE idx=? ";
        try {
            psmt = con.prepareStatement(sql);
            psmt.setString(1, idx);
            psmt.executeUpdate();
        }
        catch (Exception e) {
            System.out.println("다운로드 횟수 증가 중 예외 발생");
            e.printStackTrace();
        }
    }

    // 입력한 비밀번호가 지정한 일련번호의 게시물의 비밀번호와 일치하는지 확인합니다.
    public boolean confirmPassword(String pass, String idx) {
        boolean isCorr = true;
        try {
            String sql = "SELECT COUNT(*) FROM mvcboard WHERE pass=? AND idx=?";
            psmt = con.prepareStatement(sql);
            psmt.setString(1, pass);
            psmt.setString(2, idx);
            rs = psmt.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) {
                isCorr = false;
            }
        }
        catch (Exception e) {
            isCorr = false;
            e.printStackTrace();
        }
        return isCorr;
    }

    // 지정한 일련번호의 게시물을 삭제합니다.
    public int deletePost(String idx) {
        int result = 0;
        try {
            String query = "DELETE FROM mvcboard WHERE idx=?";
            psmt = con.prepareStatement(query);
            psmt.setString(1, idx);
            result = psmt.executeUpdate();
        }
        catch (Exception e) {
            System.out.println("게시물 삭제 중 예외 발생");
            e.printStackTrace();
        }
        return result;
    }

    // 게시글 데이터를 받아 DB에 저장되어 있던 내용을 갱신합니다(파일 업로드 지원).
    public int updatePost(MVCBoardVO vo) {
        int result = 0;
        try {
            // 쿼리문 템플릿 준비
            String query = "UPDATE mvcboard"
                    + " SET title=?, name=?, content=?, ofile=?, sfile=? "
                    + " WHERE idx=? and pass=?";

            // 쿼리문 준비
            psmt = con.prepareStatement(query);
            psmt.setString(1, vo.getTitle());
            psmt.setString(2, vo.getName());
            psmt.setString(3, vo.getContent());
            psmt.setString(4, vo.getOfile());
            psmt.setString(5, vo.getSfile());
            psmt.setString(6, vo.getIdx());
            psmt.setString(7, vo.getPass());

            // 쿼리문 실행
            result = psmt.executeUpdate();
        }
        catch (Exception e) {
            System.out.println("게시물 수정 중 예외 발생");
            e.printStackTrace();
        }
        return result;
    }
}
