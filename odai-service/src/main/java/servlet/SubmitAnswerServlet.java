package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Question;
import model.User;
import storage.AnswerDAO;
import storage.DiscordNotifier;
import storage.QuestionDAO;
import storage.UserDAO;

@WebServlet("/answer")
public class SubmitAnswerServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String questionId = req.getParameter("id");
        String answerText = req.getParameter("answer");

        if (questionId == null || questionId.isBlank() || answerText == null || answerText.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        try {
            QuestionDAO questionDAO = new QuestionDAO();
            AnswerDAO answerDAO = new AnswerDAO();

            Question question = questionDAO.findById(questionId);
            if (question == null) {
                resp.sendRedirect(req.getContextPath() + "/");
                return;
            }

            answerDAO.insert(questionId, answerText);

            if (question.getOwnerUserId() > 0) {
                User owner = new UserDAO().findById(question.getOwnerUserId());

                if (owner != null
                        && owner.getDiscordWebhookUrl() != null
                        && !owner.getDiscordWebhookUrl().isBlank()) {

                    String manageUrl = req.getScheme()
                            + "://"
                            + req.getServerName()
                            + ":"
                            + req.getServerPort()
                            + req.getContextPath()
                            + "/manage/"
                            + questionId;

                    try {
                        DiscordNotifier.sendNewAnswer(
                                owner.getDiscordWebhookUrl(),
                                owner.getDiscordUserId(),
                                question.getTitle(),
                                answerText,
                                manageUrl
                        );
                    } catch (Exception notifyError) {
                        notifyError.printStackTrace();
                    }
                }
            }

            resp.sendRedirect(req.getContextPath() + "/q/" + questionId + "?submitted=1");

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}