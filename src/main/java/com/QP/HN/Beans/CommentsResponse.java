package com.QP.HN.Beans;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Data;

@Data
@RedisHash("Comment")
public class CommentsResponse implements Serializable{
	
	@Id
	private int id;
    private String text;
    private String Author;
}
