package org.songbai.loan.user.user.auth;

import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.loan.constant.resp.UserRespCode;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

public class ImageUtils {
	private static final Logger logger = LoggerFactory.getLogger(ImageUtils.class);

	public static byte[] rotateImg(byte[] bytes, double angle, String format) {
		try {
			ByteArrayInputStream input = new ByteArrayInputStream(bytes);
			BufferedImage old_img = ImageIO.read(input);
			logger.info(">>>>image rotate old_iimg={},img_type={},format={}", old_img.toString(), old_img.getType(), format);
			int width = old_img.getWidth();
			int height = old_img.getHeight();
			double[][] newPositions = new double[4][];
			newPositions[0] = calculatePosition(0, 0, angle);
			newPositions[1] = calculatePosition(width, 0, angle);
			newPositions[2] = calculatePosition(0, height, angle);
			newPositions[3] = calculatePosition(width, height, angle);
			double minX = Math.min(
					Math.min(newPositions[0][0], newPositions[1][0]),
					Math.min(newPositions[2][0], newPositions[3][0])
			);
			double maxX = Math.max(
					Math.max(newPositions[0][0], newPositions[1][0]),
					Math.max(newPositions[2][0], newPositions[3][0])
			);
			double minY = Math.min(
					Math.min(newPositions[0][1], newPositions[1][1]),
					Math.min(newPositions[2][1], newPositions[3][1])
			);
			double maxY = Math.max(
					Math.max(newPositions[0][1], newPositions[1][1]),
					Math.max(newPositions[2][1], newPositions[3][1])
			);
			int newWidth = (int) Math.round(maxX - minX);
			int newHeight = (int) Math.round(maxY - minY);
			logger.info("minX={},minY={},maxX={},maxY={}", minX, minY, maxX, maxY);
			BufferedImage new_img = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
			logger.info("image rotate newImg={},imgType={}", new_img.toString(), new_img.getType());
			Graphics2D g = new_img.createGraphics();

			g.setRenderingHint(
					RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR
			);
			g.setRenderingHint(
					RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON
			);
			double w = newWidth / 2.0;
			double h = newHeight / 2.0;

			g.rotate(Math.toRadians(angle), w, h);
			int centerX = (int) Math.round((newWidth - width) / 2.0);
			int centerY = (int) Math.round((newHeight - height) / 2.0);

			g.drawImage(old_img, centerX, centerY, null);
			g.dispose();

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write(new_img, format, out);
			return out.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("img rotate is fail,msg={}", (Object) e.getStackTrace());
			throw new BusinessException(UserRespCode.SYSTEM_EXCEPTION);
		}

	}

	private static double[] calculatePosition(double x, double y, double angle) {
		angle = Math.toRadians(angle);
		double nx = (Math.cos(angle) * x) - (Math.sin(angle) * y);
		double ny = (Math.sin(angle) * x) + (Math.cos(angle) * y);
		return new double[]{nx, ny};
	}


	public static byte[] Rotate(byte[] bytes, int angel, String format) throws IOException {
		ByteArrayInputStream input = new ByteArrayInputStream(bytes);
		BufferedImage src = ImageIO.read(input);
		int src_width = src.getWidth(null);
		int src_height = src.getHeight(null);
		// 计算旋转后图片的尺寸
		Rectangle rect_des = CalcRotatedSize(new Rectangle(new Dimension(
				src_width, src_height)), angel);
		BufferedImage res = null;
		res = new BufferedImage(rect_des.width, rect_des.height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = res.createGraphics();
		// 进行转换
		g2.translate((rect_des.width - src_width) / 2,
				(rect_des.height - src_height) / 2);
		g2.rotate(Math.toRadians(angel), src_width / 2, src_height / 2);

		g2.drawImage(src, null, null);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(res, format, out);
		return out.toByteArray();
	}


	/**
	 * 计算旋转后的图片
	 *
	 * @param src   被旋转的图片
	 * @param angel 旋转角度
	 * @return 旋转后的图片
	 */
	public static Rectangle CalcRotatedSize(Rectangle src, int angel) {
		// 如果旋转的角度大于90度做相应的转换
		if (angel >= 90) {
			if (angel / 90 % 2 == 1) {
				int temp = src.height;
				src.height = src.width;
				src.width = temp;
			}
			angel = angel % 90;
		}

		double r = Math.sqrt(src.height * src.height + src.width * src.width) / 2;
		double len = 2 * Math.sin(Math.toRadians(angel) / 2) * r;
		double angel_alpha = (Math.PI - Math.toRadians(angel)) / 2;
		double angel_dalta_width = Math.atan((double) src.height / src.width);
		double angel_dalta_height = Math.atan((double) src.width / src.height);

		int len_dalta_width = (int) (len * Math.cos(Math.PI - angel_alpha
				- angel_dalta_width));
		int len_dalta_height = (int) (len * Math.cos(Math.PI - angel_alpha
				- angel_dalta_height));
		int des_width = src.width + len_dalta_width * 2;
		int des_height = src.height + len_dalta_height * 2;
		return new Rectangle(new Dimension(des_width, des_height));
	}

	public static String encodeImgageToBase64(MultipartFile multipartFile) {
		byte[] data = null;
// 读取图片字节数组
		try {
			InputStream in = multipartFile.getInputStream();
			data = new byte[in.available()];
			in.read(data);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 对字节数组Base64编码
		return Base64.encodeBase64String(data);// 返回Base64编码过的字节数组字符串
	}


	public static byte[] ThumbnailsRotate(byte[] bytes, int angel) throws IOException {
		ByteArrayInputStream input = new ByteArrayInputStream(bytes);
		BufferedImage old_img = ImageIO.read(input);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Thumbnails.of(old_img).rotate(angel).scale(1.0f).outputQuality(1.0f).outputFormat("jpg").toOutputStream(out);
//        Thumbnails.of(input).size(1080, 1920).outputFormat("jpg").toOutputStream(out);
		commpressPicCycle(out.toByteArray(), 2048 * 1024, out);
		return out.toByteArray();
	}

	public static void commpressPicCycle(byte[] inputByte, int desFileSize, ByteArrayOutputStream out) throws IOException {
		if (inputByte.length < desFileSize) return;
		ByteArrayInputStream input = new ByteArrayInputStream(inputByte);
		BufferedImage old_img = ImageIO.read(input);
		int srcWdith = old_img.getWidth();
		int srcHeigth = old_img.getHeight();
		int desWidth = new BigDecimal(srcWdith).multiply(
				new BigDecimal(0.9)).intValue();
		int desHeight = new BigDecimal(srcHeigth).multiply(
				new BigDecimal(0.9)).intValue();
		out.reset();
		Thumbnails.of(old_img).size(desWidth, desHeight)
				.outputQuality(0.9).outputFormat("jpg").toOutputStream(out);
		commpressPicCycle(out.toByteArray(), desFileSize, out);

	}

}
