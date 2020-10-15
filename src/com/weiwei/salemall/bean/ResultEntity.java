package com.weiwei.salemall.bean;

/**
 * @author created by edz
 *         网络请求返回实体
 */
public class ResultEntity {
    /**
     * data : null
     * code : 0
     * msg : 新增成功
     */
    private Object data;
    private int code;
    private String msg;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
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

    @Override
    public String toString() {
        return "ResultEntity{" +
                "data=" + data +
                ", code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}

