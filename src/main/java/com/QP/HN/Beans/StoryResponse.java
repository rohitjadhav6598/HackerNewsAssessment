package com.QP.HN.Beans;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Data;

@Data
@RedisHash("Story")
public class StoryResponse implements Serializable{
	
	@Id
	private int id;
	private String Author;
    private int score;
    private Date time;
    private String title;
    private String url;
}
