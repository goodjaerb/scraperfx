///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.goodjaerb.scraperfx;
//
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.LinkedBlockingQueue;
//import javafx.animation.AnimationTimer;
//import javafx.scene.control.TextArea;
//
///**
// *
// * @author goodjaerb <goodjaerb@gmail.com>
// */
//public class QueuedMessageBox extends TextArea {
//    private final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
//    private final AnimationTimer        messageConsumer = new AnimationTimer() {
//
//        @Override
//        public void handle(long now) {
//            final String message = messageQueue.poll();
//            if(message != null) {
//                appendText(message + "\n");
//            }
////            final List<String> messages = new ArrayList<>();
////            messageQueue.drainTo(messages);
////            messages.forEach(msg -> appendText(msg + "\n"));
//        }
//    };
//    
//    public QueuedMessageBox() {
//        this("");
//    }
//    
//    public QueuedMessageBox(String s) {
//        super(s);
//        setEditable(false);
//    }
//    
//    public void queueMessage(String message) {
//        messageQueue.offer(message);
//    }
//    
//    public void start() {
//        messageConsumer.start();
//    }
//    
//    public void stop() {
//        messageConsumer.stop();
//    }
//}
