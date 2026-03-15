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

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String discordWebhookUrl = req.getParameter("discordWebhookUrl");

        if (username == null) username = "";
        if (password == null) password = "";
        if (discordWebhookUrl == null) discordWebhookUrl = "";

        username = username.trim();
        password = password.trim();
        discordWebhookUrl = discordWebhookUrl.trim();

        if (username.isEmpty() || password.isEmpty()) {
            req.setAttribute("errorMessage", "ユーザー名とパスワードを入力してください。");
            req.setAttribute("username", username);
            req.setAttribute("discordWebhookUrl", discordWebhookUrl);
            req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();

            if (userDAO.existsByUsername(username)) {
                req.setAttribute("errorMessage", "そのユーザー名は既に使われています。");
                req.setAttribute("username", username);
                req.setAttribute("discordWebhookUrl", discordWebhookUrl);
                req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
                return;
            }

            userDAO.insert(username, password, discordWebhookUrl);

            User loginUser = userDAO.authenticate(username, password);
            HttpSession session = req.getSession();
            session.setAttribute("loginUser", loginUser);

            resp.sendRedirect(req.getContextPath() + "/");

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}