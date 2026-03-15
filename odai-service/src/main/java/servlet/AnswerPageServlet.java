package servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Question;
import storage.DBInit;
import storage.QuestionDAO;

@WebServlet("/q/*")
public class AnswerPageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        try {
            DBInit.init();

            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.length() <= 1) {
                resp.getWriter().println("存在しません");
                return;
            }

            String id = pathInfo.substring(1);

            QuestionDAO dao = new QuestionDAO();
            Question q = dao.findById(id);

            if (q == null) {
                resp.getWriter().println("存在しません");
                return;
            }

            req.setAttribute("question", q);

            RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/jsp/answer.jsp");
            rd.forward(req, resp);

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}