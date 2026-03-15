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

@WebServlet("/mark-answer")
public class MarkAnswerServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");

        if (!AuthUtil.requireLogin(req, resp)) {
            return;
        }

        String questionId = req.getParameter("questionId");
        String answerIdParam = req.getParameter("answerId");
        String action = req.getParameter("action");
        String pageParam = req.getParameter("page");
        int page = 1;
        if (pageParam != null && !pageParam.isBlank()) {
            page = Integer.parseInt(pageParam);
        }

        User loginUser = AuthUtil.getLoginUser(req);

        try {
            Question q = new QuestionDAO().findByIdForOwner(questionId, loginUser.getUserId());
            if (q == null) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().print("{\"ok\":false}");
                return;
            }

            int answerId = Integer.parseInt(answerIdParam);
            AnswerDAO dao = new AnswerDAO();

            if ("final".equals(action)) {
                dao.setFinalOnePerFavoritePage(questionId, page, 10, answerId);
                PrintWriter out = resp.getWriter();
                out.print("{\"ok\":true,\"action\":\"final\",\"selectedAnswerId\":" + answerId + "}");
                out.flush();
                return;
            }

            if ("special".equals(action)) {
                dao.toggleSpecialType(answerId, "special");
            } else if ("chihuahua".equals(action)) {
                dao.toggleChihuahuaType(answerId);
            } else {
                throw new IllegalArgumentException("不正なactionです: " + action);
            }

            AnswerState state = dao.getAnswerState(answerId);
            PrintWriter out = resp.getWriter();
            out.print("{");
            out.print("\"ok\":true,");
            out.print("\"answerId\":" + answerId + ",");
            out.print("\"action\":\"" + escapeJson(action) + "\",");
            out.print("\"specialType\":\"" + escapeJson(state == null ? "" : state.getSpecialType()) + "\",");
            out.print("\"special\":" + (state != null && state.isSpecial()) + ",");
            out.print("\"chihuahua\":" + (state != null && state.isChihuahua()) + ",");
            out.print("\"finalFlag\":" + (state != null && state.isFinalFlag()));
            out.print("}");
            out.flush();

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().print("{\"ok\":false}");
        }
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
