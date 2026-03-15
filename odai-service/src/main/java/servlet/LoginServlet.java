package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import storage.UserDAO;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final int SESSION_TIMEOUT_SECONDS = 60 * 60 * 24 * 7; // 7日

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (username == null) username = "";
        if (password == null) password = "";

        username = username.trim();
        password = password.trim();

        if (username.isEmpty() || password.isEmpty()) {
            req.setAttribute("errorMessage", "ユーザー名とパスワードを入力してください。");
            req.setAttribute("username", username);
            req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();
            User loginUser = userDAO.authenticate(username, password);

            if (loginUser == null) {
                req.setAttribute("errorMessage", "ユーザー名またはパスワードが違います。");
                req.setAttribute("username", username);
                req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
                return;
            }

            HttpSession oldSession = req.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }

            HttpSession session = req.getSession(true);
            session.setMaxInactiveInterval(SESSION_TIMEOUT_SECONDS);
            session.setAttribute("loginUser", loginUser);

            resp.sendRedirect(req.getContextPath() + "/");

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}