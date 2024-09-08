package br.com.camila.aws.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("aws/s3")
public class AwsController {

    @Autowired
    private AmazonS3 amazonS3;

    @Autowired
    private AmazonSQSAsync sqs;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/note")
    public void note(@RequestParam String name, @RequestParam String content){
        //amazonS3.putObject("arquivos-servico", name + ".txt", content);
        new QueueMessagingTemplate(sqs).convertAndSend("queue_aws", content);
        jdbcTemplate.update("insert into nota (nome, conteudo) values (?, ?)", name, content);
    }


    @SqsListener("queue_aws")
    public void consumir(String message){
        System.out.println("OI pessoall:" + message );
    }
}
