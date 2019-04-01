/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.shinnlove.wechatpay.participator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.shinnlove.wechatpay.util.NamedThreadFactory;
import com.shinnlove.wechatpay.util.PostUtil;

/**
 * 推荐搜索。
 *
 * @author shinnlove.jinsheng
 * @version $Id: RecommendHunter.java, v 0.1 2019-03-04 11:01 shinnlove.jinsheng Exp $$
 */
public class RecommendHunter {

    /** 广度优先搜索帖子线程池 */
    private final ExecutorService      searchExecutor = Executors
                                                          .newCachedThreadPool(new NamedThreadFactory(
                                                              "bfs-search"));

    /** 待广度优先搜索文章队列 */
    private BlockingQueue<String>      searchQueue;

    /** 已搜文章队列标记 */
    private final Map<String, Boolean> searchedURL    = new ConcurrentHashMap<>(3000);

    /** 待阅读队列 */
    private BlockingQueue<String>      readQueue;

    /** 已加入阅读队列标记 */
    private final Map<String, Boolean> readURL        = new ConcurrentHashMap<>(3000);

    /** 待阅读标记锁，这把锁是怕在等待加入队列期间发生狗桩问题，有1s的等待时间 */
    private final ReentrantLock        readLock       = new ReentrantLock();

    /**
     * 每次自定义都返回一个新的推荐搜索，这样可以同时启动多个。
     *
     * @return
     */
    public static RecommendHunter custom() {
        return new RecommendHunter();
    }

    /**
     * 广度搜索队列。
     *
     * @param queue
     * @return
     */
    public RecommendHunter setSearchQueue(final BlockingQueue queue) {
        searchQueue = queue;
        return this;
    }

    /**
     * 待阅读队列。
     *
     * @param queue
     * @return
     */
    public RecommendHunter setReadQueue(final BlockingQueue queue) {
        readQueue = queue;
        return this;
    }

    /**
     * 启动线程池进行搜索。
     *
     * @return  返回搜索句柄让JVM添加关闭钩子。
     */
    public RecommendHunter start() {

        // Assert两个队列引用不空

        final String domainName = PostUtil.getDomainName();

        // 推荐主目录搜索
        searchExecutor.submit(() -> {
            for (int i = 1; i <= 50; i++) {
                try {
                    String url = PostUtil.getRecommendCatelog(i);
                    PostUtil.searchCataLog(domainName, url, searchQueue);
                } catch (Exception e) {
                    System.out.println("主目录搜索遇到问题，第" + i + "页，ex=" + e.getMessage());
                }
            }
        });

        // 秀人高质量搜索
        searchExecutor.submit(() -> {
            for (int j = 1; j <= 128; j++) {
                try {
                    String url = PostUtil.getXiuRenCatelog(j);
                    PostUtil.searchCataLog(domainName, url, searchQueue);
                } catch (Exception e) {
                    System.out.println("秀人网高质量搜索遇到问题，第" + j + "页，ex=" + e.getMessage());
                }
            }
        });

        // 尤果网童颜..
        searchExecutor.submit(() -> {
            for (int k = 1; k <= 24; k++) {
                try {
                    String url = PostUtil.getUGirlsCatelog(k);
                    PostUtil.searchCataLog(domainName, url, searchQueue);

                } catch (Exception e) {
                    System.out.println("尤果网童颜...搜索遇到问题，第" + k + "页，ex=" + e.getMessage());
                }
            }
        });

        // 异步广度优先遍历
        for (int i = 0; i < 8; i++) {
            searchExecutor.submit(() -> BFSTravers(searchQueue));
        }

        return this;
    }

    /**
     * 关闭并停止搜索。
     */
    public void stop() {
        searchExecutor.shutdown();
    }

    /**
     * 从队列中读取一篇文章并搜索相关推荐。
     */
    public void BFSTravers(final BlockingQueue queue) {
        while (true) {
            Object o = queue.poll();
            if (o == null) {
                // 没有更多文章了
                PostUtil.sleepForFun(5);
                continue;
            }

            String url = String.valueOf(o);

            // 要读吗？
            if (!readURL.containsKey(url)) {
                try {
                    // 带锁等待1秒
                    readLock.lock();

                    if (!readURL.containsKey(url)) {
                        // 此时先做塞入已读队列，再打标
                        if (PostUtil.tryOfferWithLock(readQueue, url)) {
                            readURL.put(url, true);
                        }
                    }
                } finally {
                    readLock.unlock();
                }
            }

            if (searchedURL.containsKey(url)) {
                // 已搜过
                continue;
            }

            // 没搜过就去搜一下
            searchedURL.putIfAbsent(url, true);
            searchMore(url, queue);

        } // while
    }

    /**
     * 通过一篇文章找更多相关文章链接。
     *
     * @param url
     * @param queue 队列带泛参，加入毫无违和感
     */
    public void searchMore(String url, final BlockingQueue<String> queue) {
        final List<String> moreList = new ArrayList<>();

        PostUtil.request(url, doc -> {

            // 找到推荐帖
            Elements navs = doc.getElementsByClass("relates");
            Element nav = navs.get(0);
            Elements links = nav.getElementsByTag("a");

            // 这里请求网络后再做一个hashMap的幂等性、还允许超时时间30秒，如果网络卡，完全不能解决多线程阅读同一帖子狗桩问题
            // 并发问题-Case1：做事情前没有加锁、做到一半或者做完了才加锁

            // 持有这些解析元素等了10秒(还好不是一直等)
            // OOM Case1：持有元素进入忙等、导致OOM
            for (Element e : links) {
                String pageSuffix = e.attr("href");
                moreList.add(PostUtil.getDomainName() + pageSuffix);

                // 多线程-Case3：假设1根线程能拿到推荐的8篇帖子、但运行到9000队列满了，加不进去，
                // 这根线程将有8*10（平均1分钟）的等待时间浪费在——等待队列出队
                // 而队列怎么会被消费？=>某根搜索线程的1分钟等待过去了，searchMore做完了，回到while(true)开头再继续取出一篇文章来...
                // Warning：因此当9000队列满了后，若还有大量的文章被搜索到：
                // 这些搜索线程（目前有8根）将会各自浪费1分钟，然后加不进去队列无情抛弃结果，回头继续消费一个帖子，再又出来8个，只能加进去0~1个，再等待
                // 结论：当很多文章没有被搜索过、队列满时，搜索线程组几乎进入假死状态，(0/1)*8 的减少搜索帖子队列速度，同时阅读队列几乎被消费空
                // 代码中前期没有很好的利用网络带宽拉图片和IO读写磁盘存图片

            } // for

            return null;
        });

        for (String more : moreList) {
            // 新推荐加入检索队列
            PostUtil.offerQueueOrWait(queue, more);
        }
    }

}