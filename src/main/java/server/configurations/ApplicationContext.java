package server.configurations;

import server.services.ServerVer2;

public enum ApplicationContext {
    APPLICATION_CONTEXT;
    private OnlineUsersMap onlineUsersMap;
    private MessageBufferBlockingQue messageBufferBlockingQue;

    public OnlineUsersMap getOnlineUsersHashMap() {
        return onlineUsersMap;
    }

    public void setOnlineUsersHashMap(OnlineUsersMap onlineUsersMap) {
        if (onlineUsersMap == null) {
            this.onlineUsersMap = onlineUsersMap;
        }
    }

    public void setMessageBufferBlockingQue(MessageBufferBlockingQue messageBufferBlockingQue) {
        if (messageBufferBlockingQue == null) {
            this.messageBufferBlockingQue = messageBufferBlockingQue;
        }
    }

    public void initContext() {
        messageBufferBlockingQue = MessageBufferBlockingQue.instanceOf();
        onlineUsersMap = OnlineUsersMap.instanceOf();
    }

    public boolean isUserCountPositive() {
        return onlineUsersMap.getOnlineUsers().size() > 0;
    }

    public boolean isUserOnline(String username) {
        return onlineUsersMap.getOnlineUsers().containsKey(username);
    }

    public String concatUsersWithPasswordReturnString() {
        StringBuffer concat = new StringBuffer();
        onlineUsersMap.getOnlineUsers().forEach((key, value) -> concat.append("Active UserName: :").append(key).append("Active user password: ").append(value).append("\n"));
        return concat.toString();
    }

    public void addUserAndServerInstance(String username, ServerVer2 serverVer2) {
        onlineUsersMap.getOnlineUsers().put(username, serverVer2);
    }

    public void disconnectUser(String username) {
        onlineUsersMap.getOnlineUsers().remove(username);
    }

    public ServerVer2 fetchServerInstance(String username) {
        return onlineUsersMap.getOnlineUsers().get(username);
    }

    public void addAuthenticationAttempt(String message) {
        messageBufferBlockingQue.getMessageBuffer().add(message);
    }
}
