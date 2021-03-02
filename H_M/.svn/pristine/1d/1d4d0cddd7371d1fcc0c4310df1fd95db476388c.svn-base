package models.mattermost;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ChannelPosts {
    @SerializedName("order")
    @Expose
    private ArrayList<String> order;
    @SerializedName("posts")
    @Expose
    private Object postBlock;

    public ArrayList<String> getOrder() {
        return order;
    }

    public void setOrder(ArrayList<String> order) {
        this.order = order;
    }

    public Object getPostBlock() {
        return postBlock;
    }

    public void setPostBlock(Object postBlock) {
        this.postBlock = postBlock;
    }

    public int getPostCount() {
        return order.size();
    }
}
