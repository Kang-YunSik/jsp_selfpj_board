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

}
