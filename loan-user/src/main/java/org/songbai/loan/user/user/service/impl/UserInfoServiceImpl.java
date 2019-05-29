package org.songbai.loan.user.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.utils.base.Ret;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.cloud.basics.utils.regular.Regular;
import org.songbai.loan.common.util.Date8Util;
import org.songbai.loan.common.util.PhoneUtil;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.rediskey.UserRedisKey;
import org.songbai.loan.constant.resp.UserRespCode;
import org.songbai.loan.model.statistic.dto.UserStatisticDTO;
import org.songbai.loan.model.user.AuthenticationModel;
import org.songbai.loan.model.user.UserContactModel;
import org.songbai.loan.model.user.UserInfoModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.service.user.service.ComUserService;
import org.songbai.loan.user.user.auth.AliyunUtil;
import org.songbai.loan.user.user.auth.BaiduOcrUtil;
import org.songbai.loan.user.user.auth.FaceMatch;
import org.songbai.loan.user.user.auth.ImageUtils;
import org.songbai.loan.user.user.dao.AuthenticationDao;
import org.songbai.loan.user.user.dao.UserDao;
import org.songbai.loan.user.user.dao.UserInfoDao;
import org.songbai.loan.user.user.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.*;

/**
 * 用户个人信息认证
 *
 * @author wjl
 * @date 2018年10月30日 10:31:11
 * @description
 */
@Service
@Slf4j
public class UserInfoServiceImpl implements UserInfoService {
    private static final Logger logger = LoggerFactory.getLogger(UserInfoService.class);
    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private AuthenticationDao authDao;
    @Autowired
    private ComUserService comUserService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private SpringProperties springProperties;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private BaiduOcrUtil baiduOcrUtil;
    @Autowired
    private AliyunUtil aliyunUtil;
    @Autowired
    FaceMatch faceMatch;

    @Override
    @Transactional
    public void save(UserInfoModel model, String type, boolean flag) {//偷个懒 不想重写方法了，价格flag
        if (model.getIdcardNum() != null) {
            String idcardNum = model.getIdcardNum();
            char c = idcardNum.charAt(idcardNum.length() - 2);
            if ((int) c % 2 == 0) {
                model.setSex(2);
            } else {
                model.setSex(1);
            }
        }
        userInfoDao.updateById(model);
        Integer userId = model.getUserId();
        redisTemplate.opsForHash().delete(UserRedisKey.USER_DATA, userId);//删除缓存
        if (!flag) {
            return;
        }
        //拿到真实姓名更新user表中的真是姓名
        UserModel userModel = comUserService.selectUserModelById(userId);
        if (StringUtils.isBlank(userModel.getName()) && StringUtils.isNotBlank(model.getName())) {
            userModel.setName(model.getName());
            userDao.updateById(userModel);
            redisTemplate.opsForHash().delete(UserRedisKey.USER_INFO, userId);//删除缓存
        }
        //然后更新auth表中的状态
        AuthenticationModel authModel = authDao.selectById(userId);
        if (type.equalsIgnoreCase("idcard")) {
            authModel.setIdcardStatus(1);
            authModel.setMoney(authModel.getMoney() + springProperties.getInteger("user.auth.idcard", 300));
            authModel.setIdcardTime(new Date());
            // 实名认证成功
            UserStatisticDTO dto = new UserStatisticDTO();
            dto.setRegisterDate(Date8Util.date2LocalDate(userModel.getCreateTime()));
            dto.setAgencyId(userModel.getAgencyId());
            dto.setChannelCode(userModel.getChannelCode());
            dto.setActionDate(LocalDate.now());
            dto.setIsIdcard(CommonConst.YES);
            dto.setVestId(userModel.getVestId());
            jmsTemplate.convertAndSend(JmsDest.USER_STATISTIC, dto);
            logger.info(">>>>发送统计,用户行为(实名认证)jms ,data={}", dto);
        }
        if (type.equalsIgnoreCase("info")) {
            authModel.setInfoStatus(1);
            authModel.setMoney(authModel.getMoney() + springProperties.getInteger("user.auth.info", 300));
            authModel.setInfoTime(new Date());
            // 个人信息认证成功
            UserStatisticDTO dto = new UserStatisticDTO();
            dto.setRegisterDate(Date8Util.date2LocalDate(userModel.getCreateTime()));
            dto.setAgencyId(userModel.getAgencyId());
            dto.setChannelCode(userModel.getChannelCode());
            dto.setActionDate(LocalDate.now());
            dto.setIsInfo(CommonConst.YES);
            dto.setVestId(userModel.getVestId());
            jmsTemplate.convertAndSend(JmsDest.USER_STATISTIC, dto);
            logger.info(">>>>发送统计,用户行为(个人信息认证)jms ,data={}", dto);
        }
        authDao.updateById(authModel);
    }

