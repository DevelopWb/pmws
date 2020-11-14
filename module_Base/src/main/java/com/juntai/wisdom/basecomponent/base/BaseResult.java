package com.juntai.wisdom.basecomponent.base;

public class BaseResult  {


    public int status;
    public String message;
    public String error;
    public String msg;
    public int code;
    public String type;
    public boolean success;


    @Override
    public String toString() {
        return "BaseResult{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", error='" + error + '\'' +
                ", msg='" + msg + '\'' +
                ", code=" + code +
                ", type='" + type + '\'' +
                ", success=" + success +
                '}';
    }
}
