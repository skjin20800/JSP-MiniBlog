package com.cos.blog.domain.board;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.cos.blog.config.DB;
import com.cos.blog.domain.board.dto.DetailRespDto;
import com.cos.blog.domain.board.dto.SaveReqDto;
import com.cos.blog.domain.board.dto.UpdateReqDto;

public class BoardDao {
	public List<Board> search(String keyword, int page) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * ");
		sb.append("FROM board ");
		sb.append("WHERE title LIKE ? ");
		sb.append("ORDER BY id DESC LIMIT ?, 4");
		
		String sql = sb.toString();
		Connection conn = DB.getConnection();
		PreparedStatement pstmt = null;
		ResultSet  rs = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, "%"+keyword+"%"); 
			pstmt.setInt(2, page*4);
			rs = pstmt.executeQuery();
	
			List<Board> boardList = new ArrayList();
			
			while(rs.next()) {
				Board board = Board.builder()
						.id(rs.getInt("id"))
						.title(rs.getString("title"))
						.content(rs.getString("content"))
						.userId(rs.getInt("userId"))
						.createDate(rs.getTimestamp("createDate"))
						.build();
				boardList.add(board);
			}
			return boardList;
		} catch (Exception e) {
			e.printStackTrace();
		} finally { // 무조건 실행
			DB.close(conn, pstmt);
		}
		return null;
	}

	public int update(UpdateReqDto dto) {
		String sql = "UPDATE board SET title = ?, content = ? WHERE id = ?";
		Connection conn = DB.getConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dto.getTitle());
			pstmt.setString(2, dto.getContent());
			pstmt.setInt(3, dto.getId());
			int result = pstmt.executeUpdate();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally { // 무조건 실행
			DB.close(conn, pstmt);
		}
		return -1;
	}

	
	
	public int save(SaveReqDto dto) { // 회원가입
		String sql = "INSERT INTO board(userId, title, content, createDate) VALUES(?,?,?, now())";
		Connection conn = DB.getConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, dto.getUserId());
			pstmt.setString(2, dto.getTitle().replace("<", "&lt;").replace(">","&gt;"));
			pstmt.setString(3, dto.getContent());
			int result = pstmt.executeUpdate();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally { // 무조건 실행
			DB.close(conn, pstmt);
		}
		return -1;
	}
	
	public List<Board> findAll(int page) { // 회원가입
		String sql = "SELECT * FROM  board ORDER BY id DESC LIMIT ?, 4"; // 0,4   4,4   8,4
		Connection conn = DB.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, page*4); // 0 -> 0, 1 ->4, 2->8
			rs = pstmt.executeQuery();
	
			List<Board> boardList = new ArrayList();
			
			while(rs.next()) {
				Board board = Board.builder()
						.id(rs.getInt("id"))
						.title(rs.getString("title"))
						.content(rs.getString("content"))
						.userId(rs.getInt("userId"))
						.createDate(rs.getTimestamp("createDate"))
						.build();
				boardList.add(board);
			}
			System.out.println("Dao성공");
			return boardList;
			
		} catch (Exception e) {
			System.out.println("Dao실패");
			e.printStackTrace();
		} finally { // 무조건 실행
			DB.close(conn, pstmt);
		}
		return null;
	}

	
	public int findAllPage() { // 
		String sql = "SELECT count(*) pageAll FROM  board";
		Connection conn = DB.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				int pageAll = rs.getInt("pageAll");
				return pageAll;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally { // 무조건 실행
			DB.close(conn, pstmt);
		}
		return 0;
	}
	public int searchAllPage(String keyword) { // 
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT count(*) pageAll ");
		sb.append("FROM board ");
		sb.append("WHERE title LIKE ?");
		
		String sql = sb.toString();
		Connection conn = DB.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1,"%"+keyword+"%");
			rs = pstmt.executeQuery();
			if(rs.next()) {
				int pageAll = rs.getInt("pageAll");
				return pageAll;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally { // 무조건 실행
			DB.close(conn, pstmt);
		}
		return 0;
	}
	
	public DetailRespDto findById(int id){
		StringBuffer sb = new StringBuffer();
		sb.append("select b.id, b.title, b.content, b.readCount, b.userId, u.username ");
		sb.append("from board b inner join user u ");
		sb.append("on b.userId = u.id ");
		sb.append("where b.id = ?");

		String sql = sb.toString();
		Connection conn = DB.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs  = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			rs =  pstmt.executeQuery();

			// Persistence API
			if(rs.next()) { // 커서를 이동하는 함수
				DetailRespDto dto = new DetailRespDto();
				dto.setId(rs.getInt("b.id"));
				dto.setTitle(rs.getString("b.title"));
				dto.setContent(rs.getString("b.content"));
				dto.setReadCount(rs.getInt("b.readCount"));
				dto.setUserId(rs.getInt("b.userId"));
				dto.setUsername(rs.getString("u.username"));
				return dto;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally { // 무조건 실행
			DB.close(conn, pstmt, rs);
		}
		return null;
	}
	public int insertReadCount(int id) {
	
			String sql = "UPDATE board SET readCount = readCount + 1 WHERE id = ?;";
			Connection conn = DB.getConnection();
			PreparedStatement pstmt = null;
			try {
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, id);
				int result = pstmt.executeUpdate();
				
				return result;
			} catch (Exception e) {
				e.printStackTrace();
			} finally { // 무조건 실행
				DB.close(conn, pstmt);
			}
			return -1;
		}	
	
	public int deleteById(int id) { // 회원가입
		String sql = "DELETE FROM board WHERE id = ?";
		Connection conn = DB.getConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			int result = pstmt.executeUpdate();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally { // 무조건 실행
			DB.close(conn, pstmt);
		}
		return -1;
	}
	
}