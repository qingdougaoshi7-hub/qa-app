package storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.Question;

public class QuestionDAO {

    public void insert(Question q) throws Exception {
        String sql = "INSERT INTO questions (id, title, limit_count, owner_user_id) VALUES (?, ?, ?, ?)";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, q.getId());
            ps.setString(2, q.getTitle());
            ps.setInt(3, q.getLimit());
            ps.setInt(4, q.getOwnerUserId());
            ps.executeUpdate();
        }
    }

    public Question findById(String id) throws Exception {
        String sql =
                "SELECT q.id, q.title, q.limit_count, q.owner_user_id, COUNT(a.answer_id) AS answer_count " +
                "FROM questions q " +
                "LEFT JOIN answers a ON q.id = a.question_id " +
                "WHERE q.id = ? " +
                "GROUP BY q.id, q.title, q.limit_count, q.owner_user_id";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapQuestion(rs);
                }
            }
        }

        return null;
    }

    public Question findByIdForOwner(String id, int ownerUserId) throws Exception {
        String sql =
                "SELECT q.id, q.title, q.limit_count, q.owner_user_id, COUNT(a.answer_id) AS answer_count " +
                "FROM questions q " +
                "LEFT JOIN answers a ON q.id = a.question_id " +
                "WHERE q.id = ? AND q.owner_user_id = ? " +
                "GROUP BY q.id, q.title, q.limit_count, q.owner_user_id";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, id);
            ps.setInt(2, ownerUserId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapQuestion(rs);
                }
            }
        }

        return null;
    }

    public List<Question> findAllWithAnswerCount() throws Exception {
        List<Question> list = new ArrayList<>();

        String sql =
                "SELECT q.id, q.title, q.limit_count, q.owner_user_id, COUNT(a.answer_id) AS answer_count " +
                "FROM questions q " +
                "LEFT JOIN answers a ON q.id = a.question_id " +
                "GROUP BY q.id, q.title, q.limit_count, q.owner_user_id " +
                "ORDER BY q.id DESC";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapQuestion(rs));
            }
        }

        return list;
    }

    public List<Question> findAllWithAnswerCountByOwner(int ownerUserId) throws Exception {
        List<Question> list = new ArrayList<>();

        String sql =
                "SELECT q.id, q.title, q.limit_count, q.owner_user_id, COUNT(a.answer_id) AS answer_count " +
                "FROM questions q " +
                "LEFT JOIN answers a ON q.id = a.question_id " +
                "WHERE q.owner_user_id = ? " +
                "GROUP BY q.id, q.title, q.limit_count, q.owner_user_id " +
                "ORDER BY q.id DESC";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, ownerUserId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapQuestion(rs));
                }
            }
        }

        return list;
    }

    public List<Question> searchByTitleWithAnswerCount(String keyword) throws Exception {
        List<Question> list = new ArrayList<>();

        String sql =
                "SELECT q.id, q.title, q.limit_count, q.owner_user_id, COUNT(a.answer_id) AS answer_count " +
                "FROM questions q " +
                "LEFT JOIN answers a ON q.id = a.question_id " +
                "WHERE q.title LIKE ? " +
                "GROUP BY q.id, q.title, q.limit_count, q.owner_user_id " +
                "ORDER BY q.id DESC";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapQuestion(rs));
                }
            }
        }

        return list;
    }

    public List<Question> searchByTitleWithAnswerCountByOwner(String keyword, int ownerUserId) throws Exception {
        List<Question> list = new ArrayList<>();

        String sql =
                "SELECT q.id, q.title, q.limit_count, q.owner_user_id, COUNT(a.answer_id) AS answer_count " +
                "FROM questions q " +
                "LEFT JOIN answers a ON q.id = a.question_id " +
                "WHERE q.owner_user_id = ? AND q.title LIKE ? " +
                "GROUP BY q.id, q.title, q.limit_count, q.owner_user_id " +
                "ORDER BY q.id DESC";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, ownerUserId);
            ps.setString(2, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapQuestion(rs));
                }
            }
        }

        return list;
    }

    public void deleteById(String questionId) throws Exception {
        try (Connection con = DBUtil.getConnection()) {
            try (PreparedStatement ps1 = con.prepareStatement(
                    "DELETE FROM answers WHERE question_id = ?")) {
                ps1.setString(1, questionId);
                ps1.executeUpdate();
            }

            try (PreparedStatement ps2 = con.prepareStatement(
                    "DELETE FROM questions WHERE id = ?")) {
                ps2.setString(1, questionId);
                ps2.executeUpdate();
            }
        }
    }

    public void deleteByIdAndOwner(String questionId, int ownerUserId) throws Exception {
        try (Connection con = DBUtil.getConnection()) {
            try (PreparedStatement ps1 = con.prepareStatement(
                    "DELETE FROM answers WHERE question_id = ? " +
                    "AND EXISTS (SELECT 1 FROM questions q WHERE q.id = ? AND q.owner_user_id = ?)")) {
                ps1.setString(1, questionId);
                ps1.setString(2, questionId);
                ps1.setInt(3, ownerUserId);
                ps1.executeUpdate();
            }

            try (PreparedStatement ps2 = con.prepareStatement(
                    "DELETE FROM questions WHERE id = ? AND owner_user_id = ?")) {
                ps2.setString(1, questionId);
                ps2.setInt(2, ownerUserId);
                ps2.executeUpdate();
            }
        }
    }

    public int claimQuestionsWithoutOwner(int ownerUserId) throws Exception {
        String sql =
                "UPDATE questions " +
                "SET owner_user_id = ? " +
                "WHERE owner_user_id IS NULL OR owner_user_id = 0";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, ownerUserId);
            return ps.executeUpdate();
        }
    }

    private Question mapQuestion(ResultSet rs) throws Exception {
        Question q = new Question(
                rs.getString("id"),
                rs.getString("title"),
                rs.getInt("limit_count")
        );
        q.setAnswerCount(rs.getInt("answer_count"));
        q.setOwnerUserId(rs.getInt("owner_user_id"));
        return q;
    }
}