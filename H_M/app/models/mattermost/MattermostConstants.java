package models.mattermost;


public class MattermostConstants {

    public static final String API_V4 = "api/v4";

    public static final String USERS_LOGIN_URL = API_V4 + "/users/login";

    public static final String CREATE_TEAM_URL = API_V4 + "/teams";

    public static final String DELETE_TEAM_URL = API_V4 + "/teams/%s";

    public static final String USERS_URL = API_V4 + "/users/0/100";

    public static final String USERS_ID_URL = API_V4 + "/users/%s";

    public static final String TEAMS_URL = API_V4 + "/teams/members";

    public static final String USERS_STATUS_IDS_URL = API_V4 + "/users/status/ids";

    public static final String WEBSOCKET_URL = API_V4 + "/websocket";

    public static final String USER_TEAMS = API_V4 + "/teams";

    public static final String USER_TEAM_CHANNELS = API_V4 + "/users/%s/teams/%s/channels";

    public static final String USER_POSTS = API_V4 + "/posts";

    public static final String USER_FILE_UPLOAD = API_V4 + "/files";

    public static final String USER_FILE_DOWNLOAD = API_V4 + "/files/%s";

    public static final String USER_FILE_INFO = API_V4 + "/files/%s/info";

    public static final String USER_POSTS_FOR_CHANNEL = API_V4 + "/channels/%s/posts";

    public static final String CHANNELS_URL = API_V4 + "/teams/%s/channels/";

    public static final String CHANNEL_CREATE_URL = API_V4 + "/channels/direct";

    public static final String CHANNEL_POSTS_URL = API_V4 + "/users/%s/channels/%s/unread";

    public static final String CHANNEL_BY_ID_URL = API_V4 + "/teams/%s/channels/%s/";

    public static final String CHANNEL_MEMBERS_IDS_URL = API_V4 + "/teams/%s/channels/users/0/20";

    public static final String CREATE_POST_URL = API_V4 + "/teams/%s/channels/%s/posts/create";

    public static final String VIEW_CHANNEL = API_V4 + "/teams/%s/channels/view";

    public static final String CREATE_CHANNEL = API_V4 + "/channels";
    
    public static final String UPDATE_CHANNEL = API_V4 + "/channels/%s";
   
    public static final String DELETE_CHANNEL = API_V4 + "/channels/%s";

    public static final String CREATE_USER = API_V4 + "/users";

    public static final String UPDATE_USER = API_V4 + "/users/%s";
    
    public static final String ADD_USER_TO_TEAM = API_V4 + "/teams/%s/members";

    public static final String DELETE_USER_FROM_TEAM = API_V4 + "/teams/%s/members/%s";

    public static final String ADD_USER_TO_CHANNEL = API_V4 + "/channels/%s/members";

    public static final String DELETE_USER_FROM_CHANNEL = API_V4 + "/channels/%s/members/%s";

    public static final String DEACTIVATE_USER = API_V4 + "/users/%s";

    //Statistics Related 
    public static final String TOTAL_USER_COUNT = API_V4 + "/users/stats";

    public static final String USER_ACCESS_TOKEN_VALIDATION = API_V4 + "/users/tokens/%s";

    public static final String CHANNEL_MEMBERS = API_V4 + "/channels/%s/members";


    public static final String TEAM_STAT = API_V4 + "/analytics/old?name=standard&team_id=%s";

    public static final String TEAM_POST_COUNT_DAY = API_V4 + "/analytics/old?name=post_counts_day&team_id=%s";

    public static final String TEAM_POST_COUNT_USER = API_V4 + "/analytics/old?name=user_counts_with_posts_day&team_id=%s";
    
    public static final String TEAM_USER_LAST_ACTIVITY_AT = API_V4 + "/users?in_team=%s&page=0&per_page=100&sort=last_activity_at";

    public static final String TEAM_USER_CREATE_AT = API_V4 + "/users?in_team=%s&page=0&per_page=100&sort=create_at";

    public static final String RESET_PASSWORD_USER = API_V4 + "/users/%s/password";

}