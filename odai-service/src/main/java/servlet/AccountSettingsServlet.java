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

@WebServlet("/account")
public class AccountSettingsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        User loginUser = session == null ? null : (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            User freshUser = new UserDAO().findById(loginUser.getUserId());
            if (freshUser != null) {
                session.setAttribute("loginUser", freshUser);
                req.setAttribute("loginUser", freshUser);
            } else {
                req.setAttribute("loginUser", loginUser);
            }
        } catch (Exception e) {
            req.setAttribute("loginUser", loginUser);
        }

        req.getRequestDispatcher("/WEB-INF/jsp/account.jsp").forward(req, resp);
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

        String webhookUrl = req.getParameter("discordWebhookUrl");
        String discordUserId = req.getParameter("discordUserId");

        try {
            new UserDAO().updateDiscordSettings(loginUser.getUserId(), webhookUrl, discordUserId);

            User updatedUser = new UserDAO().findById(loginUser.getUserId());
            session.setAttribute("loginUser", updatedUser);

            resp.sendRedirect(req.getContextPath() + "/account?updated=1");
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "設定の保存に失敗しました。");
            req.setAttribute("loginUser", loginUser);
            req.getRequestDispatcher("/WEB-INF/jsp/account.jsp").forward(req, resp);
        }
    }
}