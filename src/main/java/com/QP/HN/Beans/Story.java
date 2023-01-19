package com.QP.HN.Beans;

import java.util.List;

import lombok.Data;

@Data
public class Story {
	
	private String by;
    private long descendants;
    private int id;
    private List<Integer> kids;
    private int score;
    private long time;
    private String title;
    private String type;
    private String url;
    private long parent;
    private String text;
}
