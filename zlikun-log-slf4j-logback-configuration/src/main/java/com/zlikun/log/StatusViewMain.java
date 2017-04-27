package com.zlikun.log;

import ch.qos.logback.classic.ViewStatusMessagesServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.eclipse.jetty.servlet.*;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * LoggerContext内部状态监控
 * @auther zlikun <zlikun-dev@hotmail.com>
 * @date 2017/4/27 16:52
 */
public class StatusViewMain {

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

        WebAppContext wac = new WebAppContext();
        wac.setContextPath("/");
        wac.setResourceBase("/data");
        server.setHandler(wac);

        // https://logback.qos.ch/manual/configuration.html#viewingStatusMessages
        wac.addFilter(EncodingFilter.class ,"/" ,null) ;
        wac.addServlet(ViewStatusMessagesServlet.class, "/") ;

        server.start();
        server.join();
    }

    public static final class EncodingFilter implements Filter {

        @Override
        public void init(FilterConfig cfg) throws ServletException {

        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse resp = (HttpServletResponse) response;

            req.setCharacterEncoding("UTF-8");
            resp.setCharacterEncoding("UTF-8");

            chain.doFilter(req ,resp);
        }

        @Override
        public void destroy() {

        }
    }

}
