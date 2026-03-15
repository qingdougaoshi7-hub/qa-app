package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Answer;
import model.Question;
import model.User;
import storage.AnswerDAO;
import storage.AuthUtil;
import storage.DBInit;
import storage.QuestionDAO;

@WebServlet("/manage/*")
public class ManageServlet extends HttpServlet {

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
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.length() <= 1) {
                resp.getWriter().println("存在しません");
                return;
            }

            String id = pathInfo.substring(1);
            String keyword = req.getParameter("keyword");
            String mode = req.getParameter("mode");
            String pageParam = req.getParameter("page");

            int page = 1;
            if (pageParam != null && !pageParam.isBlank()) {
                page = Integer.parseInt(pageParam);
            }

            QuestionDAO questionDAO = new QuestionDAO();
            AnswerDAO answerDAO = new AnswerDAO();

            Question q = questionDAO.findByIdForOwner(id, loginUser.getUserId());

            if (q == null) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "このお題は管理できません。");
                return;
            }

            List<Answer> sourceAnswers;
            if ("favorites".equals(mode)) {
                sourceAnswers = answerDAO.findFavoritesByQuestionId(id);
            } else if ("final".equals(mode)) {
                sourceAnswers = answerDAO.findFinalistsByQuestionId(id);
            } else if ("special".equals(mode)) {
                sourceAnswers = answerDAO.findByQuestionId(id).stream().filter(Answer::isSpecialFrame).toList();
            } else {
                sourceAnswers = answerDAO.findByQuestionId(id);
            }

            List<Answer> filtered = new ArrayList<>();
            for (Answer a : sourceAnswers) {
                boolean matchKeyword = true;
                if (keyword != null && !keyword.isBlank()) {
                    matchKeyword = a.getText().contains(keyword);
                }
                if (matchKeyword) {
                    filtered.add(a);
                }
            }

            int pageSize = "favorites".equals(mode) ? 10 : filtered.size();
            if (pageSize <= 0) {
                pageSize = 10;
            }

            int totalCount = filtered.size();
            int totalPages = (int) Math.ceil((double) totalCount / pageSize);
            if (totalPages == 0) {
                totalPages = 1;
            }
            if (page < 1) {
                page = 1;
            }
            if (page > totalPages) {
                page = totalPages;
            }

            int fromIndex = (page - 1) * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, totalCount);

            List<Answer> pageAnswers = new ArrayList<>();
            if (fromIndex < totalCount) {
                pageAnswers = filtered.subList(fromIndex, toIndex);
            }

            double answerRate = 0.0;
            List<Answer> allAnswers = answerDAO.findByQuestionId(id);
            if (q.getLimit() != -1 && q.getLimit() > 0) {
                answerRate = (double) allAnswers.size() / q.getLimit() * 100.0;
            }

            List<Answer> ranking = answerDAO.findRankingByQuestionId(id);

            req.setAttribute("question", q);
            req.setAttribute("filteredAnswers", pageAnswers);
            req.setAttribute("allAnswerCount", allAnswers.size());
            req.setAttribute("keyword", keyword == null ? "" : keyword);
            req.setAttribute("mode", mode == null ? "" : mode);
            req.setAttribute("answerRate", answerRate);
            req.setAttribute("currentPage", page);
            req.setAttribute("totalPages", totalPages);
            req.setAttribute("ranking", ranking);

            RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/jsp/manage.jsp");
            rd.forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
