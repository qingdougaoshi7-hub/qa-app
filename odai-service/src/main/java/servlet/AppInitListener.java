package servlet;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import storage.DBInit;

@WebListener
public class AppInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("アプリ起動");
        DBInit.init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("アプリ停止");
    }
}