package com.QP.HN.Controller;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.QP.HN.Beans.CommentsResponse;
import com.QP.HN.Beans.StoryResponse;
import com.QP.HN.Dao.RedisDao;
import com.QP.HN.service.QPService;

@RestController
public class QPController {
	
	@Autowired
	QPService service;
	
	@GetMapping("v1/top-stories")
	public List<StoryResponse> gettopstory() {
		
		try {
			return service.gettopstories();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		} catch (ExecutionException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	@GetMapping("v1/comments/{id}")
	public List<CommentsResponse> getComments(@PathVariable int id){
		return service.getComments(id);
	}
	
	@GetMapping("v1/past-stories")
	public List<StoryResponse> getCommentsFromRedis(){
		return service.getPastStories();
	}
	
	
}
