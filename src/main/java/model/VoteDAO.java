package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class VoteDAO {
	Connection conn = null;
	PreparedStatement ps = null;
	ResultSet rs = null;	 
	
	public static Connection getConnection() throws Exception {
		Class.forName("oracle.jdbc.OracleDriver");
		Connection conn = DriverManager.getConnection
				("jdbc:oracle:thin:@//localhost:1521/xe","system","1234");
		return conn;
	}
	
	// 투표자 입력
	public int insertVote(HttpServletRequest request, HttpServletResponse response) {
		
		int result = 0;
		try {
			conn = getConnection();
			String v_jumin = request.getParameter("v_jumin");
			String v_name = request.getParameter("v_name");
			String m_no = request.getParameter("m_no");
			String v_time = request.getParameter("v_time");
			String v_area = request.getParameter("v_area");
			String v_confirm = request.getParameter("v_confirm");
	        
			String sql = "INSERT INTO tbl_vote_202005 VALUES(?,?,?,?,?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, v_jumin);
			ps.setString(2, v_name);
			ps.setString(3, m_no);
			ps.setString(4, v_time);
			ps.setString(5, v_area);
			ps.setString(6, v_confirm);
				
			result = ps.executeUpdate(); // 0실패, 1성공
			System.out.println(result);	
			
			conn.close();
			ps.close();
				
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return result;
	}
	
	// 후보조회
	public String selectMember(HttpServletRequest request, HttpServletResponse response) {
	  	
		ArrayList<MemberDTO> list = new ArrayList<MemberDTO>();
			
		try {
			conn = getConnection();
			
			//후보 조회 화면 쿼리
			String sql = "SELECT ";
			       sql+= " M.m_no, "; // 후보번호
			       sql+= " M.m_name, "; // 성명
			       sql+= " P.p_name, "; // 소속정당
			       
			       // 학력
			       // decode(code, 'ko', '대한민국', 'us', '미국', 'jp', '일본', 'cn', '중국', '기타')
			       // if(code == 'ko') return '대한민국'
			       // else if(code == 'us') return '미국'
			       // else if(code == 'jp') return '일본'
			       // else if(code == 'cn') return '중국'
			       // else return '기타'
			       sql+= " DECODE(M.p_school,'1','고졸','2','학사','3','석사','박사') p_school, ";
			       
			       // 주민번호를 '-' 으로 구분해서 가져옴
			       sql+= " substr(M.m_jumin,1,6)|| "; // 앞 6자리 잘라오기
			       sql+= " '-'||substr(M.m_jumin,7) m_jumin, ";
			       
			       // 지역구
			       sql+= " M.m_city, ";
			       
			       // 대표전화
			       sql+= " substr(P.p_tel1,1,2)||'-'||P.p_tel2||'-'||";
			       sql+= " (substr(P.p_tel3,4)||"; // 맨뒷자리 수를 가져와서 합침
			       sql+= "  substr(P.p_tel3,4)||";
			       sql+= "  substr(P.p_tel3,4)||";
			       sql+= "  substr(P.p_tel3,4)) p_tel ";
			       
			       sql+= " FROM tbl_member_202005 M, tbl_party_202005 P ";
			       sql+= " WHERE M.p_code = P.p_code";
			       
				 ps = conn.prepareStatement(sql);
				 rs = ps.executeQuery();
				 
			while(rs.next()) {
				MemberDTO memberDTO = new MemberDTO();
				memberDTO.setM_no(rs.getString(1));
				memberDTO.setM_name(rs.getString(2));
				memberDTO.setP_name(rs.getString(3));
				memberDTO.setP_school(rs.getString(4));
				memberDTO.setM_jumin(rs.getString(5));
				memberDTO.setM_city(rs.getString(6));
				memberDTO.setP_tel(rs.getString(7));
					 
				list.add(memberDTO);
			}
			// 객체바인딩
			request.setAttribute("list",list);
				conn.close();
				ps.close();
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
		}	
		return "memberList.jsp";
	}
	
	// 투표검수조회
	public String selectAll(HttpServletRequest request, HttpServletResponse response){
	 	
		ArrayList<VoteDTO> list = new ArrayList<VoteDTO>();	
		try {
		conn = getConnection();
				
			//투표검수조회 화면 쿼리
		String sql = "SELECT v_name,";
		       sql+= " '19'||substr(v_jumin,1,2)||";
		       sql+= " '년'||substr(v_jumin,3,2)||";
		       sql+= " '월'||substr(v_jumin,5,2)||";
		       sql+= " '일생' v_jumin,";
		       
		       // 현재날짜에서 생년월일 빼기 => 나이
		       sql+= " '만 '||(to_number(to_char(sysdate,'yyyy'))";
		       sql+= " - to_number('19'||substr(v_jumin,1,2)))||'세' v_age,";
		       
		       // 성멸
		       sql+= " DECODE(substr(v_jumin,7,1),'1','남','여') v_gender, ";		   
		       
		       sql+= " m_no, ";
		       
		       // 투표시간
		       sql+= " substr(v_time,1,2)||':'||substr(v_time,3,2) v_time, ";
		       
		       // 유권자 확인
		       sql+= " DECODE(v_confirm,'Y','확인','미확인') v_confirm ";
		       
		       sql+= " FROM tbl_vote_202005 ";
		       sql+= " WHERE v_area='제1투표장'";
		       
		ps = conn.prepareStatement(sql); // 명령어를 보낸다.
		rs = ps.executeQuery();
			
		while(rs.next()) {
			VoteDTO voteDTO = new VoteDTO();
				
			voteDTO.setV_name(rs.getString(1));
			voteDTO.setV_jumin(rs.getString(2));
			voteDTO.setV_age(rs.getString(3));
			voteDTO.setV_gender(rs.getString(4));
			voteDTO.setM_no(rs.getString(5));
			voteDTO.setV_time(rs.getString(6));
			voteDTO.setV_confirm(rs.getString(7));
			
			list.add(voteDTO);
		}
		
		// 객체 바인딩
		request.setAttribute("list",list);
		conn.close();
		ps.close();
		rs.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return "voteList.jsp";
	}
	
	// 후보자 등수
	public String selectResult(HttpServletRequest request, HttpServletResponse response) {
	   	
		ArrayList<ResultDTO> list = new ArrayList<ResultDTO>();
			
		try {
			conn = getConnection();
			//후보자 등수 화면 쿼리
			String sql = "SELECT ";
			       sql+= " M.m_no, M.m_name, count(*) AS v_total";
			       sql+= " FROM tbl_member_202005 M, tbl_vote_202005 V";
			       
			       // 투표테이블과 후보테이블 조인, 확인된 유권자
			       sql+= " WHERE M.m_no = V.m_no AND V.v_confirm = 'Y' ";
			       
			       sql+= " GROUP BY M.m_no, M.m_name";
			       sql+= " ORDER BY v_total DESC";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
		
			while(rs.next()) {
				ResultDTO resultDTO = new ResultDTO();
				
				resultDTO.setM_no(rs.getString(1));
				resultDTO.setM_name(rs.getString(2));
				resultDTO.setV_total(rs.getString(3));
				list.add(resultDTO);
			}
			
			// 객체 바인딩
			request.setAttribute("list",list);
			conn.close();
			ps.close();
			rs.close();		
			
		} catch (Exception e) {
				
			e.printStackTrace();
		}		
		return "voteResult.jsp";
	}
}
