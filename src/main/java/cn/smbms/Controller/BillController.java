package cn.smbms.Controller;

import cn.smbms.pojo.Bill;
import cn.smbms.pojo.Provider;
import cn.smbms.pojo.User;
import cn.smbms.service.bill.BillService;
import cn.smbms.service.bill.BillServiceImpl;
import cn.smbms.service.provider.ProviderService;
import cn.smbms.service.provider.ProviderServiceImpl;
import cn.smbms.tools.Constants;
import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import sun.dc.pr.PRError;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class BillController {
    @Resource
    private BillService billService;
    @Resource
    private ProviderService providerService;


    /**
     * 显示所有订单信息
     *
     * @param model
     * @param queryProductName
     * @param queryProviderId
     * @param queryIsPayment
     * @return
     */
    @RequestMapping("/billList.html")
    public String query(Model model, String queryProductName, String queryProviderId, String queryIsPayment) {
        List<Provider> providerList = new ArrayList<>();
        providerList = providerService.getProviderList("", "");
        model.addAttribute("providerList", providerList);

        if (StringUtils.isNullOrEmpty(queryProductName)) {
            queryProductName = "";
        }

        List<Bill> billList = new ArrayList<Bill>();
        Bill bill = new Bill();
        if (StringUtils.isNullOrEmpty(queryIsPayment)) {
            bill.setIsPayment(0);
        } else {
            bill.setIsPayment(Integer.parseInt(queryIsPayment));
        }

        if (StringUtils.isNullOrEmpty(queryProviderId)) {
            bill.setProviderId(0);
        } else {
            bill.setProviderId(Integer.parseInt(queryProviderId));
        }
        bill.setProductName(queryProductName);
        billList = billService.getBillList(bill);
        model.addAttribute("billList", billList);
        model.addAttribute("queryProductName", queryProductName);
        model.addAttribute("queryProviderId", queryProviderId);
        model.addAttribute("queryIsPayment", queryIsPayment);
        return "billlist";
    }

    /**
     * 添加用户页面跳转
     *
     * @return
     */
    @RequestMapping("/billadd.html")
    public String addbill(Model model) {
        List<Provider> providerList = new ArrayList<>();
        providerList = providerService.getProviderList("", "");
        model.addAttribute("providerList", providerList);
        return "billadd";
    }

    /**
     * 添加用户
     *
     * @param model
     * @param bill
     * @param
     * @return
     */
    @RequestMapping(value = "/billadd.html", method = RequestMethod.POST)
    public String add(Model model, Bill bill) {
        bill.setCreationDate(new Date());//创建时间
        if (billService.add(bill)) {
            return "redirect:/billList.html";
        }
        return "billadd";
    }

    /**
     * 查看
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "/view.html/{id}")
    public String view(@PathVariable String id, Model model) {
        Bill bill = billService.getBillById(id);
        model.addAttribute("bill", bill);
        return "billview";
    }

    /**
     * 修改传值
     *
     * @return
     */
    @RequestMapping("/modify.html")
    public String modify(String id, Model model) {
        List<Provider> providerList = new ArrayList<>();
        Bill bill = billService.getBillById(id);
        providerList = providerService.getProviderList("", "");
        model.addAttribute("bill", bill);
        model.addAttribute("providerList", providerList);
        return "billmodify";
    }

    /**
     * 修改订单
     * @param bill
     * @param
     * @return
     */
    @RequestMapping(value = "/modify.html",method = RequestMethod.POST)
    public String savemodify(Bill bill) {
        bill.setCreationDate(new Date());//创建时间
        if (billService.modify(bill)) {
            return "redirect:/billList.html";
        }
        return "billadd";
    }


}
