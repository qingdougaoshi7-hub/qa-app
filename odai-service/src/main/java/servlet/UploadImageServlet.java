package servlet;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet("/upload")
@MultipartConfig
public class UploadImageServlet extends HttpServlet {

 protected void doPost(HttpServletRequest req,HttpServletResponse resp){

  try{

   Part file=req.getPart("image");

   String fileName=file.getSubmittedFileName();

   String path=getServletContext().getRealPath("/img");

   file.write(path+"/"+fileName);

   resp.getWriter().println("アップロード完了");

  }catch(Exception e){

   e.printStackTrace();

  }

 }

}