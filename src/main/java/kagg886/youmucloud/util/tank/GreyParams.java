package kagg886.youmucloud.util.tank;


public class GreyParams {
    /**
     * 表图的亮度比例
     * */
    float outsideLight = 1.0f;

    /**
     * 里图的亮度比例
     * */
    float insideLight = 0.3f;

    private static final GreyParams DEFAULT = new GreyParams();

    public static GreyParams getDefault() {
        return DEFAULT;
    }
}
