package com.zlikun.log;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * LoggerContext内部状态监控
 * @auther zlikun <zlikun-dev@hotmail.com>
 * @date 2017/4/27 16:52
 */
public class AccessLogViewMain {

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.setStopAtShutdown(true);
        server.setSessionIdManager(new HashSessionIdManager());

        ServerConnector http = new ServerConnector(server);
        http.setHost("localhost");
        http.setPort(80);
        http.setStopTimeout(30000);
        http.setIdleTimeout(30000);
        server.addConnector(http);

        server.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

                Logger log = LoggerFactory.getLogger(this.getClass()) ;
                log.info("请求URI：{}" ,request.getRequestURI());

                // Declare response encoding and types
                response.setContentType("text/html; charset=utf-8");
                // Declare response status code
                response.setStatus(HttpServletResponse.SC_OK);
                // Write back response
                response.getWriter().println("<h1>Hello Logback!</h1>");
                // Inform jetty that this request has now been handled
                baseRequest.setHandled(true);
            }
        });

        server.start();
        server.join();
    }

}
