package servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Answer;
import model.Question;
import storage.AnswerDAO;
import storage.DBInit;
import storage.QuestionDAO;

@WebServlet("/ranking/*")
public class RankingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        try {
            DBInit.init();

            String pathInfo = req.getPathInfo();
            QuestionDAO questionDAO = new QuestionDAO();
            AnswerDAO answerDAO = new AnswerDAO();

            if (pathInfo == null || pathInfo.equals("/") || pathInfo.isBlank()) {
                List<Question> questions = questionDAO.findAllWithAnswerCount();
                req.setAttribute("questions", questions);

                RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/jsp/ranking.jsp");
                rd.forward(req, resp);
                return;
            }

            String id = pathInfo.substring(1);
            Question q = questionDAO.findById(id);

            if (q == null) {
                resp.getWriter().println("存在しません");
                return;
            }

            List<Answer> ranking = answerDAO.findRankingByQuestionId(id);
            Answer chihuahuaAward = answerDAO.findChihuahuaAwardByQuestionId(id);

            req.setAttribute("question", q);
            req.setAttribute("ranking", ranking);
            req.setAttribute("chihuahuaAward", chihuahuaAward);

            RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/jsp/ranking.jsp");
            rd.forward(req, resp);

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}