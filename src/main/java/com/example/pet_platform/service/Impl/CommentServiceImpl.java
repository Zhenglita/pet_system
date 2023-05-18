package com.example.pet_platform.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.pet_platform.controller.util.CensorResult;
import com.example.pet_platform.controller.util.R;
import com.example.pet_platform.entity.Article;
import com.example.pet_platform.entity.Comment;
import com.example.pet_platform.mapper.ArticleMapper;
import com.example.pet_platform.mapper.CommentMapper;
import com.example.pet_platform.service.BaiduContentCensorService;
import com.example.pet_platform.service.CommentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private BaiduContentCensorService baiduContentCensorService;

    @Override
    public R getSensitiveAll() {
        List<Comment> list =commentMapper.selectList(null);
        for (Comment comment : list) {
            Article byId =articleMapper.selectById(comment.getForegin_id());
            comment.setArticlename(byId.getTitle());
        }
        List<Comment> collect = list.stream().filter(comment -> !"正常评论".equals(comment.getContent_type())).collect(Collectors.toList());
        return new R(true, collect);
    }

    @Override
    public R add(Comment comment) {
        Article byId =articleMapper.selectById(comment.getForegin_id());
        byId.setComment(byId.getComment() + 1);
        articleMapper.updateById(byId);
        CensorResult commonTextCensorResult = baiduContentCensorService.getCommonTextCensorResult(comment.getContent());
        System.err.println(commonTextCensorResult.getTextCensorJson());
        Map map = JSONObject.parseObject(JSONObject.toJSONString(JSONObject.parseObject(commonTextCensorResult.getTextCensorJson())), Map.class);
        if (map.get("data") != null) {
            List list = (List) map.get("data");
            JSONObject jsonObject = (JSONObject) JSONObject.toJSON(list.get(0));
            int length = comment.getContent().length();
            comment.setReal_content(comment.getContent());
            StringBuilder x = new StringBuilder();
            for (int i = 0; i < length; i++) {
                x.append("*");
            }
            comment.setContent(x.toString());
            comment.setContent_type(jsonObject.get("msg").toString());
        } else {
            comment.setReal_content(comment.getContent());
            comment.setContent_type("正常评论");
        }
        return new R(true,commentMapper.insert(comment));
    }

    @Override
    public R getByIdOwn(Integer foreginId) {
        List<Comment> list = commentMapper.findAllByForeginId(foreginId);
        List<Comment> collect = list.stream().filter(comment -> comment.getPid() == null).collect(Collectors.toList());
        for (Comment comment : collect) {
            comment.setChildren(list.stream().filter(m -> comment.getId().equals(m.getPid())).collect(Collectors.toList()));
        }
        return new R(true, collect);
    }

    @Override
    public R deleteByIdOwn(Integer id) {
        Comment comment = commentMapper.selectById(id);
        Article byId = articleMapper.selectById(comment.getForegin_id());
        byId.setComment(byId.getComment() - 1);
        articleMapper.updateById(byId);
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pid", comment.getId());
        List<Comment> list =commentMapper.selectList(queryWrapper);
        for (Comment comment1 : list) {
            commentMapper.deleteById(comment1.getId());
        }
        return new R(true, commentMapper.deleteById(id));
    }

    @Override
    public R getByUserId(Integer uid) {
        List<Comment> allByUserId = commentMapper.findAllByUserId(uid);
        for (Comment comment : allByUserId) {
            QueryWrapper<Comment> lqw = new QueryWrapper<>();
            lqw.eq("pid", comment.getId());
            comment.setCount(commentMapper.selectCount(lqw));
        }

        return new R(true, allByUserId);
    }

    @Override
    public R getTenNewComment() {
        QueryWrapper<Comment> qw = new QueryWrapper<>();
        qw.select().orderByDesc("createtime");
        qw.eq("content_type", "正常评论");
        List<Comment> list =commentMapper.selectList(qw);
        if (list.size() > 10) {
            List<Comment> comments = list.subList(0, 10);
            return new R(true, comments);
        } else {
            List<Comment> comments = list.subList(0, list.size());
            return new R(true, comments);
        }
    }

    @Override
    public R getTheArticleNewComment(Integer id) {
        QueryWrapper<Comment> qw = new QueryWrapper<>();
        qw.select().orderByDesc("createtime");
        qw.eq("content_type", "正常评论");
        qw.eq("foregin_id", id);
        List<Comment> list =commentMapper.selectList(qw);
        if (list.size() > 5) {
            List<Comment> comments = list.subList(0, 5);
            return new R(true, comments);
        } else {
            List<Comment> comments = list.subList(0, list.size());
            return new R(true, comments);
        }
    }
}
