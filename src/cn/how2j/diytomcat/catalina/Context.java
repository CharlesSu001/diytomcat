/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: Context
 * Author:   苏晨宇
 * Date:     2020/12/13 19:25
 * Description: 代表一个应用
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cn.how2j.diytomcat.catalina;

import cn.how2j.diytomcat.classloader.WebappClassLoader;
import cn.how2j.diytomcat.exception.WebConfigDuplicatedException;
import cn.how2j.diytomcat.http.ApplicationContext;
import cn.how2j.diytomcat.http.StandardServletConfig;
import cn.how2j.diytomcat.util.ContextXMLUtil;
import cn.how2j.diytomcat.watcher.ContextFileChangeWatcher;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import org.apache.jasper.JspC;
import org.apache.jasper.compiler.JspRuntimeContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.util.*;

/**
 * 〈一句话功能简述〉<br>
 * 〈代表一个应用〉
 *
 * @author 苏晨宇
 * @create 2020/12/13
 * @since 1.0.0
 */
public class Context {
    private String path;
    private String docBase;
    private File contextWebXmlFile;
    private Map<String, String> url_servletClassName;
    private Map<String, String> url_servletName;
    private Map<String, String> servletName_className;
    private Map<String, String> className_servletName;
    private WebappClassLoader webappClassLoader;
    private Host host;
    private boolean reloadable;
    private ContextFileChangeWatcher contextFileChangeWatcher;
    private ServletContext servletContext;
    private Map<Class<?>, HttpServlet> servletPool;
    private Map<String, Map<String, String>> servlet_className_init_params;
    private List<String> loadOnStartupServletClassNames;


    private Map<String, List<String>> url_filterClassName;
    private Map<String, List<String>> url_filterNames;
    private Map<String, String> filterName_className;
    private Map<String, String> className_filterName;
    private Map<String, Map<String, String>> filter_className_init_params;

    private Map<String, Filter> filterPool;

    private List<ServletContextListener> listeners;


    public Context(String path, String docBase, Host host, boolean reloadable) {
        TimeInterval timeInterval = DateUtil.timer();
        this.host = host;
        this.reloadable = reloadable;

        this.path = path;
        this.docBase = docBase;
//        LogFactory.get().info("Deploying web application directory {}", this.docBase);
//        LogFactory.get().info("Deployment of web application directory {} has finished in {} ms", this.docBase, timeInterval.intervalMs());
        this.contextWebXmlFile = new File(docBase, ContextXMLUtil.getWatchedResource());
        this.url_servletClassName = new HashMap<>();
        this.url_servletName = new HashMap<>();
        this.servletName_className = new HashMap<>();
        this.className_servletName = new HashMap<>();


        this.url_filterClassName = new HashMap<>();
        this.url_filterNames = new HashMap<>();
        this.filterName_className = new HashMap<>();
        this.className_filterName = new HashMap<>();
        this.filter_className_init_params = new HashMap<>();

        this.servletContext = new ApplicationContext(this);

        this.servletPool = new HashMap<>();

        this.filterPool=new HashMap<>();

        this.listeners=new ArrayList<>();

        ClassLoader commonClassLoader = Thread.currentThread().getContextClassLoader();
        this.webappClassLoader = new WebappClassLoader(docBase, commonClassLoader);
        this.servlet_className_init_params = new HashMap<>();
        this.loadOnStartupServletClassNames = new ArrayList<>();

        LogFactory.get().info("Deploying web application directory {}", this.docBase);
        deploy();
        LogFactory.get().info("Deployment of web application directory {} has finished in {} ms", this.docBase, timeInterval.intervalMs());
    }

    private void init() {
        if (!contextWebXmlFile.exists())
            return;
        try {
            checkDuplicated();
        } catch (WebConfigDuplicatedException e) {
            e.printStackTrace();
            return;
        }

        String xml = FileUtil.readUtf8String(contextWebXmlFile);
        Document d = Jsoup.parse(xml);

        parseServletMapping(d);
        parseFilterMapping(d);

        parseServletInitParams(d);
        parseFilterInitParams(d);

        initFilter();

        parseLoadOnStartup(d);
        handleLoadOnStartup();

        fireEvent("init");
    }

    private void deploy() {
        loadListeners();
        init();
        //LogFactory.get().info("Deployment of web application directory {} has finished in {} ms", this.docBase, timeInterval.intervalMs());
        if (reloadable) {
            contextFileChangeWatcher = new ContextFileChangeWatcher(this);
            contextFileChangeWatcher.start();
        }

        JspC c = new JspC();
        new JspRuntimeContext(servletContext, c);
    }

    public void stop() {
        webappClassLoader.stop();
        contextFileChangeWatcher.stop();
        destroyServlets();
        fireEvent("destroy");
    }

    public void reload() {
        host.reload(this);
    }

    public boolean isReloadable() {
        return reloadable;
    }

