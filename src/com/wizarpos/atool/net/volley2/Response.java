package com.wizarpos.atool.net.volley2;

public class Response {
    public int code;// 0:成功
    public String msg;
    public Object result;

    public Response() {
    }

    public Response(int code, String msg) {
        super();
        this.code = code;
        this.msg = msg;
    }

    public Response(int code, String msg, Object result) {
        super();
        this.code = code;
        this.msg = msg;
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

}
