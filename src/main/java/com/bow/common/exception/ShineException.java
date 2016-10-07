package com.bow.common.exception;

/**
 * Created by vv on 2016/8/19.
 */
public class ShineException extends RuntimeException {

    public ShineException(){
        super(ShineExceptionCode.fail.toString());
    }

    public ShineException(ShineExceptionCode code){
        this(code.toString());
    }

    public ShineException(ShineExceptionCode code,String detailMessage){
        this(code.toString()+"--"+detailMessage);
    }

    public ShineException(ShineExceptionCode code,Throwable e){
        this(code.toString(),e);
    }

    public ShineException(String message){
        super(message);
    }

    public ShineException(String message,Throwable e){
        super(message,e);
    }

}
