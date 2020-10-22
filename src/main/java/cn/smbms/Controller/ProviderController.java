package cn.smbms.Controller;

import cn.smbms.dao.BaseDao;
import cn.smbms.pojo.Bill;
import cn.smbms.pojo.Provider;
import cn.smbms.pojo.User;
import cn.smbms.service.provider.ProviderService;
import cn.smbms.service.provider.ProviderServiceImpl;
import cn.smbms.tools.Constants;
import com.mysql.jdbc.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/pro")
public class ProviderController {

    @Resource
    private ProviderService providerService;

    /**
     * 显示所有商家信息
     * @param queryProName
     * @param queryProCode
     * @param model
     * @return
     */
    @RequestMapping(value = "/provider.html")
    public String query(String queryProName, String queryProCode, Model model){
        if(StringUtils.isNullOrEmpty(queryProName)){
            queryProName = "";
        }
        if(StringUtils.isNullOrEmpty(queryProCode)){
            queryProCode = "";
        }
        List<Provider> providerList = new ArrayList<Provider>();
        providerList = providerService.getProviderList(queryProName,queryProCode);
        model.addAttribute("providerList", providerList);
        model.addAttribute("queryProName", queryProName);
        model.addAttribute("queryProCode", queryProCode);
        return "providerlist";
    }

    /**
     * 添加页面跳转
     * @return
     */
    @RequestMapping("/provideradd.html")
    public String addprovider(){
        return "provideradd";
    }

    /**
     * 添加商家
     * @param provider
     * @return
     */
    @RequestMapping(value = "/provideradd.html", method = RequestMethod.POST)
    public String add(HttpSession session, Provider provider){
        provider.setCreationDate(new Date());//创建时间
        provider.setCreatedBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());
        if (providerService.add(provider)) {
            return "redirect:/pro/provider.html";
        }
        return "provideradd";
    }



    /**
     * 查看
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "/view.html/{id}")
    public String view(@PathVariable String id, Model model) {
        Provider provider=providerService.getProviderById(id);
        model.addAttribute("provider", provider);
        return "providerview";
    }

    /**
     * 修改传值
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/modify.html")
    public String modify(String id, Model model){
        Provider provider = providerService.getProviderById(id);
        model.addAttribute("provider", provider);
        return "providermodify";
    }

    /**
     * 修改供应商
     * @param provider
     * @param
     * @return
     */
    @RequestMapping(value ="/modify.html",method = RequestMethod.POST)
    public  String savemodify(Provider provider){
        if (providerService.modify(provider)) {
            return "redirect:/pro/provider.html";
        }
        return "modify";
    }




}
