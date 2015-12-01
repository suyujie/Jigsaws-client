package gamecore.io;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * GameOutput实现。内部使用ByteArrayOutputStream。
 */
public class ByteArrayGameOutput implements GameOutput {

    private ByteArrayOutputStream baos;
    private DataOutputStream out;

    public ByteArrayGameOutput() {
        baos = new ByteArrayOutputStream();
        out = new DataOutputStream(baos);
    }

    @Override
    public int size() {
        return out.size();
    }

    /**
     * 返回内部的字节数组。
     */
    @Override
    public byte[] toByteArray() {
        return baos.toByteArray();
    }

    @Override
    public void putBoolean(boolean b) {
        try {
            out.writeBoolean(b);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void put(byte b) {
        try {
            out.writeByte(b);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void putShort(short s) {
        try {
            out.writeShort(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void putInt(int i) {
        try {
            out.writeInt(i);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void putLong(long l) {
        try {
            out.writeLong(l);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void putChar(char value) {
        try {
            out.writeChar(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void putFloat(float value) {
        try {
            out.writeFloat(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void putDouble(double value) {
        try {
            out.writeDouble(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void putBytes(byte[] bytes) {
        try {
            if (bytes == null) {
                out.writeInt(0);
                return;
            }

            out.writeInt(bytes.length);
            out.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void putInts(int[] ints) {
        try {
            if (ints == null) {
                out.writeInt(0);
                return;
            }
            out.writeInt(ints.length);
            for (int i = 0; i < ints.length; i++) {
                out.writeInt(ints[i]);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void putBytesNoLength(byte[] bytes) {
        try {
            out.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void putString(String s) {
        try {
            if (s == null || s.isEmpty()) {
                out.writeInt(0);
                return;
            }

            byte[] bytes = s.getBytes("UTF-8");
            out.writeInt(bytes.length);
            out.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
