package main;


import org.apache.logging.log4j.core.config.Configurator;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.IOException;

/**
 * movie 启动函数
 */
public class Main {
    private static Logger logger;

    private static final int PORT = 80;

    private static final String CONTEXT_PATH = "/";
    private static final String MAPPING_URL = "/";
    private static final String WEBAPP_DIRECTORY = "D:\\project\\movie\\src\\main\\resources/META-INF/webapp";

    private static final String LOG4J_PATH = "D:\\project\\movie\\src\\main\\resources\\log4j2.xml";

    /**
     * 初始化 日志路径
     *
     * @param path
     */
    public static void initLog(String path) {
        Configurator.initialize(null, path);
        logger = LoggerFactory.getLogger(Main.class);
    }

    public static void main(String[] args) throws Exception {

        //1、初始化日志log日志文件
        initLog(LOG4J_PATH);

        //启动jetty容器
        startJetty(PORT);
    }

    private static void startJetty(int port) throws Exception {
        logger.debug("Starting server at port {}", port);
        Server server = new Server();

        server.addConnector(getHttpConnector(server,port));

        server.setHandler(getServletContextHandler());

        server.start();
        logger.info("Server started at port {}", port);
        server.join();
    }


    private static ServerConnector getHttpConnector(Server server,int port) {

        HttpConfiguration httpConfig = new HttpConfiguration();

        ServerConnector http = new ServerConnector(server,
                new HttpConnectionFactory(httpConfig));
        http.setPort(port);
        http.setIdleTimeout(30000);

        return http;
    }

    private static ServletContextHandler getServletContextHandler() throws IOException {
        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS); // SESSIONS requerido para JSP
        contextHandler.setErrorHandler(null);

        contextHandler.setResourceBase(WEBAPP_DIRECTORY);
        contextHandler.setContextPath(CONTEXT_PATH);

        // JSP
//        contextHandler.setClassLoader(Thread.currentThread().getContextClassLoader()); // Necesario para cargar JspServlet

        // Spring
        WebApplicationContext webAppContext = getWebApplicationContext();
        DispatcherServlet dispatcherServlet = new DispatcherServlet(webAppContext);
        ServletHolder springServletHolder = new ServletHolder("mvc-dispatcher", dispatcherServlet);
        contextHandler.addServlet(springServletHolder, MAPPING_URL);
        contextHandler.addEventListener(new ContextLoaderListener(webAppContext));

        return contextHandler;
    }

    private static WebApplicationContext getWebApplicationContext() {
        XmlWebApplicationContext context = new XmlWebApplicationContext();
        context.setConfigLocation("classpath:spring-mvc.xml");
        return context;
    }

}
