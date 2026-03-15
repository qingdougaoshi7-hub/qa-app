package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Question;
import model.User;
import storage.AnswerDAO;
import storage.AnswerDAO.AnswerState;
import storage.AuthUtil;
import storage.QuestionDAO;

@WebServlet("/favorite")
public class FavoriteServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");

        if (!AuthUtil.requireLogin(req, resp)) {
            return;
        }

        String answerIdParam = req.getParameter("answerId");
        User loginUser = AuthUtil.getLoginUser(req);

        try {
            int answerId = Integer.parseInt(answerIdParam);
            AnswerDAO dao = new AnswerDAO();
            String questionId = dao.findQuestionIdByAnswerId(answerId);
            Question q = new QuestionDAO().findByIdForOwner(questionId, loginUser.getUserId());
            if (q == null) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().print("{\"ok\":false}");
                return;
            }

            dao.toggleFavorite(answerId);
            AnswerState state = dao.getAnswerState(answerId);

            PrintWriter out = resp.getWriter();
            out.print("{");
            out.print("\"ok\":true,");
            out.print("\"answerId\":" + answerId + ",");
            out.print("\"favorite\":" + (state != null && state.isFavorite()));
            out.print("}");
            out.flush();

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter out = resp.getWriter();
            out.print("{\"ok\":false}");
            out.flush();
        }
    }
}
