package com.bowlong.reflect;

import java.lang.reflect.InvocationHandler;  
import java.lang.reflect.Method;  
import java.lang.reflect.Proxy;  
  
public abstract class AbstractHandler implements InvocationHandler{  
    protected Object target;  
    public void setTarget(Object target){  
        this.target = target;  
    }  
      
    public Object invoke(Object proxy, Method method, Object[] args){  
        doBefore();  
        Object result = null;  
        try{  
            result = method.invoke(target, args);  
        }catch(Exception e){  
            doThrow();  
        }  
        doAfter();  
        return result;  
    }  
      
    public void doBefore(){}  
    public void doAfter(){}  
    public void doThrow(){}  
      
    public Object getProxy(){  
        return Proxy.newProxyInstance(  
                target.getClass().getClassLoader(),   
                target.getClass().getInterfaces(),  
                this  
                );  
    }  
      
    public Object getProxy(Object target){  
        setTarget(target);  
        return getProxy();  
    }  
}  