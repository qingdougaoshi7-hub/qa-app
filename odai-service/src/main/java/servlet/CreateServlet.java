package servlet;

import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Question;
import model.User;
import storage.QuestionDAO;

@WebServlet("/create")
public class CreateServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        User loginUser = session == null ? null : (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        req.getRequestDispatcher("/WEB-INF/jsp/create.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        User loginUser = session == null ? null : (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String title = req.getParameter("title");
        String limitParam = req.getParameter("limit");

        if (title == null) {
            title = "";
        }
        title = title.trim();

        if (title.isEmpty()) {
            req.setAttribute("errorMessage", "お題を入力してください。");
            req.getRequestDispatcher("/WEB-INF/jsp/create.jsp").forward(req, resp);
            return;
        }

        int limit;
        try {
            if (limitParam == null || limitParam.trim().isEmpty()) {
                limit = -1;
            } else {
                limit = Integer.parseInt(limitParam.trim());
                if (limit <= 0) {
                    limit = -1;
                }
            }
        } catch (Exception e) {
            limit = -1;
        }

        try {
            String id = UUID.randomUUID().toString().replace("-", "").substring(0, 10);

            Question question = new Question(id, title, limit);
            question.setOwnerUserId(loginUser.getUserId());

            QuestionDAO dao = new QuestionDAO();
            dao.insert(question);

            resp.sendRedirect(req.getContextPath() + "/manage/" + id);

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}