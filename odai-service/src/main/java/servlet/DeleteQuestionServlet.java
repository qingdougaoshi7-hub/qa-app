package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import storage.AuthUtil;
import storage.QuestionDAO;

@WebServlet("/delete-question")
public class DeleteQuestionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        if (!AuthUtil.requireLogin(req, resp)) {
            return;
        }

        String questionId = req.getParameter("questionId");
        User loginUser = AuthUtil.getLoginUser(req);

        try {
            new QuestionDAO().deleteByIdAndOwner(questionId, loginUser.getUserId());
        } catch (Exception e) {
            throw new ServletException(e);
        }

        resp.sendRedirect(req.getContextPath() + "/questions");
    }
}
