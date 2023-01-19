package com.QP.HN.Beans;

import java.util.List;

import lombok.Data;

@Data
public class Comment {
	private String by;
    private long id;
    private List<Integer> kids;
    private long parent;
    private String text;
    private long time;
    private String type;
}
