package org.songbai.loan.constant.user;


/**
 * 绑卡、资金相关类型
 *
 * @author wjl
 * @date 2018年11月09日 15:00:14
 * @description
 */
public class FinanceConstant {

	public static final String PAY = "pay";
	public static final String PAYCONFIRM = "payConfirm";
	public static final String BIND = "bind";
	public static final String BINDCONFIRM = "bindConfirm";


	public enum FlowType{
		ONLINE(1, "线上还款"), OFFLINE(2, "线下还款"), DEDUCT(3, "自动扣款");

		public int type;
		public String typeDetail;

		FlowType(int type, String typeDetail) {
			this.type = type;
			this.typeDetail = typeDetail;
		}
	}

	public enum PayType {
		PAY(-1, "财务打款"), REPAY(1, "用户还款"), DEDUCT(2, "自动扣款");

		public int type;
		public String typeDetail;

		PayType(int type, String typeDetail) {
			this.type = type;
			this.typeDetail = typeDetail;
		}
	}

	public enum IoStatus {
		//io表的状态（0 初始化，1 还、放款成功，2还、放款失败，3 等待短验，4处理中，5拒绝放款）
		INIT(0, "初始化"), SUCCESS(1, "还、放款成功"), FAILED(2, "还、放款失败"), WAIT(3, "等待短验"), PROCESSING(4, "交易处理中"), REFUSE(5, "拒绝放款");

		public int key;
		public String value;

		IoStatus(int key, String value) {
			this.key = key;
			this.value = value;
		}
	}

	public enum PayPlatform {
		// 1 畅捷  2 易宝
        CHANGJIE("changjie", "畅捷支付"), YIBAO("yibao", "易宝支付"),
        TEST("test", "测试支付"), ALIPAYY("aliPay", "支付宝"), WXPAY("wxPay", "微信支付");

		public String code;
		public String name;

		PayPlatform(String code, String name) {
			this.code = code;
			this.name = name;
		}

		public static String getName(String code) {
			PayPlatform[] platforms = PayPlatform.values();
			for (PayPlatform platform : platforms) {
				if (platform.code.equals(code)) {
					return platform.name;
				}
			}
			return null;
		}
	}

	public enum BankCardType {
		//该张卡是否是默认 0不是 1是
		OTHER(0), DEFAULT(1);

		public int key;

		BankCardType(int key) {
			this.key = key;
		}
	}

	public enum BankCardStatus {
		//0待绑定  1已绑定 2 已解绑
		INIT(0), BIND(1), UNBIND(2);

		public int key;

		BankCardStatus(int key) {
			this.key = key;
		}
	}

	public enum Status {
		//0禁用  1启用
		DISABLE(0), ENABLE(1);

		public int key;

		Status(int key) {
			this.key = key;
		}
	}
}
