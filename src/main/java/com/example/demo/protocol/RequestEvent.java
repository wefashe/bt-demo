package com.example.demo.protocol;

public enum RequestEvent {

    NONE("0","none","无"),
    COMPLETED("1","completed","完成"),
    STARTED("2","started","开始"),
    STOPPED("3", "stopped", "结束");

    private String code;
    private String name;
    private String desc;

    RequestEvent(String code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }
}
