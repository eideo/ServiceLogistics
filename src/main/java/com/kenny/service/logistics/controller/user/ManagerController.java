package com.kenny.service.logistics.controller.user;

import com.kenny.service.logistics.exception.ErrorCode;
import com.kenny.service.logistics.exception.ErrorCodeException;
import com.kenny.service.logistics.exception.UserErrorCode;
import com.kenny.service.logistics.json.JsonBean;
import com.kenny.service.logistics.json.response.PageResponse;
import com.kenny.service.logistics.model.user.User;
import com.kenny.service.logistics.model.user.UserInfo;
import com.kenny.service.logistics.model.user.UserSet;
import com.kenny.service.logistics.service.user.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;


/**
 * Created by Administrator on 2016/11/4.
 */
@Api(value = "/v1/user/manager", description = "用户管理模块")
@RequestMapping(value = "/v1/user/manager")
@RestController
public class ManagerController {
    @Autowired
    UserManagerService userManagerService;
    @Autowired
    UserInfoService userInfoService;
    @Autowired
    UserCustomerService userCustomerService;
    @Autowired
    UserMoneyService userMoneyService;

    @ApiOperation(value = "获取用户列表")
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @ResponseBody
    public JsonBean<PageResponse<User>> UserList(@ApiParam(value = "从第几个开始列出") @RequestParam(required = false, defaultValue = "0") Integer offset,
                                                 @ApiParam(value = "每页数量") @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return new JsonBean(UserErrorCode.SUCCESS, userManagerService.selectPage(offset, pageSize));
    }

    @ApiOperation(value = "获取用户详细信息列表")
    @RequestMapping(value = "/usersets", method = RequestMethod.GET)
    @ResponseBody
    public JsonBean<PageResponse<UserSet>> UserSetList(@ApiParam(value = "从第几个开始列出") @RequestParam(required = false, defaultValue = "0") Integer offset,
                                                       @ApiParam(value = "每页数量") @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return new JsonBean(UserErrorCode.SUCCESS, userManagerService.GetUserSetList(offset, pageSize));
    }

    @ApiOperation(value = "获取客户类型用户详细信息列表")
    @RequestMapping(value = "/ex/customer", method = RequestMethod.GET)
    @ResponseBody
    public JsonBean<PageResponse<UserSet>> CustomerInfoListEx(@ApiParam(value = "从第几个开始列出") @RequestParam(required = false, defaultValue = "0") Integer offset,
                                                              @ApiParam(value = "每页数量") @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return new JsonBean(UserErrorCode.SUCCESS, userManagerService.selectPageByTypeEx(offset, pageSize, "customer"));
    }

    @ApiOperation(value = "获取管理员类型用户详细信息列表")
    @RequestMapping(value = "/ex/admin", method = RequestMethod.GET)
    @ResponseBody
    public JsonBean<PageResponse<UserSet>> AdminInfoListEx(@ApiParam(value = "从第几个开始列出") @RequestParam(required = false, defaultValue = "0") Integer offset,
                                                           @ApiParam(value = "每页数量") @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return new JsonBean(UserErrorCode.SUCCESS, userManagerService.selectPageByTypeEx(offset, pageSize, "admin"));
    }

    @ApiOperation(value = "增加客户类型用户")
    @RequestMapping(value = "/customer", method = RequestMethod.POST)
    @ResponseBody
    public JsonBean<User> CustomerAdd(@ApiParam("账户") @RequestParam String username,
                                      @ApiParam("密码") @RequestParam String password,
                                      @ApiParam("昵称") @RequestParam(required = false) String nickname,
                                      @ApiParam("性别") @RequestParam(required = false) String sex,
                                      @ApiParam("上传图像地址") @RequestParam(required = false) String img,
                                      @ApiParam("公司名称") @RequestParam String company,
                                      @ApiParam("金额") @RequestParam Integer money) {
        try {
            User user = userCustomerService.insertUserName(username,password);
            userInfoService.insert(user.getId(),nickname,sex,img,null,company,money);
            return new JsonBean(ErrorCode.SUCCESS,user);
        } catch (ErrorCodeException e) {
            return new JsonBean(e.getErrorCode());
        }
    }

    @ApiOperation(value = "用户充值")
    @RequestMapping(value = "/customer/money/{id}", method = RequestMethod.POST)
    @ResponseBody
    public JsonBean CustomerMoney(@ApiParam(value = "用户ID", required = true) @PathVariable Integer id,
                                  @ApiParam("金额") @RequestParam Integer money) {
        try {
            //充值
            userInfoService.updateAddMoney(id,money);
            //添加记录
            userMoneyService.insert(id,money,"in","");
            return new JsonBean(ErrorCode.SUCCESS);
        } catch (ErrorCodeException e) {
            return new JsonBean(e.getErrorCode());
        }
    }

    @ApiOperation(value = "通过ID获取用户")
    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    @ResponseBody
    public JsonBean<User> UserGet(@ApiParam(value = "用户ID", required = true) @PathVariable Integer id) {
        try {
            return new JsonBean(UserErrorCode.SUCCESS, userManagerService.selectByPrimaryKey(id));
        } catch (ErrorCodeException e) {
            return new JsonBean(e.getErrorCode());
        }
    }

    @ApiOperation(value = "通过ID获取用户信息")
    @RequestMapping(value = "/users/{id}/info", method = RequestMethod.GET)
    @ResponseBody
    public JsonBean<UserInfo> UserInfoGet(@ApiParam(value = "用户ID", required = true) @PathVariable Integer id) {
        try {
            return new JsonBean(UserErrorCode.SUCCESS, userInfoService.GetUserInfo(id));
        } catch (ErrorCodeException e) {
            return new JsonBean(e.getErrorCode());
        }
    }

    @ApiOperation(value = "重置密码")
    @RequestMapping(value = "/users/{id}/password", method = RequestMethod.PUT)
    @ResponseBody
    public JsonBean<User> UserPass(@ApiParam(value = "用户ID", required = true) @PathVariable Integer id,
                                   @ApiParam(value = "密码", required = true) @RequestParam String password) {
        try {
            return new JsonBean(UserErrorCode.SUCCESS, userManagerService.resetPassword(id, password));
        } catch (ErrorCodeException e) {
            return new JsonBean(e.getErrorCode());
        }
    }

    @ApiOperation(value = "修改用户信息")
    @RequestMapping(value = "/users/{id}/info", method = RequestMethod.PUT)
    @ResponseBody
    public JsonBean<UserInfo> UserEditInfo(@ApiParam(value = "用户ID", required = true) @PathVariable Integer id,
                                           @ApiParam(value = "昵称", required = true) @RequestParam String nickname,
                                           @ApiParam(value = "性别", required = true) @RequestParam String sex,
                                           @ApiParam(value = "头像", required = true) @RequestParam String img,
                                           @ApiParam(value = "生日", required = true) @RequestParam long birthday,
                                           @ApiParam("") @RequestParam(required = false) String company,
                                           @ApiParam("") @RequestParam(required = false) int money) {
        try {
            return new JsonBean(UserErrorCode.SUCCESS, userInfoService.update(id, nickname, sex, img, new Date(birthday),company,money));
        } catch (ErrorCodeException e) {
            return new JsonBean(e.getErrorCode());
        }
    }

    @ApiOperation(value = "删除用户")
    @RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public JsonBean UserDelete(@ApiParam(value = "用户ID", required = true) @PathVariable Integer id) {
        userManagerService.deleteByPrimaryKey(id);
        userInfoService.DeleteUserInfo(id);
        return new JsonBean(UserErrorCode.SUCCESS);
    }
}
