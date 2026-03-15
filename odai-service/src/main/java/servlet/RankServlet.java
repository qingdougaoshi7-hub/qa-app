package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Question;
import model.User;
import storage.AnswerDAO;
import storage.AuthUtil;
import storage.QuestionDAO;

@WebServlet("/rank")
public class RankServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        if (!AuthUtil.requireLogin(req, resp)) {
            return;
        }

        String questionId = req.getParameter("questionId");
        String answerIdParam = req.getParameter("answerId");
        String rankParam = req.getParameter("rank");
        User loginUser = AuthUtil.getLoginUser(req);

        try {
            Question q = new QuestionDAO().findByIdForOwner(questionId, loginUser.getUserId());
            if (q == null) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            int answerId = Integer.parseInt(answerIdParam);
            int rank = Integer.parseInt(rankParam);
            if (rank >= 1 && rank <= 5) {
                new AnswerDAO().setFinalRank(answerId, rank);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }

        resp.sendRedirect(req.getContextPath() + "/manage/" + questionId + "?mode=final");
    }
}
