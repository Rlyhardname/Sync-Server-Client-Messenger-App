package server.configurations;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageBufferBlockingQue {
    private final BlockingQueue<String> MessageBuffer;

    private MessageBufferBlockingQue() {
        MessageBuffer = new LinkedBlockingQueue<>();
    }

    public static MessageBufferBlockingQue instanceOf() {
        return new MessageBufferBlockingQue();
    }

    public BlockingQueue<String> getMessageBuffer() {
        return MessageBuffer;
    }
}
