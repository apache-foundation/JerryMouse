package com.github.apachefoundation.jerrymouse.container.wrapper;

import com.github.apachefoundation.jerrymouse.container.Container;

import javax.servlet.http.HttpServlet;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

/**
 * @Author: xiantang
 * @Date: 2019/5/24 20:17
 */
public interface Wrapper extends Container {
    /**
     * 分配一个已经初始化的实例
     * @return
     */
    public HttpServlet allocate() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException, MalformedURLException, ClassNotFoundException;

    void setServletClass(String servletClass);

    public void load() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException, MalformedURLException, ClassNotFoundException;

    Container getContainer();

    void setContainer(Container container);

    public String getName();

    public void setName(String name);

    public String getServletClass();

}
