package com.reddate.wuhanddc.dto.wuhanchain;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author kuan
 * Created on 21/1/12.
 * @description
 */
public class ReqJsonRpcBean {
    private static AtomicLong nextId = new AtomicLong(0L);

    private String jsonrpc = "2.0";
    private String method;
    private long id = nextId.getAndIncrement();
    private List<Object> params;

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Object> getParams() {
        return params;
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }


    @Override
    public String toString() {
        return "ReqJsonRpcBean{" +
                "jsonrpc='" + jsonrpc + '\'' +
                ", method='" + method + '\'' +
                ", id=" + id +
                ", params=" + params +
                '}';
    }
}
