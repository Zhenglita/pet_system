package com.example.pet_platform.service.Impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.pet_platform.controller.util.R;
import com.example.pet_platform.entity.*;
import com.example.pet_platform.mapper.*;
import com.example.pet_platform.service.ArticleService;
import com.example.pet_platform.service.FollowService;
import com.example.pet_platform.service.MessageService;
import com.example.pet_platform.service.UserService;
import com.example.pet_platform.util.JWTUtils;
import com.example.pet_platform.util.RedisGetUser;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    @Autowired
    private ArticleMapper articleMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private FollowMapper followMapper;
    @Resource
    private MessageMapper messageMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private CommentMapper commentMapper;


    @Override
    public Map<String, Object> getById(Integer id, HttpServletRequest request) {
        Article byId = articleMapper.selectById(id);
        String userid = RedisGetUser.getUserid(stringRedisTemplate,request);
        //判断是否关注与否
        QueryWrapper<Follow> qw = new QueryWrapper<>();
        qw.eq("b_uid", byId.getUid());
        qw.eq("f_uid", Integer.parseInt(userid));
        List<Follow> list = followMapper.selectList(qw);
        //获取该文章作者的头像
        Article article = articleMapper.selectById(id);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", article.getUid());
        User one = userMapper.selectOne(queryWrapper);
        article.setUimage(one.getAvatar());
        //获取审批该文章的管理员信息
        QueryWrapper<User> qw_admin = new QueryWrapper<>();
        qw_admin.eq("uid", article.getEnable_uid());
        User admin = userMapper.selectOne(qw_admin);
        article.setEnable_user(admin);
        Map<String, Object> result = new HashMap<>();
        result.put("article", article);
        result.put("size", list.size());
        return result;
    }

    @Override
    public List<Article> allByUserId(Integer userId) {
        return articleMapper.findAllByUserId(userId);
    }

    @Override
    public int update(Article article, HttpServletRequest request) {
        String userid = RedisGetUser.getUserid(stringRedisTemplate,request);
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("uid", Integer.parseInt(userid));
        User one = userMapper.selectOne(qw);
        Message message = new Message();
        message.setFrom_uid(Integer.parseInt(userid));
        message.setToUsername(article.getAuthor());
        message.setUsername(one.getUsername());
        message.setTo_uid(article.getUid());
        message.setEnable(true);
        message.setMessage(article.getAuthor() + "用户你好，你最近" + article.getDate() + "发布的文章" + article.getTitle() + "经过管理员的审核通过");
        messageMapper.insert(message);
        return articleMapper.updateById(article);
    }

    @Override
    public List<Article> getByAbleAndUserId(boolean enable, Integer userId) {
        return articleMapper.findAllByAbleAndUserId(enable, userId);
    }

    @Override
    public List<Article> getByAble(boolean enable) {
        return articleMapper.findAllByAble(enable);
    }

    @Override
    public List<Article> getTen() {
        String key = "cache:newTen";
        String newTen = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(newTen)) {
            JSONArray jsonArray = JSONUtil.parseArray(newTen);
            List<Article> list = JSONUtil.toList(jsonArray, Article.class);
            return list;
        }
        QueryWrapper<Article> qw = new QueryWrapper<>();
        qw.select().orderByDesc("date").last("limit 0,10");
        List<Article> list = articleMapper.selectList(qw);
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(list), 30, TimeUnit.MINUTES);
        return list;
    }

    @Override
    public Map<String, List<Comment>> getTenCommentArticle(Integer size) {
        Map<String, List<Comment>> map = new HashMap<>();
        String key = "cache:commentTen";
        String commentTen = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(commentTen)) {
            JSONArray jsonArray = JSONUtil.parseArray(commentTen);
            List<Comment> list = JSONUtil.toList(jsonArray, Comment.class);
            map.put("list", list);
            return map;
        }
        QueryWrapper<Comment> qw = new QueryWrapper<>();
        qw.select("foregin_id").groupBy("foregin_id");
        List<Comment> list = commentMapper.selectList(qw);
        for (Comment comment : list) {
            LambdaQueryWrapper<Comment> lqw1 = new LambdaQueryWrapper<>();
            lqw1.eq(true, Comment::getForegin_id, comment.getForegin_id());
            List<Comment> list1 = commentMapper.selectList(lqw1);
            LambdaQueryWrapper<Article> lqw2 = new LambdaQueryWrapper<>();
            lqw2.eq(true, Article::getAid, comment.getForegin_id());
            Article one = articleMapper.selectOne(lqw2);
            comment.setArticlename(one.getTitle());
            comment.setCount(list1.size());
            comment.setArticleUrl(one.getImage());
        }

