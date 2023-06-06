package dev.vality.alerting.mayday.servlet;

import dev.vality.alerting.mayday.AlertingServiceSrv;
import dev.vality.woody.thrift.impl.http.THServiceBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;


import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;

@WebServlet("/alerting")
@RequiredArgsConstructor
public class AlertingServlet extends GenericServlet {

    private Servlet thriftServlet;

    @Autowired
    private AlertingServiceSrv.Iface requestHandler;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        thriftServlet = new THServiceBuilder()
                .build(AlertingServiceSrv.Iface.class, requestHandler);
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        thriftServlet.service(req, res);
    }
}
