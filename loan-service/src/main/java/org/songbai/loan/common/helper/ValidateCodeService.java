package org.songbai.loan.common.helper;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

public class ValidateCodeService {

    // 图片的宽度。
    private int width = 80*2;
    // 图片的高度。
    private int height = 30*2;
    // 验证码字符个数
    private int codeCount = 4;
    // 验证码干扰线数
    private int lineCount = 20*4;
    // 验证码
    private String code = null;
    // 验证码图片Buffer
    private BufferedImage buffImg = null;

    private static final char[] codeSequence = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    private static Font defaultFont;

    static {
        try {
//            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//            System.out.println(ValidateCodeService.class.getClassLoader().getResource("xczt.ttf"));
            InputStream in = ValidateCodeService.class.getClassLoader().getResourceAsStream("xczt.ttf");
            defaultFont = Font.createFont(Font.TRUETYPE_FONT, in);

            defaultFont = defaultFont.deriveFont(Font.BOLD, 56);
        } catch (Exception e) {
            String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();//获得系统字体

            defaultFont = new Font(fonts[new Random().nextInt(fonts.length)], Font.BOLD, 56);
        }
    }

    public ValidateCodeService() {
        this.createCode();
    }

    /**
     * @param width  图片宽
     * @param height 图片高
     */
    public ValidateCodeService(int width, int height) {
        this.width = width;
        this.height = height;
        this.createCode();
    }

    /**
     * @param width     图片宽
     * @param height    图片高
     * @param codeCount 字符个数
     * @param lineCount 干扰线条数
     */
    public ValidateCodeService(int width, int height, int codeCount, int lineCount) {
        this.width = width;
        this.height = height;
        this.codeCount = codeCount;
        this.lineCount = lineCount;
        this.createCode();
    }

    public void createCode() {
        int x = 0, codeY = 0;
        int red = 0, green = 0, blue = 0;

        x = width / (codeCount + 2);// 每个字符的宽度
        codeY = height - 10;

        // 图像buffer
        buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = buffImg.getGraphics();
        // 生成随机数
        Random random = new Random();
        // 将图像填充为白色
        Color color = new Color(255, 255, 255);
        g.setColor(color);
        g.fillRect(0, 0, width, height);
        // 创建字体
//        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();//获得系统字体
//        Font font = new Font(fonts[0], Font.BOLD, 26);
//        g.setFont(font);

        for (int i = 0; i < lineCount; i++) {
            int xs = random.nextInt(width);
            int ys = random.nextInt(height);
            int xe = xs + random.nextInt(width / 16);
            int ye = ys + random.nextInt(height / 16);
            red = random.nextInt(230);
            green = random.nextInt(230);
            blue = random.nextInt(230);
            g.setColor(new Color(red, green, blue));

            switch (i % 5){
                case 2:
                    g.drawRect(xs,ys,random.nextInt(4),random.nextInt(4));
                    break;
                case 1:
                    g.drawOval(xs,ys,random.nextInt( 4),random.nextInt( 4));
                    break;
                default:
//                    g.drawLine(xs, ys, xe, ye);
                    g.drawLine(xs, ys, xe, ye);
                    break;
            }

//            g.drawRect(xs,ys,random.nextInt(width / 8),random.nextInt(height / 8));
//            g.draw3DRect(xs,ys,random.nextInt(width / 8),random.nextInt(height / 8),true);
        }

        g.setFont(defaultFont);
        if(g instanceof Graphics2D){
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);;
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
        }

        // randomCode记录随机产生的验证码
        StringBuffer randomCode = new StringBuffer();
        // 随机产生codeCount个字符的验证码。
        for (int i = 0; i < codeCount; i++) {
            String strRand = String.valueOf(codeSequence[random.nextInt(codeSequence.length)]);
            // 产生随机的颜色值，让输出的每个字符的颜色值都将不同。
            red = random.nextInt(120) + 50;
            green = random.nextInt(120) + 50;
            blue = random.nextInt(120) + 50;
            g.setColor(new Color(red, green, blue));
//            g.setColor(new Color(34, 140, 215));
            g.drawString(strRand, (i + 1) * x, codeY);
            // 将产生的四个随机数组合在一起。
            randomCode.append(strRand);
        }
        // 将四位数字的验证码保存到Session中。
        code = randomCode.toString();
    }

    public void write(String path) throws IOException {
        OutputStream sos = new FileOutputStream(path);
        this.write(sos);
    }

    public void write(OutputStream sos) throws IOException {
        ImageIO.write(buffImg, "png", sos);
        sos.close();
    }

    public BufferedImage getBuffImg() {
        return buffImg;
    }

    public String getCode() {
        return code;
    }
}