//        for (int i = 0; i < list.size(); i++) {
//            int minIndex = i;
//            // 把当前遍历的数和后面所有的数进行比较，并记录下最小的数的下标
//            for (int j = i + 1; j < list.size(); j++) {
//                if (list.get(j).getCount() < list.get(minIndex).getCount()) {
//                    // 记录最小的数的下标
//                    minIndex = j;
//                }
//            }
//            // 如果最小的数和当前遍历的下标不一致，则交换
//            if (i != minIndex) {
//                Comment comment = list.get(i);
//                list.set(i,list.get(minIndex));
//                list.set(minIndex,comment);
//            }
//        }

        for (int i = 0; i < list.size(); i++) {
            int maxIndex = i;
            // 把当前遍历的数和后面所有的数进行比较，并记录下最小的数的下标
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(j).getCount() > list.get(maxIndex).getCount()) {
                    // 记录最小的数的下标
                    maxIndex = j;
                }
            }
            // 如果最小的数和当前遍历的下标不一致，则交换
            if (i != maxIndex) {
                Comment comment = list.get(i);
                list.set(i, list.get(maxIndex));
                list.set(maxIndex, comment);
            }
        }
        if (size == null) {
            if (list.size() > 10) {
                List<Comment> articles = list.subList(0, 10);
                try {
                    stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(articles), 30, TimeUnit.MINUTES);
                }catch (Exception e){
                    e.printStackTrace();
                }
                map.put("list", articles);
                return map;
            } else {
                List<Comment> articles = list.subList(0, list.size());
                QueryWrapper<Article> QW = new QueryWrapper<>();
                QW.select("aid");
                List<Article> ArticleList = articleMapper.selectList(QW);
                List<Integer> collect = ArticleList.stream().map(Article::getAid).collect(Collectors.toList());
                for (int i = collect.size() - 1; i >= 0; i--) {
                    Integer integer = collect.get(i);
                    boolean present = articles.stream().anyMatch(a -> a.getForegin_id().equals(integer));
                    if (present) {
                        collect.remove(collect.get(i));
                    }
                }
                while (articles.size() < 10) {
                    Random rand = new Random();
                    int n = rand.nextInt(collect.size());
                    Integer x = collect.get(n);
                    collect.remove(x);
                    //获取id为x的文章，将该文章的名字加入articles里
                    QueryWrapper<Article> qw_id = new QueryWrapper<>();
                    qw_id.eq("aid", x);
                    Article one = articleMapper.selectOne(qw_id);
                    Comment comment = new Comment();
                    comment.setForegin_id(x);
                    comment.setArticlename(one.getTitle());
                    comment.setArticleUrl(one.getImage());
                    articles.add(comment);
                }
                try {
                    stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(articles), 30, TimeUnit.MINUTES);
                }catch (Exception e){
                    e.printStackTrace();
                }

                map.put("list", articles);
                return map;
            }
        } else {
            if (list.size() > size) {
                List<Comment> articles = list.subList(0, size);
                try {
                    stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(articles), 30, TimeUnit.MINUTES);
                }catch (Exception e){
                 e.printStackTrace();
                }
                map.put("list", articles);
                return map;
            } else {
                List<Comment> articles = list.subList(0, list.size());
                QueryWrapper<Article> QW = new QueryWrapper<>();
                QW.select("aid");
                List<Article> ArticleList = articleMapper.selectList(QW);
                List<Integer> collect = ArticleList.stream().map(Article::getAid).collect(Collectors.toList());
                for (int i = collect.size() - 1; i >= 0; i--) {
                    Integer integer = collect.get(i);
                    boolean present = articles.stream().anyMatch(a -> a.getForegin_id().equals(integer));
                    if (present) {
                        collect.remove(collect.get(i));
                    }
                }
                while (articles.size() < size) {
                    Random rand = new Random();
                    int n = rand.nextInt(collect.size());
                    Integer x = collect.get(n);
                    collect.remove(x);
                    //获取id为x的文章，将该文章的名字加入articles里
                    QueryWrapper<Article> qw_id = new QueryWrapper<>();
                    qw_id.eq("aid", x);
                    Article one = articleMapper.selectOne(qw_id);
                    Comment comment = new Comment();
                    comment.setForegin_id(x);
                    comment.setArticlename(one.getTitle());
                    articles.add(comment);
                }
                try {
                    stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(articles), 30, TimeUnit.MINUTES);
                }catch (Exception e){
                    e.printStackTrace();
                }
                map.put("list", articles);
                return map;
            }
        }
    }

    @Override
    public List<Article> getTenRandomArticle() {
        List<Article> list = articleMapper.selectList(null);
        List<Integer> collect = list.stream().map(Article::getAid).collect(Collectors.toList());
        List<Article> articles = new ArrayList<>();
        while (articles.size() < 10) {
            Random rand = new Random();
            int n = rand.nextInt(collect.size());
            Integer x = collect.get(n);
            collect.remove(x);
            QueryWrapper<Article> qw_id = new QueryWrapper<>();
            qw_id.eq("aid", x);
            Article one = articleMapper.selectOne(qw_id);
            articles.add(one);
        }
        return articles;
    }

    @Override
    public List<Article> getTenMendArticle() {
        String key = "cache:mendTen";
        String mendTen = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(mendTen)) {
            JSONArray jsonArray = JSONUtil.parseArray(mendTen);
            List<Article> list = JSONUtil.toList(jsonArray, Article.class);
            return list;
        }
        List<Article> list = articleMapper.selectList(null);
        for (int i = 0; i < list.size(); i++) {
            int maxIndex = i;
            // 把当前遍历的数和后面所有的数进行比较，并记录下最小的数的下标
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(j).getEnable_rate() > list.get(maxIndex).getEnable_rate()) {
                    // 记录最小的数的下标
                    maxIndex = j;
                }
            }
            // 如果最小的数和当前遍历的下标不一致，则交换
            if (i != maxIndex) {
                Article article = list.get(i);
                list.set(i, list.get(maxIndex));
                list.set(maxIndex, article);
            }
        }
        if (list.size() > 10) {
            List<Article> articles = list.subList(0, 10);
            stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(articles), 30, TimeUnit.MINUTES);
            return articles;
        } else {
            List<Article> articles = list.subList(0, list.size());
            stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(articles), 30, TimeUnit.MINUTES);
            return articles;
        }
    }

    @Override
    public IPage<Article> getPage(int currentPage, int pageSize) {
        IPage<Article> page = new Page<>(currentPage, pageSize);
        articleMapper.selectPage(page, null);
        return page;
    }

    @Override
    public IPage<Article> getPage(int currentPage, int pageSize, Article article) {
        LambdaQueryWrapper<Article> lqw = new LambdaQueryWrapper<>();
        if (article != null) {
            lqw.like(Strings.isNotEmpty(article.getTitle()), Article::getTitle, article.getTitle());
            lqw.eq(true, Article::getEnable, true);
            lqw.eq(Strings.isNotEmpty(article.getType()), Article::getType, article.getType());
        }
        IPage<Article> page = new Page<>(currentPage, pageSize);
        articleMapper.selectPage(page, lqw);
        return page;
    }

}
