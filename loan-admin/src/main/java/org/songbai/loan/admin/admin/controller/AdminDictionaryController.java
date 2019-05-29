package org.songbai.loan.admin.admin.controller;

import org.apache.commons.lang3.StringUtils;
import org.songbai.cloud.basics.exception.ResolveMsgException;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.mvc.RespCode;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.loan.admin.admin.model.AdminDictionaryModel;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.admin.service.AdminDictionaryService;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.constant.resp.AdminRespCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping("/dictionary")
@RestController
public class AdminDictionaryController {
    @Autowired
    private AdminDictionaryService dictionaryService;
    @Autowired
    private AdminUserHelper adminUserHelper;


    @RequestMapping("/saveDictionary")
    public Response saveDictionary(AdminDictionaryModel dictionaryModel, HttpServletRequest request) {
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
        dictionaryModel.setCreateUser(userModel.getName());
        dictionaryModel.setUpdateUser(userModel.getName());
        dictionaryService.saveDictionary(dictionaryModel);


        return Response.response(RespCode.SUCCESS, "添加成功");

    }

    @RequestMapping(value = {"/updateDictionary", "/updateDictionaryStatus"})
    public Response updateDictionary(AdminDictionaryModel dictionaryModel, HttpServletRequest request) {
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
        dictionaryModel.setCreateUser(userModel.getName());
        dictionaryModel.setUpdateUser(userModel.getName());
        dictionaryService.updateDictionary(dictionaryModel);

        return Response.response(RespCode.SUCCESS, "更新成功");

    }

    @RequestMapping("/findDictionaryByPage")
    public Response findDictionaryByPage(AdminDictionaryModel dictionaryModel, Integer page, Integer pageSize) {
        Page<AdminDictionaryModel> dictionaryPage = dictionaryService.findDictionaryByPage(dictionaryModel, page, pageSize);

        return Response.response(RespCode.SUCCESS, "查询成功", dictionaryPage);
    }


    @GetMapping("/findDictionaryByCode")
    public Response findDictionaryByCode(String type, String code) {
        List<AdminDictionaryModel> dictionaryPage = dictionaryService.findDictionaryByCode(type, code);
        return Response.success(dictionaryPage);
    }

    @RequestMapping("/deleteDictionary")
    public Response deleteDictionary(String ids) {

        dictionaryService.deleteDictionaryByIds(ids);


        return Response.response(RespCode.SUCCESS, "删除成功");
    }

}
