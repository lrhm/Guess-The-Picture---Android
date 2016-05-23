package ir.treeco.aftabe2.Util;

public class ImageKey {
    String data;

    ImageKey(int resourceId, int width, int height) {
        data = "_" + resourceId + "," + width + "," + height;
    }

    ImageKey(String relativePath, int width, int height) {
        data = "@" + relativePath + "," + width + "," + height;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageKey imageKey = (ImageKey) o;

        if (data != null ? !data.equals(imageKey.data) : imageKey.data != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return data != null ? data.hashCode() : 0;
    }


    @Override
    public String toString() {
        return data;
    }
}