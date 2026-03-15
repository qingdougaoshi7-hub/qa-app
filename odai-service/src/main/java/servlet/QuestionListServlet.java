package servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Question;
import model.User;
import storage.AuthUtil;
import storage.DBInit;
import storage.QuestionDAO;

@WebServlet("/questions")
public class QuestionListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        try {
            DBInit.init();
            if (!AuthUtil.requireLogin(req, resp)) {
                return;
            }

            User loginUser = AuthUtil.getLoginUser(req);
            String keyword = req.getParameter("keyword");
            List<Question> questions;

            QuestionDAO dao = new QuestionDAO();

            if (keyword != null && !keyword.isBlank()) {
                questions = dao.searchByTitleWithAnswerCountByOwner(keyword, loginUser.getUserId());
            } else {
                questions = dao.findAllWithAnswerCountByOwner(loginUser.getUserId());
            }

            req.setAttribute("questions", questions);
            req.setAttribute("keyword", keyword == null ? "" : keyword);

            RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/jsp/questions.jsp");
            rd.forward(req, resp);

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
