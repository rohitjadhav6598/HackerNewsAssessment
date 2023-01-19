package com.QP.HN.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.QP.HN.Beans.CommentsResponse;
import com.QP.HN.Beans.Story;
import com.QP.HN.Beans.StoryResponse;
import com.QP.HN.Config.AsyncConfig;
import com.QP.HN.Dao.RedisDao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class QPService {
	
	private static final Logger logger = LoggerFactory.getLogger(QPService.class);
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	RedisDao redis;
	
	@Value("${hackerNews.getStory}")
	private String getStoryUrl;
	
	@Value("${hackerNews.topStories}")
	private String topStoryUrl;
	
	@Async
	private CompletableFuture<Story> getStoryAsync(int id) {
//		logger.info("getting story "+id);
		long start = System.currentTimeMillis();
		logger.info("getting story {}, {}",id,"" + Thread.currentThread().getName());
		Story story = restTemplate.getForObject(getStoryUrl, Story.class, Map.of("id",Integer.toString(id)));
		long end = System.currentTimeMillis();
        logger.info("Total time {}", (end - start));
		return CompletableFuture.completedFuture(story);
		
	}
	
	private Story getStory(int id) {
		
		return restTemplate.getForObject(getStoryUrl, Story.class, Map.of("id",Integer.toString(id)));
		
	}

	private List<Integer> topstories() {
		List<Integer> beststories= restTemplate.getForObject(topStoryUrl, List.class);
		return beststories;
	}
	
	public List<StoryResponse> gettopstories() throws InterruptedException, ExecutionException {
		
		List<StoryResponse> cachedResponse = redis.fetchTopStories();
		if(null!= cachedResponse && !cachedResponse.isEmpty()) return cachedResponse;
		
		//getting top stories from Hacker News API
		List<Integer> topstories = topstories();
		logAsJson(topstories);
		
		//start asynchronous calls to get all top stories
		List<CompletableFuture<Story>> allFutures = new ArrayList<>();

		topstories.stream().forEach(id->allFutures.add(getStoryAsync(id)));
		
        CompletableFuture.allOf(allFutures.toArray(new CompletableFuture[0])).join();
        
		List<Story> stories = allFutures.stream().map(f->{
			try {
				return f.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return null;
			} catch (ExecutionException e) {
				e.printStackTrace();
				return null;
			}
		}).collect(Collectors.toList());
		
		//generate response
		List<StoryResponse> storyResponse = generateTopStoryResponse(stories);
		// save to cache
		redis.saveStory(storyResponse);
		return storyResponse;
	
	}
	
	private List<StoryResponse> generateTopStoryResponse(List<Story> stories){
		
		stories.sort((s1,s2) -> s2.getScore()-s1.getScore());
		
		List<StoryResponse> storyResponse = stories.stream().limit(10).map(s->{
			StoryResponse story = new StoryResponse();
			story.setId(s.getId());
			story.setTitle(s.getTitle());
			story.setUrl(s.getUrl());
			story.setScore(s.getScore());
			story.setTime(new java.util.Date((long)s.getTime()*1000));
			story.setAuthor(s.getBy());
			return story;
		}).collect(Collectors.toList());
		
		return storyResponse;
		
	}

	public List<CommentsResponse> getComments(int id) {
		List<CommentsResponse> cachedResponse = redis.fetchAllComments(id);
		if(null!= cachedResponse && !cachedResponse.isEmpty()) return cachedResponse;
		//get story details
		Story story = getStory(id);
		
		//start asynchronous calls to get all comments
		List<CompletableFuture<Story>> allFutures = new ArrayList<>();

		story.getKids().forEach(kid->allFutures.add(getStoryAsync(kid)));
				
        CompletableFuture.allOf(allFutures.toArray(new CompletableFuture[0])).join();
		        
		List<Story> comments = allFutures.stream().map(f->{
			try {
				return f.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return null;
			} catch (ExecutionException e) {
				e.printStackTrace();
				return null;
			}
		}).collect(Collectors.toList());
		
		List<CommentsResponse> response = generateTopCommentResponse(comments);
		
		redis.saveComment(response, id);
		return response;
	}
	
	private List<CommentsResponse> generateTopCommentResponse(List<Story> comments){
		
		comments.sort((s1,s2) -> {
			if(s2.getKids()==null) return -1;
			if(s1.getKids()==null) return 1;
			return s2.getKids().size()-s1.getKids().size();
		});
		
		List<CommentsResponse> commentResponse = comments.stream().limit(10).map(s->{
			logAsJson(s.toString());
			CommentsResponse c = new CommentsResponse();
			c.setId(s.getId());
			c.setAuthor(s.getBy());
			c.setText(s.getText());
			return c;
		}).collect(Collectors.toList());
		
		return commentResponse;
		
	}
	
	private void logAsJson(Object o) {
		ObjectMapper mapper = new ObjectMapper();
	      //Converting the Object to JSONString
	    String jsonString;
		try {
			jsonString = mapper.writeValueAsString(o);
			logger.info(jsonString);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
	}
	
	public List<StoryResponse> getPastStories(){
		return redis.fetchPastStories();
	}
	
}
