package com.bowlong.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import com.bowlong.objpool.StringBufPool;

@SuppressWarnings("unused")
public class FileEx {
	public static final RandomAccessFile openRandomAccessFile(File f,
			String mode) throws FileNotFoundException {
		return new RandomAccessFile(f, mode);
	}

	public static final RandomAccessFile openRandomAccessFile(String f,
			String mode) throws FileNotFoundException {
		return new RandomAccessFile(f, mode);
	}

	public static final InputStream openInputStreamByUrl(String url)
			throws MalformedURLException, IOException {
		URL u = new URL(url);
		InputStream in = openInputStreamByUrl(u);
		return in;
	}

	public static final InputStream openInputStreamByUrl(URL url)
			throws IOException {
		InputStream in = url.openStream();
		return in;
	}

	public static final byte[] readFully(URL url) throws MalformedURLException,
			IOException {
		InputStream in = openInputStreamByUrl(url);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buff = null;
		int len = 0;
		do {
			buff = new byte[8 * 1024];
			len = in.read(buff);
			if (len < 0)
				break;

			baos.write(buff, 0, len);
		} while (len > 0);

		return baos.toByteArray();
	}

	public static final int getLength(String file) {
		File f = openFile(file);
		return (int) f.length();
	}

	/**
	 * ����Ŀ¼ createFolder
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static final boolean createFolder(String path) {
		File file = new File(path);
		boolean b = file.mkdirs();
		file = null;
		return b;
	}

	/**
	 * ����ļ��Ƿ���� exists
	 */
	public static final boolean exists(String path) {
		File file = new File(path);
		boolean b = file.exists();
		file = null;
		return b;
	}

	/**
	 * �½��ļ�
	 * 
	 * @param file
	 *            String �ļ�·�������� ��c:/fqf.txt
	 * @param fileContent
	 *            String �ļ�����
	 * @return boolean
	 */
	public static final void newFile(String file, String fileContent) {

		try {
			String filePath = file;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			if (!myFilePath.exists()) {
				myFilePath.createNewFile();
			}
			FileWriter resultFile = new FileWriter(myFilePath);
			PrintWriter myFile = new PrintWriter(resultFile);
			String strContent = fileContent;
			myFile.println(strContent);
			resultFile.close();

			myFilePath = null;
			resultFile = null;
			myFile = null;
			strContent = null;
		} catch (Exception e) {
			// Log.println("�½�Ŀ¼��������");
			e.printStackTrace();
		}

	}

