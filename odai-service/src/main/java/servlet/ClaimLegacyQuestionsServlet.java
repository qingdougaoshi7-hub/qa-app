package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import storage.QuestionDAO;

@WebServlet("/claim-legacy-questions")
public class ClaimLegacyQuestionsServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        User loginUser = session == null ? null : (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            QuestionDAO dao = new QuestionDAO();
            int updatedCount = dao.claimQuestionsWithoutOwner(loginUser.getUserId());

            resp.sendRedirect(req.getContextPath() + "/account?claimed=1&count=" + updatedCount);

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}