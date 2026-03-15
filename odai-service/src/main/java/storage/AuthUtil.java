package storage;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

public class AuthUtil {

    public static User getLoginUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return null;
        }
        Object value = session.getAttribute("loginUser");
        if (value instanceof User user) {
            return user;
        }
        return null;
    }

    public static boolean requireLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (getLoginUser(req) != null) {
            return true;
        }

        String target = req.getRequestURI();
        String qs = req.getQueryString();
        if (qs != null && !qs.isBlank()) {
            target += "?" + qs;
        }

        req.getSession(true).setAttribute("afterLoginRedirect", target);
        resp.sendRedirect(req.getContextPath() + "/login");
        return false;
    }

    public static void login(HttpServletRequest req, User user) {
        HttpSession session = req.getSession(true);
        session.setAttribute("loginUser", user);
    }

    public static void logout(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
