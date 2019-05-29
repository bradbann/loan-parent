package org.songbai.loan;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.songbai.cloud.basics.helper.upload.AliyunOssHelper;
import org.songbai.loan.user.user.auth.AliyunUtil;
import org.songbai.loan.user.user.auth.BaiduOcrUtil;
import org.songbai.loan.user.user.auth.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IdcardTest {
    @Autowired
    BaiduOcrUtil baiduOcrUtil;
    @Autowired
    AliyunOssHelper aliyunOssHelper;
    @Autowired
    AliyunUtil aliyunUtil;


    @Test
    public void test() throws IOException {
        long start = System.currentTimeMillis();
        String imgUrl = "https://bitexcn.oss-cn-shanghai.aliyuncs.com/upload/20180920180218219.jpg";
        File xx = new File("C:\\Users\\Administrator\\Desktop\\107494_idcard_front.png");
//        FileInputStream inputStream = new FileInputStream(file);
        byte[] file = FileUtils.readFileToByteArray(xx);
//        byte[] file = IOUtils.toByteArray(new URL(imgUrl));
//        ByteArrayInputStream input = new ByteArrayInputStream(file);
        System.out.println("图片原来的大小...size>>>" + file.length + "耗时>>>" + (System.currentTimeMillis() - start));


        ByteArrayOutputStream out = new ByteArrayOutputStream();
        long agaim = System.currentTimeMillis();
        ImageUtils.commpressPicCycle(file, 2048 * 1024, out);


        System.out.println("输出的流大小...size>>>" + out.toByteArray().length + "耗时>>>" + (System.currentTimeMillis() - agaim));

        aliyunUtil.innerSaveImageByte("zcm_test.jpg", out.toByteArray(), "jpg");
        System.out.println("输出的流大小...size>>>" + out.toByteArray().length + "耗时>>>" + (System.currentTimeMillis() - start));
    }


    //public static void main(String[] args) throws IOException {
    //	File file = new File("C:\\Users\\Administrator\\Desktop\\22_idcard_front.png");
    //	InputStream input = new FileInputStream(file);
    //	byte[] bytes = new byte[input.available()];
    //	System.out.println(bytes.length);
    //	byte[] ss = org.apache.commons.codec.binary.Base64.encodeBase64(bytes);
    //	System.out.println(ss.length);
    //}


}