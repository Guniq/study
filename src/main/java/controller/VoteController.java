package controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.VoteDAO;

/**
 * Servlet implementation class VoteController
 */
@WebServlet("*.do")
public class VoteController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public VoteController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8");
		
		/* URL check */
		String uri = request.getRequestURI();
		String context = request.getContextPath();
		String command = uri.substring(context.length());
		String viewPage = null;
		
		System.out.println("command : "+command);
		
		VoteDAO vote = new VoteDAO();
		
		switch(command) {
		case "/main.do" : 
			
			viewPage = "index.jsp";
			break;
		case "/vote.do" : 
			int result = vote.insertVote(request, response);
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out=response.getWriter();
			if(result == 1) {
				out.println("<script>");
				out.println("alert('투표하기 정보가 정상적으로 등록 되었습니다!'); location.href='"+context+"'; ");
				out.println("</script>");
				out.flush();
			}else {
				out.println("<script>");
				out.println("alert('등록실패!'); location.href='"+context+"'; ");
				out.println("</script>");
				out.flush();
			}		
			break;
			
		case "/memberList.do" : 
			
			viewPage = vote.selectMember(request, response);
			break;
			
		case "/voteMember.do" : 
			
			viewPage = "voteMember.jsp";
			break;
			
		case "/voteList.do" : 
			
			viewPage = vote.selectAll(request, response);
			break;
			
		case "/voteResult.do" : 
			
			viewPage = vote.selectResult(request, response);
			break;
			
		default : break;
		}
		/* 결과 */
		RequestDispatcher dispatcher = request.getRequestDispatcher(viewPage);
		dispatcher.forward(request, response);
	}
}
