package com.lxw.videoworld.task;

import com.lxw.videoworld.dao.YgdySourceDao;
import com.lxw.videoworld.domain.Source;
import com.lxw.videoworld.spider.*;
import com.lxw.videoworld.utils.URLUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Spider;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lxw9047 on 2017/4/20.
 */
@Component("sourceYgdyTask")
public class YgdySourceTask {
    @Autowired
    private YgdyHomePagePipeline ygdyHomePagePipeline;
    @Autowired
    private YgdyHotListPipeline ygdyHotListPipeline;
    @Autowired
    private YgdyClassicalListPipeline ygdyClassicalListPipeline;
    @Autowired
    private YgdySourceDao ygdySourceDao;


    // 每天凌晨4点执行
    @Scheduled(cron = "0 04 00 * * ?")
    public void getYgdySource() {
        // 阳光电影首页
//        Spider.create(new YgdyHomePageProcessor()).thread(1)
//                .addUrl(URLUtil.URL_YGDY_HOME_PAGE)
//                .addPipeline(ygdyHomePagePipeline)
//                .run();
        // 阳光电影排行
//        Spider.create(new YgdyHotListProcessor()).thread(2)
//                .addUrl(URLUtil.URL_YGDY_HOME_DY)
//                .addPipeline(ygdyHomePagePipeline)
//                .addPipeline(ygdyHotListPipeline)
//                .run();
        // 阳光电影高分经典
        Spider.create(new YgdyClassicalListProcessor()).thread(2)
                .addUrl(URLUtil.URL_YGDY_GFJDDY)
                .addPipeline(ygdyHomePagePipeline)
                .addPipeline(ygdyClassicalListPipeline)
                .run();
        // 阳光电影菜单
//        Spider.create(ygdyMenuPageProcessor).thread(5)
//                .addUrl(URLUtil.URL_YGDY_ZXDY)
//                .run();

        // 阳光电影详情
        final List<Source> sources = ygdySourceDao.findAllNoDetail();
        if(sources != null && sources.size() > 0){
            if(sources.size() == 1){
                Spider.create(new YgdySourceDetailProcessor()).thread(1)
                        .addUrl(sources.get(0).getUrl())
                        .run();
            }else{
                List<String> urlList = new ArrayList<>();
                for(int i = 1; i < sources.size(); i++){
                    urlList.add(sources.get(i).getUrl());
                }
                Spider.create(new YgdySourceDetailProcessor(){
                    @Override
                    public void addTargetRequest(Page page) {
                        super.addTargetRequest(page);
                        page.addTargetRequests(urlList);
                    }
                }).thread(50)
                        .addUrl(sources.get(0).getUrl())
                        .run();
            }
        }
    }
}