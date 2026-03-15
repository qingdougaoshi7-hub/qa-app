package storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.Answer;

public class AnswerDAO {

    public void insert(String questionId, String text) throws Exception {
        String sql = """
            INSERT INTO answers
            (question_id, text, favorite, read_flag, special_type, final_flag, final_rank)
            VALUES (?, ?, FALSE, FALSE, '', FALSE, 0)
        """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, questionId);
            ps.setString(2, text);
            ps.executeUpdate();
        }
    }

    public List<Answer> findByQuestionId(String questionId) throws Exception {
        return findList("""
            SELECT answer_id, text, favorite, read_flag, special_type, final_flag, final_rank
            FROM answers
            WHERE question_id = ?
            ORDER BY answer_id DESC
        """, questionId);
    }

    public List<Answer> findFavoritesByQuestionId(String questionId) throws Exception {
        return findList("""
            SELECT answer_id, text, favorite, read_flag, special_type, final_flag, final_rank
            FROM answers
            WHERE question_id = ? AND favorite = TRUE
            ORDER BY answer_id DESC
        """, questionId);
    }

    public List<Answer> findFinalistsByQuestionId(String questionId) throws Exception {
        return findList("""
            SELECT answer_id, text, favorite, read_flag, special_type, final_flag, final_rank
            FROM answers
            WHERE question_id = ? AND final_flag = TRUE
            ORDER BY answer_id DESC
        """, questionId);
    }

    public List<Answer> findRankingByQuestionId(String questionId) throws Exception {
        return findList("""
            SELECT answer_id, text, favorite, read_flag, special_type, final_flag, final_rank
            FROM answers
            WHERE question_id = ? AND final_rank BETWEEN 1 AND 5
            ORDER BY final_rank ASC
        """, questionId);
    }

    public Answer findChihuahuaAwardByQuestionId(String questionId) throws Exception {
        String sql = """
            SELECT answer_id, text, favorite, read_flag, special_type, final_flag, final_rank
            FROM answers
            WHERE question_id = ? AND special_type = 'chihuahua'
            ORDER BY answer_id DESC
            LIMIT 1
        """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAnswer(rs);
                }
            }
        }
        return null;
    }

    public void toggleFavorite(int answerId) throws Exception {
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE answers SET favorite = NOT favorite WHERE answer_id = ?")) {
            ps.setInt(1, answerId);
            ps.executeUpdate();
        }
    }

    public void toggleSpecialType(int answerId, String type) throws Exception {
        String sql = """
            UPDATE answers
            SET special_type = CASE WHEN special_type = ? THEN '' ELSE ? END
            WHERE answer_id = ?
        """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, type);
            ps.setString(2, type);
            ps.setInt(3, answerId);
            ps.executeUpdate();
        }
    }

    public void toggleChihuahuaType(int answerId) throws Exception {
        String sql = """
            UPDATE answers
            SET special_type = CASE WHEN special_type = 'chihuahua' THEN '' ELSE 'chihuahua' END
            WHERE answer_id = ?
        """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, answerId);
            ps.executeUpdate();
        }
    }

    public void setFinalOnePerFavoritePage(String questionId, int page, int pageSize, int selectedAnswerId) throws Exception {
        List<Answer> favorites = findFavoritesByQuestionId(questionId);
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, favorites.size());
        if (fromIndex >= favorites.size()) {
            return;
        }

        try (Connection con = DBUtil.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("UPDATE answers SET final_flag = FALSE WHERE answer_id = ?")) {
                for (int i = fromIndex; i < toIndex; i++) {
                    ps.setInt(1, favorites.get(i).getAnswerId());
                    ps.executeUpdate();
                }
            }
            try (PreparedStatement ps = con.prepareStatement("UPDATE answers SET final_flag = TRUE WHERE answer_id = ?")) {
                ps.setInt(1, selectedAnswerId);
                ps.executeUpdate();
            }
        }
    }

    public void setFinalRank(int answerId, int rank) throws Exception {
        try (Connection con = DBUtil.getConnection()) {
            try (PreparedStatement ps1 = con.prepareStatement("UPDATE answers SET final_rank = 0 WHERE final_rank = ?")) {
                ps1.setInt(1, rank);
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = con.prepareStatement("UPDATE answers SET final_rank = ?, final_flag = TRUE WHERE answer_id = ?")) {
                ps2.setInt(1, rank);
                ps2.setInt(2, answerId);
                ps2.executeUpdate();
            }
        }
    }

    public String findQuestionIdByAnswerId(int answerId) throws Exception {
        String sql = "SELECT question_id FROM answers WHERE answer_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, answerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("question_id");
                }
            }
        }
        return null;
    }

    public AnswerState getAnswerState(int answerId) throws Exception {
        String sql = "SELECT favorite, special_type, final_flag FROM answers WHERE answer_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, answerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new AnswerState(
                            rs.getBoolean("favorite"),
                            rs.getString("special_type"),
                            rs.getBoolean("final_flag")
                    );
                }
            }
        }
        return null;
    }

    private List<Answer> findList(String sql, String questionId) throws Exception {
        List<Answer> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAnswer(rs));
                }
            }
        }
        return list;
    }

    private Answer mapAnswer(ResultSet rs) throws Exception {
        Answer a = new Answer(rs.getInt("answer_id"), rs.getString("text"));
        if (rs.getBoolean("favorite")) {
            a.toggleFavorite();
        }
        if (rs.getBoolean("read_flag")) {
            a.markRead();
        }
        a.setSpecialType(rs.getString("special_type"));
        a.setFinalFlag(rs.getBoolean("final_flag"));
        a.setFinalRank(rs.getInt("final_rank"));
        return a;
    }

    public static class AnswerState {
        private final boolean favorite;
        private final String specialType;
        private final boolean finalFlag;

        public AnswerState(boolean favorite, String specialType, boolean finalFlag) {
            this.favorite = favorite;
            this.specialType = specialType == null ? "" : specialType;
            this.finalFlag = finalFlag;
        }

        public boolean isFavorite() {
            return favorite;
        }

        public String getSpecialType() {
            return specialType;
        }

        public boolean isFinalFlag() {
            return finalFlag;
        }

        public boolean isSpecial() {
            return "special".equals(specialType);
        }

        public boolean isChihuahua() {
            return "chihuahua".equals(specialType);
        }
    }
}
