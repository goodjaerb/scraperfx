///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.goodjaerb.scraperfx.datasource.cached;
//
//import java.nio.file.Path;
//import java.util.EnumMap;
//import java.util.Map;
//
///**
// *
// * @author goodjaerb <goodjaerb@gmail.com>
// */
//public abstract class CachedDataPlan {
//    public enum Operation {
//        SYSTEM_NAMES,
//        SYSTEM_GAME_NAMES,
//        META_DATA,
//        VIDEO_LINKS;
//    }
//    
//    public class OperationProperties<T> {
//        private Path cachePath;
//        private String url;
//        private Map<String, String> params;
//        private Class<T> dataClass;
//
//        public Path getCachePath() {
//            return cachePath;
//        }
//
//        public void setCachePath(Path cachePath) {
//            this.cachePath = cachePath;
//        }
//
//        public String getUrl() {
//            return url;
//        }
//
//        public void setUrl(String url) {
//            this.url = url;
//        }
//
//        public Map<String, String> getParams() {
//            return params;
//        }
//
//        public void setParams(Map<String, String> params) {
//            this.params = params;
//        }
//
//        public Class<T> getDataClass() {
//            return dataClass;
//        }
//
//        public void setDataClass(Class<T> dataClass) {
//            this.dataClass = dataClass;
//        }
//    }
//
//    private final Map<Operation, OperationProperties<?>> cacheOperationsMap = new EnumMap<>(Operation.class);
//    
//    private boolean refreshCacheFromSource;
//    
//    public CachedDataPlan(boolean refreshCacheFromSource) {
//        this.refreshCacheFromSource = refreshCacheFromSource;
//    }
//    
//    public CachedDataPlan() {
//        this(false);
//    }
//    
//    public abstract boolean isCachedDataAvailable(Operation op);
//    
//    public void registerCacheOperation(Operation op, OperationProperties props) {
//        cacheOperationsMap.put(op, props);
//    }
//    
//    public boolean refreshCacheFromSource() {
//        return refreshCacheFromSource;
//    }
//    
//    public void setRefreshCacheFromSource(boolean b) {
//        refreshCacheFromSource = b;
//    }
//}
