package com.github.apachefoundation.jerrymouse.http;

import com.github.apachefoundation.jerrymouse.exception.RequestInvalidException;
import com.github.apachefoundation.jerrymouse.utils.PropertyUtil;
import com.github.apachefoundation.jerrymouse.utils.SocketInputBuffer;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;

import static com.github.apachefoundation.jerrymouse.constants.Constants.*;

/**
 * @Author: xiantang
 * @Date: 2019/4/17 14:45
 */
public class HttpRequest implements HttpServletRequest {

    private String contentType;
    private int contentLength = -1;
    private boolean keepAlive = true;
    private String method;
    private String queryString;
    private String requestURL;
    private String requestURI;
    private String protocol;
    private String body;
    private String serverName;
    private int serverPort;
    private ArrayList<Cookie> cookies = new ArrayList<Cookie>();
    private Map<String, String> headersMap;
    private Map<String, List<String>> parametersMap;
    private SocketInputBuffer inputBuffer;
    private String remoteAddr;



    public HttpRequest(SocketInputBuffer inputBuffer) throws IOException, RequestInvalidException {
        headersMap = new HashMap<>();
        parametersMap = new HashMap<>();
        serverName = PropertyUtil.getProperty("server.name");
        serverPort = Integer.parseInt(PropertyUtil.getProperty("server.port"));
        this.inputBuffer = inputBuffer;
    }




    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> parametersMap0 = new HashMap<>();

        Set<String> keySet = parametersMap.keySet();
        for (String k : keySet){
            parametersMap0.put(k, parametersMap.get(k).toArray(new String[0]));
        }
        return parametersMap0;
    }

    public void setParameter(String key, String value) {
        if (!parametersMap.containsKey(key)) {
            parametersMap.put(key, new ArrayList<String>());
        }
        parametersMap.get(key).add(value);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Vector<String> headerNames = new Vector<String>();
        headerNames.addAll(headersMap.keySet());
        return headerNames.elements();
    }

    @Override
    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    @Override
    public String[] getParameterValues(String s) {
        return parametersMap.get(s).toArray(new String[0]);
    }

    //==================================================
    //==================================================

    public void setContentLength(int length) {
        this.contentLength = length;
    }

    public void setContentType(String type) {
        this.contentType = type;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public void setServerName(String name) {
        this.serverName = name;
    }

    public void setServerPort(int port) {
        this.serverPort = port;
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }



    public void setHead(String key, String value) {
        headersMap.put(key, value);
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void addCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    @Override
    public String getAuthType() {
        return null;
    }

    @Override
    public Cookie[] getCookies() {
        return (Cookie[]) cookies.toArray();
    }

    @Override
    public long getDateHeader(String s) {
        return 0;
    }

    @Override
    public String getHeader(String s) {
        return headersMap.get(s);
    }

    @Override
    public Enumeration<String> getHeaders(String s) {
        return null;
    }



    @Override
    public int getIntHeader(String s) {
        return Integer.parseInt(headersMap.get(s));
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public String getPathInfo() {
        return null;
    }

    @Override
    public String getPathTranslated() {
        return null;
    }

    @Override
    public String getContextPath() {
        return null;
    }

    @Override
    public String getQueryString() {
        return queryString;
    }

    @Override
    public String getRemoteUser() {
        return null;
    }

    @Override
    public boolean isUserInRole(String s) {
        return false;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public String getRequestedSessionId() {
        return null;
    }

    @Override
    public String getRequestURI() {
        return requestURI;
    }

    @Override
    public StringBuffer getRequestURL() {
        return new StringBuffer(requestURL);
    }

    @Override
    public String getServletPath() {
        return null;
    }

    @Override
    public HttpSession getSession(boolean b) {
        return null;
    }

    @Override
    public HttpSession getSession() {

        return null;
    }

    @Override
    public String changeSessionId() {
        return null;
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

    @Override
    public boolean authenticate(HttpServletResponse httpServletResponse) throws IOException, ServletException {
        return false;
    }

    @Override
    public void login(String s, String s1) throws ServletException {

    }

    @Override
    public void logout() throws ServletException {

    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return null;
    }

    @Override
    public Part getPart(String s) throws IOException, ServletException {
        return null;
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> aClass) throws IOException, ServletException {
        return null;
    }

    @Override
    public Object getAttribute(String s) {
        return null;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return null;
    }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public void setCharacterEncoding(String s) throws UnsupportedEncodingException {

    }

    @Override
    public int getContentLength() {
        return contentLength;
    }

    @Override
    public long getContentLengthLong() {
        return 0;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return null;
    }






    @Override
    public String getParameter(String s) {
        return null;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return null;
    }
    @Override
    public String getProtocol() {
        return protocol;
    }

    @Override
    public String getScheme() {
        return null;
    }

    @Override
    public String getServerName() {
        return serverName;
    }

    @Override
    public int getServerPort() {
        return serverPort;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return null;
    }



    @Override
    public String getRemoteHost() {
        return null;
    }

    @Override
    public void setAttribute(String s, Object o) {

    }

    @Override
    public void removeAttribute(String s) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return null;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String s) {
        return null;
    }

    @Override
    public String getRealPath(String s) {
        return null;
    }

    @Override
    public int getRemotePort() {
        return 0;
    }

    @Override
    public String getLocalName() {
        return null;
    }

    @Override
    public String getLocalAddr() {
        return null;
    }

    @Override
    public int getLocalPort() {
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return null;
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return null;
    }

    @Override
    public boolean isAsyncStarted() {
        return false;
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public AsyncContext getAsyncContext() {
        return null;
    }

    @Override
    public DispatcherType getDispatcherType() {
        return null;
    }
}
