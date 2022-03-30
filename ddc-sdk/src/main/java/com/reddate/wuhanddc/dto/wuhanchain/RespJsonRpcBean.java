package com.reddate.wuhanddc.dto.wuhanchain;

/**
 * @author kuan
 * Created on 21/1/23.
 * @description
 */
public class RespJsonRpcBean {

    private ErrorBean error;
    private String id;
    private String jsonrpc;
    private Object result;

    public ErrorBean getError() {
        return error;
    }

    public void setError(ErrorBean error) {
        this.error = error;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public static class ErrorBean {
        /**
         * code :  -32603
         * data :  null
         * message :
         */

        private String code;
        private String data;
        private String message;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    @Override
    public String toString() {
        return "ResJsonRpcBean{" +
                "error=" + error +
                ", id='" + id + '\'' +
                ", jsonrpc='" + jsonrpc + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}
