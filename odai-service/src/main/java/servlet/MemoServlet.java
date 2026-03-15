package servlet;

import java.io.IOException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Memo;
import storage.DataStore;

@WebServlet("/memo")
public class MemoServlet extends HttpServlet{

 protected void doPost(HttpServletRequest req,HttpServletResponse resp)
  throws IOException{

  req.setCharacterEncoding("UTF-8");

  String memo=req.getParameter("memo");

  DataStore.memoList.add(new Memo(memo));

  resp.getWriter().println("メモ保存");

 }

}