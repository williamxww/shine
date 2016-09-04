package com.bow.config.spring;

import com.bow.common.exception.ShineException;
import com.bow.common.exception.ShineExceptionCode;
import com.bow.config.ServiceBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author vv
 * @since 2016/8/19.
 */
public class ShineBeanDefinitionParser implements BeanDefinitionParser {


    private Class<?> beanClass;

    public ShineBeanDefinitionParser(Class<?> beanClass){
        this.beanClass = beanClass;
    }
    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        //Object source = parserContext.extractSource(element);
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(beanClass);

        String id = element.getAttribute("id");
        beanDefinition.getPropertyValues().addPropertyValue("id", id);

        String interfaceName = element.getAttribute("interface");
        beanDefinition.getPropertyValues().addPropertyValue("interfaceName", interfaceName);
        try {
            beanDefinition.getPropertyValues().addPropertyValue("interfaceClass", Class.forName(interfaceName));
        } catch (ClassNotFoundException e) {
            throw new ShineException(ShineExceptionCode.configException,e);
        }


        if(ServiceBean.class.equals(beanClass)){
            String refId = element.getAttribute("ref");
            RuntimeBeanReference ref = new RuntimeBeanReference(refId);
            beanDefinition.getPropertyValues().addPropertyValue("ref", ref);
        }

        parseProperties(element.getChildNodes(), beanDefinition);
        parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
        beanDefinition.setLazyInit(false);
        return beanDefinition;
    }


    private static void parseProperties(NodeList nodeList, RootBeanDefinition beanDefinition) {
        if (nodeList != null && nodeList.getLength() > 0) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node instanceof Element) {
                    if ("property".equals(node.getNodeName())
                            || "property".equals(node.getLocalName())) {
                        String name = ((Element) node).getAttribute("name");
                        if (name != null && name.length() > 0) {
                            String value = ((Element) node).getAttribute("value");
                            String ref = ((Element) node).getAttribute("ref");
                            if (value != null && value.length() > 0) {
                                beanDefinition.getPropertyValues().addPropertyValue(name, value);
                            } else if (ref != null && ref.length() > 0) {
                                beanDefinition.getPropertyValues().addPropertyValue(name, new RuntimeBeanReference(ref));
                            } else {
                                throw new UnsupportedOperationException("Unsupported <property name=\"" + name + "\"> sub tag, Only supported <property name=\"" + name + "\" ref=\"...\" /> or <property name=\"" + name + "\" value=\"...\" />");
                            }
                        }
                    }
                }
            }
        }
    }
}
