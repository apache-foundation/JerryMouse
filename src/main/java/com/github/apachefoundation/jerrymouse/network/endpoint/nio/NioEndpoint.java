package com.github.apachefoundation.jerrymouse.network.endpoint.nio;

import com.github.apachefoundation.jerrymouse.container.Contained;
import com.github.apachefoundation.jerrymouse.container.Container;
import com.github.apachefoundation.jerrymouse.container.context.Context;
import com.github.apachefoundation.jerrymouse.container.context.SimpleContext;
import com.github.apachefoundation.jerrymouse.container.loader.Loader;
import com.github.apachefoundation.jerrymouse.container.loader.SimpleLoader;
import com.github.apachefoundation.jerrymouse.container.mapper.Mapper;
import com.github.apachefoundation.jerrymouse.container.pipeline.Pipeline;
import com.github.apachefoundation.jerrymouse.container.valve.Valve;
import com.github.apachefoundation.jerrymouse.container.valve.context.SimpleContextValve;
import com.github.apachefoundation.jerrymouse.container.valve.wapper.SimpleWrapperValve;
import com.github.apachefoundation.jerrymouse.container.wrapper.Wrapper;
import com.github.apachefoundation.jerrymouse.context.WebContext;
import com.github.apachefoundation.jerrymouse.context.WebHandler;
import com.github.apachefoundation.jerrymouse.entity.Mapping;
import com.github.apachefoundation.jerrymouse.exception.RequestInvalidException;
import com.github.apachefoundation.jerrymouse.http.HttpRequest;
import com.github.apachefoundation.jerrymouse.network.wrapper.nio.NioSocketWrapper;
import com.github.apachefoundation.jerrymouse.network.connector.nio.NioAcceptor;
import com.github.apachefoundation.jerrymouse.network.connector.nio.NioPoller;
import com.github.apachefoundation.jerrymouse.network.endpoint.Endpoint;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;


import javax.servlet.http.HttpServlet;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: xiantang
 * @Date: 2019/4/17 14:45
 */
public class NioEndpoint extends Endpoint {


    private static WebHandler phandler;

    /**
     初始化webContext存入servlet以及他的映射
     **/
    static {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parse = factory.newSAXParser();
            phandler = new WebHandler();
            // 当前线程的类加载器
            parse.parse(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("web.xml"), phandler);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }


    private ServerSocketChannel server;
    private NioAcceptor acceptor;
    private Logger logger = Logger.getLogger(NioEndpoint.class);
    /**
     * Poller线程数量是cpu的核数 参考tomcat
     * 对于计算密集性的任务 当线程池的大小为Ncpu+1 通常能实现最优的利用率
     * (当计算密集型的线程偶尔由于页缺失或者其他情况而暂停的时候
     * ，这个额外的线程可以CPU时钟周期不会被浪费)
     * <p>
     * *********************新加注释**********************
     * poller 线程池用于监听 socket 事件，开销应比 work 线程要小，故分配 1/4 的线程数量
     * 加一是因为我电脑没那么多核 搞成 0 个线程了都
     */
    private int pollerCount = Math.min(2, Runtime.getRuntime().availableProcessors()) / 4 + 1;
    private List<NioPoller> nioPollers;


    /**
     * poller轮询器
     */
    private AtomicInteger pollerRotater = new AtomicInteger(0);


    private Container context = null;


    /**
     * 初始化ServerSocket
     *
     * @param port
     */
    private void initSeverSocket(int port) throws IOException {
        server = ServerSocketChannel.open();
        // 监听地址
        server.bind(new InetSocketAddress(port));
        // 设置阻塞
        server.configureBlocking(true);
        logger.info("初始化SeverSocket完成");
    }

    /**
     * 初始化initAcceptor
     */
    private void initAcceptor() {
        acceptor = new NioAcceptor(this);
        Thread t = new Thread(acceptor);

        t.start();
        logger.info("初始化Acceptor完成");

    }

    /**
     * 初始化initPoller
     * 线程数为CPU核心数目
     */
    private void initPoller() throws IOException {
        nioPollers = new ArrayList<>(pollerCount);
        for (int i = 0; i < pollerCount; i++) {
            String pollName = "NioPoller-" + i;
            NioPoller nioPoller = new NioPoller(this, pollName);
            Thread pollerThread = new Thread(nioPoller);
            pollerThread.setDaemon(true);
            pollerThread.start();
            nioPollers.add(nioPoller);
        }
        logger.info("初始化Poller完成");
    }


    private void initContext() {
        context = new SimpleContext();
        Valve simpleContextValve = new SimpleContextValve();

        ((Contained) simpleContextValve).setContainer(context);
        ((SimpleContext) context).setBasic(simpleContextValve);
        List<Mapping> mappings = phandler.getMappings();
        List<Wrapper> wrappers = phandler.getWrappers();
        Loader loader = new SimpleLoader();
        try {
            for (Wrapper wrapper : wrappers
            ) {
                wrapper.setLoader(loader);
                wrapper.load();
                SimpleWrapperValve simpleWrapperValve = new SimpleWrapperValve();
                ((Contained) simpleWrapperValve).setContainer(wrapper);
                ((Pipeline) wrapper).setBasic(simpleWrapperValve);
                context.addChild(wrapper);
            }
            for (Mapping map :
                    mappings) {
                for (String pattern : map.getPatterns()
                ) {
                    ((Context) context).addServletMapping(pattern, map.getName());
                }
            }


        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }



    }

    public Container getContext() {
        return context;
    }

    public NioPoller getPoller() {
        int idx = Math.abs(pollerRotater.incrementAndGet()) % nioPollers.size();
        return nioPollers.get(idx);
    }


    public void registerToPoller(SocketChannel socket, boolean isNewSocket, int eventType, NioSocketWrapper nioSocketWrapper) throws IOException {
//        server.configureBlocking(false);
        getPoller().register(socket, isNewSocket, eventType, nioSocketWrapper);
//        server.configureBlocking(true);

    }


    @Override
    public void start(int port) {
        try {
            initSeverSocket(port);
            initPoller();
            initContext();
            initAcceptor();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public SocketChannel accept() throws IOException {
        return server.accept();
    }


    @Override
    public void close() {

    }

    public static void main(String[] args) throws IOException, RequestInvalidException {
        NioEndpoint nioEndpoint = new NioEndpoint();
        nioEndpoint.initContext();
        Container container = nioEndpoint.getContext();

    }
}
