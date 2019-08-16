/**
 * FileName: ProfileController
 * Author:   xjh
 * Date:     2019-08-16 15:11
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.seagold.community.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.seagold.community.entity.Question;
import com.seagold.community.entity.User;
import com.seagold.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author xjh
 * @create 2019-08-16
 * @since 1.0.0
 */
@Controller
public class ProfileController {

    @Autowired
    private QuestionService questionService;

    /**
     * 用户查询自己发起的提问及查询自己的回复
     * @param action
     * @param session
     * @param page
     * @param size
     * @param model
     * @return
     */
    @GetMapping("/profile/{action}")
    public String profile(@PathVariable(name = "action") String action,
                          HttpSession session,
                          @RequestParam(name = "page", defaultValue = "1") Integer page,
                          @RequestParam(name = "size", defaultValue = "10") Integer size,
                          Model model){

        User user = (User) session.getAttribute("user");
        if(user == null){
            return "redirect:/";
        }

        if ("questions".equals(action)){
            model.addAttribute("section", action);
            model.addAttribute("sectionName", "我的提问");
            PageHelper.startPage(page, size);
            List<Question> all = questionService.findAllByCreator(user.getId());
            PageInfo<Question> pageInfo = new PageInfo<>(all);
            model.addAttribute("questions", pageInfo);
        }else if ("replies".equals(action)){
            model.addAttribute("section", "replies");
            model.addAttribute("sectionName", "我的回复");
        }


        return "profile";
    }
}