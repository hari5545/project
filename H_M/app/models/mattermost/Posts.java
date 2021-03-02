package models.mattermost;

import java.util.Map;

public class Posts {

    private Map<String, Post> postListMap;

    public Map<String, Post> getPostListMap() {
        return postListMap;
    }

    public void setPostListMap(Map<String, Post> postList) {
        this.postListMap = postList;
    }


}
