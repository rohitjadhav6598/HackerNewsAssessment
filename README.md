# HackerNewsAssessment

Spring Boot Microservice for hacker news firebase api with Redis cache

Details of api:
1. /top-stories - It will get the top storiew from hackernew topstories api, Fetch all the details of the stories from hackernews story api and return top 10 stories by score.
It stores the response in redis with key "topstories" with 15 minute expiry time and with key "paststories" without any expiry time. It will use cached responsed upto 15 minutes by fetching stories from cache.

2. /past-stories - Will return all the stories that were served previously from the /top-stories endpoint by fetching the stories from redis with "paststories" key;

3. /comments/{id} - Will return top 10 commnets(sorted by no of child comments) for story of given id. it will store the response in redis cache with key "Comments-"+id with 15 minute expiry time. It will use cached responsed upto 15 minutes by fetching stories from cache.