    @Override
    public UserInfoModel selectByUserId(Integer userId) {
        UserInfoModel userInfoModel = userInfoDao.selectById(userId);
        userInfoModel.setUserId(null);
        return userInfoModel;
    }

    @Override
    public UserInfoModel getUserInfoByIdCardNum(String idCardNum, Integer agencyId, Integer vestId) {
        return userInfoDao.getUserInfoByIdCardNum(idCardNum, agencyId,vestId);
    }

    @Override
    public void saveUserContact(Integer userId, List<UserContactModel> list) {
        Date date = new Date();
        list.forEach(e -> {

            if (StringUtil.isEmpty(e.getPhone())) {
                return;
            }
            String phone = PhoneUtil.trimSpaceAndAreaCode(e.getPhone());
            if (StringUtils.isEmpty(phone)) {
                return;
            }
            e.setPhone(phone);
            Query query = new Query();
            query.addCriteria(Criteria.where("userId").is(userId));
            query.addCriteria(Criteria.where("phone").is(e.getPhone()));
            UserContactModel model = mongoTemplate.findOne(query, UserContactModel.class, "loan_user_contacts");
            if (model == null) {
                e.setUserId(userId);
                e.setCreateTime(date);
                if (StringUtils.isEmpty(e.getName())) {
                    e.setName("未知");
                }
                mongoTemplate.insert(e, "loan_user_contacts");
            }
        });
    }


    private static LinkedHashMap<List<String>, String> REL_MAP = new LinkedHashMap<>();

    static {
        List<String> husband = Arrays.asList("丈夫", "郎君", "夫君", "良人", "官人", "相公", "老公", "爱人", "卿卿", "外子", "外人", "老头子", "老伴");
        List<String> wife = Arrays.asList("妻子", "娘子", "内人", "良人", "内子", "老婆", "爱人", "卿卿", "老婆子", "老伴");
        List<String> father = Arrays.asList("父亲", "爸爸", "爹", "爹爹", "爹亲", "爹地", "大大", "老爸", "爸比", "爸", "老爷子");
        List<String> mother = Arrays.asList("妈妈", "娘", "娘娘", "娘亲", "娘妮", "老妈", "妈咪", "妈", "老娘");
        List<String> brother1 = Arrays.asList("兄亲", "兄兄", "哥", "兄长", "兄台", "兄亲");
        List<String> sister1 = Arrays.asList("姊亲", "姊姊", "姐", "姊长", "姊台", "姊亲");
        List<String> brother2 = Arrays.asList("弟亲", "弟弟", "弟", "兄弟", "弟子", "弟亲");
        List<String> sister2 = Arrays.asList("妹亲", "妹妹", "妹", "姊妹", "妹子", "妹亲");


        REL_MAP.put(husband, husband.get(0));
        REL_MAP.put(wife, wife.get(0));
        REL_MAP.put(father, father.get(0));
        REL_MAP.put(mother, mother.get(0));
        REL_MAP.put(brother1, brother1.get(0));
        REL_MAP.put(sister1, sister1.get(0));
        REL_MAP.put(brother2, brother2.get(0));
        REL_MAP.put(sister2, sister2.get(0));
    }


