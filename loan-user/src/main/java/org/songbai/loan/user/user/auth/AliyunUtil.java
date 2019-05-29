package org.songbai.loan.user.user.auth;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectRequest;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.utils.date.SimpleDateFormatUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Random;

/**
 * 阿里云图片工具类
 *
 * @author wjl
 * @date 2018年11月06日 20:44:53
 * @description
 */
@Component
public class AliyunUtil {
	private static final Logger logger = LoggerFactory.getLogger(AliyunUtil.class);
	@Value("${config.aliyun.oss.bucketName}")
	private String bucketName;

	@Value("${config.aliyun.oss.innerBucketName}")
	private String innerBucketName;

	@Value("${config.aliyun.oss.url}")
	private String url;

	@Value("${config.aliyun.oss.pkg:upload}")
	private String pkg;

	@Value("${config.aliyun.oss.bufferSize:2014}")
	private int bufferSize;

	@Value("${config.aliyun.oss.endpoint}")
	private String endpoint;

	@Value("${config.aliyun.accessKeyId}")
	private String accessKeyId;

	@Value("${config.aliyun.accessKeySecret}")
	private String accessKeySecret;

	private static Random random = new Random();

	/**
	 * 将阿里云图片转为base64位编码
	 *
	 * @param filePath 图片地址
	 * @return
	 */
	public String getImgBase64ByImgPath(String filePath) {
		byte[] imgData;
		try {
			OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
			OSSObject ossObject = ossClient.getObject(innerBucketName, filePath);
			InputStream inputStream = ossObject.getObjectContent();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			int len = 0;
			while ((len = inputStream.read(b)) != -1) {
				bos.write(b, 0, len);
			}
			imgData = bos.toByteArray();
			return Base64.encodeBase64String(imgData);
		} catch (Exception e) {
		}
		return null;
	}

	public String generateDateKey(Integer userId, String type) {
		Date date = new Date();
		String month = SimpleDateFormatUtil.dateToString(date, FastDateFormat.getInstance("yyyyMM"));
		String days = SimpleDateFormatUtil.dateToString(date, SimpleDateFormatUtil.DATE_FORMAT1);
		return month + "/" + days + "/" + userId + type;
	}

	public String generateDateKey2(Integer userId, String type) {
		Date date = new Date();
		String month = SimpleDateFormatUtil.dateToString(date, FastDateFormat.getInstance("yyyyMM"));
		String days = SimpleDateFormatUtil.dateToString(date, SimpleDateFormatUtil.DATE_FORMAT1);
		return month + "/" + days + "/" + userId + type + random.nextInt(9999);
	}

	public String innerSaveImageByte(String objectkey, byte[] bytes, String imageFormat) {
		OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
		String storeKey = getStoreKey(objectkey);
		try {
			PutObjectRequest putObjectRequest = new PutObjectRequest(innerBucketName, storeKey, new ByteArrayInputStream(bytes));
			ossClient.putObject(putObjectRequest);
		} catch (Exception e) {
			logger.error("aliyun oss upload byte is error,message={}", (Object) e.getStackTrace());
			e.printStackTrace();
		} finally {
			ossClient.shutdown();
		}
		return storeKey;
	}

	private String getStoreKey(String objectkey) {

		return pkg.endsWith("/") ? pkg + objectkey : pkg + "/" + objectkey;
	}

}
