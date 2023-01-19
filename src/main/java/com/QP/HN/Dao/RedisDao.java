package com.QP.HN.Dao;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.QP.HN.Beans.CommentsResponse;
import com.QP.HN.Beans.StoryResponse;

@Repository
public class RedisDao {

	@Autowired
	private RedisTemplate template;

	public boolean saveComment(List<CommentsResponse> cr, int Id) {

		try {
			template.opsForHash().put("Comments", "" + Id, cr);
			template.expire("Comments", 15, TimeUnit.MINUTES);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean saveStory(List<StoryResponse> sr) {

		try {
			//store complete in top story
			template.opsForHash().put("Story", "", sr);
			template.expire("Story", 15, TimeUnit.MINUTES);
			
			// store all sories without expiry and with unique keys
			sr.forEach(story->template.opsForHash().put("PastStory", ""+story.getId(), story));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<CommentsResponse> fetchAllComments(int Id) {
		return (List<CommentsResponse>) template.opsForHash().get("Comments", "" + Id);

	}

	public List<StoryResponse> fetchTopStories() {
		return (List<StoryResponse>) template.opsForHash().get("Story","");
		
	}

	public List<StoryResponse> fetchPastStories() {
		return (List<StoryResponse>) template.opsForHash().values("PastStory");
		
	}
	
	public void putStringByKey(final String key, final String value) {
		template.execute(new RedisCallback<String>() {
			@Override
			public String doInRedis(RedisConnection connection) throws DataAccessException {
				connection.set(key.getBytes(), value.getBytes());
				return null;
			}
		});
	}
}
