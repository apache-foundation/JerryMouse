package com.github.apachefoundation.jerrymouse.container.valve.context;

import com.github.apachefoundation.jerrymouse.container.Contained;
import com.github.apachefoundation.jerrymouse.container.Container;
import com.github.apachefoundation.jerrymouse.container.context.Context;
import com.github.apachefoundation.jerrymouse.container.valve.Valve;
import com.github.apachefoundation.jerrymouse.container.valve.ValveContext;
import com.github.apachefoundation.jerrymouse.container.wrapper.Wrapper;
import com.github.apachefoundation.jerrymouse.http.HttpRequest;
import com.github.apachefoundation.jerrymouse.http.HttpResponse;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Context实例的基础阀
 * @Author: xiantang
 * @Date: 2019/5/29 17:03
 */
public class SimpleContextValve implements Valve, Contained {
    private Container container;

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public String getInfo() {
        return null;
    }

    @Override
    public void invoke(HttpRequest request, HttpResponse response, ValveContext valveContext) throws ServletException, IOException {
        String relativeUrl = request.getRequestURI().toLowerCase();
        Context context = (Context) getContainer();
        Wrapper wrapper = null;
        try {
            wrapper = (Wrapper) context.map(request, true);
        } catch (IllegalArgumentException e) {
            //TODO 包装成为bad Request
            e.printStackTrace();
            return;
        }
        if (wrapper == null) {
            //TODO 加载无法访问的静态html 或者 servlet
            System.out.println("404");
            return;
        }

        wrapper.invoke(request, response);


    }
}
