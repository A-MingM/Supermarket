package cn.smbms.Controller;

import cn.smbms.dao.BaseDao;
import cn.smbms.pojo.Role;
import cn.smbms.pojo.User;
import cn.smbms.service.role.RoleService;
import cn.smbms.service.role.RoleServiceImpl;
import cn.smbms.service.user.UserService;
import cn.smbms.service.user.UserServiceImpl;
import cn.smbms.tools.Constants;
import cn.smbms.tools.PageSupport;
import com.alibaba.fastjson.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.*;

@Controller
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private RoleService roleService;

    /**
     * 跳转登录页面方法
     *
     * @return
     */
    @RequestMapping("/login.html")
    public String login() {
        return "login";
    }

    /**
     * 登出页面方法
     * redirect:指示符，表重定向
     *
     * @return
     */
    @RequestMapping("/logout.html")
    public String logout(HttpSession session) {
        session.removeAttribute(Constants.USER_SESSION);//清空session
        return "redirect:/user/login.html";
    }

    @RequestMapping(value = "/login.html", method = RequestMethod.POST)
    public String doLogin(String userCode, String userPassword, HttpSession session, HttpServletRequest request) {
        User user = userService.login(userCode, userPassword);
        if (user != null) {
            session.setAttribute(Constants.USER_SESSION, user);
            return "redirect:/user/frame.html";
        } else {
            request.setAttribute("error", "用户名或密码不符!请重新登录!");
            return "login";
        }
    }

    /**
     * 验证用户是否登录
     * 权限操作
     *
     * @return
     */
    @RequestMapping("/frame.html")
    public String frame(HttpSession session) {
        User user = (User) session.getAttribute(Constants.USER_SESSION);
        if (user == null) {
            return "login";
        }
        return "frame";
    }

    /* *//**
     * 局部异常，只能捕获本类的异常
     * @param e
     * @param session
     * @return
     *//*
    @ExceptionHandler(value = RuntimeException.class)
    public String sysError(RuntimeException e,HttpSession session){
        session.setAttribute("e",e.getMessage());
        return "error";
    }
*/

    /**
     * 管理用户界面的分页效果
     *
     * @param model
     * @param queryname
     * @param userRole
     * @param pageIndex
     * @return
     */
    @RequestMapping("/userlist.html")
    public String main(Model model, String queryname, @RequestParam(value = "queryUserRole", defaultValue = "0") Integer userRole,
                       @RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex) {
        //查询用户列表
        List<User> userList = null;
        //设置页面容量
        int pageSize = Constants.pageSize;
        //当前页码
        int currentPageNo = 1;

     /*   System.out.println("queryUserName servlet--------" + queryname);
        System.out.println("queryUserRole servlet--------" + userRole);
        System.out.println("query pageIndex--------- > " + pageIndex);*/
        //总数量（表）
        int totalCount = userService.getUserCount(queryname, userRole);
        //总页数
        PageSupport pages = new PageSupport();
        pages.setCurrentPageNo(pageIndex);
        pages.setPageSize(pageSize);
        pages.setTotalCount(totalCount);

        int totalPageCount = pages.getTotalPageCount();

        //控制首页和尾页
        if (pageIndex < 1) {
            pageIndex = 1;
        } else if (pageIndex > totalPageCount) {
            pageIndex = totalPageCount;
        }
        userList = userService.getUserList(queryname, userRole, pageIndex, pageSize);
        model.addAttribute("userList", userList);
        List<Role> roleList = null;
        roleList = roleService.getRoleList();
        model.addAttribute("roleList", roleList);
        model.addAttribute("queryUserName", queryname);
        model.addAttribute("queryUserRole", userRole);
        model.addAttribute("totalPageCount", totalPageCount);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("currentPageNo", pageIndex);
        return "userlist";
    }

    /**
     * 删除用户
     *
     * @return
     */
    @RequestMapping("/delUser.html")
    public String deleteUser(Integer uid, Model model, HttpServletResponse response) {

        HashMap<String, String> resultMap = new HashMap<String, String>();
        if (uid <= 0) {
            resultMap.put("delResult", "notexist");
        } else {
            if (userService.deleteUserById(uid)) {
                resultMap.put("delResult", "true");
            } else {
                resultMap.put("delResult", "false");
            }
        }

        //把resultMap转换成json对象输出
        response.setContentType("application/json");
        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.write(JSONArray.toJSONString(resultMap));
        out.flush();
        out.close();
        return "userlist";
    }

    /**
     * 跳转添加页面
     *
     * @return
     */
    @RequestMapping("/addUser.html")
    public String addUser() {
        return "useradd";
    }

    /* *//**
     * 添加
     * @param user
     * @param session
     * @return
     *//*
    @RequestMapping(value = "/addUser.html",method = RequestMethod.POST)
    public String saveUser(User user,HttpSession session){
        user.setCreationDate(new Date());//创建时间
        User userLogin=(User)session.getAttribute(Constants.USER_SESSION);
        user.setCreatedBy(userLogin.getId());
        if(userService.add(user)){
            return "redirect:/user/userlist.html";
        }
        return "user/useradd";
    }*/

    /**
     * 传值获取用户
     *
     * @return
     */
    @RequestMapping("/modify.html")
    public String modify(String uid, Model model) {
        User user = userService.getUserById(uid);
        model.addAttribute("user", user);
        return "usermodify";
    }

    /**
     * 修改用户
     *
     * @return
     */
    @RequestMapping(value = "/modify.html", method = RequestMethod.POST)
    public String savemodify(User user, HttpSession session) {
        user.setCreationDate(new Date());//创建时间
        //创建者
        User userLogin = (User) session.getAttribute(Constants.USER_SESSION);
        user.setCreatedBy(userLogin.getId());
        if (userService.modify(user)) {
            return "redirect:/user/userlist.html";
        }
        return "useradd";
    }

    /* *//**
     * 单文件·上传
     * @param user
     * @param
     * @param attan
     * @return
     *//*
    @RequestMapping(value = "/addUser.html",method = RequestMethod.POST)
    public String saveUser(User user, HttpServletRequest request, @RequestParam(value = "attan",required = false)MultipartFile attan){
       String picPath="";//图片路径
       if(attan.isEmpty()){//判断是否有文件上传
           //File.separator文件的系统自适应分隔符
           String path=request.getServletContext().getRealPath("/statics")+ File.separator+"/fileUpload";//文件上传的路径
           System.out.println("路径===============>"+path);
           if(attan.getSize()>5000000){
            request.setAttribute("error","文件大小不符!");
            return "useradd";
        }
        List<String> suffexs= Arrays.asList(new String[]{".jpg",".png",".jpeg",".gif"});
        String oldFileName=attan.getOriginalFilename();//获取文件名
           //获取文件后缀
        String suffex=oldFileName.substring(oldFileName.lastIndexOf("."),oldFileName.length());
           System.out.println("文件后缀名是:"+suffex);
           if(!suffexs.contains(suffex)){
               request.setAttribute("error","文件类型错误，上传失败!");
               return "useradd";
           }
           //文件重命名，1.解决文件重命名 2.解决乱码
           //重命名规则是：当前时间的毫秒+1000000随机数
           String newFileName=System.currentTimeMillis()+""+new Random().nextInt(100000)+suffex;
            File file=new File(path,newFileName);
            if(!file.exists()){
                file.mkdir();
            }
           try {
               attan.transferTo(file);
           } catch (IOException e) {
               e.printStackTrace();
               request.setAttribute("error","上传失败!");
               return "useradd";
           }
            picPath=path+File.separator+newFileName;//图片路径
       }

        user.setPicPath(picPath);
        user.setCreationDate(new Date());//创建时间
        //创建者
        User userLogin=(User)request.getAttribute(Constants.USER_SESSION);
        user.setCreatedBy(userLogin.getId());
        if(userService.add(user)){
            return "redirect:/user/userlist.html";
        }
        return "user/useradd";
    }*/

    /**
     * 查看
     *
     * @param id
     * @param model
     * @return
     */
    /*@RequestMapping(value = "/view.html/{id}")
    public String view(@PathVariable String id, Model model) {
        User user = userService.getUserById(id);
       *//* String picPath=user.getPicPath();
        user.setPicPath(picPath.substring(picPath.lastIndexOf("\\")+1));*//*
        model.addAttribute("user", user);
        return "userview";
    }*/

    /**
     * 查看
     *
     * @param id
     * @param model
     * @return
     * produces={"appliction/json;charset=UTF-8"} 解决中文乱码
     */
    @RequestMapping(value = "/view",produces = {"appliction/json;charset=UTF-8"})
    @ResponseBody
    public Object view(String id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return JSONArray.toJSONString(user);
    }


    /**
     * 多文件上传
     *
     * @return
     */
    @RequestMapping(value = "/addUser.html", method = RequestMethod.POST)
    public String saveUser(User user, HttpServletRequest request,
                           @RequestParam(value = "attan", required = false) MultipartFile attan,
                           @RequestParam(value = "attan_work", required = false) MultipartFile attan_work) {
        String picPath =upLoadFile(request,attan); //图片路径
        String workPicPath =upLoadFile(request,attan_work); //工作照
        if (picPath==null||workPicPath==null){
            return "useradd";
        }
        user.setPicPath(picPath);
        user.setWorkPicPach(workPicPath);
        user.setCreationDate(new Date());//创建时间
        //创建者
        User user_login = (User) request.getSession().getAttribute(Constants.USER_SESSION);
        user.setCreatedBy(user_login.getId());
        if (userService.add(user)) {
            return "redirect:/user/userlist.html";
        }
        return "useradd";
    }

    /**
     * 文件上传方法提取
     *
     * @param request
     * @param attan
     * @return null上传失败
     */
    public String upLoadFile(HttpServletRequest request, MultipartFile attan) {
        String picPath="";
        if (!attan.isEmpty()) { //判断是否有上传文件
            String path = request.getServletContext().getRealPath("/statics" + File.separator + "fileUpload");//文件上传的路径
            System.out.println("路径；“========" + path);
            if (attan.getSize() > 5000000) {//判断文件大小
                request.setAttribute("error", "图片超出文件大小!");
                return null;
            }
            //==========if分割线=========
            List<String> suffexs = Arrays.asList(new String[]{".jpg", ".png", "jpeg", ".gif"});
            String oldFileName = attan.getOriginalFilename();//获取文件名
            //获取后缀
            String suffex = oldFileName.substring(oldFileName.lastIndexOf("."), oldFileName.length());
            System.out.println("后缀；“========" + suffex);
            if (!suffexs.contains(suffex)) {
                request.setAttribute("error", "文件类型错误,上传失败!");
                return null;
            }
            //==========if分割线=========
            //文件的重命名  1.解决重命名问题。2.解决中文乱码
            //重命名的规则是:  当前时间的毫秒+1000000的随机数
            String newFileName = System.currentTimeMillis() + "" + new Random().nextInt(1000000) + suffex;
            File file = new File(path, newFileName);
            if (!file.exists()) {
                file.mkdirs();
            }
            try {
                attan.transferTo(file);
            } catch (IOException e) {
                e.printStackTrace();
                request.setAttribute("error", "上传失败!");
                return null;
            }
            picPath=newFileName;
        }//大if括号
        return picPath;//图片路径
    }

    /**
     * ajax验证账户
     * @param userCode
     * @return
     */
    @RequestMapping("/isExits")
    @ResponseBody
    public Object isExits(String userCode){
        User user=userService.selectUserCodeExist(userCode);
        Map<String,Object> map=new HashMap<>();
        if (user!=null){
            map.put("userCode","exist");
        }else{
            map.put("userCode","noexist");
        }
        return  JSONArray.toJSONString(map);
    }



}