    @Override
    @Async
    public void doubtfulContactRelation(Integer userId) {
        try {


            Query query = Query.query(Criteria.where("userId").is(userId));

            List<UserContactModel> list = mongoTemplate.find(query, UserContactModel.class);

            for (UserContactModel model : list) {

                if (model.getRelation() != null || model.getDoubtfulRelation() != null) {
                    continue;
                }

                for (List<String> keys : REL_MAP.keySet()) {

                    for (String k : keys) {
                        if (model.getName().equalsIgnoreCase(k) || model.getName().contains(k)) {
                            model.setDoubtfulRelation(REL_MAP.get(keys));
                            break;
                        }
                    }

                    if (model.getDoubtfulRelation() != null) {
                        break;
                    }
                }

                if (model.getDoubtfulRelation() != null) {

                    Update update = new Update();

                    update.set("doubtfulRelation", model.getDoubtfulRelation());

                    mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(model.getId())), update, UserContactModel.class);
                }

            }


        } catch (Exception e) {

            log.error("推断用户关系异常" + userId, e);
        }
    }

    @Override
    public Ret idcardAuth(MultipartFile multipartFile, Integer userId, String idcardSide) {
        AuthenticationModel authModel = authDao.selectById(userId);
        if (authModel == null || authModel.getIdcardStatus() == 1) {
            throw new BusinessException(UserRespCode.NOT_REPEAT_AUTH);
        }
        if (idcardSide.equalsIgnoreCase("front")) {
            faceMatch.searchIdcardFace(multipartFile);
        }
        //身份证识别结果
        JSONObject idcardResult = baiduOcrUtil.authIdcard(multipartFile, idcardSide);

        //阿里云图片地址
        String filePath = createImagePath(multipartFile, idcardResult, userId, idcardSide);

        UserInfoModel model = new UserInfoModel();
        if (idcardSide.equalsIgnoreCase("front")) {
            model.setIdcardFrontImg(filePath);
        }
        if (idcardSide.equalsIgnoreCase("back")) {
            model.setIdcardBackImg(filePath);
        }
        model.setUserId(userId);
        save(model, idcardSide, false);

        //处理返回结果
        Map<String, String> map = handleIdcardResultMap(JSONObject.parseObject(idcardResult.getString("words_result")), idcardSide);

        Ret ret = Ret.create();
        ret.put(map);
        return ret;

    }

    private Map<String, String> handleIdcardResultMap(JSONObject jsonObject, String idcardSide) {
        Map<String, String> map = new HashMap<>();
        if (idcardSide.equalsIgnoreCase("front")) {
            String address = getJsonValueFromJson(jsonObject.getJSONObject("住址"), "words");
            String idcard = getJsonValueFromJson(jsonObject.getJSONObject("公民身份号码"), "words");
            String name = getJsonValueFromJson(jsonObject.getJSONObject("姓名"), "words");
            String root = getJsonValueFromJson(jsonObject.getJSONObject("民族"), "words");
            String sex = getJsonValueFromJson(jsonObject.getJSONObject("性别"), "words");
            if (StringUtils.isNotBlank(idcard) && Regular.checkIdCardMatch(idcard)
                    && StringUtils.isNotBlank(name) && StringUtils.isNotBlank(root)
                    && StringUtils.isNotBlank(sex) && sex.length() == 1 && StringUtils.isNotBlank(address)) {
                map.put("idcard", idcard);
                map.put("name", name);
                map.put("root", root);
                map.put("sex", sex);
                map.put("address", address);
                return map;
            } else {
                throw new BusinessException(UserRespCode.AUTH_FAILED);
            }
        }
        String where = getJsonValueFromJson(jsonObject.getJSONObject("签发机关"), "words");
        String start = getJsonValueFromJson(jsonObject.getJSONObject("签发日期"), "words");
        String end = getJsonValueFromJson(jsonObject.getJSONObject("失效日期"), "words");
        if (StringUtils.isNotBlank(where) && StringUtils.isNotBlank(start) && StringUtils.isNotBlank(end)) {
            map.put("where", where);
            map.put("validate", start + "-" + end);
        } else {
            throw new BusinessException(UserRespCode.AUTH_FAILED);
        }

        return map;
    }

    private String getJsonValueFromJson(JSONObject json, String key) {
        if (json != null && json.getString(key) != null) {
            return json.getString(key);
        }
        return null;
    }

    private String createImagePath(MultipartFile multipartFile, JSONObject idcardResult, Integer userId, String idcardSide) {
        try {
            String imgBase64 = ImageUtils.encodeImgageToBase64(multipartFile);
            byte[] bts = Base64.decodeBase64(imgBase64);
            String string = multipartFile.getOriginalFilename().toLowerCase();
            String fileName = aliyunUtil.generateDateKey(userId, "_idcard_" + idcardSide + string.substring(string.lastIndexOf(".")));//生成图片名称c

//			String format = "";
//			int dot = string.lastIndexOf(".");
//			if ((dot > -1) && (dot < (string.length() - 1))) {
//				format = string.substring(dot + 1);
//			}
            Integer direction = idcardResult.getInteger("direction");
            if (direction != null) {
                if (direction.equals(1)) {
                    byte[] bytes = ImageUtils.ThumbnailsRotate(bts, 90);
                    return aliyunUtil.innerSaveImageByte(fileName, bytes, multipartFile.getContentType());
                } else if (direction.equals(2)) {
                    byte[] bytes = ImageUtils.ThumbnailsRotate(bts, 180);
                    return aliyunUtil.innerSaveImageByte(fileName, bytes, multipartFile.getContentType());
                } else if (direction.equals(3)) {
                    byte[] bytes = ImageUtils.ThumbnailsRotate(bts, 270);
                    return aliyunUtil.innerSaveImageByte(fileName, bytes, multipartFile.getContentType());
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageUtils.commpressPicCycle(bts, 2048 * 1024, out);
            if (out.size() > 0) bts = out.toByteArray();
            return aliyunUtil.innerSaveImageByte(fileName, bts, multipartFile.getContentType());
        } catch (Exception e) {
            logger.error("idcard auth create image is error", e);
            throw new BusinessException(UserRespCode.SYSTEM_EXCEPTION);
        }
    }

}
