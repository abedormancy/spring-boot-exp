package ga.vabe.kafka.domain;

import lombok.Data;

import java.util.Date;

@Data
public class Message {

    private Long id;    //id

    private int code;  //返回码

    private String msg; //消息

    private Date startTime;  //时间戳

    private Date sendTime;  //时间戳

    private String logPath; //日志地址
}
