package com.xeiam.helloreverseajax;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author timmolter
 */
@javax.servlet.annotation.WebServlet(urlPatterns = { "/async" }, asyncSupported = true, initParams = { @WebInitParam(name = "threadpoolsize", value = "10") })
public class AsyncServlet extends HttpServlet {

    public static final int CALLBACK_TIMEOUT = 60000; // ms

    /** executor service */
    private ExecutorService exec;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        int size = Integer.parseInt(getInitParameter("threadpoolsize"));
        exec = Executors.newFixedThreadPool(size);
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        final AsyncContext ctx = req.startAsync();
        final HttpSession session = req.getSession();

        // set the timeout
        ctx.setTimeout(CALLBACK_TIMEOUT);

        // attach listener to respond to lifecycle events of this AsyncContext
        ctx.addListener(new AsyncListener() {
            @Override
            public void onComplete(AsyncEvent event) throws IOException {
                System.out.println("onComplete called");
            }

            @Override
            public void onTimeout(AsyncEvent event) throws IOException {
                System.out.println("onTimeout called");
                ctx.getResponse().getWriter().write("TIMEOUT");
                ctx.complete();
            }

            @Override
            public void onError(AsyncEvent event) throws IOException {
                System.out.println("onError called: " + event.toString());
                ctx.getResponse().getWriter().write("ERROR");
                ctx.complete();
            }

            @Override
            public void onStartAsync(AsyncEvent event) throws IOException {
                System.out.println("onStartAsync called");
            }
        });

        enqueLongRunningTask(ctx);
    }

    private void enqueLongRunningTask(final AsyncContext ctx) {

        exec.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    try {

                        int waitTime = (int) (10 * Math.random()) * 1000; // ms
                        Thread.sleep(waitTime);
                        ServletResponse response = ctx.getResponse();
                        response.getWriter().write("Wait time was: " + waitTime + " ms.");
                        ctx.complete(); // ONLY use this in conjunction with .write(), Not with .dispatch() !!!

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } catch (IllegalStateException ex) {
                    System.out.println("Response object from context is null! (nothing to worry about.)"); // just means the context was already timeout, timeout listener already called.
                } catch (Exception e) {
                    System.err.println("ERROR IN AsyncServlet");
                }
            }
        });
    }

    /** destroy the executor */
    @Override
    public void destroy() {
        exec.shutdown();
    }
}