	/**
	 * ɾ���ļ�
	 * 
	 * @param file
	 *            String �ļ�·�������� ��c:/fqf.txt
	 * @param fileContent
	 *            String
	 * @return boolean
	 */
	public static final void delFile(String file) {
		try {
			String filePath = file;
			filePath = filePath.toString();
			java.io.File myDelFile = new java.io.File(filePath);
			myDelFile.delete();
			myDelFile = null;

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * ɾ���ļ���
	 * 
	 * @param filePathAndName
	 *            String �ļ���·�������� ��c:/fqf
	 * @param fileContent
	 *            String
	 * @return boolean
	 */
	public static final void delFolder(String folderPath) {
		delAllFile(folderPath); // ɾ����������������
		String filePath = folderPath;
		filePath = filePath.toString();
		java.io.File myFilePath = new java.io.File(filePath);
		myFilePath.delete(); // ɾ�����ļ���
		myFilePath = null;
	}

	/**
	 * ɾ���ļ�������������ļ�
	 * 
	 * @param path
	 *            String �ļ���·�� �� c:/fqf
	 */
	public static final void delAllFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			file = null;
			return;
		}
		if (!file.isDirectory()) {
			file = null;
			return;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]); // ��ɾ���ļ���������ļ�
				delFolder(path + "/" + tempList[i]); // ��ɾ�����ļ���
			}
		}
		temp = null;
		tempList = null;
	}

	/**
	 * ���Ƶ����ļ�
	 * 
	 * @param oldPath
	 *            String ԭ�ļ�·�� �磺c:/fqf.txt
	 * @param newPath
	 *            String ���ƺ�·�� �磺f:/fqf.txt
	 * @return boolean
	 * @throws IOException
	 */
	public static final void copyFile(String oldPath, String newPath)
			throws IOException {
		int bytesum = 0;
		int byteread = 0;
		File oldfile = new File(oldPath);
		if (oldfile.exists()) { // �ļ�����ʱ
			InputStream inStream = new FileInputStream(oldPath); // ����ԭ�ļ�
			FileOutputStream fs = new FileOutputStream(newPath);
			byte[] buffer = new byte[1024];
			// int length;
			while ((byteread = inStream.read(buffer)) != -1) {
				bytesum += byteread; // �ֽ��� �ļ���С
				fs.write(buffer, 0, byteread);
			}
			inStream.close();
			fs.close();
			inStream = null;
			fs = null;
		}

		oldfile = null;
	}

	/**
	 * ���������ļ�������
	 * 
	 * @param oldPath
	 *            String ԭ�ļ�·�� �磺c:/fqf
	 * @param newPath
	 *            String ���ƺ�·�� �磺f:/fqf/ff
	 * @return boolean
	 * @throws IOException
	 */
	public static final void copyFolder(String oldPath, String newPath)
			throws IOException {

		(new File(newPath)).mkdirs(); // ����ļ��в����� �������ļ���
		File a = new File(oldPath);
		String[] file = a.list();
		File temp = null;
		for (int i = 0; i < file.length; i++) {
			if (oldPath.endsWith(File.separator)) {
				temp = new File(oldPath + file[i]);
			} else {
				temp = new File(oldPath + File.separator + file[i]);
			}

			if (temp.isFile()) {
				FileInputStream input = new FileInputStream(temp);
				FileOutputStream output = new FileOutputStream(newPath + "/"
						+ (temp.getName()).toString());
				byte[] b = new byte[1024 * 5];
				int len;
				while ((len = input.read(b)) != -1) {
					output.write(b, 0, len);
				}
				output.flush();
				output.close();
				input.close();
				input = null;
				output = null;
			}
			if (temp.isDirectory()) { // ��������ļ���
				copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
			}

			a = null;
			file = null;
			temp = null;
		}

	}

	/**
	 * �ƶ��ļ���ָ��Ŀ¼
	 * 
	 * @param oldPath
	 *            String �磺c:/fqf.txt
	 * @param newPath
	 *            String �磺d:/fqf.txt
	 * @throws IOException
	 */
	public static final void moveFile(String oldPath, String newPath)
			throws IOException {
		copyFile(oldPath, newPath);
		delFile(oldPath);
	}

	/**
	 * �ƶ��ļ���ָ��Ŀ¼
	 * 
	 * @param oldPath
	 *            String �磺c:/fqf.txt
	 * @param newPath
	 *            String �磺d:/fqf.txt
	 * @throws IOException
	 */
	public static final void moveFolder(String oldPath, String newPath)
			throws IOException {
		copyFolder(oldPath, newPath);
		delFolder(oldPath);
	}

	public static final boolean rename(String srcPath, String name) {
		File file = new File(srcPath);
		File newFile = new File(file.getParent() + "/" + name);
		boolean b = file.renameTo(newFile);
		file = null;
		newFile = null;
		return b;
	}

	/**
	 * �ж��Ƿ���Ŀ¼
	 * 
	 * @param path
	 * @return
	 */
	public static final boolean isDir(String path) {
		File file = new File(path);
		boolean b = file.isDirectory();
		file = null;
		return b;
	}

	/**
	 * �ж��Ƿ����ļ�
	 * 
	 * @param path
	 * @return
	 */
	public static final boolean isFile(String path) {
		File file = new File(path);
		boolean b = file.isFile();
		file = null;
		return b;
	}

	/**
	 * read
	 * 
	 * @param file
	 * @param skip
	 * @param len
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public static final byte[] read(String file, long skip, int len)
			throws IOException {
		InputStream input = openInputStream(file);
		byte[] data = new byte[len];
		input.skip(skip);
		int readlen = input.read(data);
		input.close();
		input = null;
		if (len == readlen) {
			return data;
		} else {
			byte[] readData = new byte[readlen];
			System.arraycopy(data, 0, readData, 0, readlen);
			return readData;
		}
	}

	public static final byte[] readFully(File f) {
		byte[] b = null;

		if (f == null)
			return b;

		try {
			int len = (int) f.length();
			b = new byte[len];
			DataInputStream dis = new DataInputStream(new FileInputStream(f));
			dis.readFully(b);
			dis.close();
			return b;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static final byte[] readFully(InputStream in) {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();

		if (in == null)
			return null;

		try {
			int len = 0;
			do {
				byte[] buff = new byte[8 * 1024];
				len = in.read(buff);
				if (len < 0)
					break;

				buf.write(buff, 0, len);
			} while (len > 0);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return buf.toByteArray();
	}

	public static final byte[] readFully(String file) throws IOException {
		File f = new File(file);
		DataInputStream dis = new DataInputStream(new FileInputStream(f));
		byte[] data = new byte[(int) f.length()];
		dis.readFully(data);
		dis.close();
		f = null;
		dis = null;
		return data;
	}

	public static final Properties readProperties(File f) {
		try {
			if (f == null || !f.exists())
				return null;
			Properties p = new Properties();
			FileInputStream fis;
			fis = new FileInputStream(f);
			p.load(fis);
			fis.close();
			return p;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static final String readAll(String file) throws IOException {
		return readAll(new File(file));
	}

	public static final String readAll(File file) throws IOException {
		BufferedReader br = openBufferedReader(file);
		String line = "";
		StringBuffer sb = StringBufPool.borrowObject();
		try {
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\r\n");
			}
			br.close();

			br = null;
			return sb.toString();
		} finally {
			StringBufPool.returnObject(sb);
		}
	}

	public static final List<String> readLines(String file) throws IOException {
		return readLines(new File(file));
	}

	public static final List<String> readLines(File file) throws IOException {
		BufferedReader br = openBufferedReader(file);
		List<String> ret = new Vector<String>();
		int times = 100000;
		while (true) {
			if (times-- <= 0)
				break;

			String line = br.readLine();
			if (line == null)
				break;
			ret.add(line);
		}
		br.close();
		br = null;
		return ret;
	}

	public static final void write(String file, boolean append, byte[] data)
			throws IOException {
		File f = new File(file);
		FileOutputStream output = new FileOutputStream(f, append); // 2��ʾ׷��
		output.write(data);
		output.flush();
		output.close();
		f = null;
		output = null;
	}

	public static final void write(String file, byte[] data) throws IOException {
		boolean append = false;
		write(file, append, data);
	}

	public static final void writeAll(String file, boolean append, String str)
			throws IOException {
		File f = new File(file);
		FileOutputStream output = new FileOutputStream(f, append); // 2��ʾ׷��
		output.write(str.getBytes());
		output.flush();
		output.close();
		f = null;
		output = null;
	}

	public static final void writeAll(String file, String str)
			throws IOException {
		boolean append = false;
		writeAll(file, append, str);
	}

	public static final void writeFully(File f, byte[] b) throws IOException {
		writeFully(f, b, false);
	}

	public static final void writeFully(File f, byte[] b, boolean append)
			throws IOException {
		FileOutputStream output = new FileOutputStream(f, append); // 2��ʾ׷��
		output.write(b);
		output.flush();
		output.close();
		f = null;
		output = null;
	}

	public static final void writeFully(String filename, byte[] b)
			throws IOException {
		writeFully(filename, b, false);
	}

	public static final void writeFully(String filename, byte[] b,
			boolean append) throws IOException {
		File f = new File(filename);
		writeFully(f, b, append);
	}

	public static final InputStream openInputStream(String file)
			throws FileNotFoundException {
		FileInputStream fis = new FileInputStream(file);
		return fis;
	}

	public static final BufferedInputStream openBufferedInputStream(
			InputStream is) {
		return new BufferedInputStream(is);
	}

	public static final BufferedInputStream openBufferedInputStream(String file)
			throws FileNotFoundException {
		InputStream is = openInputStream(file);
		return openBufferedInputStream(is);
	}

	public static final DataInputStream openDataInputStream(InputStream is)
			throws FileNotFoundException {
		BufferedInputStream bis = openBufferedInputStream(is);
		return new DataInputStream(bis);
	}

	public static final DataInputStream openDataInputStream(String file)
			throws FileNotFoundException {
		InputStream is = openInputStream(file);
		return openDataInputStream(is);
	}

	public static final OutputStream openOutputStream(String file,
			boolean append) throws FileNotFoundException {
		FileOutputStream fos = new FileOutputStream(file, append);
		return fos;
	}

	public static final BufferedOutputStream openBufferedOutputStream(
			OutputStream os) {
		return new BufferedOutputStream(os);
	}

	public static final OutputStream openOutputStream(String file)
			throws FileNotFoundException {
		boolean append = false;
		return openOutputStream(file, append);
	}

	public static final DataOutputStream openDataOutputStream(OutputStream os) {
		BufferedOutputStream bos = openBufferedOutputStream(os);
		return new DataOutputStream(bos);
	}

	public static final DataOutputStream openDataOutputStream(String file,
			boolean append) throws FileNotFoundException {
		OutputStream os = openOutputStream(file, append);
		return openDataOutputStream(os);
	}

	public static final DataOutputStream openDataOutputStream(String file)
			throws FileNotFoundException {
		boolean append = false;
		return openDataOutputStream(file, append);
	}

	public static final File openFile(String file) {
		return new File(file);
	}

	public static final InputStream openInputStream(File file)
			throws FileNotFoundException {
		FileInputStream fis = new FileInputStream(file);
		return fis;
	}

	public static final DataInputStream openDataInputStream(File file)
			throws FileNotFoundException {
		InputStream is = openInputStream(file);
		return openDataInputStream(is);
	}

	public static final FileWriter openFileWriter(File file) throws IOException {
		return new FileWriter(file);
	}

	public static final FileReader openFileReader(File file)
			throws FileNotFoundException {
		return new FileReader(file);
	}

	public static final BufferedReader openBufferedReader(Reader reader) {
		return new BufferedReader(reader);
	}

	public static final FileReader openFileReader(String file)
			throws FileNotFoundException {
		File f = openFile(file);
		return openFileReader(f);
	}

	public static final BufferedReader openBufferedReader(File file)
			throws FileNotFoundException {
		FileReader fr = openFileReader(file);
		return openBufferedReader(fr);
	}

	public static final BufferedReader openBufferedReader(String file)
			throws FileNotFoundException {
		FileReader fr = openFileReader(file);
		return openBufferedReader(fr);
	}

	public static final InputStreamReader openInputStreamReader(InputStream is) {
		return new InputStreamReader(is);
	}

	public static final FileWriter openFileWriter(String file)
			throws IOException {
		File f = openFile(file);
		return openFileWriter(f);
	}

	public static final BufferedWriter openBufferedWriter(Writer writer) {
		return new BufferedWriter(writer);
	}

	public static final BufferedWriter openBufferedWriter(String file)
			throws IOException {
		FileWriter fw = openFileWriter(file);
		return openBufferedWriter(fw);
	}

	public static final String getPath(String filename) {
		File f = new File(filename);
		return f.getParentFile().getPath();
	}

	public static final String getName(String filename) {
		File f = new File(filename);
		return f.getName();
	}

	public static final String getExtension(String filename) {
		if (filename == null) {
			return null;
		}
		int index = indexOfExtension(filename);
		if (index == -1) {
			return "";
		} else {
			return filename.substring(index + 1);
		}
	}

	public static final char EXTENSION_SEPARATOR = '.';
	private static final char UNIX_SEPARATOR = '/';
	private static final char WINDOWS_SEPARATOR = '\\';

	public static final int indexOfExtension(String filename) {
		if (filename == null) {
			return -1;
		}
		int extensionPos = filename.lastIndexOf(EXTENSION_SEPARATOR);
		int lastSeparator = indexOfLastSeparator(filename);
		return (lastSeparator > extensionPos ? -1 : extensionPos);
	}

	public static final int indexOfLastSeparator(String filename) {
		if (filename == null) {
			return -1;
		}
		int lastUnixPos = filename.lastIndexOf(UNIX_SEPARATOR);
		int lastWindowsPos = filename.lastIndexOf(WINDOWS_SEPARATOR);
		return Math.max(lastUnixPos, lastWindowsPos);
	}

	public static final String getAppRoot() {
		return System.getProperty("user.dir");
	}

	public static void main(String[] args) {
		try {
			String s = "D:/Temp/GlassfishSvc.txt";
			String p = FileEx.getPath(s);
			System.out.println(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
