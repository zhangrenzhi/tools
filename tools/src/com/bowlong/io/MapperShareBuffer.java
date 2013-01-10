package com.bowlong.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.FileLock;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.bowlong.concurrent.bio2.B2Codec;

@SuppressWarnings("rawtypes")
public class MapperShareBuffer {
	FileChannel channel;
	MappedByteBuffer buffer;
	public int TRY_NUM = 99; // ����99��
	public int TRY_WAIT = 10; // ÿ�����Լ��10ms

	public MapperShareBuffer(File file, int size) throws Exception {
		// "r" ��ֻ����ʽ�򿪡����ý��������κ� write �������������׳� IOException��
		// "rw" ���Ա��ȡ��д�롣������ļ��в����ڣ����Դ������ļ���
		// "rws" ���Ա��ȡ��д�룬���� "rw"����Ҫ����ļ������ݻ�Ԫ���ݵ�ÿ�����¶�ͬ��д�뵽�ײ�洢�豸��
		// "rwd" ���Ա��ȡ��д�룬���� "rw"����Ҫ����ļ����ݵ�ÿ�����¶�ͬ��д�뵽�ײ�洢�豸��
		RandomAccessFile raf = new RandomAccessFile(file, "rws");
		channel = raf.getChannel();
		buffer = channel.map(MapMode.READ_WRITE, 0, size);
		raf.close();
	}

	// /////////////////////////////////////////////////////////
	private final FileLock getLock() throws InterruptedException, IOException {
		FileLock result = null;
		for (int i = 0; i < TRY_NUM; i++) {
			result = channel.tryLock();
			if (result != null) {
				break;
			}
			Thread.sleep(TRY_WAIT);
		}
		return result;
	}

	// /////////////////////////////////////////////////////////
	public final int position(int pos) throws Exception {
		this.buffer.position(pos);
		return pos;
	}

	// /////////////////////////////////////////////////////////
	public final MappedByteBuffer buffer(){
		return this.buffer;
	}
	
	// /////////////////////////////////////////////////////////
	public final Map readMap(int pos) throws Exception {
		FileLock lock = getLock();
		if (lock == null)
			throw new Exception("tryLock TIMEOUT.");

		try {
			position(pos);
			return B2Codec.toMap(buffer);
		} catch (Exception e) {
			throw e;
		} finally {
			lock.release();
		}
	}

	public final Map readMap(MappedByteBuffer buffer) throws Exception {
		FileLock lock = getLock();
		if (lock == null)
			throw new Exception("tryLock TIMEOUT.");

		try {
			return B2Codec.toMap(buffer);
		} catch (Exception e) {
			throw e;
		} finally {
			lock.release();
		}
	}

	// /////////////////////////////////////////////////////////
	public final void putMap(int pos, Map m) throws Exception {
		FileLock lock = getLock();
		if (lock == null)
			throw new Exception("tryLock TIMEOUT.");

		try {
			position(pos);
			B2Codec.toBytes(buffer, m);
		} catch (Exception e) {
			throw e;
		} finally {
			lock.release();
		}
	}

	public final void putMap(Map m) throws Exception {
		FileLock lock = getLock();
		if (lock == null)
			throw new Exception("tryLock TIMEOUT.");

		try {
			B2Codec.toBytes(buffer, m);
		} catch (Exception e) {
			throw e;
		} finally {
			lock.release();
		}
	}

	// /////////////////////////////////////////////////////////
	public final List readList(int pos) throws Exception {
		FileLock lock = getLock();
		if (lock == null)
			throw new Exception("tryLock TIMEOUT.");

		try {
			position(pos);
			return B2Codec.toList(buffer);
		} catch (Exception e) {
			throw e;
		} finally {
			lock.release();
		}
	}

	public final List readList() throws Exception {
		FileLock lock = getLock();
		if (lock == null)
			throw new Exception("tryLock TIMEOUT.");

		try {
			return B2Codec.toList(buffer);
		} catch (Exception e) {
			throw e;
		} finally {
			lock.release();
		}
	}

	// /////////////////////////////////////////////////////////
	public final void putList(int pos, List l) throws Exception {
		FileLock lock = getLock();
		if (lock == null)
			throw new Exception("tryLock TIMEOUT.");

		try {
			position(pos);
			B2Codec.toBytes(buffer, l);
		} catch (Exception e) {
			throw e;
		} finally {
			lock.release();
		}
	}

	public final void putList(List l) throws Exception {
		FileLock lock = getLock();
		if (lock == null)
			throw new Exception("tryLock TIMEOUT.");

		try {
			B2Codec.toBytes(buffer, l);
		} catch (Exception e) {
			throw e;
		} finally {
			lock.release();
		}
	}

	// /////////////////////////////////////////////////////////

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		List l = new Vector();
		l.add(1);
		l.add(2);
		l.add(3);
		l.add("4");
		l.add(false);
		l.add("�Ǻ�");
		l.add(11.2233);
//		for (int i = 0; i < 10000; i++) {
//			l.add(i);
//		}
		File f = new File("m.bin");
		MapperShareBuffer msb = new MapperShareBuffer(f, 100 * 1024 * 1024);
		long _1 = System.currentTimeMillis();
		msb.putList(l);
		long _2 = System.currentTimeMillis();

		List l2 = msb.readList(0);
		long _3 = System.currentTimeMillis();
		System.out.println(l2);
		System.out.println("2-1:"+(_2 - _1) + " 3-2:" + (_3 - _2));
	}

}
