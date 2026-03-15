package servlet;

import java.io.IOException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Answer;
import storage.DataStore;

@WebServlet("/search")
public class SearchServlet extends HttpServlet{

 protected void doGet(HttpServletRequest req,HttpServletResponse resp)
  throws IOException{

  resp.setContentType("text/html; charset=UTF-8");

  String keyword=req.getParameter("q");

  for(Answer a:DataStore.answers){

   if(a.getText().contains(keyword)){

    resp.getWriter().println("<p>"+a.getText()+"</p>");

   }

  }

 }

}