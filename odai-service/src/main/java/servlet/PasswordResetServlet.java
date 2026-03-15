package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import storage.UserDAO;

@WebServlet("/reset-password")
public class PasswordResetServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.getRequestDispatcher("/WEB-INF/jsp/reset-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String username = req.getParameter("username");
        String newPassword = req.getParameter("newPassword");
        String discordWebhookUrl = req.getParameter("discordWebhookUrl");

        if (username == null) username = "";
        if (newPassword == null) newPassword = "";
        if (discordWebhookUrl == null) discordWebhookUrl = "";

        username = username.trim();
        newPassword = newPassword.trim();
        discordWebhookUrl = discordWebhookUrl.trim();

        if (username.isEmpty() || newPassword.isEmpty()) {
            req.setAttribute("errorMessage", "ユーザー名と新しいパスワードを入力してください。");
            req.setAttribute("username", username);
            req.setAttribute("discordWebhookUrl", discordWebhookUrl);
            req.getRequestDispatcher("/WEB-INF/jsp/reset-password.jsp").forward(req, resp);
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();
            boolean updated = userDAO.resetPassword(username, newPassword, discordWebhookUrl);

            if (!updated) {
                req.setAttribute("errorMessage", "再設定に失敗しました。ユーザー名またはWebhook URLを確認してください。");
                req.setAttribute("username", username);
                req.setAttribute("discordWebhookUrl", discordWebhookUrl);
                req.getRequestDispatcher("/WEB-INF/jsp/reset-password.jsp").forward(req, resp);
                return;
            }

            resp.sendRedirect(req.getContextPath() + "/login?reset=1");

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}