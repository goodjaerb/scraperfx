///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.goodjaerb.scraperfx.datasource;
//
//import com.goodjaerb.scraperfx.datasource.cached.CachedDataPlan;
//import com.goodjaerb.scraperfx.settings.Game;
//import com.goodjaerb.scraperfx.settings.MetaData;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
///**
// *
// * @author goodjaerb <goodjaerb@gmail.com>
// */
//public final class CachedDataSource implements DataSource {
//    
//    private final CachingDataSource source;
//    private final CachedDataPlan plan;
//    
//    public CachedDataSource(CachingDataSource source) {
//        this.source = source;
//        this.plan = source.getCachedDataPlan();
//        if(this.plan == null) {
//            throw new UnsupportedOperationException();
//        }
//    }
//
//    @Override
//    public String getSourceName() {
//        return source.getSourceName() + " (Cached)";
//    }
//    
//    @Override
//    public List<String> getSystemNames() {
//        if(!plan.isCachedDataAvailable(CachedDataPlan.Operation.SYSTEM_NAMES) || plan.refreshCacheFromSource()) {
//            Logger.getLogger(CachedDataSource.class.getName()).log(Level.INFO, "Initializing local " + plan.getDataClass().getName() + "...");
//            
//            final Path cachePath = plan.getCachePath();
//            if(!Files.exists(cachePath)) {
//                Logger.getLogger(CachedDataSource.class.getName()).log(Level.INFO, "Creating local file ''{0}''...", platformsFilePath.toString());
//                try {
//                    Files.createDirectories(platformsFilePath.getParent());
//                    Files.createFile(platformsFilePath);
//                } catch (IOException ex) {
//                    Logger.getLogger(CachedDataSource.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//            
//            plan.loadCachedData();
//            if(!plan.isCachedDataAvailable() || plan.refreshCacheFromSource()) {
//                plan.getAndCacheData();
//            }
//        }
//        
//        if(plan.isCachedDataAvailable()) {
//            return plan.CachedOperation.SYSTEM_NAMES.get();
//        }
//        return null;
//    }
//
//    @Override
//    public List<String> getSystemGameNames(String systemName) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public MetaData getMetaData(String systemName, Game game) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//    
//}
