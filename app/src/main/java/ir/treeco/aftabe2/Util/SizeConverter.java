package ir.treeco.aftabe2.Util;

public class SizeConverter {

    public int mHeight;
    public int mWidth;
    private int baseWidth;
    private int baseHeight;
    private int style;

    public int getHeight() {
        return mHeight;
    }

    public int getWidth() {
        return mWidth;
    }

    public int convertWidth(int amount) {
        return (int) ((amount / (float) baseWidth) * (mWidth));

    }

    public int convertHeight(int amount) {
        return (int) (((amount / (float) baseHeight) * mHeight));
    }

    public int convertHeightCalcOffset(int amount) {
        return (int) (((amount / (float) baseHeight) * (mHeight + getTopOffset())));

    }

    public int getOffset() {
        switch (style) {
            case 1:
                return SizeManager.getScreenWidth() - mWidth;
            case 2:
                return SizeManager.getScreenHeight() - mHeight;

            default:
                return 0;
        }
    }

    public int getLeftOffset() {
        return SizeManager.getScreenWidth() - mWidth;
    }

    public int getTopOffset() {
        return SizeManager.getScreenHeight() - mHeight;
    }

    public static SizeConverter SizeConvertorFormHeight(double mHeight,
                                                        int baseWidth, int baseHeight) {
        SizeConverter convertor = new SizeConverter();
        convertor.style = 1;
        convertor.mHeight = (int) mHeight;
        convertor.baseHeight = baseHeight;
        convertor.baseWidth = baseWidth;
        convertor.mWidth = (int) ((mHeight / (float) baseHeight) * baseWidth);
        return convertor;
    }

    public static SizeConverter SizeConvertorFromWidth(float mWidth,
                                                       int baseWidth, int baseHeight) {
        SizeConverter convertor = new SizeConverter();
        convertor.style = 2;

        convertor.mWidth = (int) mWidth;
        convertor.baseHeight = baseHeight;
        convertor.baseWidth = baseWidth;
        convertor.mHeight = (int) ((mWidth / (float) baseWidth) * baseHeight);
        return convertor;
    }

    public static SizeConverter SizeConverterFromLessOffset(float mWidth, float mHeight, int baseWidth, int baseHeight) {

        SizeConverter fromWidth = SizeConvertorFromWidth(mWidth, baseWidth, baseHeight);
        SizeConverter fromHeight = SizeConvertorFormHeight(mHeight, baseWidth, baseHeight);

        if (fromWidth.getOffset() <= 0) {
            return fromWidth;
        }
        if (fromHeight.getOffset() <= 0) {
            return fromHeight;
        }

        if (fromHeight.getOffset() < fromWidth.getOffset()) {
            return fromHeight;
        }
        return fromWidth;

    }

}