    public void setReloadable(boolean reloadable) {
        this.reloadable = reloadable;
    }


    public WebappClassLoader getWebappClassLoader() {
        return webappClassLoader;
    }

    public void parseLoadOnStartup(Document d) {
        Elements es = d.select("load-on-startup");
        for (Element e : es) {
            String loadOnStartupServletClassName = e.parent().select("servlet-class").text();
            loadOnStartupServletClassNames.add(loadOnStartupServletClassName);
        }
    }

    public void handleLoadOnStartup() {
        for (String loadOnStartupServletClassName : loadOnStartupServletClassNames) {
            try {
                Class<?> clazz = webappClassLoader.loadClass(loadOnStartupServletClassName);
                getServlet(clazz);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | ServletException e) {
                e.printStackTrace();
            }
        }
    }


    private void initFilter(){
        Set<String> classNames=className_filterName.keySet();
        for(String className:classNames){
            try{
                Class clazz=this.getWebappClassLoader().loadClass(className);
                Map<String,String> initParameters=filter_className_init_params.get(className);
                String filterName=className_filterName.get(className);
                FilterConfig filterConfig=new StandardFilterConfig(servletContext,filterName,initParameters);
                Filter filter=filterPool.get(clazz);

                if(filter==null){
                    filter=(Filter) ReflectUtil.newInstance(clazz);
                    filter.init(filterConfig);
                    filterPool.put(className,filter);
                }
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }
    }

    private void parseServletMapping(Document d) {
        //url_ServletName
        Elements mappingurlElements = d.select("servlet-mapping url-pattern");
        for (Element mappingurlElement : mappingurlElements) {
            String urlPattern = mappingurlElement.text();
            String servletName = mappingurlElement.parent().select("servlet-name").first().text();
            url_servletName.put(urlPattern, servletName);
        }

        //servletName_className/className_servletName
        Elements servletNameElements = d.select("servlet servlet-name");
        for (Element servletNameElement : servletNameElements) {
            String servletName = servletNameElement.text();
            String servletClass = servletNameElement.parent().select("servlet-class").first().text();
            servletName_className.put(servletName, servletClass);
            className_servletName.put(servletClass, servletName);
        }

        //url_servletClassName
        Set<String> urls = url_servletName.keySet();
        for (String url : urls) {
            String servletName = url_servletName.get(url);
            String servletClassName = servletName_className.get(servletName);
            url_servletClassName.put(url, servletClassName);
        }
    }


    public void parseFilterMapping(Document d) {
        //filter_utl_name
        Elements mappingurlElements = d.select("filter-mapping url-pattern");
        for (Element mappingurlElement : mappingurlElements) {
            String urlPattern = mappingurlElement.text();
            String filterName = mappingurlElement.parent().select("filter-name").first().text();

            List<String> filterNames = url_filterNames.get(urlPattern);
            if (filterNames == null) {
                filterNames = new ArrayList<>();
                url_filterNames.put(urlPattern, filterNames);
            }
            filterNames.add(filterName);
        }

        //class_name_filter_name
        Elements filterNameElements = d.select("filter filter-name");
        for (Element filterNameElement : filterNameElements) {
            String filterName = filterNameElement.text();
            String filterClass = filterNameElement.parent().select("filter-class").first().text();
            filterName_className.put(filterName, filterClass);
            className_filterName.put(filterClass, filterName);
        }

        //url_filterClassName

        Set<String> urls = url_filterNames.keySet();
        for (String url : urls) {
            List<String> filterNames = url_filterNames.get(url);
            if (filterNames == null) {
                filterNames = new ArrayList<>();
                url_filterNames.put(url, filterNames);
            }

            for (String filterName : filterNames) {
                String filterClassName = filterName_className.get(filterName);
                List<String> fileterClassNames = url_filterClassName.get(url);
                if (fileterClassNames == null) {
                    fileterClassNames = new ArrayList<>();
                    url_filterClassName.put(url, fileterClassNames);
                }
                fileterClassNames.add(filterClassName);
            }
        }
    }


    private void parseServletInitParams(Document d) {
        Elements servletClassNameElements = d.select("servlet-class");
        for (Element servletClassNameElement : servletClassNameElements) {
            String servletClassName = servletClassNameElement.text();
            Elements initElements = servletClassNameElement.parent().select("init-param");
            if (initElements.isEmpty())
                continue;
            Map<String, String> initParams = new HashMap<>();
            for (Element element : initElements) {
                String name = element.select("param-name").get(0).text();
                String value = element.select("param-value").get(0).text();
                initParams.put(name, value);
            }
            servlet_className_init_params.put(servletClassName, initParams);
        }
    }

    private void parseFilterInitParams(Document d) {
        Elements filterClassNameElements = d.select("filter-class");
        for (Element filterClassNameElement : filterClassNameElements) {
            String filterClassName = filterClassNameElement.text();

            Elements initElements = filterClassNameElement.parent().select("init-param");
            if (initElements.isEmpty())
                continue;
            Map<String, String> initParams = new HashMap<>();
            for (Element element : initElements) {
                String name = element.select("param-name").get(0).text();
                String value = element.select("param-value").get(0).text();
                initParams.put(name, value);
            }

            filter_className_init_params.put(filterClassName, initParams);
        }
    }


    private void loadListeners(){
        try{
            if(!contextWebXmlFile.exists())
                return;
            String xml=FileUtil.readUtf8String(contextWebXmlFile);
            Document d=Jsoup.parse(xml);

            Elements es=d.select("listener listener-class");
            for(Element e:es){
                String listenerClassName=e.text();
                Class<?>clazz=this.getWebappClassLoader().loadClass(listenerClassName);
                ServletContextListener listener=(ServletContextListener)clazz.newInstance();
                addListener(listener);
            }

        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private void fireEvent(String type){
        ServletContextEvent event=new ServletContextEvent(servletContext);
        for(ServletContextListener servletContextListener:listeners){
            if(type.equals("init"))
                servletContextListener.contextInitialized(event);
            if(type.equals("destroy"))
                servletContextListener.contextDestroyed(event);
        }
    }


    private void checkDuplicated(Document d, String mapping, String desc) throws WebConfigDuplicatedException {
        Elements elements = d.select(mapping);
        //mapping放入集合排序 看相邻元素是否相同
        List<String> contents = new ArrayList<>();
        for (Element e : elements) {
            contents.add(e.text());
        }

        Collections.sort(contents);

        for (int i = 0; i < contents.size() - 1; i++) {
            String contentPre = contents.get(i);
            String contentNext = contents.get(i + 1);
            if (contentPre.equals(contentNext)) {
                throw new WebConfigDuplicatedException(StrUtil.format(desc, contentPre));
            }
        }
    }

    private void checkDuplicated() throws WebConfigDuplicatedException {
        String xml = FileUtil.readUtf8String(contextWebXmlFile);
        Document d = Jsoup.parse(xml);
        checkDuplicated(d, "servlet-mapping url-pattern", "servlet url 重复,请保持其唯一性:{} ");
        checkDuplicated(d, "servlet servlet-name", "servlet 名称重复,请保持其唯一性:{} ");
        checkDuplicated(d, "servlet servlet-class", "servlet 类名重复,请保持其唯一性:{} ");
    }

    private boolean match(String pattern,String uri){
        //完全匹配
        if(StrUtil.equals(pattern,uri))
            return true;

        //*模式
        if(StrUtil.equals(pattern,"/*"))
            return true;
        //后缀名 /*.jsp
        if(StrUtil.startWith(pattern,"/*.")){
            String patternExtName=StrUtil.subAfter(pattern,'.',false);
            String uriExtName=StrUtil.subAfter(uri,'.',false);
            return StrUtil.equals(patternExtName, uriExtName);
        }
        return false;
    }

    public List<Filter> getMatchedFilters(String  uri){
        List<Filter> filters=new ArrayList<>();
        Set<String>patterns=url_filterClassName.keySet();
        Set<String> matchedPatterns=new HashSet<>();
        for(String pattern:patterns){
            if(match(pattern,uri)){
                matchedPatterns.add(pattern);
            }
        }
        Set<String> mathchedFilterClassNames=new HashSet<>();
        for(String pattern:matchedPatterns){
            List<String>filterClassName=url_filterClassName.get(pattern);
            mathchedFilterClassNames.addAll(filterClassName);
        }

        for(String filterClassName:mathchedFilterClassNames){
            Filter filter=filterPool.get(filterClassName);
            filters.add(filter);
        }
        return filters;
    }

    public String getServletClassName(String uri) {
        return url_servletClassName.get(uri);
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDocBase() {
        return docBase;
    }

    public void setDocBase(String docBase) {
        this.docBase = docBase;
    }

    public void addListener(ServletContextListener listener){
        listeners.add(listener);
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public synchronized HttpServlet getServlet(Class<?> clazz) throws IllegalAccessException, InstantiationException, ServletException {
        HttpServlet servlet = servletPool.get(clazz);
        if (servlet == null) {
            servlet = (HttpServlet) clazz.newInstance();
            ServletContext servletContext = this.getServletContext();
            String className = clazz.getName();
            String servletName = className_servletName.get(className);
            Map<String, String> initParameters = servlet_className_init_params.get(className);
            ServletConfig servletConfig = new StandardServletConfig(servletContext, servletName, initParameters);
            servlet.init(servletConfig);
            servletPool.put(clazz, servlet);
        }
        return servlet;
    }

    private void destroyServlets() {
        Collection<HttpServlet> servlets = servletPool.values();
        for (HttpServlet servlet : servlets) {
            servlet.destroy();
        }
    }
}
 
