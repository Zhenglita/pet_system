package com.example.pet_platform.controller;

import cn.hutool.json.JSONObject;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.pet_platform.controller.util.R;
import com.example.pet_platform.controller.util.TokenUtils;
import com.example.pet_platform.entity.*;
import com.example.pet_platform.mapper.ArticleMapper;
import com.example.pet_platform.mapper.CommentMapper;
import com.example.pet_platform.service.*;

import com.example.pet_platform.util.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/articles")
@CrossOrigin
public class ArticleController {
    @Resource
    private FollowService followService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private ArticleMapper articleMapper;
    @Resource
    private CommentService commentService;
    @Resource
    private UserService userService;
    @Resource
    private MessageService messageService;
//    获取所有文章
    @GetMapping
    public R getAll(){
        return   new R(true,articleService.list());
    }
//    查找指定id的文章
    @GetMapping("/s/{id}")
    public R getById(@PathVariable Integer id, HttpServletRequest request){
        Article byId = articleService.getById(id);
        String token = request.getHeader("Authorization");
        DecodedJWT verify = JWTUtils.verify(token);
        String userid = verify.getClaim("userid").asString();
        QueryWrapper<Follow> qw=new QueryWrapper<>();
        qw.eq("b_uid",byId.getUid());
        qw.eq("f_uid",Integer.parseInt(userid));
        List<Follow> list = followService.list(qw);
        if (list.size()>0){
            return  new R(true,articleService.getById(id),"已关注");
        }
        return  new R(true,articleService.getById(id),"未关注");
    }
//    @GetMapping("/{currentPage}/{pageSize}")
//    public R getPage(@PathVariable int currentPage, @PathVariable int pageSize){
//        IPage<Article> page=articleService.getPage(currentPage,pageSize);
//        if (currentPage>page.getPages()){
//            page=articleService.getPage((int)page.getPages(),pageSize);
//        }
//        return new R(true,page);
//    }
//    分页查询
//    @Cacheable(value = "R",key="'getPage'")
    @GetMapping("/{currentPage}/{pageSize}")
    public R getPage(@PathVariable int currentPage,@PathVariable int pageSize,Article article){
        IPage<Article> page=articleService.getPage(currentPage,pageSize,article);
        if (currentPage>page.getPages()){
            page=articleService.getPage((int)page.getPages(),pageSize,article);
             }
                return new R(true,page);
            }
//            查找指定文章的作者名字
    @GetMapping("/a/{userId}")
    public R getByUserId(@PathVariable Integer userId){
        return  new R(true,articleMapper.findAllByUserId(userId));
    }
//    保存上传文章
    @PostMapping
    public  R save(@RequestBody Article article,HttpServletRequest request){
        String token = request.getHeader("Authorization");
        DecodedJWT verify = JWTUtils.verify(token);
        String userid = verify.getClaim("userid").asString();
        QueryWrapper<User> qw=new QueryWrapper<>();
        qw.eq("uid",Integer.parseInt(userid));
        User one = userService.getOne(qw);
        if (one.getRole().equals("ROLE_READER")){
            return new R(false);
        }
        return new R(true,articleService.save(article));}

    @DeleteMapping("{id}")
    public R delete(@PathVariable Integer id){
        return  new R(true,articleService.removeById(id));
    }
    @PutMapping
    public R update(@RequestBody Article article,HttpServletRequest request){
        String token = request.getHeader("Authorization");
        DecodedJWT verify = JWTUtils.verify(token);
        String userid = verify.getClaim("userid").asString();
        QueryWrapper<User> qw=new QueryWrapper<>();
        qw.eq("uid",Integer.parseInt(userid));
        User one = userService.getOne(qw);
        Message message = new Message();
        message.setFrom_uid(Integer.parseInt(userid));
        message.setToUsername(article.getAuthor());
        message.setUsername(one.getUsername());
        message.setTo_uid(article.getUid());
        message.setEnable(true);
        message.setMessage(article.getAuthor()+"用户你好，你最近"+article.getDate()+"发布的文章"+article.getTitle()+"经过管理员的审核通过");
        messageService.save(message);

        return new R(true,articleService.updateById(article));
    }
    //用户查看自己发表文章是否被批准
    @GetMapping("/e/{enable}/{userId}")
    public R getByAbleAndUserId(@PathVariable boolean enable,@PathVariable  Integer userId){
        return  new R(true,articleMapper.findAllByAbleAndUserId(enable,userId));
    }
    @GetMapping("/admin/{enable}")
    public R getByAble(@PathVariable boolean enable){
        return  new R(true,articleMapper.findAllByAble(enable));
    }

    //首页最新文章
    @GetMapping("/home/new")
    public R getTen(){
        QueryWrapper<Article> qw=new QueryWrapper<>();
        qw.select().orderByDesc("date").last("limit 0,10");
        return new R (true,articleService.list(qw));
    }
    //热门文章
    @GetMapping("/home/comment")
    public R getTenCommentArticle(){
        QueryWrapper<Comment> qw=new QueryWrapper<>();
        qw.select("foregin_id").groupBy("foregin_id");
        List<Comment> list = commentService.list(qw);
        for (Comment comment:list){
            LambdaQueryWrapper<Comment> lqw1=new LambdaQueryWrapper<>();
            lqw1.eq(true,Comment::getForegin_id,comment.getForegin_id());
            List<Comment> list1 = commentService.list(lqw1);
            LambdaQueryWrapper<Article> lqw2=new LambdaQueryWrapper<>();
            lqw2.eq(true,Article::getAid,comment.getForegin_id());
            Article one = articleService.getOne(lqw2);
            comment.setArticlename(one.getTitle());
            comment.setCount(list1.size());
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
                list.set(i,list.get(maxIndex));
                list.set(maxIndex,comment);
            }
        }
        if (list.size()>10){
            List<Comment> articles=list.subList(0,10);
            return new R (true,articles);
        }else {
            List<Comment> articles=list.subList(0,list.size());
            return new R (true,articles);
        }
//        return new R (true,articles);
    }
}