<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>파일 첨부형 게시판</title>
</head>
<body>
<h2>파일 첨부형 게시판 - 상세 보기(View)</h2>

<table border="1" width="90%">
    <colgroup>
        <col width="15%"/> <col width="35%"/>
        <col width="15%"/> <col width="*"/>
    </colgroup>

    <!-- 게시글 정보 -->
    <tr>
        <td>번호</td> <td>${ vo.idx }</td>
        <td>작성자</td> <td>${ vo.name }</td>
    </tr>
    <tr>
        <td>작성일</td> <td>${ vo.postdate }</td>
        <td>조회수</td> <td>${ vo.visitcount }</td>
    </tr>
    <tr>
        <td>제목</td>
        <td colspan="3">${ vo.title }</td>
    </tr>
    <tr>
        <td>내용</td>
        <td colspan="3" height="100">
            ${ vo.content }
            <c:if test="${ not empty vo.ofile and isImage eq true }">
                <br><img src="../Uploads/${ vo.sfile }" style="max-width:100%;"/>
            </c:if>
        </td>
    </tr>

    <!-- 첨부파일 -->
    <tr>
        <td>첨부파일</td>
        <td>
            <c:if test="${ not empty vo.ofile }">
                ${ vo.ofile }
                <a href="../Controller/download.do?ofile=${ vo.ofile }&sfile=${ vo.sfile }&idx=${ vo.idx }">
                    [다운로드]
                </a>
            </c:if>
        </td>
        <td>다운로드수</td>
        <td>${ vo.downcount }</td>
    </tr>

    <!-- 하단 메뉴(버튼) -->
    <tr>
        <td colspan="4" align="center">
            <button type="button" onclick="location.href='../Controller/pass.do?mode=edit&idx=${ param.idx }';">
                수정하기
            </button>
            <button type="button" onclick="location.href='../Controller/pass.do?mode=delete&idx=${ param.idx }';">
                삭제하기
            </button>
            <button type="button" onclick="location.href='../Controller/list.do';">
                목록 바로가기
            </button>
        </td>
    </tr>
</table>
</body>
</html>