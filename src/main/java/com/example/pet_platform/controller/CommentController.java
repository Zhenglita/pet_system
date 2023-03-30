package com.example.pet_platform.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.pet_platform.controller.util.CensorResult;
import com.example.pet_platform.controller.util.R;
import com.example.pet_platform.entity.Article;
import com.example.pet_platform.entity.Comment;
import java.lang.reflect.Field;

import com.example.pet_platform.entity.User;
import com.example.pet_platform.mapper.CommentMapper;
import com.example.pet_platform.mapper.UserMapper;
import com.example.pet_platform.service.ArticleService;
import com.example.pet_platform.service.BaiduContentCensorService;
import com.example.pet_platform.service.CommentService;
import com.example.pet_platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
//import sun.dc.pr.PRError;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/comments")
@CrossOrigin
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentMapper commentMapper;
    @Resource
    private ArticleService articleService;
    @Resource
    private BaiduContentCensorService baiduContentCensorService;
    @GetMapping
    public R getall(){
        return new R(true,commentService.list());
    }
    @GetMapping("/admin")
    public R getSensitiveAll(){
        List<Comment> list = commentService.list();
        for(Comment comment:list){
            Article byId = articleService.getById(comment.getForegin_id());
            comment.setArticlename(byId.getTitle());
        }
        List<Comment> collect = list.stream().filter(comment -> !"正常评论".equals(comment.getContent_type())).collect(Collectors.toList());
        return new R(true,collect);
    }
    @PostMapping
    public R add(@RequestBody Comment comment) throws IllegalAccessException {
        Article byId = articleService.getById(comment.getForegin_id());
        byId.setComment(byId.getComment()+1);
        articleService.updateById(byId);
        CensorResult commonTextCensorResult = baiduContentCensorService.getCommonTextCensorResult(comment.getContent());
        System.err.println(commonTextCensorResult.getTextCensorJson());
        Map map = JSONObject.parseObject(JSONObject.toJSONString(JSONObject.parseObject(commonTextCensorResult.getTextCensorJson())), Map.class);
        if (map.get("data")!=null){
        List list =(List) map.get("data");
        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(list.get(0));
            int length = comment.getContent().length();
            comment.setReal_content(comment.getContent());
            StringBuilder x= new StringBuilder();
            for(int i=0;i<length;i++){
                x.append("*");
            }
            comment.setContent(x.toString());
            comment.setContent_type(jsonObject.get("msg").toString());
        }else {
            comment.setReal_content(comment.getContent());
            comment.setContent_type("正常评论");
        }

        return new R(true,commentService.save(comment));
    }
    @GetMapping("/s/{foreginId}")
    public R getById(@PathVariable Integer foreginId){
        List<Comment> list=commentMapper.findAllByForeginId(foreginId);
        List<Comment> collect = list.stream().filter(comment -> comment.getPid() == null).collect(Collectors.toList());
        for(Comment comment:collect){
            comment.setChildren(list.stream().filter(m->comment.getId().equals(m.getPid())).collect(Collectors.toList()));
        }
        return new R(true,collect);
    }
    @DeleteMapping("{id}")
    public R deleteById(@PathVariable Integer id){
        Comment comment = commentService.getById(id);
        Article byId = articleService.getById(comment.getForegin_id());
        byId.setComment(byId.getComment()-1);
        articleService.updateById(byId);
        return new R(true,commentService.removeById(id));
    }
    @GetMapping("/a/{uid}")
    public R getByUserId(@PathVariable Integer uid){
        return new R(true, commentMapper.findAllByUserId(uid));
    }
}
