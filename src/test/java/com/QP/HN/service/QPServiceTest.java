package com.QP.HN.service;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import com.QP.HN.Beans.CommentsResponse;
import com.QP.HN.Beans.StoryResponse;
import com.QP.HN.Dao.RedisDao;

class QPServiceTest {
	
	@Autowired
	QPService service;
	
	@Mock
	RedisDao redis;

	@Test
	void testGetCommentsFromCache() {
		Mockito.when(redis.fetchAllComments(1)).thenReturn(getComments());
		assertNotNull(service.getComments(1));
	}
	@Test
	void testGettopstoriesFromCache() {
		Mockito.when(redis.fetchTopStories()).thenReturn(getStories());
		assertNotNull(getStories());
	}

	@Test
	void testGetPastStoriesFromCache() {
		Mockito.when(redis.fetchPastStories()).thenReturn(getStories());
		assertNotNull(service.getPastStories());
	}
	// setting data
	private List<StoryResponse> getStories() {
		List<StoryResponse> list = new ArrayList<>();
		StoryResponse sResponse = new StoryResponse();	
		sResponse.setAuthor("Arjun");
		sResponse.setId(10);
		sResponse.setScore(99);
		sResponse.setTime(new Date());
		sResponse.setTitle("sachin tendulkar");
		sResponse.setUrl("www.master.com");
		list.add(sResponse);
		return list;
	}
	private List<CommentsResponse> getComments() {
		List<CommentsResponse> list = new ArrayList<>();
		CommentsResponse cResponse = new CommentsResponse();	
		cResponse.setAuthor("Akash");
		cResponse.setId(11);
		cResponse.setText("Akash Air is ready to fly..");
		list.add(cResponse);
		return list;
	}
}