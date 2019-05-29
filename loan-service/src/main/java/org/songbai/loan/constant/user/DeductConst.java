package org.songbai.loan.constant.user;

public class DeductConst {

    public enum Status {
        WAIT(1, "等待扣款"), DEDUCT(2, "扣款中"), FINISH(3, "扣款结束"), FAIL(4, "异常结束");

        public int code;
        public String name;

        Status(Integer code, String name) {
            this.code = code;
            this.name = name;
        }
    }

    public enum Flow {
        WAIT(1, "等待成功"), SUCCESS(2, "成功"), FAIL(3, "失败");

        public int code;
        public String name;

        Flow(Integer code, String name) {
            this.code = code;
            this.name = name;
        }
    }

    public enum DeductType {
        RATE(1, "比例扣款"), FIX(2, "固定扣款");
        public int code;
        public String name;

        DeductType(Integer code, String name) {
            this.code = code;
            this.name = name;
        }
    }

}
